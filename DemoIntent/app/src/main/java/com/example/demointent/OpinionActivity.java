package com.example.demointent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OpinionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_opinion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String nombre = getIntent().getStringExtra("nombre");
        if (nombre == null) nombre = "Sin nombre";

        TextView tvPregunta = findViewById(R.id.tvPregunta);
        tvPregunta.setText(nombre + ", ¿qué te pareció la app?");

        RatingBar rbEstrellas = findViewById(R.id.rbEstrellas);
        EditText etComentario = findViewById(R.id.etComentario);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        btnEnviar.setOnClickListener(v -> {
            float estrellas = rbEstrellas.getRating();
            if (estrellas == 0) {
                Toast.makeText(this, "Elige al menos una estrella", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent respuesta = new Intent();
            respuesta.putExtra("estrellas", estrellas);
            respuesta.putExtra("comentario", etComentario.getText().toString().trim());
            setResult(RESULT_OK, respuesta);
            finish();
        });

        btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
