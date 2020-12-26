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
import com.sigdue.db.Inmovilizacion;
import com.sigdue.db.InmovilizacionDao;
import com.sigdue.db.Persona;
import com.sigdue.db.Vehiculo;
import com.sigdue.utilidadesgenerales.DBConnection;
import com.sigdue.utilidadesgenerales.Filtro;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.webservice.api.WSGruparClient;
import com.sigdue.webservice.api.WSGruparInterface;

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

import static com.sigdue.Constants.RUTAMULTIMEDIAINMOVILIZACION;

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
            InmovilizacionDao inmovilizacionDao = this.daoSession.getInmovilizacionDao();
            int totalComparendosEnviados = 0;
            int totalInmovilizacionesProcesadas = 0;
            if (this.daoSession != null) {
                List<Inmovilizacion> inmovilizaciones = inmovilizacionDao.queryBuilder().where(InmovilizacionDao.Properties.Estado.notEq("E")).list();
                if (inmovilizaciones != null && inmovilizaciones.size() > 0) {
                    this.mBuilder.setProgress(100, 0, false);
                    this.mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    this.mBuilder.setDefaults(1);
                    this.mNotifyManager.notify(Constants.NOTIFICACIONES_ID, this.mBuilder.build());
                    for (Inmovilizacion inmovilizacion : inmovilizaciones) {
                        inmovilizacion.__setDaoSession(this.daoSession);
                        resultado = enviarInmovilizacion(inmovilizacion);
                        if (resultado.getError() == null && resultado.getResult() != null && resultado.getResult().equals("ok")) {
                            this.mBuilder.setVibrate(null);
                            this.mBuilder.setContentText("Inmovilizacion enviada: " + (totalComparendosEnviados + 1));
                            this.mBuilder.setSound(null);
                            this.mBuilder.setProgress(100, ((totalInmovilizacionesProcesadas + 1) * 100) / inmovilizaciones.size(), false);
                            this.mNotifyManager.notify(Constants.NOTIFICACIONES_ID, this.mBuilder.build());
                            Thread.sleep(1500);
                            totalComparendosEnviados++;
                            try {
                                inmovilizacionDao.delete(inmovilizacion);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            this.mBuilder.setVibrate(null);
                            this.mBuilder.setContentText("La inmovilizacion no pudo ser enviada: " + (totalComparendosEnviados + 1));
                            this.mBuilder.setSound(null);
                            this.mBuilder.setProgress(100, ((totalInmovilizacionesProcesadas + 1) * 100) / inmovilizaciones.size(), false);
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

    private ResultadoServicio<String> enviarInmovilizacion(Inmovilizacion inmovilizacion) {
        ResultadoServicio<String> resultado = new ResultadoServicio<String>("false");
        try {
            SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            WSGruparInterface service = WSGruparClient.getClient();
            com.sigdue.webservice.modelo.Inmovilizacion inmovilizacionJSON = new com.sigdue.webservice.modelo.Inmovilizacion();
            inmovilizacionJSON.setNo_comparendo(inmovilizacion.getNo_comparendo());
            inmovilizacionJSON.setFec_ini_inm(formatFecha.format(inmovilizacion.getFec_ini_inm()));
            inmovilizacionJSON.setDesenganche(inmovilizacion.getDesenganche());
            inmovilizacionJSON.setPropietario_presente(inmovilizacion.getPropietario_presente());
            inmovilizacionJSON.setId_grua(inmovilizacion.getId_grua());
            inmovilizacionJSON.setId_parqueadero(inmovilizacion.getId_parqueadero());
            inmovilizacionJSON.setId_zona(inmovilizacion.getId_zona());
            inmovilizacionJSON.setId_infraccion(inmovilizacion.getId_infraccion());
            inmovilizacionJSON.setDireccion(inmovilizacion.getDireccion());
            inmovilizacionJSON.setObservacion(inmovilizacion.getObservacion());
            Vehiculo vehiculo = inmovilizacion.getVehiculo();
            if (vehiculo != null) {
                inmovilizacionJSON.setPlaca(vehiculo.getPlaca());
                inmovilizacionJSON.setNo_motor(vehiculo.getNo_motor());
                inmovilizacionJSON.setNo_chasis(vehiculo.getNo_chasis());
                inmovilizacionJSON.setNo_serie(vehiculo.getNo_serie());
                inmovilizacionJSON.setId_color(vehiculo.getId_color());
                inmovilizacionJSON.setId_clase_vehiculo(vehiculo.getId_clase_vehiculo());
                inmovilizacionJSON.setId_tipo_servicio(vehiculo.getId_tipo_servicio());
            }
            Persona infractor = inmovilizacion.getInfractor();
            if (infractor != null) {
                inmovilizacionJSON.setId_tipo_identificacion(infractor.getId_tipo_identificacion());
                inmovilizacionJSON.setNo_identificacion(infractor.getNo_identificacion());
                inmovilizacionJSON.setNombre1(infractor.getNombre1());
                inmovilizacionJSON.setNombre2(infractor.getNombre2());
                inmovilizacionJSON.setApellido1(infractor.getApellido1());
                inmovilizacionJSON.setApellido2(infractor.getApellido2());
                inmovilizacionJSON.setId_municipio(infractor.getId_municipio());
            }
            Persona usuario = inmovilizacion.getUsuario();
            if (usuario != null) {
                inmovilizacionJSON.setId_usuario(usuario.getId_persona());
                inmovilizacionJSON.setId_dispositivo(usuario.getId_dispositivo());
            }
            Persona agente = inmovilizacion.getAgente();
            if (agente != null) {
                inmovilizacionJSON.setId_agente(agente.getId_persona());
            }
            Call<ResponseBody> crearinmovilizacionCall = service.crearinmovilizacion(inmovilizacionJSON);
            Response<ResponseBody> response = crearinmovilizacionCall.execute();
            if (response != null) {
                int codigo = response.code();
                int idInmovilizacion = 0;
                String error = "";
                Headers headers = response.headers();
                if (headers != null && headers.size() > 0) {
                    try {
                        String valor = headers.get("Id-Inmovilizacion");
                        idInmovilizacion = Integer.parseInt(valor);
                    } catch (Exception ex) {
                        idInmovilizacion = -1;
                    }
                    try {
                        error = headers.get("Error");
                    } catch (Exception ex) {
                        error = "";
                    }
                }
                if (codigo == 200) {
                    if (idInmovilizacion > 0) {
                        resultado = new ResultadoServicio<String>("ok");
                        File rutaArchivos = new File(Environment.getExternalStorageDirectory(), RUTAMULTIMEDIAINMOVILIZACION);
                        String inicio = inmovilizacion.getId_inmovilizacion() + "_foto_";
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

                                    Call<ResponseBody> crearMultimediaCall = service.crearmultimedia(idInmovilizacion, archivo.getName(), multimedia, "", formatFecha.format(inmovilizacion.getFec_ini_inm()));
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

                        inicio = inmovilizacion.getId_inmovilizacion() + "_video_";
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

                                    Call<ResponseBody> crearMultimediaCall = service.crearmultimedia(idInmovilizacion, archivo.getName(), multimedia, "", formatFecha.format(inmovilizacion.getFec_ini_inm()));
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
                        throw new Exception("Error al enviar inmovilizacion: " + error);
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