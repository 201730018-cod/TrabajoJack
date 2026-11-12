package com.jackbrandon.chatea.data.notificaciones;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.auth.SessionManager;
import com.jackbrandon.chatea.data.model.Sesion;
import com.jackbrandon.chatea.data.remote.SupabaseApi;
import com.jackbrandon.chatea.data.remote.SupabaseClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenRepository {

    private static final String TAG = "FCM";
    private static final String ARCHIVO = "fcm";
    private static final String TOKEN = "token";

    private final Context context;
    private final SupabaseApi api;
    private final AuthRepository auth;
    private final SessionManager sesiones;
    private final SharedPreferences prefs;

    public TokenRepository(Context context) {
        this.context = context.getApplicationContext();
        this.api = SupabaseClient.getApi();
        this.auth = new AuthRepository(context);
        this.sesiones = SessionManager.get(context);
        this.prefs = this.context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    public boolean firebaseConfigurado() {
        return !FirebaseApp.getApps(context).isEmpty();
    }

    public String tokenGuardado() {
        return prefs.getString(TOKEN, null);
    }

    public void obtenerYRegistrar() {
        if (!firebaseConfigurado()) {
            Log.w(TAG, "Falta google-services.json: Firebase no está configurado");
            return;
        }
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tarea -> {
            if (!tarea.isSuccessful() || tarea.getResult() == null) {
                Log.w(TAG, "No se pudo obtener el token", tarea.getException());
                return;
            }
            registrar(tarea.getResult());
        });
    }

    public void registrar(String token) {
        prefs.edit().putString(TOKEN, token).apply();
        Log.d(TAG, "Token FCM: " + token);
        if (!sesiones.haySesion()) return;

        Map<String, String> parametros = new HashMap<>();
        parametros.put("token", token);
        parametros.put("modelo_celular", Build.MANUFACTURER + " " + Build.MODEL);

        auth.asegurarSesion(new AuthRepository.Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                api.registrarDispositivo(sesiones.bearer(), parametros).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        Log.d(TAG, response.isSuccessful()
                                ? "Token registrado en Supabase"
                                : "Supabase rechazó el token: " + response.code());
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.w(TAG, "Sin conexión al registrar el token");
                    }
                });
            }

            @Override
            public void error(String mensaje) {}
        });
    }

    public void borrarDeEsteDispositivo(Runnable alTerminar) {
        String token = tokenGuardado();
        if (token == null || !sesiones.haySesion()) {
            alTerminar.run();
            return;
        }

        auth.asegurarSesion(new AuthRepository.Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                api.borrarDispositivo(sesiones.bearer(), Collections.singletonMap("token", token))
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                alTerminar.run();
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                alTerminar.run();
                            }
                        });
            }

            @Override
            public void error(String mensaje) {
                alTerminar.run();
            }
        });
    }
}
