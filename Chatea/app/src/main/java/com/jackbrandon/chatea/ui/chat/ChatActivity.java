package com.jackbrandon.chatea.ui.chat;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.conversaciones.ConversacionesRepository;
import com.jackbrandon.chatea.data.mensajes.MensajesRepository;
import com.jackbrandon.chatea.data.model.Mensaje;
import com.jackbrandon.chatea.data.model.Participante;
import com.jackbrandon.chatea.data.notificaciones.Notificaciones;
import com.jackbrandon.chatea.data.realtime.RealtimeConexion;
import com.jackbrandon.chatea.ui.Fechas;
import com.jackbrandon.chatea.ui.Imagenes;
import com.jackbrandon.chatea.ui.Navegacion;
import com.jackbrandon.chatea.ui.VerFotoActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_CONVERSACION = "conversacion_id";
    public static final String EXTRA_ID = "destinatario_id";
    public static final String EXTRA_NOMBRE = "destinatario_nombre";
    public static final String EXTRA_FOTO = "destinatario_foto";
    public static final String EXTRA_TIPO = "tipo_chat";

    public static volatile String conversacionVisible;

    private static final String ESTADO_CONVERSACION = "conversacion_id";
    private static final long AVISO_ESCRIBIENDO_MS = 2_500;
    private static final long DURACION_ESCRIBIENDO_MS = 4_000;
    private static final long ACTUALIZAR_CONEXION_MS = 30_000;

    private TextView tvParticipantes;
    private TextView tvSinMensajes;
    private EditText etMensaje;
    private FloatingActionButton fabEnviar;
    private SwipeRefreshLayout refrescar;
    private RecyclerView rvMensajes;
    private LinearLayoutManager layoutManager;
    private MensajesAdapter adapter;

    private ConversacionesRepository conversaciones;
    private MensajesRepository mensajesRepo;
    private final Gson gson = new Gson();
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());

    private final List<Mensaje> mensajes = new ArrayList<>();
    private final List<String> nombresParticipantes = new ArrayList<>();
    private String conversacionId;
    private String destinatarioId;
    private String nombre;
    private boolean hayMasAntiguos = true;
    private boolean cargandoAntiguos = false;
    private boolean enPantalla = false;

    private RealtimeConexion realtime;
    private RealtimeConexion.Canal canalChat;
    private String otroRecibidoEn;
    private String otroLeidoEn;
    private String otroUltimaConexion;
    private boolean otroEscribiendo = false;
    private long ultimoAvisoEscribiendo = 0;

    private boolean esGrupo;
    private String foto;
    private String quienEscribe;
    private final Map<String, String> nombresGrupo = new HashMap<>();
    private final Map<String, Participante> miembrosGrupo = new HashMap<>();
    private final Map<String, byte[]> fotosPendientes = new HashMap<>();
    private final ExecutorService segundoPlano = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<PickVisualMediaRequest> elegirFoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) confirmarFoto(uri);
            });

    private final Runnable dejarDeEscribir = () -> {
        otroEscribiendo = false;
        actualizarSubtitulo();
    };

    private final Runnable revisarConexion = new Runnable() {
        @Override
        public void run() {
            actualizarSubtitulo();
            hiloPrincipal.postDelayed(this, ACTUALIZAR_CONEXION_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        conversaciones = new ConversacionesRepository(this);
        mensajesRepo = new MensajesRepository(this);

        conversacionId = savedInstanceState != null
                ? savedInstanceState.getString(ESTADO_CONVERSACION)
                : getIntent().getStringExtra(EXTRA_CONVERSACION);
        destinatarioId = getIntent().getStringExtra(EXTRA_ID);
        nombre = getIntent().getStringExtra(EXTRA_NOMBRE);
        foto = getIntent().getStringExtra(EXTRA_FOTO);
        esGrupo = "grupo".equals(getIntent().getStringExtra(EXTRA_TIPO));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnClickListener(v -> mostrarParticipantes());
        if (esGrupo) {
            toolbar.inflateMenu(R.menu.menu_chat_grupo);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.accionInfoGrupo) {
                    mostrarParticipantes();
                } else if (item.getItemId() == R.id.accionSalirGrupo) {
                    confirmarSalirDelGrupo();
                }
                return true;
            });
        }

        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        TextView tvNombre = findViewById(R.id.tvNombre);
        tvParticipantes = findViewById(R.id.tvParticipantes);
        tvSinMensajes = findViewById(R.id.tvSinMensajes);
        etMensaje = findViewById(R.id.etMensaje);
        fabEnviar = findViewById(R.id.fabEnviar);
        refrescar = findViewById(R.id.refrescar);
        rvMensajes = findViewById(R.id.rvMensajes);

        tvNombre.setText(nombre);
        Imagenes.cargarAvatar(ivAvatar, foto);
        ivAvatar.setOnClickListener(v -> VerFotoActivity.abrirPublica(this, foto, nombre));
        findViewById(R.id.btnFoto).setOnClickListener(v -> {
            if (conversacionId == null) return;
            elegirFoto.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        adapter = new MensajesAdapter(new MensajesAdapter.Acciones() {
            @Override
            public void reintentar(Mensaje mensaje) {
                ChatActivity.this.reintentar(mensaje);
            }

            @Override
            public void opciones(Mensaje mensaje) {
                confirmarEliminar(mensaje);
            }

            @Override
            public void verImagen(Mensaje mensaje, String remitente) {
                boolean mio = mensajesRepo.miId().equals(mensaje.remitenteId);
                String titulo = mio ? "Tú" : remitente != null ? remitente : nombre;
                VerFotoActivity.abrirDelChat(ChatActivity.this,
                        mensaje.uriLocal == null ? mensaje.archivoUrl : null,
                        mensaje.uriLocal, titulo, mensaje.contenido);
            }
        });
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMensajes.setLayoutManager(layoutManager);
        rvMensajes.setAdapter(adapter);
        rvMensajes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && layoutManager.findFirstVisibleItemPosition() <= 2) {
                    cargarAnteriores();
                }
            }
        });

        refrescar.setColorSchemeResources(R.color.verde_oscuro);
        refrescar.setOnRefreshListener(this::cargarNuevos);

        fabEnviar.setEnabled(false);
        fabEnviar.setOnClickListener(v -> enviar());
        etMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                actualizarBotonEnviar();
                avisarQueEstoyEscribiendo(s);
            }
        });

        if (conversacionId == null) {
            crearConversacion();
        } else {
            alTenerConversacion();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        enPantalla = true;
        escucharEnTiempoReal();
        hiloPrincipal.post(revisarConexion);
        if (conversacionId != null) conversaciones.marcarLeido(conversacionId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        conversacionVisible = conversacionId;
        if (conversacionId != null) Notificaciones.quitarDeConversacion(this, conversacionId);
    }

    @Override
    protected void onPause() {
        if (conversacionId != null && conversacionId.equals(conversacionVisible)) {
            conversacionVisible = null;
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        enPantalla = false;
        hiloPrincipal.removeCallbacksAndMessages(null);
        if (realtime != null) {
            realtime.detener();
            realtime = null;
            canalChat = null;
        }
        super.onStop();
    }

    private void crearConversacion() {
        tvParticipantes.setText("Abriendo chat…");
        conversaciones.obtenerOCrearIndividual(destinatarioId, new AuthRepository.Resultado<String>() {
            @Override
            public void exito(String id) {
                conversacionId = id;
                alTenerConversacion();
            }

            @Override
            public void error(String mensaje) {
                tvParticipantes.setText("");
                mostrarAviso("No se pudo abrir el chat:\n" + mensaje);
            }
        });
    }

    private void alTenerConversacion() {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            conversacionVisible = conversacionId;
        }
        actualizarBotonEnviar();
        cargarParticipantes();
        cargarRecientes();
        escucharEnTiempoReal();
    }

    private void escucharEnTiempoReal() {
        if (!enPantalla || conversacionId == null || realtime != null) return;

        realtime = new RealtimeConexion(this, conectado -> {
            if (!conectado) tvParticipantes.setText("Conectando…");
        });

        canalChat = realtime.canal("chat-" + conversacionId)
                .cambios("INSERT", "mensajes", "conversacion_id=eq." + conversacionId)
                .cambios("UPDATE", "mensajes", "conversacion_id=eq." + conversacionId)
                .cambios("UPDATE", "conversaciones_miembros", "conversacion_id=eq." + conversacionId)
                .conBroadcast();
        if (!esGrupo && destinatarioId != null) {
            canalChat.cambios("UPDATE", "perfiles", "id=eq." + destinatarioId);
        }

        canalChat.oyente(new RealtimeConexion.Oyente() {
            @Override
            public void cambio(String tabla, String tipo, JsonObject registro) {
                switch (tabla) {
                    case "mensajes":
                        Mensaje mensaje = gson.fromJson(registro, Mensaje.class);
                        if ("UPDATE".equals(tipo)) {
                            reemplazarMensaje(mensaje);
                        } else {
                            alLlegarMensaje(mensaje);
                        }
                        break;
                    case "conversaciones_miembros":
                        alCambiarLectura(registro);
                        break;
                    case "perfiles":
                        otroUltimaConexion = texto(registro, "ultima_conexion");
                        actualizarSubtitulo();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void broadcast(String evento, JsonObject datos) {
                if (!"escribiendo".equals(evento)) return;
                String quien = texto(datos, "perfil_id");
                if (mensajesRepo.miId().equals(quien)) return;
                quienEscribe = nombresGrupo.get(quien);
                otroEscribiendo = true;
                actualizarSubtitulo();
                hiloPrincipal.removeCallbacks(dejarDeEscribir);
                hiloPrincipal.postDelayed(dejarDeEscribir, DURACION_ESCRIBIENDO_MS);
            }

            @Override
            public void suscrito(boolean esReconexion) {
                actualizarSubtitulo();
                if (esReconexion || ultimoMensajeGuardado() != null) cargarNuevos();
                if (esReconexion) cargarParticipantes();
            }
        }).unirse();

        realtime.iniciar();
    }

    private void alLlegarMensaje(Mensaje mensaje) {
        boolean esDelOtro = !mensajesRepo.miId().equals(mensaje.remitenteId);
        if (esDelOtro) {
            otroEscribiendo = false;
            hiloPrincipal.removeCallbacks(dejarDeEscribir);
            actualizarSubtitulo();
            if (enPantalla) conversaciones.marcarLeido(conversacionId);
        } else {
            quitarPendienteIgual(mensaje);
        }
        agregarSinRepetir(Collections.singletonList(mensaje));
    }

    private void alCambiarLectura(JsonObject registro) {
        String perfilId = texto(registro, "perfil_id");
        if (mensajesRepo.miId().equals(perfilId)) return;

        if (esGrupo) {
            Participante miembro = miembrosGrupo.get(perfilId);
            if (miembro == null) return;
            miembro.ultimoRecibidoEn = texto(registro, "ultimo_recibido_en");
            miembro.ultimoLeidoEn = texto(registro, "ultimo_leido_en");
            calcularLecturasDelGrupo();
        } else {
            otroRecibidoEn = texto(registro, "ultimo_recibido_en");
            otroLeidoEn = texto(registro, "ultimo_leido_en");
        }
        mostrarMensajes(false);
    }

    private void calcularLecturasDelGrupo() {
        otroRecibidoEn = fechaMasAntigua(true);
        otroLeidoEn = fechaMasAntigua(false);
    }

    private String fechaMasAntigua(boolean recibido) {
        String masAntigua = null;
        for (Participante p : miembrosGrupo.values()) {
            String fecha = recibido ? p.ultimoRecibidoEn : p.ultimoLeidoEn;
            if (fecha == null) return null;
            if (masAntigua == null || !Fechas.noAntesDe(fecha, masAntigua)) masAntigua = fecha;
        }
        return masAntigua;
    }

    private void reemplazarMensaje(Mensaje actualizado) {
        for (int i = 0; i < mensajes.size(); i++) {
            if (mensajes.get(i).id.equals(actualizado.id)) {
                mensajes.set(i, actualizado);
                mostrarMensajes(false);
                return;
            }
        }
    }

    private void confirmarEliminar(Mensaje mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar mensaje?")
                .setMessage("Se eliminará para todos los participantes del chat.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar para todos", (d, w) ->
                        mensajesRepo.eliminar(mensaje, new AuthRepository.Resultado<Void>() {
                            @Override
                            public void exito(Void dato) {
                                Mensaje eliminado = copiaEliminada(mensaje);
                                reemplazarMensaje(eliminado);
                            }

                            @Override
                            public void error(String error) {
                                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        }))
                .show();
    }

    private static Mensaje copiaEliminada(Mensaje original) {
        Mensaje copia = new Mensaje();
        copia.id = original.id;
        copia.conversacionId = original.conversacionId;
        copia.remitenteId = original.remitenteId;
        copia.tipo = original.tipo;
        copia.creadoEn = original.creadoEn;
        copia.eliminado = true;
        return copia;
    }

    private void confirmarFoto(Uri uri) {
        View vista = getLayoutInflater().inflate(R.layout.dialogo_enviar_foto, null);
        ImageView ivVista = vista.findViewById(R.id.ivVistaPrevia);
        EditText etPie = vista.findViewById(R.id.etPieDeFoto);
        Glide.with(this).load(uri).into(ivVista);
        etPie.setText(etMensaje.getText().toString().trim());

        new AlertDialog.Builder(this)
                .setTitle("Enviar foto a " + nombre)
                .setView(vista)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Enviar", (d, w) -> {
                    etMensaje.setText("");
                    enviarFoto(uri, etPie.getText().toString().trim());
                })
                .show();
    }

    private void enviarFoto(Uri uri, String pie) {
        Mensaje local = mensajeLocal("imagen", pie.isEmpty() ? null : pie);
        local.uriLocal = uri.toString();
        mensajes.add(local);
        mostrarMensajes(true);

        segundoPlano.execute(() -> {
            try {
                byte[] jpeg = Imagenes.prepararParaChat(getApplicationContext(), uri);
                hiloPrincipal.post(() -> {
                    fotosPendientes.put(local.id, jpeg);
                    mandarAlServidor(local);
                });
            } catch (IOException e) {
                hiloPrincipal.post(() -> {
                    local.estado = Mensaje.Estado.ERROR;
                    mostrarMensajes(false);
                    Toast.makeText(this, "No se pudo leer la foto", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void confirmarSalirDelGrupo() {
        new AlertDialog.Builder(this)
                .setTitle("¿Salir de \"" + nombre + "\"?")
                .setMessage("Ya no recibirás mensajes de este grupo.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Salir", (d, w) ->
                        conversaciones.salirDeGrupo(conversacionId, new AuthRepository.Resultado<Void>() {
                            @Override
                            public void exito(Void dato) {
                                Toast.makeText(ChatActivity.this, "Saliste del grupo", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void error(String mensaje) {
                                Toast.makeText(ChatActivity.this, mensaje, Toast.LENGTH_LONG).show();
                            }
                        }))
                .show();
    }

    private void avisarQueEstoyEscribiendo(Editable texto) {
        if (canalChat == null || texto.toString().trim().isEmpty()) return;
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoAvisoEscribiendo < AVISO_ESCRIBIENDO_MS) return;
        ultimoAvisoEscribiendo = ahora;

        JsonObject datos = new JsonObject();
        datos.addProperty("perfil_id", mensajesRepo.miId());
        canalChat.enviarBroadcast("escribiendo", datos);
    }

    private void actualizarSubtitulo() {
        if (otroEscribiendo) {
            tvParticipantes.setText(esGrupo && quienEscribe != null
                    ? quienEscribe + " está escribiendo…" : "escribiendo…");
        } else if (!esGrupo && otroUltimaConexion != null) {
            tvParticipantes.setText(Fechas.ultimaConexion(otroUltimaConexion));
        } else {
            tvParticipantes.setText(TextUtils.join(", ", nombresParticipantes));
        }
    }

    private void mostrarParticipantes() {
        if (nombresParticipantes.isEmpty()) return;
        new AlertDialog.Builder(this)
                .setTitle("Participantes")
                .setMessage(TextUtils.join("\n", nombresParticipantes))
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void quitarPendienteIgual(Mensaje recibido) {
        for (int i = 0; i < mensajes.size(); i++) {
            Mensaje m = mensajes.get(i);
            if (m.getEstado() == Mensaje.Estado.ENVIANDO
                    && Objects.equals(m.tipo, recibido.tipo)
                    && Objects.equals(m.contenido, recibido.contenido)) {
                mensajes.remove(i);
                fotosPendientes.remove(m.id);
                return;
            }
        }
    }

    private void cargarParticipantes() {
        conversaciones.participantes(conversacionId, new AuthRepository.Resultado<List<Participante>>() {
            @Override
            public void exito(List<Participante> lista) {
                nombresParticipantes.clear();
                nombresGrupo.clear();
                miembrosGrupo.clear();
                for (Participante p : lista) {
                    if (p.perfilId.equals(conversaciones.miId())) {
                        nombresParticipantes.add(0, "Tú");
                        continue;
                    }
                    String nombreMiembro = p.perfil != null ? p.perfil.nombre : "Usuario";
                    nombresParticipantes.add("admin".equals(p.rol) && esGrupo
                            ? nombreMiembro + " (admin)" : nombreMiembro);
                    nombresGrupo.put(p.perfilId, nombreMiembro);
                    miembrosGrupo.put(p.perfilId, p);

                    if (!esGrupo) {
                        if (p.perfil != null) otroUltimaConexion = p.perfil.ultimaConexion;
                        if (destinatarioId == null) destinatarioId = p.perfilId;
                        otroRecibidoEn = p.ultimoRecibidoEn;
                        otroLeidoEn = p.ultimoLeidoEn;
                    }
                }
                if (esGrupo) calcularLecturasDelGrupo();
                actualizarSubtitulo();
                mostrarMensajes(false);
            }

            @Override
            public void error(String mensaje) {
                actualizarSubtitulo();
            }
        });
    }

    private void cargarRecientes() {
        refrescar.setRefreshing(true);
        mensajesRepo.recientes(conversacionId, new AuthRepository.Resultado<List<Mensaje>>() {
            @Override
            public void exito(List<Mensaje> lista) {
                refrescar.setRefreshing(false);
                hayMasAntiguos = lista.size() == MensajesRepository.TAMANO_PAGINA;
                mensajes.clear();
                mensajes.addAll(lista);
                mostrarMensajes(true);
                if (enPantalla) conversaciones.marcarLeido(conversacionId);
            }

            @Override
            public void error(String mensaje) {
                refrescar.setRefreshing(false);
                mostrarAviso("No se pudieron cargar los mensajes:\n" + mensaje);
            }
        });
    }

    private void cargarAnteriores() {
        if (!hayMasAntiguos || cargandoAntiguos || conversacionId == null) return;
        String masAntiguo = primerMensajeGuardado();
        if (masAntiguo == null) return;

        cargandoAntiguos = true;
        mensajesRepo.anterioresA(conversacionId, masAntiguo, new AuthRepository.Resultado<List<Mensaje>>() {
            @Override
            public void exito(List<Mensaje> lista) {
                cargandoAntiguos = false;
                hayMasAntiguos = lista.size() == MensajesRepository.TAMANO_PAGINA;
                mensajes.addAll(0, lista);
                mostrarMensajes(false);
            }

            @Override
            public void error(String mensaje) {
                cargandoAntiguos = false;
            }
        });
    }

    private void cargarNuevos() {
        if (conversacionId == null) {
            refrescar.setRefreshing(false);
            return;
        }
        String masNuevo = ultimoMensajeGuardado();
        if (masNuevo == null) {
            cargarRecientes();
            return;
        }

        mensajesRepo.posterioresA(conversacionId, masNuevo, new AuthRepository.Resultado<List<Mensaje>>() {
            @Override
            public void exito(List<Mensaje> lista) {
                refrescar.setRefreshing(false);
                agregarSinRepetir(lista);
                if (!lista.isEmpty() && enPantalla) conversaciones.marcarLeido(conversacionId);
            }

            @Override
            public void error(String mensaje) {
                refrescar.setRefreshing(false);
                Toast.makeText(ChatActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviar() {
        String texto = etMensaje.getText().toString().trim();
        if (texto.isEmpty() || conversacionId == null) return;
        etMensaje.setText("");

        Mensaje local = mensajeLocal("texto", texto);
        mensajes.add(local);
        mostrarMensajes(true);
        mandarAlServidor(local);
    }

    private Mensaje mensajeLocal(String tipo, String contenido) {
        Mensaje local = new Mensaje();
        local.id = "local-" + UUID.randomUUID();
        local.conversacionId = conversacionId;
        local.remitenteId = mensajesRepo.miId();
        local.tipo = tipo;
        local.contenido = contenido;
        local.eliminado = false;
        local.creadoEn = Fechas.ahoraParaSupabase();
        local.estado = Mensaje.Estado.ENVIANDO;
        return local;
    }

    private void reintentar(Mensaje mensaje) {
        mensaje.estado = Mensaje.Estado.ENVIANDO;
        mostrarMensajes(false);
        mandarAlServidor(mensaje);
    }

    private void mandarAlServidor(Mensaje local) {
        AuthRepository.Resultado<Mensaje> alTerminar = new AuthRepository.Resultado<Mensaje>() {
            @Override
            public void exito(Mensaje guardado) {
                fotosPendientes.remove(local.id);
                int posicion = mensajes.indexOf(local);
                if (posicion >= 0) mensajes.remove(posicion);
                agregarSinRepetir(Collections.singletonList(guardado));
                mostrarMensajes(false);
                cargarNuevos();
            }

            @Override
            public void error(String mensaje) {
                local.estado = Mensaje.Estado.ERROR;
                mostrarMensajes(false);
                Toast.makeText(ChatActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        };

        if (local.esImagen()) {
            byte[] jpeg = fotosPendientes.get(local.id);
            if (jpeg == null) {
                alTerminar.error("La foto ya no está disponible, elígela otra vez");
                return;
            }
            mensajesRepo.enviarImagen(conversacionId, jpeg, local.contenido, alTerminar);
        } else {
            mensajesRepo.enviar(conversacionId, local.contenido, alTerminar);
        }
    }

    private void agregarSinRepetir(List<Mensaje> nuevos) {
        Set<String> existentes = new HashSet<>();
        for (Mensaje m : mensajes) existentes.add(m.id);

        boolean huboCambios = false;
        for (Mensaje m : nuevos) {
            if (existentes.add(m.id)) {
                int posicion = posicionAntesDePendientes();
                mensajes.add(posicion, m);
                huboCambios = true;
            }
        }
        if (huboCambios) mostrarMensajes(true);
    }

    private int posicionAntesDePendientes() {
        int posicion = mensajes.size();
        while (posicion > 0 && mensajes.get(posicion - 1).getEstado() != Mensaje.Estado.ENVIADO) {
            posicion--;
        }
        return posicion;
    }

    private String primerMensajeGuardado() {
        for (Mensaje m : mensajes) {
            if (m.getEstado() == Mensaje.Estado.ENVIADO) return m.creadoEn;
        }
        return null;
    }

    private String ultimoMensajeGuardado() {
        for (int i = mensajes.size() - 1; i >= 0; i--) {
            if (mensajes.get(i).getEstado() == Mensaje.Estado.ENVIADO) return mensajes.get(i).creadoEn;
        }
        return null;
    }

    private void mostrarMensajes(boolean bajarAlFinal) {
        List<ElementoChat> elementos = ElementoChat.armar(
                new ArrayList<>(mensajes), mensajesRepo.miId(), otroRecibidoEn, otroLeidoEn,
                esGrupo ? nombresGrupo : null);
        adapter.submitList(elementos, () -> {
            if (bajarAlFinal && !elementos.isEmpty()) {
                rvMensajes.scrollToPosition(elementos.size() - 1);
            }
        });

        if (mensajes.isEmpty()) {
            mostrarAviso("Aún no hay mensajes con " + nombre + ".\n¡Escribe el primero!");
        } else {
            tvSinMensajes.setVisibility(View.GONE);
        }
    }

    private void mostrarAviso(String texto) {
        tvSinMensajes.setText(texto);
        tvSinMensajes.setVisibility(View.VISIBLE);
    }

    private void actualizarBotonEnviar() {
        boolean hayTexto = etMensaje.getText().toString().trim().length() > 0;
        fabEnviar.setEnabled(hayTexto && conversacionId != null);
        fabEnviar.setAlpha(fabEnviar.isEnabled() ? 1f : 0.5f);
    }

    private static String texto(JsonObject json, String campo) {
        JsonElement valor = json.get(campo);
        return valor == null || valor.isJsonNull() ? null : valor.getAsString();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ESTADO_CONVERSACION, conversacionId);
    }

    @Override
    protected void onDestroy() {
        segundoPlano.shutdown();
        super.onDestroy();
    }
}
