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

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v4.app.DialogFragment;
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

import com.sigdue.aplication.AplicacionSIGDUE;
import com.sigdue.db.ClaseVehiculo;
import com.sigdue.db.ClaseVehiculoDao;
import com.sigdue.db.Color;
import com.sigdue.db.ColorDao;
import com.sigdue.db.Departamento;
import com.sigdue.db.DepartamentoDao;
import com.sigdue.db.Grua;
import com.sigdue.db.GruaDao;
import com.sigdue.db.Infraccion;
import com.sigdue.db.InfraccionDao;
import com.sigdue.db.Inmovilizacion;
import com.sigdue.db.InmovilizacionDao;
import com.sigdue.db.Municipio;
import com.sigdue.db.MunicipioDao;
import com.sigdue.db.Parqueadero;
import com.sigdue.db.ParqueaderoDao;
import com.sigdue.db.Persona;
import com.sigdue.db.PersonaDao;
import com.sigdue.db.TipoIdentificacion;
import com.sigdue.db.TipoIdentificacionDao;
import com.sigdue.db.TipoServicio;
import com.sigdue.db.TipoServicioDao;
import com.sigdue.db.Vehiculo;
import com.sigdue.db.VehiculoDao;
import com.sigdue.db.Zonas;
import com.sigdue.db.ZonasDao;
import com.sigdue.listadapter.InformacionSIGDUERecyclerView;
import com.sigdue.service.EnviarInformacionSIGDUEService;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.db.DaoSession;
import com.sigdue.webservice.api.WSGruparClient;
import com.sigdue.webservice.api.WSGruparInterface;
import com.sigdue.webservice.modelo.WSGruparResult;
import com.sigdue.R;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.sigdue.Constants.NOTIFICACIONES_ID;
import static com.sigdue.Constants.RESPUESTA_SERVICIO;
import static com.sigdue.Constants.SLEEP_PROGRESS_MAESTROS;

public class ListarInformacionSIGDUEActivity extends AppCompatActivity {

    private static final String TAG = "ListarInmActivity";
    private AplicacionSIGDUE app;
    private FloatingActionButton btnAgregarComparendo;
    private ClaseVehiculoDao claseVehiculoDao;
    private ColorDao coloresDao;
    private DaoSession daoSession;
    private DepartamentoDao departamentosDao;
    private ProgressDialog dialogoBuscando;
    private GruaDao gruasDao;
    private InfraccionDao infraccionDao;
    private InmovilizacionDao inmovilizacionDao;
    LinearLayoutManager llm;
    private InformacionSIGDUERecyclerView mAdapter;
    private EjecutarConsultarServiciosAsyncTask mConsultarInmovilizacionesTask;
    private ProgressDialogFragment mProgressDialog;
    private MunicipioDao municipioDao;
    private ParqueaderoDao parqueaderoDao;
    private PersonaDao personasDao;
    RecyclerView rv;
    private TipoIdentificacionDao tipoIdentificacionDao;
    private TipoServicioDao tipoServicioDao;
    private VehiculoDao vehiculoDao;
    private ZonasDao zonasDao;

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
            this.inmovilizacionDao = this.daoSession.getInmovilizacionDao();
            this.departamentosDao = this.daoSession.getDepartamentoDao();
            this.municipioDao = this.daoSession.getMunicipioDao();
            this.infraccionDao = this.daoSession.getInfraccionDao();
            this.parqueaderoDao = this.daoSession.getParqueaderoDao();
            this.zonasDao = this.daoSession.getZonasDao();
            this.tipoIdentificacionDao = this.daoSession.getTipoIdentificacionDao();
            this.gruasDao = this.daoSession.getGruaDao();
            this.coloresDao = this.daoSession.getColorDao();
            this.tipoServicioDao = this.daoSession.getTipoServicioDao();
            this.claseVehiculoDao = this.daoSession.getClaseVehiculoDao();
            this.personasDao = this.daoSession.getPersonaDao();
            this.vehiculoDao = this.daoSession.getVehiculoDao();
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
                    //if (ListarInmovilizacionesActivity.this.validarParametrizacion().equals("")) {
                        ListarInformacionSIGDUEActivity.this.startActivityForResult(new Intent(ListarInformacionSIGDUEActivity.this, AgregarInformacionSIDGDUEActivity.class), NOTIFICACIONES_ID);
                    //}
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
            mensaje = parametro == 4 ? "Actualizando maestros..." : getString(R.string.ui_listar_inmovilizaciones);
            showProgress(mensaje);
            mConsultarInmovilizacionesTask = new EjecutarConsultarServiciosAsyncTask();
            mConsultarInmovilizacionesTask.execute(parametro, consulta);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mActualizarListaComparendosBroadcastReceiver);
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
                    ejecutarConsultaServicios(4, "");
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


    public class EjecutarConsultarServiciosAsyncTask extends AsyncTask<Object, String, List<Inmovilizacion>> {
        private int opcion = -1;

        @Override
        protected List<Inmovilizacion> doInBackground(Object... params) {
            try {
                List<Inmovilizacion> inmovilizaciones = null;
                opcion = (int) params[0];
                String consulta = (String) params[1];
                WSGruparInterface service = WSGruparClient.getClient();
                switch (opcion) {
                    case 0://listar todos los inmovilizaciones
                        inmovilizaciones = inmovilizacionDao.queryBuilder().orderDesc(InmovilizacionDao.Properties.Fec_ini_inm).list();
                        break;
                    case 1:
                        //ordenar ascendentemente por fecha
                        inmovilizaciones = inmovilizacionDao.queryBuilder().orderAsc(InmovilizacionDao.Properties.Fec_ini_inm).list();
                        break;
                    case 2:
                        //ordenar descendentemente por fecha
                        inmovilizaciones = inmovilizacionDao.queryBuilder().orderDesc(InmovilizacionDao.Properties.Fec_ini_inm).list();
                        break;
                    case 3:
                        //aplicar filtro
                        QueryBuilder qb = inmovilizacionDao.queryBuilder();
                        QueryBuilder.LOG_SQL = true;
                        if (UtilidadesGenerales.isOnline()) {
                            Response<WSGruparResult<com.sigdue.webservice.modelo.Inmovilizacion>> responseInmovilizaciones = service.consultarinmovilizaciones(consulta.replaceAll("/", "-")).execute();
                            if (responseInmovilizaciones != null && responseInmovilizaciones.isSuccessful()) {
                                List<com.sigdue.webservice.modelo.Inmovilizacion> inmovilizacionesServicio = ((WSGruparResult) responseInmovilizaciones.body()).getItems();
                                if (inmovilizacionesServicio != null && inmovilizacionesServicio.size() > 0) {
                                    SimpleDateFormat formatFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    inmovilizaciones = new ArrayList();
                                    try {
                                        for (com.sigdue.webservice.modelo.Inmovilizacion inm : inmovilizacionesServicio) {
                                            Inmovilizacion inmovilizacion = new Inmovilizacion();
                                            inmovilizacion.__setDaoSession(ListarInformacionSIGDUEActivity.this.daoSession);
                                            inmovilizacion.setNo_comparendo(inm.getNo_comparendo());
                                            if (!(inm.getFec_ini_inm() == null || inm.getFec_ini_inm().equals(""))) {
                                                inmovilizacion.setFec_ini_inm(formatFechaHora.parse(inm.getFec_ini_inm()));
                                            }
                                            inmovilizacion.setId_usuario(inm.getId_usuario());
                                            inmovilizacion.setId_agente(inm.getId_agente());
                                            inmovilizacion.setPropietario_presente(inm.getPropietario_presente());
                                            inmovilizacion.setId_infraccion(inm.getId_infraccion());
                                            inmovilizacion.setId_grua(inm.getId_grua());
                                            inmovilizacion.setId_zona(inm.getId_zona());
                                            inmovilizacion.setId_parqueadero(inm.getId_parqueadero());
                                            inmovilizacion.setDesenganche(inm.getDesenganche());
                                            inmovilizacion.setObservacion(inm.getObservacion());
                                            inmovilizacion.setDireccion(inm.getDireccion());
                                            Vehiculo vehiculo = new Vehiculo();
                                            vehiculo.setPlaca(inm.getPlaca());
                                            vehiculo.setNo_chasis(inm.getNo_chasis());
                                            vehiculo.setNo_motor(inm.getNo_motor());
                                            vehiculo.setNo_serie(inm.getNo_serie());
                                            vehiculo.setId_clase_vehiculo(inm.getId_clase_vehiculo());
                                            vehiculo.setId_color(inm.getId_color());
                                            vehiculo.setId_tipo_servicio(inm.getId_tipo_servicio());
                                            inmovilizacion.setVehiculo(vehiculo);
                                            Persona infractor = new Persona();
                                            infractor.setId_tipo_identificacion(inm.getId_tipo_identificacion());
                                            infractor.setNo_identificacion(inm.getNo_identificacion());
                                            infractor.setNombre1(inm.getNombre1());
                                            infractor.setNombre2(inm.getNombre2());
                                            infractor.setApellido1(inm.getApellido1());
                                            infractor.setApellido2(inm.getApellido2());
                                            inmovilizacion.setInfractor(infractor);
                                            inmovilizacion.setEstado("E");
                                            inmovilizaciones.add(inmovilizacion);
                                        }
                                        return inmovilizaciones;
                                    } catch (Exception e) {
                                        qb.join(InmovilizacionDao.Properties.Id_infractor, Persona.class);
                                        qb.join(InmovilizacionDao.Properties.Id_agente, Persona.class);
                                        qb.join(InmovilizacionDao.Properties.Id_infraccion, Infraccion.class);
                                        qb.where(new WhereCondition.StringCondition(" CAST(T.NO_COMPARENDO AS TEXT) LIKE '%" + consulta + "%' OR  J1.NO_IDENTIFICACION LIKE '%" + consulta + "%' OR J2.placa LIKE '%" + consulta + "%' OR strftime('%d/%m/%Y', T.FEC_INI_INM/1000, 'unixepoch') LIKE '%" + consulta + "%' OR J3.CODIGO LIKE '%" + consulta + "%' "));
                                        inmovilizaciones = qb.list();
                                    }
                                } else {
                                    qb.join(InmovilizacionDao.Properties.Id_infractor, Persona.class);
                                    qb.join(InmovilizacionDao.Properties.Id_agente, Persona.class);
                                    qb.join(InmovilizacionDao.Properties.Id_infraccion, Infraccion.class);
                                    qb.where(new WhereCondition.StringCondition(" CAST(T.NO_COMPARENDO AS TEXT) LIKE '%" + consulta + "%' OR  J1.NO_IDENTIFICACION LIKE '%" + consulta + "%' OR J2.placa LIKE '%" + consulta + "%' OR strftime('%d/%m/%Y', T.FEC_INI_INM/1000, 'unixepoch') LIKE '%" + consulta + "%' OR J3.CODIGO LIKE '%" + consulta + "%' "));
                                    inmovilizaciones = qb.list();
                                }
                            } else {
                                qb.join(InmovilizacionDao.Properties.Id_infractor, Persona.class);
                                qb.join(InmovilizacionDao.Properties.Id_agente, Persona.class);
                                qb.join(InmovilizacionDao.Properties.Id_infraccion, Infraccion.class);
                                qb.where(new WhereCondition.StringCondition(" CAST(T.NO_COMPARENDO AS TEXT) LIKE '%" + consulta + "%' OR  J1.NO_IDENTIFICACION LIKE '%" + consulta + "%' OR J2.placa LIKE '%" + consulta + "%' OR strftime('%d/%m/%Y', T.FEC_INI_INM/1000, 'unixepoch') LIKE '%" + consulta + "%' OR J3.CODIGO LIKE '%" + consulta + "%' "));
                                inmovilizaciones = qb.list();
                            }
                        } else {
                            qb.join(InmovilizacionDao.Properties.Id_infractor, Persona.class);
                            qb.join(InmovilizacionDao.Properties.Id_agente, Persona.class);
                            qb.join(InmovilizacionDao.Properties.Id_infraccion, Infraccion.class);
                            qb.where(new WhereCondition.StringCondition(" CAST(T.NO_COMPARENDO AS TEXT) LIKE '%" + consulta + "%' OR  J1.NO_IDENTIFICACION LIKE '%" + consulta + "%' OR J2.placa LIKE '%" + consulta + "%' OR strftime('%d/%m/%Y', T.FEC_INI_INM/1000, 'unixepoch') LIKE '%" + consulta + "%' OR J3.CODIGO LIKE '%" + consulta + "%' "));
                            inmovilizaciones = qb.list();
                        }
                        break;
                    case 4:
                        if (UtilidadesGenerales.isOnline()) {
                            List<Inmovilizacion> inmovilizacionEnviadas = inmovilizacionDao.queryBuilder().where(InmovilizacionDao.Properties.Estado.eq("E")).list();
                            if (inmovilizacionEnviadas != null && inmovilizacionEnviadas.size() > 0) {
                                inmovilizacionDao.deleteInTx(inmovilizacionEnviadas);
                            }
                            inmovilizaciones = new ArrayList();
                            Call<WSGruparResult<Departamento>> listarDepartamentosCall = service.listarDepartamentos();
                            Response<WSGruparResult<Departamento>> responseDepartamentos = listarDepartamentosCall.execute();
                            if (responseDepartamentos != null && responseDepartamentos.isSuccessful()) {
                                WSGruparResult<Departamento> result = responseDepartamentos.body();
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

                            Call<WSGruparResult<Municipio>> listarMunicipiosCall = service.listarMunicipios();
                            Response<WSGruparResult<Municipio>> responseMunicipios = listarMunicipiosCall.execute();
                            if (responseMunicipios != null && responseMunicipios.isSuccessful()) {
                                WSGruparResult<Municipio> result = responseMunicipios.body();
                                List<Municipio> municipios = result.getItems();
                                if (municipios != null && municipios.size() > 0) {
                                    publishProgress("Actualizando municipios. N° registros: " + municipios.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    municipioDao.deleteAll();
                                    municipioDao.insertInTx(municipios);
                                } else {
                                    publishProgress("No se encontraron registros en municipios.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar municipios.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }


                            Call<WSGruparResult<Infraccion>> listarInfraccionesCall = service.listarInfracciones();
                            Response<WSGruparResult<Infraccion>> responseInfracciones = listarInfraccionesCall.execute();
                            if (responseInfracciones != null && responseInfracciones.isSuccessful()) {
                                WSGruparResult<Infraccion> result = responseInfracciones.body();
                                List<Infraccion> infracciones = result.getItems();
                                if (infracciones != null && infracciones.size() > 0) {
                                    publishProgress("Actualizando infracciones. N° registros: " + infracciones.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    infraccionDao.deleteAll();
                                    infraccionDao.insertInTx(infracciones);
                                } else {
                                    publishProgress("No se encontraron registros en infracciones.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar infracciones.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }


                            Call<WSGruparResult<Parqueadero>> listarParqueaderoCall = service.listarParqueadero();
                            Response<WSGruparResult<Parqueadero>> responseParqueadero = listarParqueaderoCall.execute();
                            if (responseParqueadero != null && responseParqueadero.isSuccessful()) {
                                WSGruparResult<Parqueadero> result = responseParqueadero.body();
                                List<Parqueadero> parqueaderos = result.getItems();
                                if (parqueaderos != null && parqueaderos.size() > 0) {
                                    publishProgress("Actualizando parqueaderos. N° registros: " + parqueaderos.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    parqueaderoDao.deleteAll();
                                    parqueaderoDao.insertInTx(parqueaderos);
                                } else {
                                    publishProgress("No se encontraron registros en parqueadero.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar parqueaderos.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }

                            Call<WSGruparResult<Zonas>> listarZonasCall = service.listarZonas();
                            Response<WSGruparResult<Zonas>> responseZonas = listarZonasCall.execute();
                            if (responseZonas != null && responseZonas.isSuccessful()) {
                                WSGruparResult<Zonas> result = responseZonas.body();
                                List<Zonas> zonas = result.getItems();
                                if (zonas != null && zonas.size() > 0) {
                                    publishProgress("Actualizando zonas. N° registros: " + zonas.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    zonasDao.deleteAll();
                                    zonasDao.insertInTx(zonas);
                                } else {
                                    publishProgress("No se encontraron registros en zonas.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar zonas.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }

                            Call<WSGruparResult<TipoIdentificacion>> listarTipoIdentificacionCall = service.listarTipoIdentificacion();
                            Response<WSGruparResult<TipoIdentificacion>> responseTipoIdentificacion = listarTipoIdentificacionCall.execute();
                            if (responseTipoIdentificacion != null && responseTipoIdentificacion.isSuccessful()) {
                                WSGruparResult<TipoIdentificacion> result = responseTipoIdentificacion.body();
                                List<TipoIdentificacion> tipoIdentificaciones = result.getItems();
                                if (tipoIdentificaciones != null && tipoIdentificaciones.size() > 0) {
                                    publishProgress("Actualizando tipo identificacion. N° registros: " + tipoIdentificaciones.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    tipoIdentificacionDao.deleteAll();
                                    tipoIdentificacionDao.insertInTx(tipoIdentificaciones);
                                } else {
                                    publishProgress("No se encontraron registros en tipo identificacion.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar tipo identificaciones.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }

                            Call<WSGruparResult<Grua>> listarGruasCall = service.listarGruas();
                            Response<WSGruparResult<Grua>> responseGruas = listarGruasCall.execute();
                            if (responseGruas != null && responseGruas.isSuccessful()) {
                                WSGruparResult<Grua> result = responseGruas.body();
                                List<Grua> gruas = result.getItems();
                                if (gruas != null && gruas.size() > 0) {
                                    publishProgress("Actualizando gruas. N° registros: " + gruas.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    gruasDao.deleteAll();
                                    gruasDao.insertInTx(gruas);
                                } else {
                                    publishProgress("No se encontraron registros en gruas.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar gruas.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }

                            Call<WSGruparResult<Color>> listarColoresCall = service.listarColores();
                            Response<WSGruparResult<Color>> responseColores = listarColoresCall.execute();
                            if (responseColores != null && responseColores.isSuccessful()) {
                                WSGruparResult<Color> result = responseColores.body();
                                List<Color> colores = result.getItems();
                                if (colores != null && colores.size() > 0) {
                                    publishProgress("Actualizando colores. N° registros: " + colores.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    coloresDao.deleteAll();
                                    coloresDao.insertInTx(colores);
                                } else {
                                    publishProgress("No se encontraron registros en colores.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar colores.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }

                            Call<WSGruparResult<TipoServicio>> listarTipoServicioCall = service.listarTipoServicios();
                            Response<WSGruparResult<TipoServicio>> responseTipoServicios = listarTipoServicioCall.execute();
                            if (responseTipoServicios != null && responseTipoServicios.isSuccessful()) {
                                WSGruparResult<TipoServicio> result = responseTipoServicios.body();
                                List<TipoServicio> tipoServicios = result.getItems();
                                if (tipoServicios != null && tipoServicios.size() > 0) {
                                    publishProgress("Actualizando tipo servicios. N° registros: " + tipoServicios.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    tipoServicioDao.deleteAll();
                                    tipoServicioDao.insertInTx(tipoServicios);
                                } else {
                                    publishProgress("No se encontraron registros en tipo servicios.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar tipo servicios.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }

                            Call<WSGruparResult<ClaseVehiculo>> listarClaseVehiculoCall = service.listarClaseVehiculos();
                            Response<WSGruparResult<ClaseVehiculo>> responseClaseVehiculo = listarClaseVehiculoCall.execute();
                            if (responseClaseVehiculo != null && responseClaseVehiculo.isSuccessful()) {
                                WSGruparResult<ClaseVehiculo> result = responseClaseVehiculo.body();
                                List<ClaseVehiculo> claseVehiculos = result.getItems();
                                if (claseVehiculos != null && claseVehiculos.size() > 0) {
                                    publishProgress("Actualizando clase vehiculos. N° registros: " + claseVehiculos.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    claseVehiculoDao.deleteAll();
                                    claseVehiculoDao.insertInTx(claseVehiculos);
                                } else {
                                    publishProgress("No se encontraron registros en clase vehiculos.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar clase vehiculos.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }

                        /*Call<WSGruparResult<Vehiculo>> listarVehiculoCall = service.listarVehiculos();
                        Response<WSGruparResult<Vehiculo>> responseVehiculo = listarVehiculoCall.execute();
                        if (responseVehiculo != null && responseVehiculo.isSuccessful()) {
                            WSGruparResult<Vehiculo> result = responseVehiculo.body();
                            List<Vehiculo> vehiculos = result.getItems();
                            if (vehiculos != null && vehiculos.size() > 0) {
                                publishProgress("Actualizando vehiculos. N° registros: " + vehiculos.size());
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                vehiculoDao.deleteAll();

                                vehiculoDao.insertInTx(vehiculos);

                            } else {
                                publishProgress("No se encontraron registros en vehiculos.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }
                        } else {
                            publishProgress("Error al consultar vehiculos.");
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                        }*/

                            Call<WSGruparResult<Persona>> listarPersonasCall = service.listarPersonas();
                            Response<WSGruparResult<Persona>> responsePersonas = listarPersonasCall.execute();
                            if (responsePersonas != null && responsePersonas.isSuccessful()) {
                                WSGruparResult<Persona> result = responsePersonas.body();
                                List<Persona> personas = result.getItems();
                                if (personas != null && personas.size() > 0) {
                                    publishProgress("Actualizando usuarios y agentes. N° registros: " + personas.size());
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                    /*List<Persona> agenteUsuarios = personasDao.queryBuilder().where(PersonaDao.Properties.Tipo.eq("U"), PersonaDao.Properties.Tipo.eq("A")).list();
                                    if (agenteUsuarios != null && agenteUsuarios.size() > 0) {
                                        personasDao.deleteInTx(agenteUsuarios);
                                    }*/
                                    personasDao.insertOrReplaceInTx(personas);
                                } else {
                                    publishProgress("No se encontraron registros en usuarios y agentes.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            } else {
                                publishProgress("Error al consultar usuarios y agentes.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }
                        } else {
                            publishProgress("No fue posible la actualizacion de maestros, verifique su conexion a internet e intente nuevamente.");
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                        }
                        break;
                }
                return inmovilizaciones;
            } catch (Exception ex) {
                Log.e(TAG, "ListarInmovilizaciones.doInBackground: fallo consultar comparendos");
                Log.e(TAG, "", ex);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {
                if (mProgressDialog != null) mProgressDialog.actualizarMensaje(values[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onPostExecute(final List<Inmovilizacion> inmovilizaciones) {
            onConsultarInmovilizacionFinish(inmovilizaciones, opcion);
        }

        @Override
        protected void onCancelled() {
            onConsultarInmovilizacionesCancel();
        }
    }


    private void onConsultarInmovilizacionFinish(List<Inmovilizacion> inmovilizaciones, int opcion) {
        this.mConsultarInmovilizacionesTask = null;
        hideProgress();
        if (opcion != 4) {
            this.mAdapter = new InformacionSIGDUERecyclerView(inmovilizaciones, this);
            this.rv.setAdapter(this.mAdapter);
        }
        AlertDialog.Builder builder;
        if ((inmovilizaciones == null || (inmovilizaciones != null && inmovilizaciones.size() == 0)) && opcion == 3) {
            builder = new AlertDialog.Builder(ListarInformacionSIGDUEActivity.this);
            builder.setTitle("Información");
            builder.setMessage("No se encontraron inmovilizaciones.");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (inmovilizaciones != null && inmovilizaciones.size() > 0 && opcion == 3) {
            builder = new AlertDialog.Builder(ListarInformacionSIGDUEActivity.this);
            builder.setTitle("Información");
            builder.setMessage("Se encontraron " + inmovilizaciones.size() + " inmovilizaciones.");
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
        this.mProgressDialog = ProgressDialogFragment.newInstance(mensaje);
        this.mProgressDialog.show(getSupportFragmentManager(), "dialog_loading");
    }

    private void hideProgress() {
        try {
            if (this.mProgressDialog != null) {
                this.mProgressDialog.dismissAllowingStateLoss();
                this.mProgressDialog = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onConsultarInmovilizacionesCancel() {
        cancel();
    }

    public void cancel() {
        if (this.mConsultarInmovilizacionesTask != null) {
            this.mConsultarInmovilizacionesTask.cancel(true);
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
            if (this.departamentosDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen departamentos registrados.\n";
            }
            if (this.municipioDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen municipios registrados.\n";
            }
            if (this.infraccionDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen infracciones registradas.\n";
            }
            if (this.zonasDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen zonas registradas.\n";
            }
            if (this.parqueaderoDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen parqueaderos registrados.\n";
            }
            if (this.personasDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen usuarios y agentes de tr\u00e1nsito registrados.\n";
            }
            if (this.tipoIdentificacionDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen tipo documentos registrados.\n";
            }
            if (this.gruasDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen gruas registradas.\n";
            }
            if (this.coloresDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen colores registrados.\n";
            }
            if (this.tipoServicioDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen tipo de servicios registrados.\n";
            }
            if (this.claseVehiculoDao.queryBuilder().count() == 0) {
                parametrosComparendos = parametrosComparendos + "-No existen clases de vehiculos registrados.\n";
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

    public static class ProgressDialogFragment extends DialogFragment {

        private ProgressDialog dialog;
        private String mensaje;

        public static ProgressDialogFragment newInstance() {
            ProgressDialogFragment frag = new ProgressDialogFragment();
            return frag;
        }

        public void inicializarMensaje(String mensaje) {
            try {
                this.mensaje = mensaje;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void actualizarMensaje(String mensaje) {
            try {
                if (this.dialog != null) {
                    this.dialog.setMessage(mensaje);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public static ProgressDialogFragment newInstance(String mensaje) {
            ProgressDialogFragment frag = new ProgressDialogFragment();
            frag.inicializarMensaje(mensaje);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage(this.mensaje);
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    Log.i(TAG, "Cargando inmovilizaciones fue cancelado por el usuario.");
                    ((ListarInformacionSIGDUEActivity) getActivity()).cancel();
                }
            });
            return dialog;
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
            ejecutarConsultaServicios(0, "");

            IntentFilter filter = new IntentFilter();
            filter.addAction(RESPUESTA_SERVICIO);
            registerReceiver(mActualizarListaComparendosBroadcastReceiver, filter);

            filter = new IntentFilter();
            filter.addAction("android.location.PROVIDERS_CHANGED");
            registerReceiver(mHabilitarGPS, filter);

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
            String tiempoSincronizacion = (String) UtilidadesGenerales.leerSharedPreferences(R.string.pref_tiempo_sincronizacion_gps_key, R.string.vacio, UtilidadesGenerales.STRING_TYPE);
            long time = (tiempoSincronizacion != null && !tiempoSincronizacion.equals("") ? Long.parseLong(tiempoSincronizacion) : 0);
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