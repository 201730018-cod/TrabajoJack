package com.jackbrandon.chatea.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.model.Sesion;
import com.jackbrandon.chatea.ui.Navegacion;
import com.jackbrandon.chatea.ui.perfil.PerfilActivity;

public class RegistroActivity extends AppCompatActivity {

    private TextInputLayout tilNombre;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmar;
    private TextInputEditText etNombre;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmar;
    private Button btnRegistrar;
    private ProgressBar progreso;

    private AuthRepository auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        auth = new AuthRepository(this);

        tilNombre = findViewById(R.id.tilNombre);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmar = findViewById(R.id.tilConfirmar);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmar = findViewById(R.id.etConfirmar);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        progreso = findViewById(R.id.progreso);

        btnRegistrar.setOnClickListener(v -> registrar());
        findViewById(R.id.tvYaTengoCuenta).setOnClickListener(v -> finish());
    }

    private void registrar() {
        String nombre = texto(etNombre).trim();
        String email = texto(etEmail).trim();
        String password = texto(etPassword);
        String confirmar = texto(etConfirmar);

        tilNombre.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmar.setError(null);

        if (nombre.isEmpty()) {
            tilNombre.setError("Escribe tu nombre");
            return;
        }
        if (!Navegacion.esCorreoValido(email)) {
            tilEmail.setError("Escribe un correo válido");
            return;
        }
        if (password.length() < Navegacion.PASSWORD_MINIMA) {
            tilPassword.setError("Mínimo " + Navegacion.PASSWORD_MINIMA + " caracteres");
            return;
        }
        if (!password.equals(confirmar)) {
            tilConfirmar.setError("Las contraseñas no coinciden");
            return;
        }

        cargando(true);
        auth.registrar(nombre, email, password, new AuthRepository.Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                Toast.makeText(RegistroActivity.this, "¡Bienvenido a Chatea, " + nombre + "!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistroActivity.this, PerfilActivity.class);
                intent.putExtra(PerfilActivity.EXTRA_PRIMERA_VEZ, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void error(String mensaje) {
                cargando(false);
                Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargando(boolean activo) {
        btnRegistrar.setEnabled(!activo);
        progreso.setVisibility(activo ? View.VISIBLE : View.GONE);
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}
