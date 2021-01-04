/*
 * PinDroid - http://code.google.com/p/PinDroid/
 *
 * Copyright (C) 2010 Matt Schmidt
 *
 * PinDroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * PinDroid is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PinDroid; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package com.sigdue.service;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.sigdue.Constants;
import com.sigdue.R;
import com.sigdue.db.DaoSession;
import com.sigdue.db.PredialDao;
import com.sigdue.utilidadesgenerales.DBConnection;
import com.sigdue.utilidadesgenerales.Filtro;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.webservice.api.WSSIGDUEClient;
import com.sigdue.webservice.api.WSSIGDUEInterface;
import com.sigdue.db.Predial;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.sigdue.Constants.RUTAMULTIMEDIASIGDUE;

public class EnviarInformacionSIGDUEService extends IntentService {
    private DaoSession daoSession;
    private Notification.Builder mBuilder;
    private Context mContext;
    private NotificationManager mNotifyManager;
    ResultadoServicio<String> resultadoServicio;

    public EnviarInformacionSIGDUEService() {
        super("EnviarInmovilizacionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ResultadoServicio<String> resultado = new ResultadoServicio("OK");
            PredialDao predialDao = this.daoSession.getPredialDao();
            int totalComparendosEnviados = 0;
            int totalInmovilizacionesProcesadas = 0;
            if (this.daoSession != null) {
                List<Predial> prediales = predialDao.queryBuilder().where(PredialDao.Properties.Estado.notEq("E")).list();
                if (prediales != null && prediales.size() > 0) {
                    this.mBuilder.setProgress(100, 0, false);
                    this.mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    this.mBuilder.setDefaults(1);
                    this.mNotifyManager.notify(Constants.NOTIFICACIONES_ID, this.mBuilder.build());
                    for (Predial predial : prediales) {
                        resultado = enviarIndormacionSIGDUE(predial);
                        if (resultado.getError() == null && resultado.getResult() != null && resultado.getResult().equals("ok")) {
                            this.mBuilder.setVibrate(null);
                            this.mBuilder.setContentText("Inmovilizacion enviada: " + (totalComparendosEnviados + 1));
                            this.mBuilder.setSound(null);
                            this.mBuilder.setProgress(100, ((totalInmovilizacionesProcesadas + 1) * 100) / prediales.size(), false);
                            this.mNotifyManager.notify(Constants.NOTIFICACIONES_ID, this.mBuilder.build());
                            Thread.sleep(1500);
                            totalComparendosEnviados++;
                            try {
                                predialDao.delete(predial);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            this.mBuilder.setVibrate(null);
                            this.mBuilder.setContentText("La predial no pudo ser enviada: " + (totalComparendosEnviados + 1));
                            this.mBuilder.setSound(null);
                            this.mBuilder.setProgress(100, ((totalInmovilizacionesProcesadas + 1) * 100) / prediales.size(), false);
                            this.mNotifyManager.notify(Constants.NOTIFICACIONES_ID, this.mBuilder.build());
                            Thread.sleep(1500);
                        }
                        totalInmovilizacionesProcesadas++;
                    }
                }
            }
            if (resultado.getError() == null) {
                resultado = new ResultadoServicio(String.valueOf(totalComparendosEnviados));
            }
            this.resultadoServicio = resultado;
        } catch (Exception e) {
            e.printStackTrace();
            this.resultadoServicio = new ResultadoServicio(e);
        }
    }

    private ResultadoServicio<String> enviarIndormacionSIGDUE(Predial predio) {
        ResultadoServicio<String> resultado = new ResultadoServicio<String>("false");
        try {
            SimpleDateFormat formatFecha = new SimpleDateFormat("MM/dd/yyyy");
            WSSIGDUEInterface service = WSSIGDUEClient.getClient();
            com.sigdue.webservice.modelo.Predial predialJSON = new com.sigdue.webservice.modelo.Predial();
            predialJSON.setP_DANE_SEDE(predio.getDane_sede());
            predialJSON.setP_COD_PREDIO(predio.getCod_predio());
            predialJSON.setP_CLIMA(predio.getClima());
            predialJSON.setP_DISTANCIA_MTS_SEDE_PPAL(predio.getDistancia_mts_sede_ppal());
            predialJSON.setP_DIST_KM_CENTRO_POBLADO(predio.getDist_km_centro_poblado());
            predialJSON.setP_CLASE_PREDIO(predio.getClase_predio());
            predialJSON.setP_AVALUO_CATASTRAL(predio.getAvaluo_catastral());
            predialJSON.setpP_FEC_AVALUO_CATASTRAL(formatFecha.format(predio.getFec_avaluo_catastral()));
            predialJSON.setP_AVALUO_COMERCIAL(predio.getFec_avaluo_comercial());
            predialJSON.setpP_FEC_AVALUO_COMERCIAL(formatFecha.format(predio.getFec_avaluo_comercial()));
            predialJSON.setP_AVALUO_COMERCIAL(predio.getFec_avaluo_comercial());
            predialJSON.setP_ZONA_AISLAMIENTO(predio.getZona_aislamiento());
            predialJSON.setP_ZONA_ALTO_RIESGO(predio.getZona_alto_riesgo());
            predialJSON.setP_ZONA_PROTECCION(predio.getZona_proteccion());
            predialJSON.setP_TOPOGRAFIA(predio.getTopografia());
            predialJSON.setP_PROPIEDAD_LOTE(predio.getPropiedad_lote());
            predialJSON.setP_TIPO_DOCUMENTO(predio.getTipo_documento());
            predialJSON.setP_CUAL_TIPO_DOCUMENTO(predio.getCual_tipo_documento());
            predialJSON.setP_NRO_DOCUMENTO_LEGALIZACION(predio.getNro_documento_legalizacion());
            predialJSON.setpP_FEC_EXPEDICION(formatFecha.format(predio.getFec_expedicion()));
            predialJSON.setP_NOTARIA_DEPENDENCIA_ORIGEN(predio.getNotaria_dependencia_origen());
            predialJSON.setP_LUGAR_EXPEDICION(predio.getLugar_expedicion());
            predialJSON.setP_REGISTRO_CATASTRAL(predio.getRegistro_catastral());
            predialJSON.setP_MATRICULA_INMOBILIARIA(predio.getMatricula_inmobiliaria());
            predialJSON.setP_PROPIETARIOS(predio.getPropietarios());
            predialJSON.setP_TENENCIA(predio.getTenencia());
            predialJSON.setP_CON_QUIEN_TENENCIA(predio.getCon_quien_tenencia());
            predialJSON.setP_NOM_QUIEN_TENENCIA(predio.getNom_quien_tenencia());
            predialJSON.setpP_FECHA_TENENCIA_LOTE(formatFecha.format(predio.getFecha_tenencia_lote()));

            Call<ResponseBody> crearinmovilizacionCall = service.crearpredial(predialJSON);
            Response<ResponseBody> response = crearinmovilizacionCall.execute();
            if (response != null) {
                int codigo = response.code();
                int idPredial = 0;
                String error = "";
                Headers headers = response.headers();
                if (headers != null && headers.size() > 0) {
                    try {
                        String valor = headers.get("Id-Inmovilizacion");
                        idPredial = Integer.parseInt(valor);
                    } catch (Exception ex) {
                        idPredial = -1;
                    }
                    try {
                        error = headers.get("Error");
                    } catch (Exception ex) {
                        error = "";
                    }
                }
                if (codigo == 200) {
                    if (idPredial > 0) {
                        resultado = new ResultadoServicio<String>("ok");
                        File rutaArchivos = new File(Environment.getExternalStorageDirectory(), RUTAMULTIMEDIASIGDUE);
                        String inicio = predio.getId_predial() + "_foto_";
                        String fin = ".jpg";
                        File[] archivos = rutaArchivos.listFiles(new Filtro(inicio, fin));
                        if (archivos != null && archivos.length > 0) {
                            for (int i = 0; i < archivos.length; i++) {
                                File archivo = archivos[i];
                                if (archivo.exists()) {
                                    String mimeType = UtilidadesGenerales.getMimeType(archivo.getAbsolutePath());
                                    MediaType mediaType = null;
                                    if (mimeType.equals("")) {
                                        mimeType = "image/jpeg";
                                        mediaType = MediaType.parse(mimeType);
                                    }
                                    RequestBody requestBody = RequestBody.create(mediaType, archivo);
                                    MultipartBody.Part multimedia = okhttp3.MultipartBody.Part.createFormData("imagen", archivo.getName(), requestBody);

                                    Call<ResponseBody> crearMultimediaCall = service.crearmultimedia(idPredial, archivo.getName(), multimedia);
                                    response = crearMultimediaCall.execute();
                                    if (response != null) {
                                        headers = response.headers();
                                        if (headers != null && headers.size() > 0) {
                                            try {
                                                error = headers.get("Error");
                                            } catch (Exception ex) {
                                                error = "";
                                            }
                                        }
                                        if (codigo == 200) {
                                            archivo.delete();
                                        } else {
                                            throw new Exception("Error al enviar imagen: " + archivo.getName() + (error != null && !error.equals("") ? ", " + error : ""));
                                        }
                                    }
                                }
                            }
                        }

                        inicio = predio.getId_predial() + "_video_";
                        fin = ".mp4";
                        archivos = rutaArchivos.listFiles(new Filtro(inicio, fin));
                        if (archivos != null && archivos.length > 0) {
                            for (int i = 0; i < archivos.length; i++) {
                                File archivo = archivos[i];
                                if (archivo.exists()) {
                                    String mimeType = UtilidadesGenerales.getMimeType(archivo.getAbsolutePath());
                                    MediaType mediaType = null;
                                    if (mimeType.equals("")) {
                                        mimeType = "video/mp4";
                                        mediaType = MediaType.parse(mimeType);
                                    }
                                    RequestBody requestBody = RequestBody.create(mediaType, archivo);
                                    MultipartBody.Part multimedia = okhttp3.MultipartBody.Part.createFormData("video", archivo.getName(), requestBody);

                                    Call<ResponseBody> crearMultimediaCall = service.crearmultimedia(idPredial, archivo.getName(), multimedia);
                                    response = crearMultimediaCall.execute();
                                    if (response != null) {
                                        headers = response.headers();
                                        if (headers != null && headers.size() > 0) {
                                            try {
                                                error = headers.get("Error");
                                            } catch (Exception ex) {
                                                error = "";
                                            }
                                        }
                                        if (codigo == 200) {
                                            archivo.delete();
                                        } else {
                                            throw new Exception("Error al enviar video: " + archivo.getName() + (error != null && !error.equals("") ? ", " + error : ""));
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        throw new Exception("Error al enviar predio: " + error);
                    }
                } else if (codigo == 500) {
                    throw new Exception("Ocurrio un error al procesar la solicitud en el servidor codigo 500.");
                }
            }
        } catch (OutOfMemoryError ex) {
            resultado = new ResultadoServicio<String>(new Exception("La peticion genero un desbordamiento de memoria."));
            ex.printStackTrace();
        } catch (Exception ex) {
            resultado = new ResultadoServicio<String>(ex);
            ex.printStackTrace();
        }
        return resultado;
    }

    public void onCreate() {
        super.onCreate();
        try {
            mContext = getApplicationContext();
            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new Notification.Builder(EnviarInformacionSIGDUEService.this);
            mBuilder.setContentTitle("Enviando comparendos")
                    .setContentText("Envio de datos en progreso...")
                    .setSmallIcon(R.drawable.upload_24)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.upload));
            DBConnection dbConnection = new DBConnection(mContext);
            daoSession = dbConnection.getDaoSession();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.resultadoServicio.getError() != null) {
            String mensajeError = "Envio de datos fallido. Detalles: " + this.resultadoServicio.getError().getMessage();
            mostrarMensaje("Ha ocurrido un error al enviar los datos: " + this.resultadoServicio.getError().getMessage());
            this.mBuilder.setStyle(new Notification.BigTextStyle().bigText(mensajeError));
            this.mBuilder.setProgress(0, 0, false);
            this.mBuilder.setDefaults(1);
            this.mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            this.mNotifyManager.notify(Constants.NOTIFICACIONES_ID, this.mBuilder.build());
            return;
        }
        if (this.resultadoServicio.getResult() == null || ((String) this.resultadoServicio.getResult()).equals("false")) {
            this.mBuilder.setContentText("Envio de datos fallido, uno o mas registros no pudieron ser enviados");
            this.mBuilder.setProgress(0, 0, false);
            this.mBuilder.setDefaults(1);
            this.mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            this.mNotifyManager.notify(Constants.NOTIFICACIONES_ID, this.mBuilder.build());
        } else {
            this.mBuilder.setContentText("Envio de datos completado");
            this.mBuilder.setProgress(0, 0, false);
            this.mBuilder.setDefaults(1);
            this.mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            this.mNotifyManager.notify(Constants.NOTIFICACIONES_ID, this.mBuilder.build());
        }
        Intent intentResponse = new Intent();
        intentResponse.setAction(Constants.RESPUESTA_SERVICIO);
        intentResponse.putExtra("resultado", (String) this.resultadoServicio.getResult());
        sendBroadcast(intentResponse);
    }


    public void mostrarMensaje(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

}