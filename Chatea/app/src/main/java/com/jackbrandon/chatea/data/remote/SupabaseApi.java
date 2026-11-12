package com.jackbrandon.chatea.data.remote;

import com.google.gson.JsonObject;
import com.jackbrandon.chatea.data.model.ChatResumen;
import com.jackbrandon.chatea.data.model.Mensaje;
import com.jackbrandon.chatea.data.model.Participante;
import com.jackbrandon.chatea.data.model.Perfil;
import com.jackbrandon.chatea.data.model.ResultadoBusqueda;
import com.jackbrandon.chatea.data.model.Sesion;
import com.jackbrandon.chatea.data.model.Usuario;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseApi {

    @GET("auth/v1/health")
    Call<JsonObject> revisarConexion();

    @POST("auth/v1/signup")
    Call<Sesion> registrar(@Body AuthBodies.Registro datos);

    @POST("auth/v1/token?grant_type=password")
    Call<Sesion> iniciarSesion(@Body AuthBodies.Credenciales datos);

    @POST("auth/v1/token?grant_type=refresh_token")
    Call<Sesion> renovarSesion(@Body AuthBodies.Renovar datos);

    @POST("auth/v1/logout")
    Call<Void> cerrarSesion(@Header("Authorization") String bearer);

    @POST("auth/v1/recover")
    Call<Void> enviarCodigoRecuperacion(@Body AuthBodies.Correo datos);

    @POST("auth/v1/verify")
    Call<Sesion> verificarCodigo(@Body AuthBodies.VerificarCodigo datos);

    @PUT("auth/v1/user")
    Call<Usuario> cambiarPassword(@Header("Authorization") String bearer, @Body AuthBodies.NuevaPassword datos);

    @GET("rest/v1/perfiles?select=id,nombre,telefono,info,foto_url")
    Call<List<Perfil>> obtenerPerfiles(
            @Header("Authorization") String bearer,
            @Query("id") String filtroId,
            @Query("nombre") String filtroNombre,
            @Query("order") String orden);

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/perfiles")
    Call<List<Perfil>> actualizarPerfil(
            @Header("Authorization") String bearer,
            @Query("id") String filtroId,
            @Body Map<String, Object> cambios);

    @Headers("x-upsert: true")
    @POST("storage/v1/object/avatares/{ruta}")
    Call<JsonObject> subirAvatar(
            @Header("Authorization") String bearer,
            @Path(value = "ruta", encoded = true) String ruta,
            @Body RequestBody imagen);

    @POST("rest/v1/rpc/obtener_o_crear_conversacion")
    Call<String> obtenerOCrearConversacion(
            @Header("Authorization") String bearer,
            @Body Map<String, String> parametros);

    @POST("rest/v1/rpc/mis_conversaciones")
    Call<List<ChatResumen>> misConversaciones(
            @Header("Authorization") String bearer,
            @Body Map<String, String> sinParametros);

    @GET("rest/v1/conversaciones_miembros?select=perfil_id,rol,ultimo_recibido_en,ultimo_leido_en,perfiles(id,nombre,info,foto_url,ultima_conexion)")
    Call<List<Participante>> participantes(
            @Header("Authorization") String bearer,
            @Query("conversacion_id") String filtroConversacion);

    @GET("rest/v1/mensajes?select=id,conversacion_id,remitente_id,tipo,contenido,archivo_url,eliminado,creado_en")
    Call<List<Mensaje>> mensajes(
            @Header("Authorization") String bearer,
            @Query("conversacion_id") String filtroConversacion,
            @Query("creado_en") String filtroFecha,
            @Query("order") String orden,
            @Query("limit") Integer limite);

    @Headers("Prefer: return=representation")
    @POST("rest/v1/mensajes?select=id,conversacion_id,remitente_id,tipo,contenido,archivo_url,eliminado,creado_en")
    Call<List<Mensaje>> enviarMensaje(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> mensaje);

    @POST("rest/v1/rpc/marcar_recibidos")
    Call<Void> marcarRecibidos(
            @Header("Authorization") String bearer,
            @Body Map<String, String> sinParametros);

    @POST("rest/v1/rpc/marcar_leido")
    Call<Void> marcarLeido(
            @Header("Authorization") String bearer,
            @Body Map<String, String> parametros);

    @POST("rest/v1/rpc/registrar_conexion")
    Call<Void> registrarConexion(
            @Header("Authorization") String bearer,
            @Body Map<String, String> sinParametros);

    @POST("rest/v1/rpc/registrar_dispositivo")
    Call<Void> registrarDispositivo(
            @Header("Authorization") String bearer,
            @Body Map<String, String> parametros);

    @POST("rest/v1/rpc/borrar_dispositivo")
    Call<Void> borrarDispositivo(
            @Header("Authorization") String bearer,
            @Body Map<String, String> parametros);

    @POST("rest/v1/rpc/quitar_foto_perfil")
    Call<Void> quitarFotoPerfil(
            @Header("Authorization") String bearer,
            @Body Map<String, String> sinParametros);

    @POST("storage/v1/object/chat-imagenes/{ruta}")
    Call<JsonObject> subirImagenChat(
            @Header("Authorization") String bearer,
            @Path(value = "ruta", encoded = true) String ruta,
            @Body RequestBody imagen);

    @DELETE("storage/v1/object/{bucket}/{ruta}")
    Call<JsonObject> borrarArchivo(
            @Header("Authorization") String bearer,
            @Path("bucket") String bucket,
            @Path(value = "ruta", encoded = true) String ruta);

    @POST("rest/v1/rpc/crear_grupo")
    Call<String> crearGrupo(
            @Header("Authorization") String bearer,
            @Body Map<String, Object> parametros);

    @POST("rest/v1/rpc/salir_de_grupo")
    Call<Void> salirDeGrupo(
            @Header("Authorization") String bearer,
            @Body Map<String, String> parametros);

    @POST("rest/v1/rpc/eliminar_mensaje")
    Call<Void> eliminarMensaje(
            @Header("Authorization") String bearer,
            @Body Map<String, String> parametros);

    @POST("rest/v1/rpc/buscar_mensajes")
    Call<List<ResultadoBusqueda>> buscarMensajes(
            @Header("Authorization") String bearer,
            @Body Map<String, String> parametros);
}
