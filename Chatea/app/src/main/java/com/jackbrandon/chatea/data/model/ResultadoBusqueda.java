package com.jackbrandon.chatea.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ResultadoBusqueda {

    public String id;

    @SerializedName("conversacion_id")
    public String conversacionId;

    public String contenido;

    @SerializedName("creado_en")
    public String creadoEn;

    @SerializedName("es_mio")
    public Boolean esMio;

    @SerializedName("remitente_nombre")
    public String remitenteNombre;

    @SerializedName("chat_tipo")
    public String chatTipo;

    @SerializedName("chat_nombre")
    public String chatNombre;

    @SerializedName("chat_foto_url")
    public String chatFotoUrl;

    @SerializedName("otro_id")
    public String otroId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultadoBusqueda)) return false;
        ResultadoBusqueda otro = (ResultadoBusqueda) o;
        return Objects.equals(id, otro.id) && Objects.equals(contenido, otro.contenido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contenido);
    }
}
