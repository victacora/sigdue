package com.sigdue.webservice.modelo;

public class Usuario {
    private String u;
    private String p;

    public Usuario(String u, String p) {
        this.u = u;
        this.p = p;
    }

    public Usuario() {

    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }
}
