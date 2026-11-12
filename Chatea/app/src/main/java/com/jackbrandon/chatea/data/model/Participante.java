package com.jackbrandon.chatea.data.model;

import com.google.gson.annotations.SerializedName;

public class Participante {

    @SerializedName("perfil_id")
    public String perfilId;

    public String rol;

    @SerializedName("ultimo_recibido_en")
    public String ultimoRecibidoEn;

    @SerializedName("ultimo_leido_en")
    public String ultimoLeidoEn;

    @SerializedName("perfiles")
    public Perfil perfil;
}
