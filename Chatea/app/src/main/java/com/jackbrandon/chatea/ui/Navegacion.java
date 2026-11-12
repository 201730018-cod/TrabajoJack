package com.jackbrandon.chatea.ui;

import android.app.Activity;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class Navegacion {

    public static final int PASSWORD_MINIMA = 6;

    private Navegacion() {}

    public static void irA(Activity desde, Class<? extends Activity> destino) {
        Intent intent = new Intent(desde, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        desde.startActivity(intent);
        desde.finish();
    }

    public static void aplicarMargenesDelSistema(View raiz) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });
    }

    public static boolean esCorreoValido(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
