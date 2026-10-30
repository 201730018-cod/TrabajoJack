package com.example.calculadoradesumas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView sumas;

    TextView resultado;
    Button sumar;
    EditText numero1;
    EditText numero2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        sumas = findViewById(R.id.sumas);
        resultado = findViewById(R.id.resultado);
        sumar = findViewById(R.id.boton);
        numero1 = findViewById(R.id.numero1);
        numero2 = findViewById(R.id.numero2);

        sumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numero1 = Integer.valueOf(numero1.getText().toString());
                int numero2 = Integer.valueOf(numero2.getText().toString());
                int result = numero1
            }
        });




    }
}