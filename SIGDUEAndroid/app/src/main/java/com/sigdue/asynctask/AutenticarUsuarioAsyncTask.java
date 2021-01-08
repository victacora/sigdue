package com.sigdue.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sigdue.db.DaoSession;
import com.sigdue.db.Usuario;
import com.sigdue.db.UsuarioDao;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.webservice.api.WSSIGDUEClient;
import com.sigdue.webservice.api.WSSIGDUEInterface;

import java.util.List;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.sigdue.Constants.SLEEP_PROGRESS_MAESTROS;

public class AutenticarUsuarioAsyncTask extends AsyncTask<String, String, Usuario> {
    private final AsyncTaskSIGDUE context;
    private final String TAG = "AutenAsyncTask";
    private final ProgressDialogFragment progressDialogFragment;
    private UsuarioDao usuarioDao;

    public AutenticarUsuarioAsyncTask(AsyncTaskSIGDUE context, DaoSession daoSession, ProgressDialogFragment progressDialogFragment) {
        this.context = context;
        this.progressDialogFragment = progressDialogFragment;
        this.usuarioDao = daoSession.getUsuarioDao();
    }


    @Override
    protected Usuario doInBackground(String... params) {
        try {
            Usuario resultadoAutenticacion = null;
            WSSIGDUEInterface service = WSSIGDUEClient.getClient();
            String usuario = (String) params[0];
            String contrasena = (String) params[1];
            if (UtilidadesGenerales.isOnline()) {
                Call<ResponseBody> loginUsuariosCall = service.loginUsuarios(new com.sigdue.webservice.modelo.Usuario(usuario, contrasena));
                Response<ResponseBody> response = loginUsuariosCall.execute();
                if (response != null && response.isSuccessful()) {
                    Headers headers = response.headers();
                    String valor = headers != null && headers.size() > 0 ? headers.get("r") : null;
                    if (valor != null && valor.equals(Boolean.TRUE.toString().toUpperCase())) {
                        Usuario u = new Usuario();
                        u.setNombre_municipio(getHeaderParametro(headers, "p_nom_municipio"));
                        u.setNombre_establecimiento(getHeaderParametro(headers, "p_nom_establecimiento"));
                        u.setRector_establecimiento(getHeaderParametro(headers, "p_rec_establecimiento"));
                        u.setLatitude(getHeaderParametro(headers, "p_latitud"));
                        u.setLongitude(getHeaderParametro(headers, "p_longitud"));
                        u.setNombre_sede(getHeaderParametro(headers, "p_nom_sede"));
                        u.setZona_sede(getHeaderParametro(headers, "p_zon_sede"));
                        u.setEst_sede(getHeaderParametro(headers, "p_est_sede"));
                        u.setUsuario(usuario);
                        u.setContrasena(contrasena);
                        resultadoAutenticacion = u;
                        List<Usuario> usuarios = usuarioDao.queryBuilder().where(UsuarioDao.Properties.Usuario.eq(usuario)).list();
                        List<Usuario> id = usuarioDao.queryBuilder().orderDesc(UsuarioDao.Properties.Id_usuario).list();
                        if (usuarios != null && usuarios.isEmpty()) {
                            if (id != null && !id.isEmpty())
                                u.setId_usuario(id.get(0).getId_usuario() + 1);
                            else u.setId_usuario(1);
                            usuarioDao.insert(u);
                        } else if (usuarios != null && !usuarios.isEmpty()) {
                            u.setId_usuario(usuarios.get(0).getId_usuario());
                            if(u.getLatitude().equals("Sin Datos"))u.setLatitude(usuarios.get(0).getLatitude());
                            if(u.getLongitude().equals("Sin Datos"))u.setLongitude(usuarios.get(0).getLongitude());
                            usuarioDao.update(u);
                        }
                        publishProgress("Usuario autenticado remotamente.");
                        Thread.sleep(SLEEP_PROGRESS_MAESTROS);

                    } else {
                        publishProgress("No fue posible autenticar el usuario remotamente.");
                        Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                        resultadoAutenticacion = buscarUsuarioLocalmente(resultadoAutenticacion, usuario, contrasena);
                    }
                } else {
                    publishProgress("No fue posible autenticar el usuario remotamente.");
                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                    resultadoAutenticacion = buscarUsuarioLocalmente(resultadoAutenticacion, usuario, contrasena);
                }
            } else {
                resultadoAutenticacion = buscarUsuarioLocalmente(resultadoAutenticacion, usuario, contrasena);
            }

            return resultadoAutenticacion;
        } catch (Exception ex) {
            Log.e(TAG, "AutenticarUsuarioAsyncTask.doInBackground: fallo al autenticar usuario");
            Log.e(TAG, "", ex);
            publishProgress("Error general al ejecutar la aplicacion. ");
            return null;
        }
    }

    private String getHeaderParametro(Headers headers, String parametro) {
        String valor = headers != null && headers.size() > 0 ? headers.get(parametro) : "-";
        return valor;
    }

    private Usuario buscarUsuarioLocalmente(Usuario resultadoAutenticacion, String usuario, String contrasena) throws InterruptedException {
        List<Usuario> autenticarUsuario = usuarioDao.queryBuilder().where(UsuarioDao.Properties.Usuario.eq(usuario), UsuarioDao.Properties.Contrasena.eq(contrasena)).list();
        if (autenticarUsuario != null && autenticarUsuario.size() > 0) {
            resultadoAutenticacion = autenticarUsuario.get(0);
            publishProgress("Usuario autenticado localmente.");
            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
        } else {
            publishProgress("No fue posible autenticar el usuario localmente");
            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
        }
        return resultadoAutenticacion;
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
    protected void onPostExecute(final Usuario usuario) {
        context.onPostExecute(usuario);
    }

    @Override
    protected void onCancelled() {
        context.onCancelled();
    }
}

