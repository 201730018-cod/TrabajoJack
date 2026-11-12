package com.jackbrandon.chatea.ui.grupos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.conversaciones.ConversacionesRepository;
import com.jackbrandon.chatea.data.model.Perfil;
import com.jackbrandon.chatea.data.perfiles.PerfilesRepository;
import com.jackbrandon.chatea.ui.Imagenes;
import com.jackbrandon.chatea.ui.Navegacion;
import com.jackbrandon.chatea.ui.chat.ChatActivity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NuevoGrupoActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ImageView ivFotoGrupo;
    private TextInputLayout tilNombre;
    private TextInputEditText etNombre;
    private ExtendedFloatingActionButton fabCrear;
    private ProgressBar progreso;
    private SeleccionContactosAdapter adapter;

    private PerfilesRepository perfiles;
    private ConversacionesRepository conversaciones;
    private Uri fotoElegida;

    private final ExecutorService segundoPlano = Executors.newSingleThreadExecutor();
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<PickVisualMediaRequest> elegirFoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri == null) return;
                fotoElegida = uri;
                Glide.with(this).load(uri).circleCrop().into(ivFotoGrupo);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_grupo);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        perfiles = new PerfilesRepository(this);
        conversaciones = new ConversacionesRepository(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setSubtitle("0 seleccionados");

        ivFotoGrupo = findViewById(R.id.ivFotoGrupo);
        tilNombre = findViewById(R.id.tilNombre);
        etNombre = findViewById(R.id.etNombre);
        fabCrear = findViewById(R.id.fabCrear);
        progreso = findViewById(R.id.progreso);

        ivFotoGrupo.setOnClickListener(v -> elegirFoto.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        adapter = new SeleccionContactosAdapter(cantidad ->
                toolbar.setSubtitle(cantidad + (cantidad == 1 ? " seleccionado" : " seleccionados")));
        RecyclerView rvContactos = findViewById(R.id.rvContactos);
        rvContactos.setLayoutManager(new LinearLayoutManager(this));
        rvContactos.setAdapter(adapter);

        fabCrear.setOnClickListener(v -> crear());
        cargarContactos();
    }

    private void cargarContactos() {
        progreso.setVisibility(View.VISIBLE);
        perfiles.buscarUsuarios("", new AuthRepository.Resultado<List<Perfil>>() {
            @Override
            public void exito(List<Perfil> lista) {
                progreso.setVisibility(View.GONE);
                adapter.mostrar(lista);
            }

            @Override
            public void error(String mensaje) {
                progreso.setVisibility(View.GONE);
                Toast.makeText(NuevoGrupoActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void crear() {
        String nombre = etNombre.getText() == null ? "" : etNombre.getText().toString().trim();
        List<String> miembros = adapter.idsSeleccionados();

        tilNombre.setError(null);
        if (nombre.isEmpty()) {
            tilNombre.setError("Ponle un nombre al grupo");
            return;
        }
        if (miembros.isEmpty()) {
            Toast.makeText(this, "Elige al menos a un participante", Toast.LENGTH_SHORT).show();
            return;
        }

        cargando(true);
        if (fotoElegida == null) {
            crearGrupo(nombre, null, miembros);
            return;
        }

        Uri foto = fotoElegida;
        segundoPlano.execute(() -> {
            try {
                byte[] jpeg = Imagenes.prepararAvatar(getApplicationContext(), foto);
                hiloPrincipal.post(() -> perfiles.subirFotoDeGrupo(jpeg, new AuthRepository.Resultado<String>() {
                    @Override
                    public void exito(String url) {
                        crearGrupo(nombre, url, miembros);
                    }

                    @Override
                    public void error(String mensaje) {
                        cargando(false);
                        Toast.makeText(NuevoGrupoActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    }
                }));
            } catch (IOException e) {
                hiloPrincipal.post(() -> {
                    cargando(false);
                    Toast.makeText(this, "No se pudo leer la foto", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void crearGrupo(String nombre, String fotoUrl, List<String> miembros) {
        conversaciones.crearGrupo(nombre, fotoUrl, miembros, new AuthRepository.Resultado<String>() {
            @Override
            public void exito(String conversacionId) {
                Intent intent = new Intent(NuevoGrupoActivity.this, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_CONVERSACION, conversacionId);
                intent.putExtra(ChatActivity.EXTRA_NOMBRE, nombre);
                intent.putExtra(ChatActivity.EXTRA_FOTO, fotoUrl);
                intent.putExtra(ChatActivity.EXTRA_TIPO, "grupo");
                startActivity(intent);
                finish();
            }

            @Override
            public void error(String mensaje) {
                cargando(false);
                Toast.makeText(NuevoGrupoActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargando(boolean activo) {
        fabCrear.setEnabled(!activo);
        progreso.setVisibility(activo ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        segundoPlano.shutdown();
        super.onDestroy();
    }
}
