package com.example.fragmentado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SaludoFragment extends Fragment {

    private static final String ARG_NOMBRE = "nombre";

    public static SaludoFragment nuevo(String nombre) {
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE, nombre);

        SaludoFragment fragmento = new SaludoFragment();
        fragmento.setArguments(args);
        return fragmento;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saludo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String nombre = "desconocido";
        if (getArguments() != null) {
            nombre = getArguments().getString(ARG_NOMBRE, nombre);
        }

        TextView tvSaludo = view.findViewById(R.id.tvSaludo);
        tvSaludo.setText("¡Hola, " + nombre + "! 👋\nEste texto llegó desde la Activity.");
    }
}
