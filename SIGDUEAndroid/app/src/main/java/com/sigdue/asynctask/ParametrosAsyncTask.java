package com.sigdue.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sigdue.activity.LoginActivity;
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
    ProgressDialogFragment mProgressDialog;

    public ParametrosAsyncTask(AsyncTaskSIGDUE context) {
        this.context = context;
    }


    @Override
    protected Void doInBackground(Integer... params) {
        try {
            Usuario resultadoAutenticacion = null;
            WSSIGDUEInterface service = WSSIGDUEClient.getClient();

            if (UtilidadesGenerales.isOnline()) {
                    /*Call<WSSIGDUEResult<Departamento>> listarDepartamentosCall = service.listarDepartamentos();
                    Response<WSSIGDUEResult<Departamento>> responseDepartamentos = listarDepartamentosCall.execute();
                    if (responseDepartamentos != null && responseDepartamentos.isSuccessful()) {
                        WSSIGDUEResult<Departamento> result = responseDepartamentos.body();
                        List<Departamento> departamentos = result.getItems();
                        if (departamentos != null && departamentos.size() > 0) {
                            publishProgress("Actualizando departamentos. N° registros: " + departamentos.size());
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            departamentosDao.deleteAll();
                            departamentosDao.insertInTx(departamentos);
                        } else {
                            publishProgress("No se encontraron registros en departamentos.");
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                        }
                    } else {
                        publishProgress("Error al consultar departamentos.");
                        Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                    }

                    */
            } else {
                publishProgress("No fue posible la actualizacion de maestros, verifique su conexion a internet e intente nuevamente.");
                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
            }
        } catch (Exception ex) {
            Log.e(TAG, "AutenticarUsuarioAsyncTask.doInBackground: fallo al autenticar usuario");
            Log.e(TAG, "", ex);
            publishProgress("Error general al ejecutar la aplicacion. ");
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        try {

            mProgressDialog.actualizarMensaje(values[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onCancelled() {


    }
}

