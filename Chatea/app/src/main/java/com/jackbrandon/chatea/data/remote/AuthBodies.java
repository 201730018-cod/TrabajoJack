package com.jackbrandon.chatea.data.remote;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Map;

public final class AuthBodies {

    private AuthBodies() {}

    public static class Credenciales {
        final String email;
        final String password;

        public Credenciales(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class Registro {
        final String email;
        final String password;
        final Map<String, String> data;

        public Registro(String nombre, String email, String password) {
            this.email = email;
            this.password = password;
            this.data = Collections.singletonMap("nombre", nombre);
        }
    }

    public static class Renovar {
        @SerializedName("refresh_token")
        final String refreshToken;

        public Renovar(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    public static class Correo {
        final String email;

        public Correo(String email) {
            this.email = email;
        }
    }

    public static class VerificarCodigo {
        final String type = "recovery";
        final String email;
        final String token;

        public VerificarCodigo(String email, String token) {
            this.email = email;
            this.token = token;
        }
    }

    public static class NuevaPassword {
        final String password;

        public NuevaPassword(String password) {
            this.password = password;
        }
    }
}
