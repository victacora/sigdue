package com.sigdue.utilidadesgenerales;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by VICTORAL on 15/01/2017.
 */

public class Filtro implements FilenameFilter {
    String inicio, fin;
    public Filtro(String start, String end) {
        this.inicio = start;
        this.fin = end;
    }

    @Override
    public boolean accept(File directorio, String nombreArchivo) {

        if (nombreArchivo.indexOf(this.inicio) != -1 && nombreArchivo.endsWith(this.fin))
            return true;
        return false;
    }
}

