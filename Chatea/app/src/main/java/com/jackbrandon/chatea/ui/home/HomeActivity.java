package com.jackbrandon.chatea.ui.home;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.JsonObject;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.auth.Jwt;
import com.jackbrandon.chatea.data.auth.SessionManager;
import com.jackbrandon.chatea.data.conversaciones.ConversacionesRepository;
import com.jackbrandon.chatea.data.model.ChatResumen;
import com.jackbrandon.chatea.data.model.Sesion;
import com.jackbrandon.chatea.data.notificaciones.Notificaciones;
import com.jackbrandon.chatea.data.notificaciones.TokenRepository;
import com.jackbrandon.chatea.data.realtime.RealtimeConexion;
import com.jackbrandon.chatea.ui.Navegacion;
import com.jackbrandon.chatea.ui.auth.LoginActivity;
import com.jackbrandon.chatea.ui.chat.ChatActivity;
import com.jackbrandon.chatea.ui.buscar.BuscarMensajesActivity;
import com.jackbrandon.chatea.ui.contactos.ContactosActivity;
import com.jackbrandon.chatea.ui.grupos.NuevoGrupoActivity;
import com.jackbrandon.chatea.ui.perfil.PerfilActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private static final long ESPERA_RECARGA_MS = 300;
    private static final long DURACION_ESCRIBIENDO_MS = 4_000;

    private MaterialToolbar toolbar;
    private SwipeRefreshLayout refrescar;
    private TextView tvVacio;
    private ChatsAdapter adapter;

    private AuthRepository auth;
    private ConversacionesRepository conversaciones;
    private SessionManager sesiones;
    private RealtimeConexion realtime;

    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());
    private final Runnable recargaSilenciosa = () -> cargarChats(false);
    private List<ChatResumen> chats = new ArrayList<>();
    private final Set<String> escribiendo = new HashSet<>();
    private final Map<String, Runnable> finDeEscritura = new HashMap<>();

    private TokenRepository tokens;
    private final ActivityResultLauncher<String> pedirPermisoNotificaciones =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (!concedido) {
                    Toast.makeText(this, "No verás notificaciones de mensajes nuevos", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        auth = new AuthRepository(this);
        conversaciones = new ConversacionesRepository(this);
        sesiones = SessionManager.get(this);
        tokens = new TokenRepository(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.accionBuscar) {
                startActivity(new Intent(this, BuscarMensajesActivity.class));
            } else if (id == R.id.accionNuevoGrupo) {
                startActivity(new Intent(this, NuevoGrupoActivity.class));
            } else if (id == R.id.accionPerfil) {
                startActivity(new Intent(this, PerfilActivity.class));
            } else if (id == R.id.accionSesion) {
                mostrarSesion();
            } else if (id == R.id.accionCerrarSesion) {
                tokens.borrarDeEsteDispositivo(() ->
                        auth.cerrarSesion(() -> Navegacion.irA(this, LoginActivity.class)));
            }
            return true;
        });

        tvVacio = findViewById(R.id.tvVacio);
        refrescar = findViewById(R.id.refrescar);
        refrescar.setColorSchemeResources(R.color.verde_oscuro);
        refrescar.setOnRefreshListener(() -> cargarChats(true));

        adapter = new ChatsAdapter(this::abrirChat);
        RecyclerView rvChats = findViewById(R.id.rvChats);
        rvChats.setLayoutManager(new LinearLayoutManager(this));
        rvChats.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvChats.setAdapter(adapter);

        findViewById(R.id.fabContactos).setOnClickListener(v ->
                startActivity(new Intent(this, ContactosActivity.class)));

        if (savedInstanceState == null) {
            if (!Notificaciones.tienePermiso(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pedirPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
            tokens.obtenerYRegistrar();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.asegurarSesion(new AuthRepository.Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                cargarChats(true);
            }

            @Override
            public void error(String mensaje) {
                if (mensaje != null) {
                    Toast.makeText(HomeActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
                Navegacion.irA(HomeActivity.this, LoginActivity.class);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        realtime = new RealtimeConexion(this, conectado ->
                toolbar.setSubtitle(conectado ? null : "Conectando…"));

        realtime.canal("lista")
                .cambios("INSERT", "mensajes", null)
                .cambios("UPDATE", "mensajes", null)
                .cambios("UPDATE", "conversaciones_miembros", null)
                .oyente(new RealtimeConexion.Oyente() {
                    @Override
                    public void cambio(String tabla, String tipo, JsonObject registro) {
                        if ("mensajes".equals(tabla) && "INSERT".equals(tipo)) {
                            String remitente = registro.get("remitente_id").getAsString();
                            String conversacion = registro.get("conversacion_id").getAsString();
                            if (!remitente.equals(sesiones.getUsuarioId())) {
                                conversaciones.marcarRecibidos();
                                quitarEscribiendo(conversacion);
                            }
                        }
                        hiloPrincipal.removeCallbacks(recargaSilenciosa);
                        hiloPrincipal.postDelayed(recargaSilenciosa, ESPERA_RECARGA_MS);
                    }

                    @Override
                    public void suscrito(boolean esReconexion) {
                        if (esReconexion) cargarChats(false);
                    }
                })
                .unirse();

        realtime.iniciar();
        escucharEscrituraDeCadaChat();
    }

    @Override
    protected void onStop() {
        hiloPrincipal.removeCallbacksAndMessages(null);
        finDeEscritura.clear();
        escribiendo.clear();
        if (realtime != null) {
            realtime.detener();
            realtime = null;
        }
        super.onStop();
    }

    private void escucharEscrituraDeCadaChat() {
        if (realtime == null) return;
        for (ChatResumen chat : chats) {
            String nombreCanal = "chat-" + chat.id;
            if (realtime.tieneCanal(nombreCanal)) continue;

            realtime.canal(nombreCanal)
                    .conBroadcast()
                    .oyente(new RealtimeConexion.Oyente() {
                        @Override
                        public void broadcast(String evento, JsonObject datos) {
                            if ("escribiendo".equals(evento)) marcarEscribiendo(chat.id);
                        }
                    })
                    .unirse();
        }
    }

    private void marcarEscribiendo(String conversacionId) {
        escribiendo.add(conversacionId);
        Runnable anterior = finDeEscritura.get(conversacionId);
        if (anterior != null) hiloPrincipal.removeCallbacks(anterior);

        Runnable fin = () -> quitarEscribiendo(conversacionId);
        finDeEscritura.put(conversacionId, fin);
        hiloPrincipal.postDelayed(fin, DURACION_ESCRIBIENDO_MS);
        mostrarChats();
    }

    private void quitarEscribiendo(String conversacionId) {
        Runnable fin = finDeEscritura.remove(conversacionId);
        if (fin != null) hiloPrincipal.removeCallbacks(fin);
        if (escribiendo.remove(conversacionId)) mostrarChats();
    }

    private void mostrarChats() {
        List<ChatResumen> visibles = new ArrayList<>();
        for (ChatResumen chat : chats) {
            visibles.add(chat.copiaEscribiendo(escribiendo.contains(chat.id)));
        }
        adapter.submitList(visibles);
        tvVacio.setVisibility(chats.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void cargarChats(boolean conIndicador) {
        if (conIndicador) {
            refrescar.setRefreshing(true);
            conversaciones.marcarRecibidos();
        }
        conversaciones.misConversaciones(new AuthRepository.Resultado<List<ChatResumen>>() {
            @Override
            public void exito(List<ChatResumen> lista) {
                refrescar.setRefreshing(false);
                chats = lista;
                mostrarChats();
                escucharEscrituraDeCadaChat();
            }

            @Override
            public void error(String mensaje) {
                refrescar.setRefreshing(false);
                Toast.makeText(HomeActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void abrirChat(ChatResumen chat) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CONVERSACION, chat.id);
        intent.putExtra(ChatActivity.EXTRA_ID, chat.otroId);
        intent.putExtra(ChatActivity.EXTRA_NOMBRE, chat.nombre);
        intent.putExtra(ChatActivity.EXTRA_FOTO, chat.fotoUrl);
        intent.putExtra(ChatActivity.EXTRA_TIPO, chat.tipo);
        startActivity(intent);
    }

    private void mostrarSesion() {
        JSONObject payload = Jwt.leerPayload(sesiones.getAccessToken());
        SimpleDateFormat hora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        String tokenFcm = tokens.tokenGuardado();

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this)
                .setTitle("Mi sesión")
                .setMessage("JWT (access token)\n"
                        + "sub (tu id):\n" + payload.optString("sub") + "\n\n"
                        + "email: " + payload.optString("email") + "\n"
                        + "role: " + payload.optString("role") + "\n"
                        + "emitido: " + hora.format(new Date(payload.optLong("iat") * 1000)) + "\n"
                        + "expira: " + hora.format(new Date(payload.optLong("exp") * 1000)) + "\n\n"
                        + "Token FCM de este celular:\n"
                        + (tokenFcm != null ? tokenFcm
                        : tokens.firebaseConfigurado() ? "(obteniendo…)" : "(falta configurar Firebase)"))
                .setPositiveButton("Cerrar", null);

        if (tokenFcm != null) {
            dialogo.setNeutralButton("Copiar token FCM", (d, w) -> {
                ClipboardManager portapapeles = getSystemService(ClipboardManager.class);
                portapapeles.setPrimaryClip(ClipData.newPlainText("Token FCM", tokenFcm));
                Toast.makeText(this, "Token copiado", Toast.LENGTH_SHORT).show();
            });
        }
        dialogo.show();
    }
}
