package com.jackbrandon.chatea.data.presencia;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.auth.SessionManager;
import com.jackbrandon.chatea.data.model.Sesion;
import com.jackbrandon.chatea.data.remote.SupabaseApi;
import com.jackbrandon.chatea.data.remote.SupabaseClient;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Presencia implements DefaultLifecycleObserver {

    private static final long LATIDO_MS = 30_000;

    private final AuthRepository auth;
    private final SessionManager sesiones;
    private final SupabaseApi api;
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());

    private final Runnable latido = new Runnable() {
        @Override
        public void run() {
            registrar();
            hiloPrincipal.postDelayed(this, LATIDO_MS);
        }
    };

    public Presencia(Context context) {
        this.auth = new AuthRepository(context);
        this.sesiones = SessionManager.get(context);
        this.api = SupabaseClient.getApi();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        hiloPrincipal.removeCallbacks(latido);
        hiloPrincipal.post(latido);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        hiloPrincipal.removeCallbacks(latido);
        registrar();
    }

    private void registrar() {
        if (!sesiones.haySesion()) return;
        auth.asegurarSesion(new AuthRepository.Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                api.registrarConexion(sesiones.bearer(), Collections.emptyMap()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {}

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
                });
            }

            @Override
            public void error(String mensaje) {}
        });
    }
}
