package com.jackbrandon.chatea.data.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.jackbrandon.chatea.data.model.Sesion;

import java.io.IOException;
import java.security.GeneralSecurityException;

public final class SessionManager {

    private static final String ARCHIVO = "sesion_chatea";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String EXPIRA_A = "expira_a";
    private static final String USUARIO_ID = "usuario_id";
    private static final String EMAIL = "email";

    private static final long MARGEN_SEGUNDOS = 60;

    private static SessionManager instancia;

    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = crearPreferenciasCifradas(context.getApplicationContext());
    }

    public static synchronized SessionManager get(Context context) {
        if (instancia == null) {
            instancia = new SessionManager(context);
        }
        return instancia;
    }

    @SuppressWarnings("deprecation")
    private static SharedPreferences crearPreferenciasCifradas(Context context) {
        try {
            MasterKey llave = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    ARCHIVO,
                    llave,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("No se pudo abrir el almacenamiento cifrado", e);
        }
    }

    public void guardar(Sesion sesion) {
        long expiraA = sesion.expiraA > 0 ? sesion.expiraA : Jwt.expiracion(sesion.accessToken);
        prefs.edit()
                .putString(ACCESS_TOKEN, sesion.accessToken)
                .putString(REFRESH_TOKEN, sesion.refreshToken)
                .putLong(EXPIRA_A, expiraA)
                .putString(USUARIO_ID, sesion.user.id)
                .putString(EMAIL, sesion.user.email)
                .apply();
    }

    public void borrar() {
        prefs.edit().clear().apply();
    }

    public boolean haySesion() {
        return getAccessToken() != null && getRefreshToken() != null;
    }

    public boolean estaPorExpirar() {
        long ahora = System.currentTimeMillis() / 1000;
        return ahora >= getExpiraA() - MARGEN_SEGUNDOS;
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(REFRESH_TOKEN, null);
    }

    public long getExpiraA() {
        return prefs.getLong(EXPIRA_A, 0);
    }

    public String getUsuarioId() {
        return prefs.getString(USUARIO_ID, null);
    }

    public String getEmail() {
        return prefs.getString(EMAIL, null);
    }

    public String bearer() {
        return "Bearer " + getAccessToken();
    }
}
