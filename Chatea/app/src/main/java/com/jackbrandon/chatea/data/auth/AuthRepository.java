package com.jackbrandon.chatea.data.auth;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jackbrandon.chatea.data.model.Sesion;
import com.jackbrandon.chatea.data.model.Usuario;
import com.jackbrandon.chatea.data.remote.AuthBodies;
import com.jackbrandon.chatea.data.remote.ErroresSupabase;
import com.jackbrandon.chatea.data.remote.SupabaseApi;
import com.jackbrandon.chatea.data.remote.SupabaseClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    public interface Resultado<T> {
        void exito(T dato);

        void error(String mensaje);
    }

    private static final String SIN_CONEXION = "No hay conexión a internet";

    private final SupabaseApi api;
    private final SessionManager sesiones;

    public AuthRepository(Context context) {
        this.api = SupabaseClient.getApi();
        this.sesiones = SessionManager.get(context);
    }

    public void registrar(String nombre, String email, String password, Resultado<Sesion> resultado) {
        api.registrar(new AuthBodies.Registro(nombre, email, password))
                .enqueue(guardarSesionAlTerminar(resultado));
    }

    public void iniciarSesion(String email, String password, Resultado<Sesion> resultado) {
        api.iniciarSesion(new AuthBodies.Credenciales(email, password))
                .enqueue(guardarSesionAlTerminar(resultado));
    }

    public void cerrarSesion(Runnable alTerminar) {
        if (!sesiones.haySesion()) {
            alTerminar.run();
            return;
        }

        api.cerrarSesion(sesiones.bearer()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                sesiones.borrar();
                alTerminar.run();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                sesiones.borrar();
                alTerminar.run();
            }
        });
    }

    public void asegurarSesion(Resultado<Sesion> resultado) {
        if (!sesiones.haySesion()) {
            resultado.error(null);
            return;
        }
        if (!sesiones.estaPorExpirar()) {
            resultado.exito(null);
            return;
        }

        api.renovarSesion(new AuthBodies.Renovar(sesiones.getRefreshToken())).enqueue(new Callback<Sesion>() {
            @Override
            public void onResponse(@NonNull Call<Sesion> call, @NonNull Response<Sesion> response) {
                Sesion sesion = response.body();
                if (response.isSuccessful() && sesion != null && sesion.esValida()) {
                    sesiones.guardar(sesion);
                    resultado.exito(sesion);
                } else {
                    sesiones.borrar();
                    resultado.error(ErroresSupabase.mensaje(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Sesion> call, @NonNull Throwable t) {
                resultado.error(SIN_CONEXION);
            }
        });
    }

    public void conSesion(Resultado<?> resultado, Runnable accion) {
        asegurarSesion(new Resultado<Sesion>() {
            @Override
            public void exito(Sesion sesion) {
                accion.run();
            }

            @Override
            public void error(String mensaje) {
                resultado.error(mensaje == null ? "Inicia sesión otra vez" : mensaje);
            }
        });
    }

    public void enviarCodigoRecuperacion(String email, Resultado<Void> resultado) {
        api.enviarCodigoRecuperacion(new AuthBodies.Correo(email)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    resultado.exito(null);
                } else {
                    resultado.error(ErroresSupabase.mensaje(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                resultado.error(SIN_CONEXION);
            }
        });
    }

    public void cambiarPasswordConCodigo(String email, String codigo, String nuevaPassword, Resultado<Void> resultado) {
        api.verificarCodigo(new AuthBodies.VerificarCodigo(email, codigo))
                .enqueue(guardarSesionAlTerminar(new Resultado<Sesion>() {
                    @Override
                    public void exito(Sesion sesion) {
                        cambiarPassword(nuevaPassword, resultado);
                    }

                    @Override
                    public void error(String mensaje) {
                        resultado.error(mensaje);
                    }
                }));
    }

    private void cambiarPassword(String nuevaPassword, Resultado<Void> resultado) {
        api.cambiarPassword(sesiones.bearer(), new AuthBodies.NuevaPassword(nuevaPassword))
                .enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(@NonNull Call<Usuario> call, @NonNull Response<Usuario> response) {
                        if (response.isSuccessful()) {
                            resultado.exito(null);
                        } else {
                            resultado.error(ErroresSupabase.mensaje(response));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Usuario> call, @NonNull Throwable t) {
                        resultado.error(SIN_CONEXION);
                    }
                });
    }

    private Callback<Sesion> guardarSesionAlTerminar(Resultado<Sesion> resultado) {
        return new Callback<Sesion>() {
            @Override
            public void onResponse(@NonNull Call<Sesion> call, @NonNull Response<Sesion> response) {
                Sesion sesion = response.body();
                if (!response.isSuccessful()) {
                    resultado.error(ErroresSupabase.mensaje(response));
                } else if (sesion == null || !sesion.esValida()) {
                    resultado.error("Revisa tu correo para confirmar la cuenta");
                } else {
                    sesiones.guardar(sesion);
                    resultado.exito(sesion);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Sesion> call, @NonNull Throwable t) {
                resultado.error(SIN_CONEXION);
            }
        };
    }
}
