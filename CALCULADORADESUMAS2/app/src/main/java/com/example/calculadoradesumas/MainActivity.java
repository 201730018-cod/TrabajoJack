package com.example.calculadoradesumas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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
                String texto1 = numero1.getText().toString().trim();
                String texto2 = numero2.getText().toString().trim();

                if (texto1.isEmpty() || texto2.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Escribe los dos números", Toast.LENGTH_SHORT).show();
                    return;
                }

                int a;
                int b;
                try {
                    a = Integer.parseInt(texto1);
                    b = Integer.parseInt(texto2);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Solo se permiten números enteros", Toast.LENGTH_SHORT).show();
                    return;
                }

                int result = a + b;
                resultado.setText("Resultado: " + result);
            }
        });
    }
}
