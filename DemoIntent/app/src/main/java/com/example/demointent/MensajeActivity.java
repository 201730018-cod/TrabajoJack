package com.example.demointent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MensajeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mensaje);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String nombre = getIntent().getStringExtra("nombre");
        String mensaje = getIntent().getStringExtra("mensaje");
        String hora = getIntent().getStringExtra("hora");

        if (nombre == null) nombre = "Sin nombre";
        if (mensaje == null) mensaje = "Sin mensaje";
        if (hora == null) hora = "--:--";

        TextView tvDatos = findViewById(R.id.tvDatos);
        tvDatos.setText("¡Hola, " + nombre + "! 👋\n\n" + mensaje + "\n\nEnviado a las: " + hora);

        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> finish());
    }
}