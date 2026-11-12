package com.jackbrandon.chatea.data.mensajes;

import android.content.Context;

import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.auth.AuthRepository.Resultado;
import com.jackbrandon.chatea.data.auth.SessionManager;
import com.jackbrandon.chatea.data.model.Mensaje;
import com.jackbrandon.chatea.data.model.ResultadoBusqueda;
import com.jackbrandon.chatea.data.remote.RespuestaSinCuerpo;
import com.jackbrandon.chatea.data.remote.RespuestaSupabase;
import com.jackbrandon.chatea.data.remote.SupabaseApi;
import com.jackbrandon.chatea.data.remote.SupabaseClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MensajesRepository {

    public static final int TAMANO_PAGINA = 50;
    private static final MediaType JPEG = MediaType.get("image/jpeg");

    private final SupabaseApi api;
    private final AuthRepository auth;
    private final SessionManager sesiones;

    public MensajesRepository(Context context) {
        this.api = SupabaseClient.getApi();
        this.auth = new AuthRepository(context);
        this.sesiones = SessionManager.get(context);
    }

    public String miId() {
        return sesiones.getUsuarioId();
    }

    public void recientes(String conversacionId, Resultado<List<Mensaje>> resultado) {
        pagina(conversacionId, null, resultado);
    }

    public void anterioresA(String conversacionId, String fecha, Resultado<List<Mensaje>> resultado) {
        pagina(conversacionId, "lt." + fecha, resultado);
    }

    public void posterioresA(String conversacionId, String fecha, Resultado<List<Mensaje>> resultado) {
        auth.conSesion(resultado, () ->
                api.mensajes(sesiones.bearer(), "eq." + conversacionId, "gt." + fecha, "creado_en.asc", null)
                        .enqueue(new RespuestaSupabase<>(resultado)));
    }

    public void enviar(String conversacionId, String texto, Resultado<Mensaje> resultado) {
        Map<String, Object> mensaje = new HashMap<>();
        mensaje.put("conversacion_id", conversacionId);
        mensaje.put("remitente_id", sesiones.getUsuarioId());
        mensaje.put("tipo", "texto");
        mensaje.put("contenido", texto);
        insertar(mensaje, resultado);
    }

    public void enviarImagen(String conversacionId, byte[] jpeg, String pieDeFoto, Resultado<Mensaje> resultado) {
        String ruta = conversacionId + "/" + UUID.randomUUID() + ".jpg";

        auth.conSesion(resultado, () ->
                api.subirImagenChat(sesiones.bearer(), ruta, RequestBody.create(jpeg, JPEG))
                        .enqueue(new RespuestaSinCuerpo<>(new Resultado<Void>() {
                            @Override
                            public void exito(Void dato) {
                                Map<String, Object> mensaje = new HashMap<>();
                                mensaje.put("conversacion_id", conversacionId);
                                mensaje.put("remitente_id", sesiones.getUsuarioId());
                                mensaje.put("tipo", "imagen");
                                mensaje.put("archivo_url", ruta);
                                if (pieDeFoto != null && !pieDeFoto.isEmpty()) mensaje.put("contenido", pieDeFoto);
                                insertar(mensaje, resultado);
                            }

                            @Override
                            public void error(String mensaje) {
                                resultado.error("No se pudo subir la foto: " + mensaje);
                            }
                        })));
    }

    public void eliminar(Mensaje mensaje, Resultado<Void> resultado) {
        auth.conSesion(resultado, () -> {
            if (mensaje.esImagen() && mensaje.archivoUrl != null) {
                api.borrarArchivo(sesiones.bearer(), "chat-imagenes", mensaje.archivoUrl)
                        .enqueue(new RespuestaSinCuerpo<>(new Resultado<Void>() {
                            @Override
                            public void exito(Void dato) {
                                marcarEliminado(mensaje.id, resultado);
                            }

                            @Override
                            public void error(String error) {
                                marcarEliminado(mensaje.id, resultado);
                            }
                        }));
            } else {
                marcarEliminado(mensaje.id, resultado);
            }
        });
    }

    public void buscar(String texto, Resultado<List<ResultadoBusqueda>> resultado) {
        auth.conSesion(resultado, () ->
                api.buscarMensajes(sesiones.bearer(), Collections.singletonMap("texto", texto))
                        .enqueue(new RespuestaSupabase<>(resultado)));
    }

    private void marcarEliminado(String mensajeId, Resultado<Void> resultado) {
        api.eliminarMensaje(sesiones.bearer(), Collections.singletonMap("mensaje", mensajeId))
                .enqueue(new RespuestaSinCuerpo<>(resultado));
    }

    private void insertar(Map<String, Object> mensaje, Resultado<Mensaje> resultado) {
        auth.conSesion(resultado, () ->
                api.enviarMensaje(sesiones.bearer(), mensaje)
                        .enqueue(new RespuestaSupabase<>(new Resultado<List<Mensaje>>() {
                            @Override
                            public void exito(List<Mensaje> lista) {
                                if (lista.isEmpty()) {
                                    resultado.error("El mensaje no se guardó");
                                } else {
                                    resultado.exito(lista.get(0));
                                }
                            }

                            @Override
                            public void error(String mensaje) {
                                resultado.error(mensaje);
                            }
                        })));
    }

    private void pagina(String conversacionId, String filtroFecha, Resultado<List<Mensaje>> resultado) {
        auth.conSesion(resultado, () ->
                api.mensajes(sesiones.bearer(), "eq." + conversacionId, filtroFecha, "creado_en.desc", TAMANO_PAGINA)
                        .enqueue(new RespuestaSupabase<>(new Resultado<List<Mensaje>>() {
                            @Override
                            public void exito(List<Mensaje> lista) {
                                Collections.reverse(lista);
                                resultado.exito(lista);
                            }

                            @Override
                            public void error(String mensaje) {
                                resultado.error(mensaje);
                            }
                        })));
    }
}
