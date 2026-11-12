package com.jackbrandon.chatea.data.realtime;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jackbrandon.chatea.BuildConfig;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.auth.SessionManager;
import com.jackbrandon.chatea.data.model.Sesion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RealtimeConexion {

    public interface Oyente {
        default void cambio(String tabla, String tipo, JsonObject registro) {}

        default void broadcast(String evento, JsonObject datos) {}

        default void suscrito(boolean esReconexion) {}
    }

    public interface EstadoConexion {
        void conexion(boolean conectado);
    }

    public final class Canal {
        private final String tema;
        private final JsonArray cambios = new JsonArray();
        private boolean broadcast = false;
        private Oyente oyente = new Oyente() {};
        private String refUnion;
        private boolean yaEstuvoUnido = false;

        private Canal(String nombre) {
            this.tema = "realtime:" + nombre;
        }

        public Canal cambios(String evento, String tabla, String filtro) {
            JsonObject cambio = new JsonObject();
            cambio.addProperty("event", evento);
            cambio.addProperty("schema", "public");
            cambio.addProperty("table", tabla);
            if (filtro != null) cambio.addProperty("filter", filtro);
            cambios.add(cambio);
            return this;
        }

        public Canal conBroadcast() {
            broadcast = true;
            return this;
        }

        public Canal oyente(Oyente oyente) {
            this.oyente = oyente;
            return this;
        }

        public Canal unirse() {
            canales.put(tema, this);
            if (abierto) enviarUnion(this);
            return this;
        }

        public void salir() {
            canales.remove(tema);
            if (abierto) enviar(mensaje(tema, "phx_leave", new JsonObject()));
        }

        public void enviarBroadcast(String evento, JsonObject datos) {
            if (!abierto) return;
            JsonObject payload = new JsonObject();
            payload.addProperty("type", "broadcast");
            payload.addProperty("event", evento);
            payload.add("payload", datos);
            enviar(mensaje(tema, "broadcast", payload));
        }
    }

    private static final String TAG = "Realtime";
    private static final long LATIDO_MS = 25_000;
    private static final long[] ESPERAS_RECONEXION_MS = {1_000, 2_000, 5_000, 10_000};

    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();

    private final AuthRepository auth;
    private final SessionManager sesiones;
    private final EstadoConexion estado;
    private final Map<String, Canal> canales = new LinkedHashMap<>();
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());

    private WebSocket socket;
    private boolean activo = false;
    private boolean abierto = false;
    private int intentos = 0;
    private int referencia = 0;
    private String tokenEnviado;

    private final Runnable latido = new Runnable() {
        @Override
        public void run() {
            if (!activo) return;
            enviar(mensaje("phoenix", "heartbeat", new JsonObject()));
            renovarTokenSiHaceFalta();
            hiloPrincipal.postDelayed(this, LATIDO_MS);
        }
    };

    public RealtimeConexion(Context context, EstadoConexion estado) {
        this.auth = new AuthRepository(context);
        this.sesiones = SessionManager.get(context);
        this.estado = estado;
    }

    public Canal canal(String nombre) {
        return new Canal(nombre);
    }

    public boolean tieneCanal(String nombre) {
        return canales.containsKey("realtime:" + nombre);
    }

    public void iniciar() {
        if (activo) return;
        activo = true;
        conectar();
    }

    public void detener() {
        activo = false;
        abierto = false;
        canales.clear();
        hiloPrincipal.removeCallbacksAndMessages(null);
        if (socket != null) {
            socket.close(1000, "Pantalla cerrada");
            socket = null;
        }
    }

    private void conectar() {
        auth.asegurarSesion(new AuthRepository.Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                if (!activo) return;
                String url = BuildConfig.SUPABASE_URL.trim()
                        .replaceAll("/+$", "")
                        .replaceFirst("^http", "ws")
                        + "/realtime/v1/websocket?apikey=" + BuildConfig.SUPABASE_KEY + "&vsn=1.0.0";
                socket = HTTP.newWebSocket(new Request.Builder().url(url).build(), new Oyentes());
            }

            @Override
            public void error(String mensaje) {
                programarReconexion();
            }
        });
    }

    private void enviarUnion(Canal canal) {
        JsonObject config = new JsonObject();
        if (canal.cambios.size() > 0) config.add("postgres_changes", canal.cambios);
        if (canal.broadcast) {
            JsonObject broadcast = new JsonObject();
            broadcast.addProperty("self", false);
            config.add("broadcast", broadcast);
        }

        JsonObject payload = new JsonObject();
        payload.add("config", config);
        tokenEnviado = sesiones.getAccessToken();
        payload.addProperty("access_token", tokenEnviado);

        JsonObject union = mensaje(canal.tema, "phx_join", payload);
        canal.refUnion = union.get("ref").getAsString();
        union.addProperty("join_ref", canal.refUnion);
        enviar(union);
    }

    private void renovarTokenSiHaceFalta() {
        auth.asegurarSesion(new AuthRepository.Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                String actual = sesiones.getAccessToken();
                if (!abierto || actual == null || actual.equals(tokenEnviado)) return;
                for (Canal canal : canales.values()) {
                    JsonObject payload = new JsonObject();
                    payload.addProperty("access_token", actual);
                    enviar(mensaje(canal.tema, "access_token", payload));
                }
                tokenEnviado = actual;
                Log.d(TAG, "Token renovado y enviado a los canales");
            }

            @Override
            public void error(String mensaje) {
            }
        });
    }

    private void procesar(String texto) {
        JsonObject json = JsonParser.parseString(texto).getAsJsonObject();
        String evento = json.get("event").getAsString();
        String tema = json.get("topic").getAsString();
        JsonObject payload = json.has("payload") && json.get("payload").isJsonObject()
                ? json.getAsJsonObject("payload") : new JsonObject();
        Canal canal = canales.get(tema);
        if (canal == null) return;

        switch (evento) {
            case "phx_reply":
                JsonElement ref = json.get("ref");
                if (ref == null || ref.isJsonNull() || !ref.getAsString().equals(canal.refUnion)) return;
                if ("ok".equals(texto(payload, "status"))) {
                    Log.d(TAG, "Suscrito a " + tema);
                    intentos = 0;
                    canal.oyente.suscrito(canal.yaEstuvoUnido);
                    canal.yaEstuvoUnido = true;
                } else {
                    Log.w(TAG, "Supabase rechazó " + tema + ": " + payload);
                }
                break;

            case "postgres_changes":
                JsonObject datos = payload.getAsJsonObject("data");
                if (datos != null && datos.has("record")) {
                    canal.oyente.cambio(
                            texto(datos, "table"),
                            texto(datos, "type"),
                            datos.getAsJsonObject("record"));
                }
                break;

            case "broadcast":
                JsonObject contenido = payload.has("payload") && payload.get("payload").isJsonObject()
                        ? payload.getAsJsonObject("payload") : new JsonObject();
                canal.oyente.broadcast(texto(payload, "event"), contenido);
                break;

            case "system":
                if ("ok".equals(texto(payload, "status"))) {
                    Log.d(TAG, "system: " + payload);
                } else {
                    Log.w(TAG, "system: " + payload);
                }
                break;

            case "phx_error":
                Log.w(TAG, "phx_error en " + tema + ": " + payload);
                break;

            default:
                break;
        }
    }

    private static String texto(JsonObject json, String campo) {
        JsonElement valor = json.get(campo);
        return valor == null || valor.isJsonNull() ? "" : valor.getAsString();
    }

    private void alPerderConexion() {
        abierto = false;
        if (!activo) return;
        hiloPrincipal.removeCallbacks(latido);
        estado.conexion(false);
        programarReconexion();
    }

    private void programarReconexion() {
        if (!activo) return;
        long espera = ESPERAS_RECONEXION_MS[Math.min(intentos, ESPERAS_RECONEXION_MS.length - 1)];
        intentos++;
        Log.d(TAG, "Reconectando en " + espera + " ms");
        hiloPrincipal.postDelayed(this::conectar, espera);
    }

    private JsonObject mensaje(String tema, String evento, JsonObject payload) {
        JsonObject json = new JsonObject();
        json.addProperty("topic", tema);
        json.addProperty("event", evento);
        json.add("payload", payload);
        json.addProperty("ref", String.valueOf(++referencia));
        return json;
    }

    private void enviar(JsonObject json) {
        if (socket != null) socket.send(json.toString());
    }

    private class Oyentes extends WebSocketListener {

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            hiloPrincipal.post(() -> {
                if (webSocket != socket) return;
                Log.d(TAG, "WebSocket abierto");
                abierto = true;
                estado.conexion(true);
                for (Canal canal : canales.values()) enviarUnion(canal);
                hiloPrincipal.removeCallbacks(latido);
                hiloPrincipal.postDelayed(latido, LATIDO_MS);
            });
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            hiloPrincipal.post(() -> {
                if (webSocket != socket) return;
                try {
                    procesar(text);
                } catch (RuntimeException e) {
                    Log.w(TAG, "Mensaje que no se pudo leer: " + text, e);
                }
            });
        }

        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            webSocket.close(1000, null);
        }

        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            hiloPrincipal.post(() -> {
                if (webSocket == socket) alPerderConexion();
            });
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
            Log.w(TAG, "Falló el WebSocket: " + t.getMessage());
            hiloPrincipal.post(() -> {
                if (webSocket == socket) alPerderConexion();
            });
        }
    }
}
