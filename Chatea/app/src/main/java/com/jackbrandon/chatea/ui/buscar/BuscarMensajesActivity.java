package com.jackbrandon.chatea.ui.buscar;

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
import com.jackbrandon.chatea.data.mensajes.MensajesRepository;
import com.jackbrandon.chatea.data.model.ResultadoBusqueda;
import com.jackbrandon.chatea.ui.Navegacion;
import com.jackbrandon.chatea.ui.chat.ChatActivity;

import java.util.Collections;
import java.util.List;

public class BuscarMensajesActivity extends AppCompatActivity {

    private static final long ESPERA_BUSQUEDA_MS = 350;
    private static final int MINIMO_LETRAS = 2;

    private TextView tvVacio;
    private ProgressBar progreso;
    private ResultadosAdapter adapter;

    private MensajesRepository mensajes;
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());
    private Runnable busquedaPendiente;
    private int numeroConsulta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar_mensajes);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        mensajes = new MensajesRepository(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        tvVacio = findViewById(R.id.tvVacio);
        progreso = findViewById(R.id.progreso);

        adapter = new ResultadosAdapter(this::abrirChat);
        RecyclerView rvResultados = findViewById(R.id.rvResultados);
        rvResultados.setLayoutManager(new LinearLayoutManager(this));
        rvResultados.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvResultados.setAdapter(adapter);

        TextInputEditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.requestFocus();
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (busquedaPendiente != null) hiloPrincipal.removeCallbacks(busquedaPendiente);
                String texto = s.toString().trim();
                busquedaPendiente = () -> buscar(texto);
                hiloPrincipal.postDelayed(busquedaPendiente, ESPERA_BUSQUEDA_MS);
            }
        });
    }

    private void buscar(String texto) {
        int estaConsulta = ++numeroConsulta;
        if (texto.length() < MINIMO_LETRAS) {
            progreso.setVisibility(View.GONE);
            adapter.mostrar(Collections.emptyList(), "");
            tvVacio.setText("Busca palabras en todos tus chats");
            tvVacio.setVisibility(View.VISIBLE);
            return;
        }

        progreso.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);
        mensajes.buscar(texto, new AuthRepository.Resultado<List<ResultadoBusqueda>>() {
            @Override
            public void exito(List<ResultadoBusqueda> lista) {
                if (estaConsulta != numeroConsulta) return;
                progreso.setVisibility(View.GONE);
                adapter.mostrar(lista, texto);
                if (lista.isEmpty()) {
                    tvVacio.setText("Ningún mensaje contiene \"" + texto + "\"");
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

    private void abrirChat(ResultadoBusqueda resultado) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CONVERSACION, resultado.conversacionId);
        intent.putExtra(ChatActivity.EXTRA_ID, resultado.otroId);
        intent.putExtra(ChatActivity.EXTRA_NOMBRE, resultado.chatNombre);
        intent.putExtra(ChatActivity.EXTRA_FOTO, resultado.chatFotoUrl);
        intent.putExtra(ChatActivity.EXTRA_TIPO, resultado.chatTipo);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (busquedaPendiente != null) hiloPrincipal.removeCallbacks(busquedaPendiente);
        super.onDestroy();
    }
}
