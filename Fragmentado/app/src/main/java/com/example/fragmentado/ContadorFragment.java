package com.example.fragmentado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ContadorFragment extends Fragment {

    private static final String KEY_CONTADOR = "contador";

    private int contador = 0;
    private TextView tvNumero;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contador, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            contador = savedInstanceState.getInt(KEY_CONTADOR, 0);
        }

        tvNumero = view.findViewById(R.id.tvNumero);
        Button btnSumar = view.findViewById(R.id.btnSumar);
        Button btnRestar = view.findViewById(R.id.btnRestar);
        Button btnReiniciar = view.findViewById(R.id.btnReiniciar);

        btnSumar.setOnClickListener(v -> {
            contador++;
            mostrar();
        });
        btnRestar.setOnClickListener(v -> {
            contador--;
            mostrar();
        });
        btnReiniciar.setOnClickListener(v -> {
            contador = 0;
            mostrar();
        });

        mostrar();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTADOR, contador);
    }

    private void mostrar() {
        tvNumero.setText(String.valueOf(contador));
    }
}
