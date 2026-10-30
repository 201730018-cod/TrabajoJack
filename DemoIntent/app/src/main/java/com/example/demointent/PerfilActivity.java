package com.example.demointent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recibe los Extras (getStringExtra regresa null si no llega el dato)
        String nombre = getIntent().getStringExtra("nombre");
        String carrera = getIntent().getStringExtra("carrera");
        int semestre = getIntent().getIntExtra("semestre", 0);

        if (nombre == null) nombre = "Sin nombre";
        if (carrera == null) carrera = "Sin carrera";

        TextView tvDatos = findViewById(R.id.tvDatos);
        tvDatos.setText("Nombre: " + nombre + "\nCarrera: " + carrera + "\nSemestre: " + semestre);

        // finish() cierra esta pantalla y regresa a la anterior
        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> finish());
    }
}