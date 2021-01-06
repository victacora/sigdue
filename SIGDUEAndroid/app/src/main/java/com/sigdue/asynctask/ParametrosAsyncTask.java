package com.sigdue.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sigdue.Constants;
import com.sigdue.activity.LoginActivity;
import com.sigdue.db.DaoSession;
import com.sigdue.db.Parametro;
import com.sigdue.db.ParametroDao;
import com.sigdue.db.Usuario;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.webservice.api.WSSIGDUEClient;
import com.sigdue.webservice.api.WSSIGDUEInterface;
import com.sigdue.webservice.modelo.WSSIGDUEResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.sigdue.Constants.SLEEP_PROGRESS_MAESTROS;

public class ParametrosAsyncTask extends AsyncTask<Integer, String, Void> {
    private final String TAG = "ParametrosAsyncTask";
    private final AsyncTaskSIGDUE context;
    private final ParametroDao parametroDao;
    private final ProgressDialogFragment progressDialogFragment;

    public ParametrosAsyncTask(AsyncTaskSIGDUE context, DaoSession daoSession, ProgressDialogFragment progressDialogFragment) {
        this.context = context;
        this.progressDialogFragment = progressDialogFragment;
        this.parametroDao = daoSession.getParametroDao();
    }


    @Override
    protected Void doInBackground(Integer... params) {
        try {
            WSSIGDUEInterface service = WSSIGDUEClient.getClient();
            if (UtilidadesGenerales.isOnline()) {
                parametroDao.deleteAll();
                Integer idParametro = 1;
                for (Integer tipoParametro = 1; tipoParametro <= 12; tipoParametro++) {
                    Call<WSSIGDUEResult> wssigdueResultCall = getWssigdueResultCall(tipoParametro, service);
                    Response<WSSIGDUEResult> response = wssigdueResultCall.execute();
                    if (response != null && response.isSuccessful()) {
                        WSSIGDUEResult result = response.body();
                        List<Parametro> parametros = result.getItems();
                        if (parametros != null && parametros.size() > 0) {
                            setTipoParametro(idParametro, tipoParametro, parametros);
                            idParametro = idParametro + parametros.size();
                            publishProgress("Actualizando " + Constants.tiposParametros.get(tipoParametro) + ". N° registros: " + parametros.size());
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            parametroDao.insertInTx(parametros);
                        } else {
                            publishProgress("No se encontraron registros en " + Constants.tiposParametros.get(tipoParametro) + ".");
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                        }
                    } else {
                        publishProgress("Error al consultar " + Constants.tiposParametros.get(tipoParametro) + ".");
                        Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                    }
                }
                publishProgress("Finalizo la descarga de maestros.");
                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
            } else {
                publishProgress("No fue posible la actualizacion de maestros, verifique su conexion a internet e intente nuevamente.");
                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
            }
        } catch (Exception ex) {
            Log.e(TAG, "ParametrosAsyncTask.doInBackground: fallo al consultar parametros");
            Log.e(TAG, "", ex);
            publishProgress("Error general al ejecutar la aplicacion. ");
        }
        return null;
    }

    private Call<WSSIGDUEResult> getWssigdueResultCall(int tipoParametro, WSSIGDUEInterface service) {
        if (tipoParametro == 1) return service.listarClasePredio();
        else if (tipoParametro == 2) return service.listarClima();
        else if (tipoParametro == 3) return service.listarConQuienTieneTenencia();
        else if (tipoParametro == 4) return service.listarDistanciaSedePrincipal();
        else if (tipoParametro == 5) return service.listarDistanciaCentroPoblado();
        else if (tipoParametro == 6) return service.listarPropiedadLote();
        else if (tipoParametro == 7) return service.listarTenencia();
        else if (tipoParametro == 8) return service.listarTipoDocumento();
        else if (tipoParametro == 9) return service.listarTopografia();
        else if (tipoParametro == 10) return service.listarZonaAislamiento();
        else if (tipoParametro == 11) return service.listarZonaRiesgo();
        else return service.listarZonaProteccion();
    }

    private void setTipoParametro(Integer idParametro, Integer tipoParametro, List<Parametro> parametros) {
        for (int p = 0; p < parametros.size(); p++) {
            parametros.get(p).setId_parametro(idParametro);
            parametros.get(p).setTipo(tipoParametro);
            idParametro++;
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context.onPostExecute(null);
    }

    @Override
    protected void onCancelled() {
        context.onCancelled();
    }
}

