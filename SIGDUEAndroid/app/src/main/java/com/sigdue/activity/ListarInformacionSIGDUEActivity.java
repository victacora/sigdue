
package com.sigdue.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sigdue.Constants;
import com.sigdue.R;
import com.sigdue.aplication.AplicacionSIGDUE;
import com.sigdue.asynctask.ActualizarInformacionPredioAsyncTask;
import com.sigdue.asynctask.ActualizarUbiGeoAsyncTask;
import com.sigdue.asynctask.AsyncTaskSIGDUE;
import com.sigdue.asynctask.CargarMultimediaAsyncTask;
import com.sigdue.asynctask.ParametrosAsyncTask;
import com.sigdue.asynctask.ProgressDialogFragment;
import com.sigdue.db.DaoSession;
import com.sigdue.db.Predial;
import com.sigdue.db.PredialDao;
import com.sigdue.db.Usuario;
import com.sigdue.db.UsuarioDao;
import com.sigdue.listadapter.InformacionSIGDUERecyclerView;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;

import java.util.List;

import static com.sigdue.Constants.NOTIFICACIONES_ID;

public class ListarInformacionSIGDUEActivity extends AppCompatActivity implements AsyncTaskSIGDUE, ExecuteTaskSIGDUE {

    private static final String TAG = "ListarInmActivity";
    private AplicacionSIGDUE app;
    private DaoSession daoSession;
    private ParametrosAsyncTask asyncTask = null;
    private ProgressDialogFragment mProgressDialogConsultaInfoSIGDUE = null;
    LinearLayoutManager llm;
    private InformacionSIGDUERecyclerView mAdapter;
    private EjecutarConsultarServiciosAsyncTask mConsultarinformacionSIGDUETask;
    RecyclerView rv;
    private UsuarioDao usuarioDao;
    private PredialDao predialDao;
    private int totalTareas = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {

            UtilidadesGenerales.context = this;
            setContentView(R.layout.listar_informacion_sigdue_activity);
            this.app = (AplicacionSIGDUE) getApplication();
            this.daoSession = this.app.getDaoSession();
            this.usuarioDao = this.daoSession.getUsuarioDao();
            this.predialDao = this.daoSession.getPredialDao();
            this.rv = (RecyclerView) findViewById(R.id.rv);
            this.rv.setHasFixedSize(true);
            this.llm = new LinearLayoutManager(this);
            this.rv.setLayoutManager(this.llm);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ejecutarConsultaServicios(long idUsuario) {
        try {
            showProgress(getString(R.string.ui_listar_informacion));
            mConsultarinformacionSIGDUETask = new EjecutarConsultarServiciosAsyncTask();
            mConsultarinformacionSIGDUETask.execute(idUsuario);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            boolean result = false;
            switch (item.getItemId()) {
                case R.id.menu_inm_sincronizar_maestros:
                    showProgress("Actualizando maestros...");
                    asyncTask = new ParametrosAsyncTask(ListarInformacionSIGDUEActivity.this, daoSession, mProgressDialogConsultaInfoSIGDUE);
                    asyncTask.execute();
                    result = true;
                    break;
                case R.id.menu_inm_sincronizar:
                    showProgress("Publicando información...");
                    Predial predial = null;
                    Usuario usuario = null;

                    List<Predial> infoPredial = predialDao.queryBuilder().where(PredialDao.Properties.Dane_sede.eq(app.getUsuario())).list();
                    if (infoPredial != null && !infoPredial.isEmpty()) {
                        predial = infoPredial.get(0);
                    }

                    List<Usuario> usuarios = usuarioDao.queryBuilder().where(UsuarioDao.Properties.Id_usuario.eq(app.getIdUsuario())).list();
                    if (usuarios != null && usuarios.size() > 0) {
                        usuario = usuarios.get(0);
                    }

                    ActualizarInformacionPredioAsyncTask actualizarInformacionPredioAsyncTask = new ActualizarInformacionPredioAsyncTask(this, mProgressDialogConsultaInfoSIGDUE);
                    CargarMultimediaAsyncTask cargarMultimediaAsyncTask = new CargarMultimediaAsyncTask(this, this, daoSession, mProgressDialogConsultaInfoSIGDUE);
                    ActualizarUbiGeoAsyncTask actualizarUbiGeoAsyncTask = new ActualizarUbiGeoAsyncTask(this, mProgressDialogConsultaInfoSIGDUE);
                    totalTareas = 0;
                    if (predial != null) {
                        totalTareas = 1;
                        actualizarInformacionPredioAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, predial);
                    }
                    if (usuario != null) {
                        totalTareas += 2;
                        cargarMultimediaAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, String.valueOf(usuario.getId_usuario()), usuario.getUsuario());
                        actualizarUbiGeoAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, usuario);
                    }
                    result = true;
                    break;
                case R.id.menu_inm_cerrar:
                    UtilidadesGenerales.escribirSharedPreferences(R.string.pref_usuario_key, "-1", UtilidadesGenerales.STRING_TYPE);
                    app.setIdUsuario(-1);
                    app.setUsuario("");
                    finish();
                    result = true;
                    break;
            }
            result = super.onOptionsItemSelected(item);
            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void onPostExecute(Object result) {
        onConsultarInmovilizacionFinish(null);
    }

    @Override
    public void onCancelled() {
        asyncTask = null;
        hideProgress();
    }

    @Override
    public void onCallActivity(int tarea) {
        Intent intent = new Intent(ListarInformacionSIGDUEActivity.this, AgregarInformacionSIDGDUEActivity.class);
        intent.putExtra(Constants.TASK, tarea);
        if (tarea == AgregarInformacionSIDGDUEActivity.FORMULARIO_ACTUALIZAR_UBICACION) {
            UtilidadesGenerales.habilitarGPS(ListarInformacionSIGDUEActivity.this, intent);
        } else {
            startActivityForResult(intent, NOTIFICACIONES_ID);
        }
    }


    public class EjecutarConsultarServiciosAsyncTask extends AsyncTask<Object, String, List<Usuario>> {

        @Override
        protected List<Usuario> doInBackground(Object... params) {
            try {
                List<Usuario> usuarios = null;
                Long idUsuario = (Long) params[0];
                usuarios = usuarioDao.queryBuilder().where(UsuarioDao.Properties.Id_usuario.eq(idUsuario)).list();
                return usuarios;
            } catch (Exception ex) {
                Log.e(TAG, "ListarinformacionSIGDUE.doInBackground: fallo consultar comparendos");
                Log.e(TAG, "", ex);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {
                if (mProgressDialogConsultaInfoSIGDUE != null)
                    mProgressDialogConsultaInfoSIGDUE.actualizarMensaje(values[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onPostExecute(final List<Usuario> informacionSIGDUE) {
            onConsultarInmovilizacionFinish(informacionSIGDUE);
        }

        @Override
        protected void onCancelled() {
            onConsultarinformacionSIGDUECancel();
        }
    }


    private void onConsultarInmovilizacionFinish(List<Usuario> informacionSIGDUE) {
        this.mConsultarinformacionSIGDUETask = null;
        if (totalTareas == 0 || totalTareas == 1) hideProgress();
        else totalTareas--;
        if (informacionSIGDUE != null) {
            this.mAdapter = new InformacionSIGDUERecyclerView(informacionSIGDUE, daoSession, this, this);
            this.rv.setAdapter(this.mAdapter);
        }
    }

    protected void showProgress(String mensaje) {
        mProgressDialogConsultaInfoSIGDUE = ProgressDialogFragment.newInstance(asyncTask, ListarInformacionSIGDUEActivity.this, mensaje);
        this.mProgressDialogConsultaInfoSIGDUE.show(getSupportFragmentManager(), "dialog_loading");
    }

    private void hideProgress() {
        try {
            if (this.mProgressDialogConsultaInfoSIGDUE != null) {
                this.mProgressDialogConsultaInfoSIGDUE.dismissAllowingStateLoss();
                this.mProgressDialogConsultaInfoSIGDUE = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onConsultarinformacionSIGDUECancel() {
        cancel();
    }

    public void cancel() {
        if (this.mConsultarinformacionSIGDUETask != null) {
            this.mConsultarinformacionSIGDUETask.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            setTitle(getString(R.string.informacion_title));
            if (this.app != null) {
                this.daoSession = this.app.getDaoSession();
            }
            ejecutarConsultaServicios(this.app.getIdUsuario());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

    }


}