package com.jackbrandon.chatea.data.model;

import com.google.gson.annotations.SerializedName;

public class Sesion {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("expires_in")
    public long expiraEn;

    @SerializedName("expires_at")
    public long expiraA;

    public Usuario user;

    public boolean esValida() {
        return accessToken != null && refreshToken != null && user != null;
    }
}
