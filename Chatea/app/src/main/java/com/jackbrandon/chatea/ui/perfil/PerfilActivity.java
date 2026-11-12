package com.jackbrandon.chatea.ui.perfil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.auth.SessionManager;
import com.jackbrandon.chatea.data.model.Perfil;
import com.jackbrandon.chatea.data.perfiles.PerfilesRepository;
import com.jackbrandon.chatea.ui.Imagenes;
import com.jackbrandon.chatea.ui.Navegacion;
import com.jackbrandon.chatea.ui.VerFotoActivity;
import com.jackbrandon.chatea.ui.home.HomeActivity;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerfilActivity extends AppCompatActivity {

    public static final String EXTRA_PRIMERA_VEZ = "primera_vez";

    private ImageView ivFoto;
    private TextInputLayout tilNombre;
    private TextInputEditText etNombre;
    private Button btnGuardar;
    private ProgressBar progreso;

    private PerfilesRepository perfiles;
    private boolean primeraVez;
    private Uri fotoNueva;
    private String fotoActual;

    private final ExecutorService segundoPlano = Executors.newSingleThreadExecutor();
    private final Handler hiloPrincipal = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<PickVisualMediaRequest> elegirFoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri == null) return;
                fotoNueva = uri;
                Glide.with(this).load(uri).circleCrop().into(ivFoto);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        perfiles = new PerfilesRepository(this);
        primeraVez = getIntent().getBooleanExtra(EXTRA_PRIMERA_VEZ, false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        ivFoto = findViewById(R.id.ivFoto);
        tilNombre = findViewById(R.id.tilNombre);
        etNombre = findViewById(R.id.etNombre);
        btnGuardar = findViewById(R.id.btnGuardar);
        progreso = findViewById(R.id.progreso);
        TextView tvEmail = findViewById(R.id.tvEmail);

        if (primeraVez) {
            toolbar.setTitle("Información del perfil");
            btnGuardar.setText("Continuar");
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_atras);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvEmail.setText("Correo: " + SessionManager.get(this).getEmail());

        findViewById(R.id.fabCambiarFoto).setOnClickListener(v -> mostrarOpcionesDeFoto());
        ivFoto.setOnClickListener(v -> {
            if (fotoActual != null && fotoNueva == null) {
                VerFotoActivity.abrirPublica(this, fotoActual, "Tu foto de perfil");
            } else {
                mostrarOpcionesDeFoto();
            }
        });
        btnGuardar.setOnClickListener(v -> guardar());

        cargarPerfil();
    }

    private void mostrarOpcionesDeFoto() {
        boolean tieneFoto = fotoActual != null || fotoNueva != null;
        String[] opciones = tieneFoto
                ? new String[]{"Elegir de la galería", "Ver foto", "Quitar foto"}
                : new String[]{"Elegir de la galería"};

        new AlertDialog.Builder(this)
                .setTitle("Foto de perfil")
                .setItems(opciones, (d, cual) -> {
                    if (cual == 0) {
                        elegirFoto.launch(new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                .build());
                    } else if (cual == 1) {
                        VerFotoActivity.abrirPublica(this,
                                fotoNueva != null ? fotoNueva.toString() : fotoActual, "Tu foto de perfil");
                    } else {
                        quitarFoto();
                    }
                })
                .show();
    }

    private void quitarFoto() {
        if (fotoNueva != null && fotoActual == null) {
            fotoNueva = null;
            Imagenes.cargarAvatar(ivFoto, null);
            return;
        }

        cargando(true);
        perfiles.quitarFoto(new AuthRepository.Resultado<Void>() {
            @Override
            public void exito(Void dato) {
                cargando(false);
                fotoActual = null;
                fotoNueva = null;
                Imagenes.cargarAvatar(ivFoto, null);
                Toast.makeText(PerfilActivity.this, "Foto quitada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(String mensaje) {
                cargando(false);
                Toast.makeText(PerfilActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarPerfil() {
        cargando(true);
        perfiles.obtenerMiPerfil(new AuthRepository.Resultado<Perfil>() {
            @Override
            public void exito(Perfil perfil) {
                cargando(false);
                if (etNombre.getText() == null || etNombre.getText().length() == 0) {
                    etNombre.setText(perfil.nombre);
                }
                fotoActual = perfil.fotoUrl;
                if (fotoNueva == null) {
                    Imagenes.cargarAvatar(ivFoto, perfil.fotoUrl);
                }
            }

            @Override
            public void error(String mensaje) {
                cargando(false);
                Toast.makeText(PerfilActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardar() {
        String nombre = etNombre.getText() == null ? "" : etNombre.getText().toString().trim();
        tilNombre.setError(null);
        if (nombre.isEmpty()) {
            tilNombre.setError("Escribe tu nombre");
            return;
        }

        cargando(true);
        Uri foto = fotoNueva;
        segundoPlano.execute(() -> {
            byte[] jpeg = null;
            if (foto != null) {
                try {
                    jpeg = Imagenes.prepararAvatar(getApplicationContext(), foto);
                } catch (IOException e) {
                    hiloPrincipal.post(() -> {
                        cargando(false);
                        Toast.makeText(this, "No se pudo leer la foto", Toast.LENGTH_LONG).show();
                    });
                    return;
                }
            }
            byte[] listo = jpeg;
            hiloPrincipal.post(() -> subir(nombre, listo));
        });
    }

    private void subir(String nombre, byte[] jpeg) {
        perfiles.guardarPerfil(nombre, jpeg, new AuthRepository.Resultado<Perfil>() {
            @Override
            public void exito(Perfil perfil) {
                Toast.makeText(PerfilActivity.this, "Perfil guardado", Toast.LENGTH_SHORT).show();
                if (primeraVez) {
                    Navegacion.irA(PerfilActivity.this, HomeActivity.class);
                } else {
                    finish();
                }
            }

            @Override
            public void error(String mensaje) {
                cargando(false);
                Toast.makeText(PerfilActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargando(boolean activo) {
        btnGuardar.setEnabled(!activo);
        progreso.setVisibility(activo ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        segundoPlano.shutdown();
        super.onDestroy();
    }
}
