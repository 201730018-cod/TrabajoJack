package com.example.ejemploandroid;

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
    TextView texto1;
    TextView texto2;
    Button boton;

    EditText texto3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        texto1 = findViewById(R.id.hello);
        texto2 = findViewById(R.id.message);
        boton = findViewById(R.id.button);
        texto3 = findViewById(R.id.input_txt);


        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String temp2=texto3.getText().toString();
                texto1.setText(temp2);

            }
        });

        };


    }
