package com.jackbrandon.chatea.data.conversaciones;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.auth.AuthRepository.Resultado;
import com.jackbrandon.chatea.data.auth.SessionManager;
import com.jackbrandon.chatea.data.model.ChatResumen;
import com.jackbrandon.chatea.data.model.Participante;
import com.jackbrandon.chatea.data.remote.RespuestaSinCuerpo;
import com.jackbrandon.chatea.data.remote.RespuestaSupabase;
import com.jackbrandon.chatea.data.remote.SupabaseApi;
import com.jackbrandon.chatea.data.remote.SupabaseClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversacionesRepository {

    private final SupabaseApi api;
    private final AuthRepository auth;
    private final SessionManager sesiones;

    public ConversacionesRepository(Context context) {
        this.api = SupabaseClient.getApi();
        this.auth = new AuthRepository(context);
        this.sesiones = SessionManager.get(context);
    }

    public void obtenerOCrearIndividual(String otroUsuarioId, Resultado<String> resultado) {
        auth.conSesion(resultado, () ->
                api.obtenerOCrearConversacion(
                                sesiones.bearer(),
                                Collections.singletonMap("otro_usuario", otroUsuarioId))
                        .enqueue(new RespuestaSupabase<>(resultado)));
    }

    public void misConversaciones(Resultado<List<ChatResumen>> resultado) {
        auth.conSesion(resultado, () ->
                api.misConversaciones(sesiones.bearer(), Collections.emptyMap())
                        .enqueue(new RespuestaSupabase<>(resultado)));
    }

    public void participantes(String conversacionId, Resultado<List<Participante>> resultado) {
        auth.conSesion(resultado, () ->
                api.participantes(sesiones.bearer(), "eq." + conversacionId)
                        .enqueue(new RespuestaSupabase<>(resultado)));
    }

    public void crearGrupo(String nombre, String fotoUrl, List<String> miembros, Resultado<String> resultado) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("nombre_grupo", nombre);
        parametros.put("foto", fotoUrl == null ? "" : fotoUrl);
        parametros.put("miembros", miembros);

        auth.conSesion(resultado, () ->
                api.crearGrupo(sesiones.bearer(), parametros).enqueue(new RespuestaSupabase<>(resultado)));
    }

    public void salirDeGrupo(String conversacionId, Resultado<Void> resultado) {
        auth.conSesion(resultado, () ->
                api.salirDeGrupo(sesiones.bearer(), Collections.singletonMap("conversacion", conversacionId))
                        .enqueue(new RespuestaSinCuerpo<>(resultado)));
    }

    public void marcarRecibidos() {
        auth.conSesion(SIN_AVISO, () ->
                api.marcarRecibidos(sesiones.bearer(), Collections.emptyMap()).enqueue(IGNORAR));
    }

    public void marcarLeido(String conversacionId) {
        auth.conSesion(SIN_AVISO, () ->
                api.marcarLeido(sesiones.bearer(), Collections.singletonMap("conversacion", conversacionId))
                        .enqueue(IGNORAR));
    }

    public String miId() {
        return sesiones.getUsuarioId();
    }

    private static final Resultado<Void> SIN_AVISO = new Resultado<Void>() {
        @Override
        public void exito(Void dato) {}

        @Override
        public void error(String mensaje) {}
    };

    private static final Callback<Void> IGNORAR = new Callback<Void>() {
        @Override
        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {}

        @Override
        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
    };
}
