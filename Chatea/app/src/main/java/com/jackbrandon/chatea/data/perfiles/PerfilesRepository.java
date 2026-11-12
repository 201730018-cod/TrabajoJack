package com.jackbrandon.chatea.data.perfiles;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.jackbrandon.chatea.BuildConfig;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.auth.AuthRepository.Resultado;
import com.jackbrandon.chatea.data.auth.SessionManager;
import com.jackbrandon.chatea.data.model.Perfil;
import com.jackbrandon.chatea.data.remote.ErroresSupabase;
import com.jackbrandon.chatea.data.remote.RespuestaSinCuerpo;
import com.jackbrandon.chatea.data.remote.SupabaseApi;
import com.jackbrandon.chatea.data.remote.SupabaseClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilesRepository {

    private static final String SIN_CONEXION = "No hay conexión a internet";
    private static final MediaType JPEG = MediaType.get("image/jpeg");

    private final SupabaseApi api;
    private final AuthRepository auth;
    private final SessionManager sesiones;

    public PerfilesRepository(Context context) {
        this.api = SupabaseClient.getApi();
        this.auth = new AuthRepository(context);
        this.sesiones = SessionManager.get(context);
    }

    public void obtenerMiPerfil(Resultado<Perfil> resultado) {
        conSesion(resultado, () ->
                api.obtenerPerfiles(sesiones.bearer(), "eq." + sesiones.getUsuarioId(), null, null)
                        .enqueue(new RespuestaLista<>(resultado, lista ->
                                lista.isEmpty() ? null : lista.get(0))));
    }

    public void buscarUsuarios(String texto, Resultado<List<Perfil>> resultado) {
        String limpio = texto == null ? "" : texto.replaceAll("[*%,()]", "").trim();
        String filtroNombre = limpio.isEmpty() ? null : "ilike.*" + limpio + "*";

        conSesion(resultado, () ->
                api.obtenerPerfiles(sesiones.bearer(), "neq." + sesiones.getUsuarioId(), filtroNombre, "nombre.asc")
                        .enqueue(new RespuestaLista<>(resultado, lista -> lista)));
    }

    public void guardarPerfil(String nombre, byte[] fotoJpeg, Resultado<Perfil> resultado) {
        conSesion(resultado, () -> {
            if (fotoJpeg == null) {
                actualizar(nombre, null, resultado);
                return;
            }

            String ruta = sesiones.getUsuarioId() + "/avatar.jpg";
            api.subirAvatar(sesiones.bearer(), ruta, RequestBody.create(fotoJpeg, JPEG))
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            if (!response.isSuccessful()) {
                                resultado.error("No se pudo subir la foto: " + ErroresSupabase.mensaje(response));
                                return;
                            }
                            String url = BuildConfig.SUPABASE_URL.replaceAll("/+$", "")
                                    + "/storage/v1/object/public/avatares/" + ruta
                                    + "?v=" + System.currentTimeMillis();
                            actualizar(nombre, url, resultado);
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                            resultado.error(SIN_CONEXION);
                        }
                    });
        });
    }

    public void quitarFoto(Resultado<Void> resultado) {
        conSesion(resultado, () ->
                api.borrarArchivo(sesiones.bearer(), "avatares", sesiones.getUsuarioId() + "/avatar.jpg")
                        .enqueue(new RespuestaSinCuerpo<>(new Resultado<Void>() {
                            @Override
                            public void exito(Void dato) {
                                quitarUrlDeFoto(resultado);
                            }

                            @Override
                            public void error(String mensaje) {
                                quitarUrlDeFoto(resultado);
                            }
                        })));
    }

    public void subirFotoDeGrupo(byte[] fotoJpeg, Resultado<String> resultado) {
        String ruta = sesiones.getUsuarioId() + "/grupo-" + UUID.randomUUID() + ".jpg";
        conSesion(resultado, () ->
                api.subirAvatar(sesiones.bearer(), ruta, RequestBody.create(fotoJpeg, JPEG))
                        .enqueue(new RespuestaSinCuerpo<>(new Resultado<Void>() {
                            @Override
                            public void exito(Void dato) {
                                resultado.exito(BuildConfig.SUPABASE_URL.trim().replaceAll("/+$", "")
                                        + "/storage/v1/object/public/avatares/" + ruta);
                            }

                            @Override
                            public void error(String mensaje) {
                                resultado.error("No se pudo subir la foto del grupo: " + mensaje);
                            }
                        })));
    }

    private void quitarUrlDeFoto(Resultado<Void> resultado) {
        api.quitarFotoPerfil(sesiones.bearer(), Collections.emptyMap())
                .enqueue(new RespuestaSinCuerpo<>(resultado));
    }

    private void actualizar(String nombre, String fotoUrl, Resultado<Perfil> resultado) {
        Map<String, Object> cambios = new HashMap<>();
        cambios.put("nombre", nombre);
        if (fotoUrl != null) cambios.put("foto_url", fotoUrl);

        api.actualizarPerfil(sesiones.bearer(), "eq." + sesiones.getUsuarioId(), cambios)
                .enqueue(new RespuestaLista<>(resultado, lista ->
                        lista.isEmpty() ? null : lista.get(0)));
    }

    private void conSesion(Resultado<?> resultado, Runnable accion) {
        auth.conSesion(resultado, accion);
    }

    private interface Convertir<T> {
        T desde(List<Perfil> lista);
    }

    private static class RespuestaLista<T> implements Callback<List<Perfil>> {

        private final Resultado<T> resultado;
        private final Convertir<T> convertir;

        RespuestaLista(Resultado<T> resultado, Convertir<T> convertir) {
            this.resultado = resultado;
            this.convertir = convertir;
        }

        @Override
        public void onResponse(@NonNull Call<List<Perfil>> call, @NonNull Response<List<Perfil>> response) {
            List<Perfil> lista = response.body();
            if (!response.isSuccessful() || lista == null) {
                resultado.error(ErroresSupabase.mensaje(response));
                return;
            }
            T dato = convertir.desde(lista);
            if (dato == null) {
                resultado.error("No se encontró tu perfil");
            } else {
                resultado.exito(dato);
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Perfil>> call, @NonNull Throwable t) {
            resultado.error(SIN_CONEXION);
        }
    }
}
