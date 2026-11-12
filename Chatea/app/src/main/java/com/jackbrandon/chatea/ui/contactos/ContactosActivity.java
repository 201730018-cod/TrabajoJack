package com.jackbrandon.chatea.ui.contactos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.model.Perfil;
import com.jackbrandon.chatea.data.perfiles.PerfilesRepository;
import com.jackbrandon.chatea.ui.Navegacion;
import com.jackbrandon.chatea.ui.chat.ChatActivity;

import java.util.List;

public class ContactosActivity extends AppCompatActivity {

    private static final long ESPERA_BUSQUEDA_MS = 350;

    private MaterialToolbar toolbar;
    private TextView tvVacio;
    private ProgressBar progreso;
    private ContactosAdapter adapter;

    private PerfilesRepository perfiles;
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());
    private Runnable busquedaPendiente;
    private int numeroConsulta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contactos);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        perfiles = new PerfilesRepository(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        tvVacio = findViewById(R.id.tvVacio);
        progreso = findViewById(R.id.progreso);

        adapter = new ContactosAdapter(this::abrirChat);
        RecyclerView rvContactos = findViewById(R.id.rvContactos);
        rvContactos.setLayoutManager(new LinearLayoutManager(this));
        rvContactos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvContactos.setAdapter(adapter);

        TextInputEditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (busquedaPendiente != null) hiloPrincipal.removeCallbacks(busquedaPendiente);
                String texto = s.toString();
                busquedaPendiente = () -> buscar(texto);
                hiloPrincipal.postDelayed(busquedaPendiente, ESPERA_BUSQUEDA_MS);
            }
        });

        buscar("");
    }

    private void buscar(String texto) {
        int estaConsulta = ++numeroConsulta;
        progreso.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        perfiles.buscarUsuarios(texto, new AuthRepository.Resultado<List<Perfil>>() {
            @Override
            public void exito(List<Perfil> lista) {
                if (estaConsulta != numeroConsulta) return;
                progreso.setVisibility(View.GONE);
                adapter.submitList(lista);

                if (texto.trim().isEmpty()) {
                    toolbar.setSubtitle(lista.size() + (lista.size() == 1 ? " contacto" : " contactos"));
                }
                if (lista.isEmpty()) {
                    tvVacio.setText(texto.trim().isEmpty()
                            ? "Todavía no hay otros usuarios en Chatea"
                            : "Nadie se llama \"" + texto.trim() + "\"");
                    tvVacio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void error(String mensaje) {
                if (estaConsulta != numeroConsulta) return;
                progreso.setVisibility(View.GONE);
                tvVacio.setText(mensaje);
                tvVacio.setVisibility(View.VISIBLE);
            }
        });
    }

    private void abrirChat(Perfil destinatario) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_ID, destinatario.id);
        intent.putExtra(ChatActivity.EXTRA_NOMBRE, destinatario.nombre);
        intent.putExtra(ChatActivity.EXTRA_FOTO, destinatario.fotoUrl);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (busquedaPendiente != null) hiloPrincipal.removeCallbacks(busquedaPendiente);
        super.onDestroy();
    }
}
