package com.sigdue.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sigdue.db.Predial;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.webservice.api.WSSIGDUEClient;
import com.sigdue.webservice.api.WSSIGDUEInterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.sigdue.Constants.SLEEP_PROGRESS_MAESTROS;

public class ActualizarInformacionPredioAsyncTask extends AsyncTask<Predial, String, Boolean> {
    private final AsyncTaskSIGDUE context;
    private final String TAG = "InfoPreAsyncTask";
    private final ProgressDialogFragment progressDialogFragment;

    public ActualizarInformacionPredioAsyncTask(AsyncTaskSIGDUE context, ProgressDialogFragment progressDialogFragment) {
        this.context = context;
        this.progressDialogFragment = progressDialogFragment;
    }


    @Override
    protected Boolean doInBackground(Predial... params) {
        try {
            WSSIGDUEInterface service = WSSIGDUEClient.getClient();
            Predial predial = params[0];
            if (UtilidadesGenerales.isOnline()) {
                com.sigdue.webservice.modelo.Predial predialJSON = new com.sigdue.webservice.modelo.Predial();
                predialJSON.setP_DANE_SEDE(predial.getDane_sede() != null ? predial.getDane_sede() : "");
                predialJSON.setP_COD_PREDIO(predial.getCod_predio() != null ? predial.getCod_predio() : "");
                predialJSON.setP_CLIMA(predial.getClima() != null ? predial.getClima() : "");
                predialJSON.setP_DISTANCIA_MTS_SEDE_PPAL(predial.getDistancia_mts_sede_ppal() != null ? predial.getDistancia_mts_sede_ppal() : "");
                predialJSON.setP_DIST_KM_CENTRO_POBLADO(predial.getDist_km_centro_poblado() != null ? predial.getDist_km_centro_poblado() : "");
                predialJSON.setP_CLASE_PREDIO(predial.getClase_predio() != null ? predial.getClase_predio() : "");
                predialJSON.setP_AVALUO_CATASTRAL(predial.getAvaluo_catastral() != null ? predial.getAvaluo_catastral() : "");
                predialJSON.setP_FEC_AVALUO_CATASTRAL(predial.getFec_avaluo_catastral() != null ? predial.getFec_avaluo_catastral() : "");
                predialJSON.setP_AVALUO_COMERCIAL(predial.getAvaluo_comercial() != null ? predial.getAvaluo_comercial() : "");
                predialJSON.setP_FEC_AVALUO_COMERCIAL(predial.getFec_avaluo_comercial() != null ? predial.getFec_avaluo_comercial() : "");
                predialJSON.setP_ZONA_AISLAMIENTO(predial.getZona_aislamiento() != null ? predial.getZona_aislamiento() : "");
                predialJSON.setP_ZONA_ALTO_RIESGO(predial.getZona_alto_riesgo() != null ? predial.getZona_alto_riesgo() : "");
                predialJSON.setP_ZONA_PROTECCION(predial.getZona_proteccion() != null ? predial.getZona_proteccion() : "");
                predialJSON.setP_TOPOGRAFIA(predial.getTopografia() != null ? predial.getTopografia() : "");
                predialJSON.setP_PROPIEDAD_LOTE(predial.getPropiedad_lote() != null ? predial.getPropiedad_lote() : "");
                predialJSON.setP_TIPO_DOCUMENTO(predial.getTipo_documento() != null ? predial.getTipo_documento() : "");
                predialJSON.setP_CUAL_TIPO_DOCUMENTO(predial.getCual_tipo_documento() != null ? predial.getCual_tipo_documento() : "");
                predialJSON.setP_NRO_DOCUMENTO_LEGALIZACION(predial.getNro_documento_legalizacion() != null ? predial.getNro_documento_legalizacion() : "");
                predialJSON.setP_FEC_EXPEDICION(predial.getFec_expedicion() != null ? predial.getFec_expedicion() : "");
                predialJSON.setP_NOTARIA_DEPENDENCIA_ORIGEN(predial.getNotaria_dependencia_origen() != null ? predial.getNotaria_dependencia_origen() : "");
                predialJSON.setP_LUGAR_EXPEDICION(predial.getLugar_expedicion() != null ? predial.getLugar_expedicion() : "");
                predialJSON.setP_REGISTRO_CATASTRAL(predial.getRegistro_catastral() != null ? predial.getRegistro_catastral() : "");
                predialJSON.setP_MATRICULA_INMOBILIARIA(predial.getMatricula_inmobiliaria() != null ? predial.getMatricula_inmobiliaria() : "");
                predialJSON.setP_PROPIETARIOS(predial.getPropietarios() != null ? predial.getPropietarios() : "");
                predialJSON.setP_TENENCIA(predial.getTenencia() != null ? predial.getTenencia() : "");
                predialJSON.setP_CON_QUIEN_TENENCIA(predial.getCon_quien_tenencia() != null ? predial.getCon_quien_tenencia() : "");
                predialJSON.setP_NOM_QUIEN_TENENCIA(predial.getNom_quien_tenencia() != null ? predial.getNom_quien_tenencia() : "");
                predialJSON.setP_FECHA_TENENCIA_LOTE(predial.getFecha_tenencia_lote() != null ? predial.getFecha_tenencia_lote() : "");

                Call<ResponseBody> serviceCall = service.crearPredial(predialJSON);
                Response<ResponseBody> response = serviceCall.execute();
                if (response != null && response.isSuccessful()) {
                    publishProgress("Información actualizada correctamente.");
                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                    return true;
                }
                return false;
            } else {
                publishProgress("No existe conexión a internet.");
            }

            return true;
        } catch (Exception ex) {
            Log.e(TAG, "InfoPredioAsyncTask.doInBackground: fallo al actualizar informacion predio");
            Log.e(TAG, "", ex);
            publishProgress("Error general al ejecutar la aplicacion. ");
            return false;
        }
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        try {
            progressDialogFragment.actualizarMensaje(values[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(final Boolean resultado) {
        context.onPostExecute(resultado);
    }

    @Override
    protected void onCancelled() {
        context.onCancelled();
    }
}

