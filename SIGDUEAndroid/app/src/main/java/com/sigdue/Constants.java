package com.sigdue;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int AGREGAR_INFORMACION_REQUEST_CODE = 500;
    public static final String BASE_URL_API = "http://ssipc.oracleapexservices.com/apex/restfull92/sig/";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 300;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 400;
    public static final int NOTIFICACIONES_ID = 500;
    public static final String RESPUESTA_SERVICIO = "com.sigdue.service.EnviarInformacionSIGDUEService.RESPONSE";
    public static final String RUTAMULTIMEDIASIGDUE = "sigdue";
    public static final int SLEEP_PROGRESS_MAESTROS = 2500;
    public static final int SLEEP_PROGRESS_NOTIFICACION = 1500;

    public static final int CLASE_PREDIO = 1;
    public static final int CLIMA = 2;
    public static final int CON_QUIEN_TIENE_TENENCIA = 3;
    public static final int DISTANCIA_METROS_SEDE_PRINCIPAL = 4;
    public static final int DISTANCIA_EN_KILOMETROS_CENTRO_POBLADO = 5;
    public static final int PROPIEDAD_LOTE = 6;
    public static final int TENENCIA = 7;
    public static final int TIPO_DOCUMENTO = 8;
    public static final int TOPOGRAFIA = 9;
    public static final int ZONA_AISLAMIENTO = 10;
    public static final int ZONA_ALTO_RIESGO = 11;
    public static final int ZONA_PROTECCION = 12;
    public static Map tiposParametros=new HashMap<Integer, String>() {{
        put(CLASE_PREDIO, "clase predio");
        put(CLIMA, "clima");
        put(CON_QUIEN_TIENE_TENENCIA, "con quien tiene tenencia");
        put(DISTANCIA_METROS_SEDE_PRINCIPAL, "distancia metros sede principal");
        put(DISTANCIA_EN_KILOMETROS_CENTRO_POBLADO, "distancia en kilometros centro poblado");
        put(PROPIEDAD_LOTE, "propiedad lote");
        put(TENENCIA, "tenencia");
        put(TIPO_DOCUMENTO, "tipo documento");
        put(TOPOGRAFIA, "topografia");
        put(ZONA_AISLAMIENTO, "zona aislamiento");
        put(ZONA_ALTO_RIESGO, "zona alto riesgo");
        put(ZONA_PROTECCION, "zona proteccion");
    }};
}
