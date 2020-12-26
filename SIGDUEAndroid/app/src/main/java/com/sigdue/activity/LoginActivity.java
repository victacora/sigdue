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

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.sigdue.R;
import com.sigdue.aplication.AplicacionInmovilizaciones;
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
import com.sigdue.db.Municipio;
import com.sigdue.db.MunicipioDao;
import com.sigdue.db.Parqueadero;
import com.sigdue.db.ParqueaderoDao;
import com.sigdue.db.Persona;
import com.sigdue.db.PersonaDao;
import com.sigdue.db.DaoSession;
import com.sigdue.db.TipoIdentificacion;
import com.sigdue.db.TipoIdentificacionDao;
import com.sigdue.db.TipoServicio;
import com.sigdue.db.TipoServicioDao;
import com.sigdue.db.VehiculoDao;
import com.sigdue.db.Zonas;
import com.sigdue.db.ZonasDao;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.webservice.api.WSGruparClient;
import com.sigdue.webservice.api.WSGruparInterface;
import com.sigdue.webservice.modelo.WSGruparResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.sigdue.Constants.SLEEP_PROGRESS_MAESTROS;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private AplicacionInmovilizaciones app;
    private AutenticarUsuarioAsyncTask mAuthTask = null;
    private DialogFragment mProgressDialog = null;
    private TextView mMessage;
    private String mPassword;
    private EditText mPasswordEdit;
    private String mUsername;
    private EditText mUsernameEdit;
    private DaoSession daoSession;
    private PersonaDao personasDao;
    private DepartamentoDao departamentosDao;
    private MunicipioDao municipioDao;
    private InfraccionDao infraccionDao;
    private ParqueaderoDao parqueaderoDao;
    private ZonasDao zonasDao;
    private TipoIdentificacionDao tipoIdentificacionDao;
    private GruaDao gruasDao;
    private ColorDao coloresDao;
    private TipoServicioDao tipoServicioDao;
    private ClaseVehiculoDao claseVehiculoDao;
    private VehiculoDao vehiculoDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UtilidadesGenerales.context = this;
            app = (AplicacionInmovilizaciones) getApplicationContext();
            daoSession = app.getDaoSession();
            departamentosDao = daoSession.getDepartamentoDao();
            municipioDao = daoSession.getMunicipioDao();
            infraccionDao = daoSession.getInfraccionDao();
            parqueaderoDao = daoSession.getParqueaderoDao();
            zonasDao = daoSession.getZonasDao();
            tipoIdentificacionDao = daoSession.getTipoIdentificacionDao();
            gruasDao = daoSession.getGruaDao();
            coloresDao = daoSession.getColorDao();
            tipoServicioDao = daoSession.getTipoServicioDao();
            claseVehiculoDao = daoSession.getClaseVehiculoDao();
            personasDao = daoSession.getPersonaDao();
            vehiculoDao = daoSession.getVehiculoDao();
            setContentView(R.layout.login_activity);
            mMessage = (TextView) findViewById(R.id.message);
            mUsernameEdit = (EditText) findViewById(R.id.username_edit);
            mPasswordEdit = (EditText) findViewById(R.id.password_edit);
            mPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        startLogin();
                        return true;
                    } else {
                        return false;
                    }
                }
            });


            if (app.getIdPersona() != -1) {
                Persona persona = personasDao.queryBuilder().where(PersonaDao.Properties.Id_persona.eq(app.getIdPersona())).unique();
                {
                    if (persona != null) {
                        app.setUsuario(persona.getNombre1() + " " + persona.getNombre2() + " " + persona.getApellido1() + " " + persona.getApellido2());
                        Intent listarInmovilizaciones = new Intent(LoginActivity.this, ListarInformacionSIGDUEActivity.class);
                        startActivity(listarInmovilizaciones);
                        finish();
                    } else {
                        app.setUsuario("");
                        app.setIdUsuario(-1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void handleLogin(View view) {
        startLogin();
    }

    public void startLogin() {
        try {
            UtilidadesGenerales.hideSoftKeyboard(this.getCurrentFocus());
            mUsername = mUsernameEdit.getText().toString();
            mPassword = mPasswordEdit.getText().toString();
            if (TextUtils.isEmpty(mUsername)) {
                mMessage.setText("El nombre de usuario es requerido.");
            } else if (TextUtils.isEmpty(mPassword)) {
                mMessage.setText("La contraseña es requerida.");
            } else {
                mMessage.setText("");
                showProgress(getString(R.string.ui_activity_authenticating));
                mAuthTask = new AutenticarUsuarioAsyncTask();
                mAuthTask.execute(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onAuthenticationResult(Persona persona, int opcion) {
        mAuthTask = null;
        hideProgress();
        if (persona != null) {
            try {
                if (opcion == 1) {
                    UtilidadesGenerales.escribirSharedPreferences(R.string.pref_usuario_key, String.valueOf(persona.getId_persona()), UtilidadesGenerales.STRING_TYPE);
                    app.setUsuario(persona.getNombre1() + " " + persona.getNombre2() + " " + persona.getApellido1() + " " + persona.getApellido2());
                    app.setIdUsuario(persona.getId_persona());
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Confirmación");
                    builder.setMessage("¿Desea actualizar maestros en este momento?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                showProgress("Actualizando maestros...");
                                mAuthTask = new AutenticarUsuarioAsyncTask();
                                mAuthTask.execute(2);
                                dialog.dismiss();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                mMessage.setText("");
                                mPasswordEdit.setText("");
                                mUsernameEdit.setText("");
                                Intent listarInmovilizaciones = new Intent(LoginActivity.this, ListarInformacionSIGDUEActivity.class);
                                startActivity(listarInmovilizaciones);
                                dialog.dismiss();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    mMessage.setText("");
                    mPasswordEdit.setText("");
                    mUsernameEdit.setText("");
                    Intent listarInmovilizaciones = new Intent(this, ListarInformacionSIGDUEActivity.class);
                    startActivity(listarInmovilizaciones);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (opcion == 2) {
            try {
                mMessage.setText("");
                mPasswordEdit.setText("");
                mUsernameEdit.setText("");
                Intent listarInmovilizaciones = new Intent(this, ListarInformacionSIGDUEActivity.class);
                startActivity(listarInmovilizaciones);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            mMessage.setText(getText(R.string.login_activity_loginfail_text_both));
        }
    }

    protected void showProgress(String mensaje) {
        mProgressDialog = ProgressDialogFragment.newInstance(mensaje);
        mProgressDialog.show(getSupportFragmentManager(), "dialog_authetication");
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void cancel() {
        if (mAuthTask != null) {
            mAuthTask.cancel(true);
            finish();
        }
    }

    public void onAuthenticationCancel() {
        mAuthTask = null;
        hideProgress();
    }

    public class AutenticarUsuarioAsyncTask extends AsyncTask<Integer, String, Persona> {
        int opcion = -1;

        @Override
        protected Persona doInBackground(Integer... params) {
            try {
                Persona resultadoAutenticacion = null;
                WSGruparInterface service = WSGruparClient.getClient();
                opcion = (int) params[0];
                if (opcion == 1) {
                    if (UtilidadesGenerales.isOnline()) {
                       /* String IMEI = UtilidadesGenerales.getIMEI();
                        Call<WSGruparResult<Persona>> loginUsuariosCall = service.loginUsuarios(mUsername, mPassword, IMEI);
                        Response<WSGruparResult<Persona>> response = loginUsuariosCall.execute();
                        if (response != null && response.isSuccessful()) {
                            WSGruparResult<Persona> result = response.body();
                            List<Persona> personas = result.getItems();
                            if (personas != null && personas.size() > 0) {
                                resultadoAutenticacion = personas.get(0);
                                personasDao.insertOrReplace(personas.get(0));
                                publishProgress("Usuario autenticado remotamente.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            } else {
                                publishProgress("No fue posible autenticar el usuario remotamente.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                List<Persona> autenticarUsuario = personasDao.queryBuilder().where(PersonaDao.Properties.Login.eq(mUsername), PersonaDao.Properties.Password.eq(mPassword), PersonaDao.Properties.Tipo.eq("U")).list();
                                if (autenticarUsuario != null && autenticarUsuario.size() > 0) {
                                    resultadoAutenticacion = autenticarUsuario.get(0);
                                    publishProgress("Usuario autenticado localmente.");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                } else {
                                    publishProgress("No fue posible autenticar el usuario localmente, verifique su conexion a internet e intente nuevamente");
                                    Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                                }
                            }
                        } else {
                            publishProgress("No fue posible autenticar el usuario remotamente.");
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            List<Persona> autenticarUsuario = personasDao.queryBuilder().where(PersonaDao.Properties.Login.eq(mUsername), PersonaDao.Properties.Password.eq(mPassword), PersonaDao.Properties.Tipo.eq("U")).list();
                            if (autenticarUsuario != null && autenticarUsuario.size() > 0) {
                                resultadoAutenticacion = autenticarUsuario.get(0);
                                publishProgress("Usuario autenticado localmente.");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            } else {
                                publishProgress("No fue posible autenticar el usuario localmente, verifique su conexion a internet e intente nuevamente");
                                Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                            }
                        }*/
                        resultadoAutenticacion=new Persona();
                        resultadoAutenticacion.setApellido1("Puyo");
                        resultadoAutenticacion.setApellido2("Epia");
                        resultadoAutenticacion.setNombre1("Victor");
                        resultadoAutenticacion.setApellido2("Alfonso");
                        resultadoAutenticacion.setDireccion("Carrer 65 # 1A-6");
                        resultadoAutenticacion.setId_persona(1);
                        resultadoAutenticacion.setId_tipo_identificacion("CC");
                        resultadoAutenticacion.setNo_identificacion("106168282");

                    } else {
                        List<Persona> autenticarUsuario = personasDao.queryBuilder().where(PersonaDao.Properties.Login.eq(mUsername), PersonaDao.Properties.Password.eq(mPassword), PersonaDao.Properties.Tipo.eq("U")).list();
                        if (autenticarUsuario != null && autenticarUsuario.size() > 0) {
                            resultadoAutenticacion = autenticarUsuario.get(0);
                            publishProgress("Usuario autenticado localmente.");
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                        } else {
                            publishProgress("No fue posible autenticar el usuario localmente, verifique su conexion a internet e intente nuevamente");
                            Thread.sleep(SLEEP_PROGRESS_MAESTROS);
                        }
                    }
                } else {
                    if (UtilidadesGenerales.isOnline()) {
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
                }
                return resultadoAutenticacion;
            } catch (Exception ex) {
                Log.e(TAG, "AutenticarUsuarioAsyncTask.doInBackground: fallo al autenticar usuario");
                Log.e(TAG, "", ex);
                publishProgress("Error general al ejecutar la aplicacion. ");
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {
                ((ProgressDialogFragment) mProgressDialog).actualizarMensaje(values[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(final Persona persona) {
            onAuthenticationResult(persona, opcion);
        }

        @Override
        protected void onCancelled() {
            onAuthenticationCancel();
        }
    }

    public static class ProgressDialogFragment extends DialogFragment {

        private ProgressDialog dialog;
        private String mensaje;

        public void inicializarMensaje(String mensaje) {
            try {
                this.mensaje = mensaje;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void actualizarMensaje(String mensaje) {
            try {
                if (dialog != null) dialog.setMessage(mensaje);
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
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.setMessage(mensaje);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    Log.i(TAG, "Dialogo autenticar usuario fue cancelado.");
                    ((LoginActivity) getActivity()).cancel();
                }
            });
            return dialog;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String mensaje = null;
        if (grantResults.length > 0) {
            if (requestCode == UtilidadesGenerales.READ_PHONE_STATE_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso para consultar IMEI.";
            } else if (requestCode == UtilidadesGenerales.CAMERA_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso para uso de camara.";
            }  else if (requestCode == UtilidadesGenerales.ACCESS_WIFI_STATE_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso para consutar el estado de conexion a WIFI.";
            } else if (requestCode == UtilidadesGenerales.ACCESS_NETWORK_STATE_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso para consutar el estado de conexion a redes.";
            } else if (requestCode == UtilidadesGenerales.INTERNET_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso para conexion a internet.";
            } else if (requestCode == UtilidadesGenerales.ACCESS_FINE_LOCATION_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso de consulta GPS, ubicación precisa.";
            }  else if (requestCode == UtilidadesGenerales.ACCESS_COARSE_LOCATION_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso de consulta GPS, ubicación aproximada.";
            } else if (requestCode == UtilidadesGenerales.WRITE_EXTERNAL_STORAGE_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso de almacenamiento en memoria externa.";
            }
            else if (requestCode == UtilidadesGenerales.READ_EXTERNAL_STORAGE_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso de lectura de memoria externa.";
            }
            else if (requestCode == UtilidadesGenerales.VIBRATE_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso para el manejo de notifiaciones.";
            }
            if (mensaje != null) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setCancelable(false);
                builder.setMessage(mensaje);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void verificarPersmisos() {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere tener habilitado el permiso para consultar el IMEI del equipo.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                UtilidadesGenerales.READ_PHONE_STATE_CODE);
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();

                Log.i(TAG, "Solicitar permiso para consultar IMEI.");
            } else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere tener habilitado el permiso para hacer uso de la cámara del equipo.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA},
                                UtilidadesGenerales.CAMERA_CODE);
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.i(TAG, "Solicitar permiso para uso de camara.");
            } else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere tener habilitado el permiso para consultar el estado de conexión a WIFI.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                                UtilidadesGenerales.ACCESS_WIFI_STATE_CODE);
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.i(TAG, "Solicitar permiso para consutar el estado de conexión a WIFI.");
            } else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere tener habilitado el permiso para consultar el estado de conexión a internet.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                                UtilidadesGenerales.ACCESS_NETWORK_STATE_CODE);
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.i(TAG, "Solicitar permiso para consutar el estado de conexion a redes.");
            } else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere tener habilitado el permiso para acceder a redes.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                UtilidadesGenerales.INTERNET_CODE);
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();

                Log.i(TAG, "Solicitar permiso  para conexion a redes.");
            } else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere tener habilitado el permiso para consultar el GPS del equipo.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                UtilidadesGenerales.ACCESS_FINE_LOCATION_CODE);
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();

                Log.i(TAG, "Solicitar permiso para consulta GPS por ubicación precisa.");
            }
            else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere tener habilitado el permiso para consultar el GPS del equipo, y obtener la ubicación aproximada.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                UtilidadesGenerales.ACCESS_COARSE_LOCATION_CODE);
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();

                Log.i(TAG, "Solicitar permiso para consulta GPS por ubicación aproximada.");
            }else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere permiso para almacenar información en la memoria externa del equipo.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                UtilidadesGenerales.WRITE_EXTERNAL_STORAGE_CODE);
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.i(TAG, "Solicitar permiso para escribir en memoria externa.");
            }
            else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere permiso para leer información de la memoria externa del equipo.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                UtilidadesGenerales.READ_EXTERNAL_STORAGE_CODE);
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.i(TAG, "Solicitar permiso para leer la memoria externa.");
            }

            else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere permiso para el manejo de notificaciones");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.VIBRATE},
                                UtilidadesGenerales.VIBRATE_CODE);
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.i(TAG, "Solicitar permiso para manejo de notificaciones.");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        verificarPersmisos();
    }
}