package com.jackbrandon.chatea.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Mensaje {

    public enum Estado { ENVIANDO, ENVIADO, ERROR }

    public String id;

    @SerializedName("conversacion_id")
    public String conversacionId;

    @SerializedName("remitente_id")
    public String remitenteId;

    public String tipo;
    public String contenido;
    public Boolean eliminado;

    @SerializedName("archivo_url")
    public String archivoUrl;

    @SerializedName("creado_en")
    public String creadoEn;

    public transient Estado estado = Estado.ENVIADO;
    public transient String uriLocal;

    public boolean esImagen() {
        return "imagen".equals(tipo);
    }

    public boolean estaEliminado() {
        return Boolean.TRUE.equals(eliminado);
    }

    public Estado getEstado() {
        return estado == null ? Estado.ENVIADO : estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mensaje)) return false;
        Mensaje otro = (Mensaje) o;
        return Objects.equals(id, otro.id)
                && Objects.equals(contenido, otro.contenido)
                && Objects.equals(eliminado, otro.eliminado)
                && Objects.equals(archivoUrl, otro.archivoUrl)
                && Objects.equals(creadoEn, otro.creadoEn)
                && Objects.equals(uriLocal, otro.uriLocal)
                && getEstado() == otro.getEstado();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contenido, eliminado, archivoUrl, creadoEn, uriLocal, getEstado());
    }
}
