package com.example.fragmentado;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

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
        Button btnInicio = findViewById(R.id.btnInicio);
        Button btnContador = findViewById(R.id.btnContador);
        Button btnSaludo = findViewById(R.id.btnSaludo);

        btnInicio.setOnClickListener(v -> cambiarFragmento(new InicioFragment()));
        btnContador.setOnClickListener(v -> cambiarFragmento(new ContadorFragment()));

        btnSaludo.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                etNombre.setError("Escribe tu nombre");
                return;
            }
            cambiarFragmento(SaludoFragment.nuevo(nombre));
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedor, new InicioFragment())
                    .commit();
        }
    }

    private void cambiarFragmento(Fragment fragmento) {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.contenedor, fragmento)
                .addToBackStack(null)
                .commit();
    }
}
