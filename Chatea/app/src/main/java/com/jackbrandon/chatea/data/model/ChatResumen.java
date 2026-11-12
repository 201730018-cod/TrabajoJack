package com.jackbrandon.chatea.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ChatResumen {

    public String id;
    public String tipo;
    public String nombre;

    @SerializedName("foto_url")
    public String fotoUrl;

    @SerializedName("otro_id")
    public String otroId;

    @SerializedName("actualizado_en")
    public String actualizadoEn;

    @SerializedName("ultimo_contenido")
    public String ultimoContenido;

    @SerializedName("ultimo_tipo")
    public String ultimoTipo;

    @SerializedName("ultimo_eliminado")
    public Boolean ultimoEliminado;

    @SerializedName("ultimo_es_mio")
    public Boolean ultimoEsMio;

    @SerializedName("ultimo_fecha")
    public String ultimoFecha;

    @SerializedName("otro_recibido_en")
    public String otroRecibidoEn;

    @SerializedName("otro_leido_en")
    public String otroLeidoEn;

    @SerializedName("ultimo_remitente_nombre")
    public String ultimoRemitenteNombre;

    public boolean esGrupo() {
        return "grupo".equals(tipo);
    }

    public transient boolean escribiendo;

    public boolean tieneMensajes() {
        return ultimoFecha != null;
    }

    public ChatResumen copiaEscribiendo(boolean escribiendo) {
        ChatResumen copia = new ChatResumen();
        copia.id = id;
        copia.tipo = tipo;
        copia.nombre = nombre;
        copia.fotoUrl = fotoUrl;
        copia.otroId = otroId;
        copia.actualizadoEn = actualizadoEn;
        copia.ultimoContenido = ultimoContenido;
        copia.ultimoTipo = ultimoTipo;
        copia.ultimoEliminado = ultimoEliminado;
        copia.ultimoEsMio = ultimoEsMio;
        copia.ultimoFecha = ultimoFecha;
        copia.otroRecibidoEn = otroRecibidoEn;
        copia.otroLeidoEn = otroLeidoEn;
        copia.ultimoRemitenteNombre = ultimoRemitenteNombre;
        copia.escribiendo = escribiendo;
        return copia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatResumen)) return false;
        ChatResumen otro = (ChatResumen) o;
        return Objects.equals(id, otro.id)
                && Objects.equals(nombre, otro.nombre)
                && Objects.equals(fotoUrl, otro.fotoUrl)
                && Objects.equals(ultimoContenido, otro.ultimoContenido)
                && Objects.equals(ultimoEliminado, otro.ultimoEliminado)
                && Objects.equals(ultimoFecha, otro.ultimoFecha)
                && Objects.equals(otroRecibidoEn, otro.otroRecibidoEn)
                && Objects.equals(otroLeidoEn, otro.otroLeidoEn)
                && escribiendo == otro.escribiendo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, fotoUrl, ultimoContenido, ultimoEliminado, ultimoFecha,
                otroRecibidoEn, otroLeidoEn, escribiendo);
    }
}
