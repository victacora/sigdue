package com.sigdue.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sigdue.db.Usuario;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.webservice.api.WSSIGDUEClient;
import com.sigdue.webservice.api.WSSIGDUEInterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.sigdue.Constants.SLEEP_PROGRESS_MAESTROS;

public class ActualizarUbiGeoAsyncTask extends AsyncTask<Usuario, String, Boolean> {
    private final AsyncTaskSIGDUE context;
    private final String TAG = "UbiGeoAsyncTask";
    private final ProgressDialogFragment progressDialogFragment;

    public ActualizarUbiGeoAsyncTask(AsyncTaskSIGDUE context, ProgressDialogFragment progressDialogFragment) {
        this.context = context;
        this.progressDialogFragment = progressDialogFragment;
    }


    @Override
    protected Boolean doInBackground(Usuario... params) {
        try {
            WSSIGDUEInterface service = WSSIGDUEClient.getClient();
            Usuario usuario = params[0];
            if (UtilidadesGenerales.isOnline()) {
                Call<ResponseBody> serviceCall = service.actualizarUbiGeo(new com.sigdue.webservice.modelo.UbiGeo(usuario.getUsuario(), usuario.getLatitude(),usuario.getLongitude()));
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
            Log.e(TAG, "UbiGeoAsyncTask.doInBackground: fallo al actualizar ubicacion");
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

