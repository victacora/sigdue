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

    public static Map tiposParametros=new HashMap<Integer, String>() {{
        put(1, "clase predio");
        put(2, "clima");
        put(3, "con quien tiene tenencia");
        put(4, "distancia metros sede principal");
        put(5, "distancia en kilometros centro poblado");
        put(6, "propiedad lote");
        put(7, "tenencia");
        put(8, "tipo documento");
        put(9, "topografia");
        put(10, "zona aislamiento");
        put(11, "zona alto riesgo");
        put(12, "Zona proteccion");

    }};
}
