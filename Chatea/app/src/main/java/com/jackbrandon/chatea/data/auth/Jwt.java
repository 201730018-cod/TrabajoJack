package com.jackbrandon.chatea.data.auth;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public final class Jwt {

    private Jwt() {}

    public static JSONObject leerPayload(String token) {
        if (token == null) return new JSONObject();
        String[] partes = token.split("\\.");
        if (partes.length != 3) return new JSONObject();

        try {
            byte[] bytes = Base64.decode(partes[1], Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
            return new JSONObject(new String(bytes, StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | JSONException e) {
            return new JSONObject();
        }
    }

    public static long expiracion(String token) {
        return leerPayload(token).optLong("exp", 0);
    }
}
