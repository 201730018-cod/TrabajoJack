package com.jackbrandon.chatea.data.remote;

import androidx.annotation.NonNull;

import com.jackbrandon.chatea.data.auth.AuthRepository.Resultado;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RespuestaSinCuerpo<T> implements Callback<T> {

    private final Resultado<Void> resultado;

    public RespuestaSinCuerpo(Resultado<Void> resultado) {
        this.resultado = resultado;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            resultado.exito(null);
        } else {
            resultado.error(ErroresSupabase.mensaje(response));
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        resultado.error("No hay conexión a internet");
    }
}
