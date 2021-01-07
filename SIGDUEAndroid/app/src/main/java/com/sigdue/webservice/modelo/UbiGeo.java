package com.sigdue.webservice.modelo;

public class UbiGeo {
    private String p_dane_sede;
    private String p_latitud;
    private String p_longitud;

    public UbiGeo(String p_dane_sede, String p_latitud, String p_longitud) {
        this.p_dane_sede = p_dane_sede;
        this.p_latitud = p_latitud;
        this.p_longitud = p_longitud;
    }

    public String getP_dane_sede() {
        return p_dane_sede;
    }

    public void setP_dane_sede(String p_dane_sede) {
        this.p_dane_sede = p_dane_sede;
    }

    public String getP_latitud() {
        return p_latitud;
    }

    public void setP_latitud(String p_latitud) {
        this.p_latitud = p_latitud;
    }

    public String getP_longitud() {
        return p_longitud;
    }

    public void setP_longitud(String p_longitud) {
        this.p_longitud = p_longitud;
    }
}
