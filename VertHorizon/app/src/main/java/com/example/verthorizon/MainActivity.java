package com.example.verthorizon;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "VertHorizon";

    private static final String KEY_TEXTO = "texto";
    private static final String KEY_CONTADOR = "contador";
    private static final String KEY_BITACORA = "bitacora";

    private TextView tvTexto;
    private TextView tvContador;
    private TextView tvBitacora;

    private int contador = 0;
    private final StringBuilder bitacora = new StringBuilder();

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

        TextView tvOrientacion = findViewById(R.id.tvOrientacion);
        EditText etTexto = findViewById(R.id.etTexto);
        Button btnMostrar = findViewById(R.id.btnMostrar);
        Button btnContar = findViewById(R.id.btnContar);
        tvTexto = findViewById(R.id.tvTexto);
        tvContador = findViewById(R.id.tvContador);
        tvBitacora = findViewById(R.id.tvBitacora);

        if (savedInstanceState != null) {
            tvTexto.setText(savedInstanceState.getString(KEY_TEXTO, ""));
            contador = savedInstanceState.getInt(KEY_CONTADOR, 0);
            bitacora.append(savedInstanceState.getString(KEY_BITACORA, ""));
            bitacora.append("----- se recreó la pantalla -----\n");
        }

        boolean horizontal = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        tvOrientacion.setText(horizontal ? "📱 Modo horizontal" : "📱 Modo vertical");

        actualizarContador();

        btnMostrar.setOnClickListener(v -> {
            String texto = etTexto.getText().toString().trim();
            if (texto.isEmpty()) {
                etTexto.setError("Escribe algo primero");
                return;
            }
            tvTexto.setText(texto);
            etTexto.setText("");
        });

        btnContar.setOnClickListener(v -> {
            contador++;
            actualizarContador();
        });

        registrar("onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        registrar("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registrar("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        registrar("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        registrar("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registrar("onRestart");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        registrar("onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TEXTO, tvTexto.getText().toString());
        outState.putInt(KEY_CONTADOR, contador);
        outState.putString(KEY_BITACORA, bitacora.toString());
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void actualizarContador() {
        tvContador.setText("Clics: " + contador);
    }

    private void registrar(String metodo) {
        Log.d(TAG, metodo);
        Toast.makeText(getApplicationContext(), metodo, Toast.LENGTH_SHORT).show();

        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        bitacora.append(hora).append("  ").append(metodo).append("\n");
        if (tvBitacora != null) {
            tvBitacora.setText(bitacora.toString());
        }
    }
}
