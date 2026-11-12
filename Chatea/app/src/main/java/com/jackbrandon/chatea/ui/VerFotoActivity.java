package com.jackbrandon.chatea.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.MaterialToolbar;
import com.jackbrandon.chatea.R;

public class VerFotoActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "url";
    private static final String EXTRA_RUTA_PRIVADA = "ruta_privada";
    private static final String EXTRA_TITULO = "titulo";
    private static final String EXTRA_PIE = "pie";

    public static void abrirPublica(Context context, String url, String titulo) {
        if (url == null || url.isEmpty()) return;
        context.startActivity(new Intent(context, VerFotoActivity.class)
                .putExtra(EXTRA_URL, url)
                .putExtra(EXTRA_TITULO, titulo));
    }

    public static void abrirDelChat(Context context, String ruta, String uriLocal, String titulo, String pie) {
        context.startActivity(new Intent(context, VerFotoActivity.class)
                .putExtra(EXTRA_RUTA_PRIVADA, ruta)
                .putExtra(EXTRA_URL, uriLocal)
                .putExtra(EXTRA_TITULO, titulo)
                .putExtra(EXTRA_PIE, pie));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_foto);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra(EXTRA_TITULO));
        toolbar.setNavigationOnClickListener(v -> finish());

        String pie = getIntent().getStringExtra(EXTRA_PIE);
        TextView tvPie = findViewById(R.id.tvPie);
        if (pie != null && !pie.isEmpty()) {
            tvPie.setText(pie);
            tvPie.setVisibility(View.VISIBLE);
        }

        ImageView ivFoto = findViewById(R.id.ivFoto);
        ProgressBar progreso = findViewById(R.id.progreso);
        String ruta = getIntent().getStringExtra(EXTRA_RUTA_PRIVADA);
        Object origen = ruta != null
                ? Imagenes.imagenPrivada(this, ruta)
                : getIntent().getStringExtra(EXTRA_URL);

        Glide.with(this)
                .load(origen)
                .error(R.drawable.avatar_default)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                @NonNull Target<Drawable> target,
                                                boolean isFirstResource) {
                        progreso.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource,
                                                   @NonNull Object model,
                                                   Target<Drawable> target,
                                                   @NonNull DataSource dataSource,
                                                   boolean isFirstResource) {
                        progreso.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(ivFoto);
    }
}
