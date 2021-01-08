package com.sigdue.asynctask;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sigdue.db.Archivo;
import com.sigdue.db.ArchivoDao;
import com.sigdue.db.DaoSession;
import com.sigdue.db.Predial;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.webservice.api.WSSIGDUEClient;
import com.sigdue.webservice.api.WSSIGDUEInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.sigdue.Constants.SLEEP_PROGRESS_MAESTROS;

public class CargarMultimediaAsyncTask extends AsyncTask<String, String, Boolean> {
    private final AsyncTaskSIGDUE context;
    private final String TAG = "CarMultiAsyncTask";
    private final ProgressDialogFragment progressDialogFragment;
    private final DaoSession daoSession;
    private final ContentResolver contentResolver;

    public CargarMultimediaAsyncTask(AsyncTaskSIGDUE context, ContentResolver contentResolver, DaoSession daoSession, ProgressDialogFragment progressDialogFragment) {
        this.context = context;
        this.progressDialogFragment = progressDialogFragment;
        this.daoSession = daoSession;
        this.contentResolver = contentResolver;
    }


    @Override
    protected Boolean doInBackground(String... params) {
        try {
            WSSIGDUEInterface service = WSSIGDUEClient.getClient();
            String userId = params[0];
            String dane = params[1];
            if (UtilidadesGenerales.isOnline()) {
                ArchivoDao archivoDao = daoSession.getArchivoDao();
                List<Archivo> archivos = archivoDao.queryBuilder().where(ArchivoDao.Properties.Estado.eq("P"), ArchivoDao.Properties.Id_usuario.eq(userId)).list();
                for (int i = 0; archivos != null && i < archivos.size(); i++) {
                    Archivo archivo = archivos.get(i);
                    Uri uri = Uri.parse(archivo.getRuta());
                    InputStream iStream = contentResolver.openInputStream(uri);
                    byte[] body = archivo.getTipo().equals("Video") ? archivo.getNombre().getBytes() : getBytes(iStream);
                    if (body != null && body.length > 0) {
                        String mimeType = archivo.getMedia_type();
                        MediaType mediaType = null;
                        if (mimeType.equals("")) {
                            mediaType = MediaType.parse(mimeType);
                        }
                        RequestBody requestBody = RequestBody.create(mediaType, body);
                        MultipartBody.Part multimedia = okhttp3.MultipartBody.Part.createFormData("body", archivo.getNombre(), requestBody);
                        Call<ResponseBody> serviceCall = service.crearMultimedia(dane, archivo.getNombre(), archivo.getDescripcion(), "", archivo.getTipo(), multimedia, archivo.getMedia_type());
                        Response<ResponseBody> response = serviceCall.execute();
                        if (response != null && response.isSuccessful()) {
                            publishProgress("Archivo: " + archivo.getNombre() + " Tipo: " + archivo.getTipo() + "publicado correctamente.");
                            archivo.setEstado("G");
                            archivoDao.update(archivo);
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                        }
                    }
                    archivos = archivoDao.queryBuilder().where(ArchivoDao.Properties.Estado.eq("P"), ArchivoDao.Properties.Id_usuario.eq(userId)).list();
                    return archivos != null && archivos.isEmpty();
                }
                return false;
            } else {
                publishProgress("No existe conexión a internet.");
            }

            return true;
        } catch (Exception ex) {
            Log.e(TAG, "CarMultiAsyncTask.doInBackground: fallo el cargue de archivos multimedia");
            Log.e(TAG, "", ex);
            publishProgress("Error general al ejecutar la aplicacion. ");
            return false;
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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

