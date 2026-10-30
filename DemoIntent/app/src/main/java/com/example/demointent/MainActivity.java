package com.example.demointent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNombre = findViewById(R.id.etNombre);
        Button btnPerfil = findViewById(R.id.btnPerfil);
        Button btnProducto = findViewById(R.id.btnProducto);
        Button btnMensaje = findViewById(R.id.btnMensaje);

        // Botón 1: abre PerfilActivity y manda texto + número
        btnPerfil.setOnClickListener(v -> {
            String nombre = obtenerNombre();
            if (nombre == null) return;

            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("carrera", "Ingeniería en Sistemas");
            intent.putExtra("semestre", 7);
            startActivity(intent);
        });

        // Botón 2: abre ProductoActivity y manda texto + decimal + booleano
        btnProducto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductoActivity.class);
            intent.putExtra("producto", "Audífonos inalámbricos");
            intent.putExtra("precio", 899.50);
            intent.putExtra("disponible", true);
            startActivity(intent);
        });

        // Botón 3: abre MensajeActivity y manda el nombre + un mensaje + la hora
        btnMensaje.setOnClickListener(v -> {
            String nombre = obtenerNombre();
            if (nombre == null) return;

            String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            Intent intent = new Intent(MainActivity.this, MensajeActivity.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("mensaje", "Este texto viajó desde la pantalla principal dentro de un Intent.");
            intent.putExtra("hora", hora);
            startActivity(intent);
        });
    }

    // Valida que el nombre no esté vacío. Regresa null si no es válido.
    private String obtenerNombre() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError("Escribe tu nombre");
            Toast.makeText(this, "Primero escribe tu nombre", Toast.LENGTH_SHORT).show();
            return null;
        }
        return nombre;
    }
}