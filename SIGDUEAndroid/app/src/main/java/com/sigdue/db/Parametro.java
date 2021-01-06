package com.sigdue.db;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table "PARAMETRO".
 */
@Entity
public class Parametro {

    @Id(autoincrement = true)
    private long id_parametro;
    private Integer tipo;
    private String parametro;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    @Generated
    public Parametro() {
    }

    public Parametro(long id_parametro) {
        this.id_parametro = id_parametro;
    }

    @Generated
    public Parametro(long id_parametro, Integer tipo, String parametro) {
        this.id_parametro = id_parametro;
        this.tipo = tipo;
        this.parametro = parametro;
    }

    public long getId_parametro() {
        return id_parametro;
    }

    public void setId_parametro(long id_parametro) {
        this.id_parametro = id_parametro;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
