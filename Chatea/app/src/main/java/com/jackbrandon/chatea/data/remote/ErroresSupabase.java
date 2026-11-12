package com.jackbrandon.chatea.data.remote;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public final class ErroresSupabase {

    private ErroresSupabase() {}

    public static String mensaje(Response<?> respuesta) {
        String codigo = "";
        String detalle = "";

        try (ResponseBody cuerpo = respuesta.errorBody()) {
            if (cuerpo != null) {
                JsonObject json = JsonParser.parseString(cuerpo.string()).getAsJsonObject();
                codigo = texto(json, "error_code", texto(json, "error", ""));
                detalle = texto(json, "msg", texto(json, "error_description", texto(json, "message", "")));
            }
        } catch (IOException | RuntimeException ignorado) {
        }

        switch (codigo) {
            case "invalid_credentials":
            case "invalid_grant":
                return "Correo o contraseña incorrectos";
            case "user_already_exists":
            case "email_exists":
                return "Ya existe una cuenta con ese correo";
            case "weak_password":
                return "La contraseña es muy débil (mínimo 6 caracteres)";
            case "email_address_invalid":
            case "validation_failed":
                return "El correo no es válido";
            case "otp_expired":
                return "El código es incorrecto o ya expiró";
            case "same_password":
                return "La nueva contraseña debe ser diferente a la anterior";
            case "over_email_send_rate_limit":
            case "over_request_rate_limit":
                return "Demasiados intentos, espera unos minutos";
            case "email_not_confirmed":
                return "Primero confirma tu correo";
            case "refresh_token_not_found":
            case "refresh_token_already_used":
            case "session_not_found":
                return "Tu sesión expiró, vuelve a iniciar sesión";
            default:
                return detalle.isEmpty() ? "Error " + respuesta.code() : detalle;
        }
    }

    private static String texto(JsonObject json, String campo, String porDefecto) {
        return json.has(campo) && json.get(campo).isJsonPrimitive()
                ? json.get(campo).getAsString()
                : porDefecto;
    }
}
