package com.jackbrandon.chatea.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jackbrandon.chatea.R;
import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.ui.Navegacion;
import com.jackbrandon.chatea.ui.home.HomeActivity;

public class RecuperarActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "email";

    private TextView tvInstrucciones;
    private TextInputLayout tilEmail;
    private TextInputLayout tilCodigo;
    private TextInputLayout tilNueva;
    private TextInputLayout tilConfirmar;
    private TextInputEditText etEmail;
    private TextInputEditText etCodigo;
    private TextInputEditText etNueva;
    private TextInputEditText etConfirmar;
    private Button btnEnviarCodigo;
    private Button btnCambiar;
    private View grupoCodigo;
    private ProgressBar progreso;

    private AuthRepository auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar);
        Navegacion.aplicarMargenesDelSistema(findViewById(R.id.main));

        auth = new AuthRepository(this);

        tvInstrucciones = findViewById(R.id.tvInstrucciones);
        tilEmail = findViewById(R.id.tilEmail);
        tilCodigo = findViewById(R.id.tilCodigo);
        tilNueva = findViewById(R.id.tilNueva);
        tilConfirmar = findViewById(R.id.tilConfirmar);
        etEmail = findViewById(R.id.etEmail);
        etCodigo = findViewById(R.id.etCodigo);
        etNueva = findViewById(R.id.etNueva);
        etConfirmar = findViewById(R.id.etConfirmar);
        btnEnviarCodigo = findViewById(R.id.btnEnviarCodigo);
        btnCambiar = findViewById(R.id.btnCambiar);
        grupoCodigo = findViewById(R.id.grupoCodigo);
        progreso = findViewById(R.id.progreso);

        String email = getIntent().getStringExtra(EXTRA_EMAIL);
        if (email != null) etEmail.setText(email);

        btnEnviarCodigo.setOnClickListener(v -> enviarCodigo());
        btnCambiar.setOnClickListener(v -> cambiarPassword());
    }

    private void enviarCodigo() {
        String email = texto(etEmail).trim();
        tilEmail.setError(null);

        if (!Navegacion.esCorreoValido(email)) {
            tilEmail.setError("Escribe un correo válido");
            return;
        }

        cargando(true);
        auth.enviarCodigoRecuperacion(email, new AuthRepository.Resultado<Void>() {
            @Override
            public void exito(Void dato) {
                cargando(false);
                tvInstrucciones.setText("Escribe el código que te llegó a " + email + " y tu nueva contraseña");
                btnEnviarCodigo.setText("Reenviar código");
                grupoCodigo.setVisibility(View.VISIBLE);
                etCodigo.requestFocus();
            }

            @Override
            public void error(String mensaje) {
                cargando(false);
                Toast.makeText(RecuperarActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cambiarPassword() {
        String email = texto(etEmail).trim();
        String codigo = texto(etCodigo).trim();
        String nueva = texto(etNueva);
        String confirmar = texto(etConfirmar);

        tilCodigo.setError(null);
        tilNueva.setError(null);
        tilConfirmar.setError(null);

        if (codigo.length() < 6) {
            tilCodigo.setError("Escribe el código completo");
            return;
        }
        if (nueva.length() < Navegacion.PASSWORD_MINIMA) {
            tilNueva.setError("Mínimo " + Navegacion.PASSWORD_MINIMA + " caracteres");
            return;
        }
        if (!nueva.equals(confirmar)) {
            tilConfirmar.setError("Las contraseñas no coinciden");
            return;
        }

        cargando(true);
        auth.cambiarPasswordConCodigo(email, codigo, nueva, new AuthRepository.Resultado<Void>() {
            @Override
            public void exito(Void dato) {
                Toast.makeText(RecuperarActivity.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                Navegacion.irA(RecuperarActivity.this, HomeActivity.class);
            }

            @Override
            public void error(String mensaje) {
                cargando(false);
                Toast.makeText(RecuperarActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargando(boolean activo) {
        btnEnviarCodigo.setEnabled(!activo);
        btnCambiar.setEnabled(!activo);
        progreso.setVisibility(activo ? View.VISIBLE : View.GONE);
    }

    private static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}
