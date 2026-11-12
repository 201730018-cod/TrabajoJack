package com.jackbrandon.chatea.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Perfil {

    public String id;
    public String nombre;
    public String telefono;
    public String info;

    @SerializedName("foto_url")
    public String fotoUrl;

    @SerializedName("ultima_conexion")
    public String ultimaConexion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Perfil)) return false;
        Perfil otro = (Perfil) o;
        return Objects.equals(id, otro.id)
                && Objects.equals(nombre, otro.nombre)
                && Objects.equals(info, otro.info)
                && Objects.equals(fotoUrl, otro.fotoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, info, fotoUrl);
    }
}
