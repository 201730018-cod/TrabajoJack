package com.example.demointent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class ProductoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String producto = getIntent().getStringExtra("producto");
        double precio = getIntent().getDoubleExtra("precio", 0.0);
        boolean disponible = getIntent().getBooleanExtra("disponible", false);

        if (producto == null) producto = "Sin producto";
        String textoDisponible = disponible ? "Sí ✅" : "No ❌";
        String textoPrecio = String.format(Locale.getDefault(), "%.2f", precio);

        TextView tvDatos = findViewById(R.id.tvDatos);
        tvDatos.setText("Producto: " + producto
                + "\nPrecio: $" + textoPrecio + " MXN"
                + "\nDisponible: " + textoDisponible);

        Button btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> finish());
    }
}