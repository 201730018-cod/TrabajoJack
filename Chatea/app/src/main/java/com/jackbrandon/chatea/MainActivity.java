package com.jackbrandon.chatea;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jackbrandon.chatea.data.auth.AuthRepository;
import com.jackbrandon.chatea.data.model.Sesion;
import com.jackbrandon.chatea.ui.Navegacion;
import com.jackbrandon.chatea.ui.auth.LoginActivity;
import com.jackbrandon.chatea.ui.home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AuthRepository(this).asegurarSesion(new AuthRepository.Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                Navegacion.irA(MainActivity.this, HomeActivity.class);
            }

            @Override
            public void error(String mensaje) {
                Navegacion.irA(MainActivity.this, LoginActivity.class);
            }
        });
    }
}
