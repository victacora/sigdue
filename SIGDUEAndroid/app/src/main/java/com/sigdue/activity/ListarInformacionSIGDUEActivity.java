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

package com.sigdue.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sigdue.Constants;
import com.sigdue.R;
import com.sigdue.aplication.AplicacionSIGDUE;
import com.sigdue.asynctask.AsyncTaskSIGDUE;
import com.sigdue.asynctask.ParametrosAsyncTask;
import com.sigdue.asynctask.ProgressDialogFragment;
import com.sigdue.db.DaoSession;
import com.sigdue.db.ParametroDao;
import com.sigdue.db.Predial;
import com.sigdue.db.PredialDao;
import com.sigdue.listadapter.InformacionSIGDUERecyclerView;
import com.sigdue.service.EnviarInformacionSIGDUEService;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import static com.sigdue.Constants.NOTIFICACIONES_ID;
import static com.sigdue.Constants.RESPUESTA_SERVICIO;

public class ListarInformacionSIGDUEActivity extends AppCompatActivity implements AsyncTaskSIGDUE {

    private static final String TAG = "ListarInmActivity";
    private AplicacionSIGDUE app;
    private FloatingActionButton btnAgregarComparendo;
    private DaoSession daoSession;
    private ParametrosAsyncTask asyncTask = null;
    private ProgressDialogFragment mProgressDialogConsultaInfoSIGDUE = null;
    LinearLayoutManager llm;
    private InformacionSIGDUERecyclerView mAdapter;
    private EjecutarConsultarServiciosAsyncTask mConsultarinformacionSIGDUETask;
    RecyclerView rv;
    private PredialDao predialDao;
    private ParametroDao parametroDao;

    private BroadcastReceiver mHabilitarGPS = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                habilitarGPS();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private BroadcastReceiver mActualizarListaComparendosBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String result = intent.getStringExtra("resultado");
                if (result != null && !result.equals("")) {
                    if (app != null) {
                        daoSession = app.getDaoSession();
                    }
                    ejecutarConsultaServicios(0, "");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {

            UtilidadesGenerales.context = this;
            setContentView(R.layout.listar_informacion_sigdue_activity);
            this.app = (AplicacionSIGDUE) getApplication();
            this.daoSession = this.app.getDaoSession();
            this.predialDao = this.daoSession.getPredialDao();
            this.parametroDao = this.daoSession.getParametroDao();

            this.rv = (RecyclerView) findViewById(R.id.rv);
            this.rv.setHasFixedSize(true);
            this.llm = new LinearLayoutManager(this);
            this.rv.setLayoutManager(this.llm);
            Intent intent = getIntent();

            if (Intent.ACTION_SEARCH.equals(intent.getAction()) && !intent.hasExtra("MainSearchResults") && !intent.hasExtra("query")) {
                onSearchRequested();
            }

            btnAgregarComparendo = (FloatingActionButton) findViewById(R.id.agregarComparendo);
            btnAgregarComparendo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (validarParametrizacion().equals("")) {
                        ListarInformacionSIGDUEActivity.this.startActivityForResult(new Intent(ListarInformacionSIGDUEActivity.this, AgregarInformacionSIDGDUEActivity.class), NOTIFICACIONES_ID);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void configurarBuscador(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.menu_buscar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                realizarBusqueda(query);
                return true;
            }

            public boolean onQueryTextChange(final String s) {
                return false;
            }
        });
    }

    public void realizarBusqueda(String consulta) {
        try {
            ejecutarConsultaServicios(3, consulta);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ejecutarConsultaServicios(int parametro, String consulta) {
        String mensaje;
        try {
            mensaje = parametro == 4 ? "Actualizando maestros..." : getString(R.string.ui_listar_informacion);
            showProgress(mensaje);
            mConsultarinformacionSIGDUETask = new EjecutarConsultarServiciosAsyncTask();
            mConsultarinformacionSIGDUETask.execute(parametro, consulta);
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
        configurarBuscador(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            boolean result = false;
            switch (item.getItemId()) {
                case R.id.menu_buscar:
                    onSearchRequested();
                    return true;
                case R.id.menu_inm_sort_date_asc:
                    ejecutarConsultaServicios(1, "");
                    result = true;
                    break;
                case R.id.menu_inm_sort_date_desc:
                    ejecutarConsultaServicios(2, "");
                    result = true;
                    break;
                case R.id.menu_inm_sincronizar_maestros:
                    showProgress("Actualizando maestros...");
                    asyncTask = new ParametrosAsyncTask(ListarInformacionSIGDUEActivity.this, daoSession, mProgressDialogConsultaInfoSIGDUE);
                    asyncTask.execute();
                    result = true;
                    break;
                case R.id.menu_inm_sincronizar:
                    try {
                        if (UtilidadesGenerales.isOnline()) {
                            startService(new Intent(this, EnviarInformacionSIGDUEService.class));
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ListarInformacionSIGDUEActivity.this);
                            builder.setTitle("Información");
                            builder.setMessage("Revise su conexiòn a internet. No se pudo realizar el envio de la inmovilizacion, intente nuevamente de forma manual.");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
        onConsultarInmovilizacionFinish(null, 4);
    }

    @Override
    public void onCancelled() {
        asyncTask = null;
        hideProgress();
    }


    public class EjecutarConsultarServiciosAsyncTask extends AsyncTask<Object, String, List<Predial>> {
        private int opcion = -1;

        @Override
        protected List<Predial> doInBackground(Object... params) {
            try {
                List<Predial> prediales = null;
                opcion = (int) params[0];
                String consulta = (String) params[1];
                switch (opcion) {
                    case 0://listar todos los prediales
                        prediales = predialDao.queryBuilder().orderDesc(PredialDao.Properties.Id_predial).list();
                        break;
                    case 1:
                        //ordenar ascendentemente por id
                        prediales = predialDao.queryBuilder().orderAsc(PredialDao.Properties.Id_predial).list();
                        break;
                    case 2:
                        //ordenar descendentemente por id
                        prediales = predialDao.queryBuilder().orderDesc(PredialDao.Properties.Id_predial).list();
                        break;
                    case 3:
                        //aplicar filtro
                        prediales = predialDao.queryBuilder().where(PredialDao.Properties.Dane_sede.like("%"+consulta+"%")).list();
                        break;
                }
                return prediales;
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
        protected void onPostExecute(final List<Predial> informacionSIGDUE) {
            onConsultarInmovilizacionFinish(informacionSIGDUE, opcion);
        }

        @Override
        protected void onCancelled() {
            onConsultarinformacionSIGDUECancel();
        }
    }


    private void onConsultarInmovilizacionFinish(List<Predial> informacionSIGDUE, int opcion) {
        this.mConsultarinformacionSIGDUETask = null;
        hideProgress();
        if (opcion != 4) {
            this.mAdapter = new InformacionSIGDUERecyclerView(informacionSIGDUE,daoSession, this);
            this.rv.setAdapter(this.mAdapter);
        }
        AlertDialog.Builder builder;
        if ((informacionSIGDUE == null || (informacionSIGDUE != null && informacionSIGDUE.size() == 0)) && opcion == 3) {
            builder = new AlertDialog.Builder(ListarInformacionSIGDUEActivity.this);
            builder.setTitle("Información");
            builder.setMessage("No se encontraron registros.");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (informacionSIGDUE != null && informacionSIGDUE.size() > 0 && opcion == 3) {
            builder = new AlertDialog.Builder(ListarInformacionSIGDUEActivity.this);
            builder.setTitle("Información");
            builder.setMessage("Se encontraron " + informacionSIGDUE.size() + " registros.");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTIFICACIONES_ID && resultCode == RESULT_OK) {
            try {
                ejecutarConsultaServicios(0, "");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public String validarParametrizacion() {
        String parametrosComparendos = "";
        try {
            for (Integer tipoParametro = 1; tipoParametro <= 12; tipoParametro++) {
                if (this.parametroDao.queryBuilder().where(ParametroDao.Properties.Tipo.eq(tipoParametro)).count() == 0) {
                    parametrosComparendos = parametrosComparendos + "-No existen " + Constants.tiposParametros.get(tipoParametro) + " registrados.\n";
                }
            }
            if (!parametrosComparendos.equals("")) {
                parametrosComparendos = parametrosComparendos + "\nEjecute la opci\u00f3n: sincronizar maestros.";
            }
            if (!parametrosComparendos.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListarInformacionSIGDUEActivity.this);
                builder.setTitle("Información");
                builder.setMessage(parametrosComparendos);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return parametrosComparendos;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            setTitle(getString(R.string.informacion_title));
            if (this.app != null) {
                this.daoSession = this.app.getDaoSession();
            }
            ejecutarConsultaServicios(0, "");
            IntentFilter filter = new IntentFilter();
            filter.addAction(RESPUESTA_SERVICIO);
            registerReceiver(mActualizarListaComparendosBroadcastReceiver, filter);

            filter = new IntentFilter();
            filter.addAction("android.location.PROVIDERS_CHANGED");
            registerReceiver(mHabilitarGPS, filter);

            habilitarGPS();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mHabilitarGPS);
        unregisterReceiver(mActualizarListaComparendosBroadcastReceiver);
    }


    public void habilitarGPS() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("Este aplicación requiere el uso de GPS por favor habilitelo para continuar.")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}