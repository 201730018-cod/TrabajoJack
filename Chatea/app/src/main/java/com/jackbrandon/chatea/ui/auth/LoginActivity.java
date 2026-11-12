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
import com.jackbrandon.chatea.ui.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnEntrar;
    private ProgressBar progreso;

    private AuthRepository auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        auth = new AuthRepository(this);

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        progreso = findViewById(R.id.progreso);

        btnEntrar.setOnClickListener(v -> iniciarSesion());
        findViewById(R.id.tvCrearCuenta).setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
        findViewById(R.id.tvOlvide).setOnClickListener(v -> {
            Intent intent = new Intent(this, RecuperarActivity.class);
            intent.putExtra(RecuperarActivity.EXTRA_EMAIL, texto(etEmail));
            startActivity(intent);
        });
    }

    private void iniciarSesion() {
        String email = texto(etEmail);
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString();

        tilEmail.setError(null);
        tilPassword.setError(null);

        if (!Navegacion.esCorreoValido(email)) {
            tilEmail.setError("Escribe un correo válido");
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Escribe tu contraseña");
            return;
        }

        cargando(true);
        auth.iniciarSesion(email, password, new AuthRepository.Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                Navegacion.irA(LoginActivity.this, HomeActivity.class);
            }

            @Override
            public void error(String mensaje) {
                cargando(false);
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargando(boolean activo) {
        btnEntrar.setEnabled(!activo);
        progreso.setVisibility(activo ? View.VISIBLE : View.GONE);
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
