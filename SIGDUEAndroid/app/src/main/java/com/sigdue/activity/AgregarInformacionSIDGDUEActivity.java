package com.sigdue.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;

import com.iangclifton.android.floatlabel.FloatLabel;
import com.sigdue.BuildConfig;
import com.sigdue.R;
import com.sigdue.aplication.AplicacionSIGDUE;
import com.sigdue.db.ClaseVehiculo;
import com.sigdue.db.ClaseVehiculoDao;
import com.sigdue.db.Color;
import com.sigdue.db.ColorDao;
import com.sigdue.db.DaoSession;
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
import com.sigdue.service.EnviarInformacionSIGDUEService;
import com.sigdue.ui.MoviePlayer;
import com.sigdue.ui.SearchableSpinner;
import com.sigdue.ui.SpeedControlCallback;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;
import com.sigdue.webservice.api.WSGruparClient;
import com.sigdue.webservice.api.WSGruparInterface;
import com.sigdue.webservice.modelo.WSGruparResult;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.sigdue.Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.sigdue.Constants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE;
import static com.sigdue.Constants.RUTAMULTIMEDIAINMOVILIZACION;

public class AgregarInformacionSIDGDUEActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MoviePlayer.PlayerFeedback {

    private String TAG = "AgregarInmActivity";
    private EditText apellidoDos;
    private EditText apellidoUno;
    private AplicacionSIGDUE app;
    private Button btnAtras;
    private ImageButton btnBuscarInfractor;
    private ImageButton btnBuscarVehiculo;
    private Button btnEliminar;
    private Button btnEliminarVideo;
    private Button btnGrabarVideo;
    private Button btnSiguiente;
    private Button btnTomarFoto;
    private LinearLayout buscarInfractor;
    private ClaseVehiculoDao claseVehiculoDao;
    private SearchableSpinner cmbAgente;
    private SearchableSpinner cmbClaseVehiculo;
    private SearchableSpinner cmbColor;
    private SearchableSpinner cmbDepartamento;
    private SearchableSpinner cmbGrua;
    private SearchableSpinner cmbInfraccion;
    private SearchableSpinner cmbMunicipio;
    private SearchableSpinner cmbParqueadero;
    private SearchableSpinner cmbTipoDocumento;
    private SearchableSpinner cmbTipoServicio;
    private SearchableSpinner cmbZona;
    private ColorDao colorDao;
    private DaoSession daoSession;
    private DepartamentoDao departamentoDao;
    private SwitchCompat desengancheSiNo;
    private EditText detalleZona;
    private ProgressDialog dialogoBuscando;
    private EditText direccion;
    private EditText fechaHoraComparendo;
    ArrayList<String> fotos;
    private GruaDao gruaDao;
    private SwitchCompat gruaSiNo;
    private InfraccionDao infraccionDao;
    private Inmovilizacion inmovilizacion;
    private InmovilizacionDao inmovilizacionDao;
    private boolean isBusquedaInfractor;
    private boolean isBusquedaVehiculo;
    private PhotoViewAttacher mAttacher;
    private ImageView mImageView;
    private boolean mMostrarDetenerVideo;
    private MoviePlayer.PlayTask mPlayTask;
    private boolean mSurfaceTextureReady;
    private TextureView mTextureView;
    private LinearLayout mostrarCamposGrua;
    private LinearLayout mostrarCamposInfractor;
    private LinearLayout mostrarCamposVehiculo;
    private LinearLayout mostrarFotos;
    private LinearLayout mostrarVideo;
    private MunicipioDao municipioDao;
    private EditText nombreDos;
    private EditText nombreUno;
    private EditText numeroChasis;
    private EditText numeroComparendo;
    private EditText numeroDocumento;
    int numeroFoto;
    private EditText numeroMotor;
    private EditText numeroPlaca;
    private EditText numeroSerie;
    private EditText observaciones;
    private ParqueaderoDao parqueaderoDao;
    private PersonaDao personaDao;
    int posFoto;
    private SwitchCompat propietarioPresenteSiNo;
    String rutaVideo;
    String rutafoto;
    private TipoIdentificacionDao tipoIdentificacionDao;
    private TipoServicioDao tipoServicioDao;
    private VehiculoDao vehiculoDao;
    private ZonasDao zonasDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UtilidadesGenerales.context = this;
            mSurfaceTextureReady = false;
            fotos = new ArrayList();
            numeroFoto = 0;
            posFoto = 0;
            rutafoto = "";
            rutaVideo = "";
            dialogoBuscando = null;
            isBusquedaInfractor = false;
            isBusquedaVehiculo = false;
            this.app = (AplicacionSIGDUE) getApplication();
            this.daoSession = this.app.getDaoSession();
            this.inmovilizacionDao = this.daoSession.getInmovilizacionDao();
            this.infraccionDao = this.daoSession.getInfraccionDao();
            this.zonasDao = this.daoSession.getZonasDao();
            this.gruaDao = this.daoSession.getGruaDao();
            this.parqueaderoDao = this.daoSession.getParqueaderoDao();
            this.claseVehiculoDao = this.daoSession.getClaseVehiculoDao();
            this.colorDao = this.daoSession.getColorDao();
            this.tipoServicioDao = this.daoSession.getTipoServicioDao();
            this.personaDao = this.daoSession.getPersonaDao();
            this.tipoIdentificacionDao = this.daoSession.getTipoIdentificacionDao();
            this.vehiculoDao = this.daoSession.getVehiculoDao();
            this.departamentoDao = this.daoSession.getDepartamentoDao();
            this.municipioDao = this.daoSession.getMunicipioDao();
            setContentView(R.layout.agregar_informacion_sigdue_activity);
            this.cmbInfraccion = (SearchableSpinner) findViewById(R.id.cmbInfraccion);
            this.cmbInfraccion.setTitle("Infracciones");
            this.cmbInfraccion.setPositiveButton("Cerrar");
            SpinnerAdapter adapterComboInf = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.infraccionDao.queryBuilder().list().toArray());
            this.cmbInfraccion.setAdapter(adapterComboInf);
            this.cmbZona = (SearchableSpinner) findViewById(R.id.cmbZona);
            this.cmbZona.setTitle("Zonas");
            this.cmbZona.setPositiveButton("Cerrar");
            Object[] camposCombo = this.zonasDao.queryBuilder().list().toArray();
            SpinnerAdapter adapterComboZona = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
            if (camposCombo == null || camposCombo.length <= 0) {
                this.cmbZona.setAdapter(adapterComboZona);
            } else {
                this.cmbZona.setAdapter(adapterComboZona, camposCombo[0]);
            }
            this.cmbZona.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    detalleZona.setText(((Zonas) parent.getItemAtPosition(position)).getDescripcion().trim());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            this.cmbGrua = (SearchableSpinner) findViewById(R.id.cmbGrua);
            this.cmbGrua.setTitle("Gruas");
            this.cmbGrua.setPositiveButton("Cerrar");
            camposCombo = this.gruaDao.queryBuilder().list().toArray();
            SpinnerAdapter adapterComboGrua = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
            if (camposCombo == null || camposCombo.length <= 0) {
                this.cmbGrua.setAdapter(adapterComboGrua);
            } else {
                this.cmbGrua.setAdapter(adapterComboGrua, camposCombo[0]);
            }
            this.cmbParqueadero = (SearchableSpinner) findViewById(R.id.cmbParqueadero);
            this.cmbParqueadero.setTitle("Parqueaderos");
            this.cmbParqueadero.setPositiveButton("Cerrar");
            camposCombo = this.parqueaderoDao.queryBuilder().list().toArray();
            SpinnerAdapter adapterComboParqueadero = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
            if (camposCombo == null || camposCombo.length <= 0) {
                this.cmbParqueadero.setAdapter(adapterComboParqueadero);
            } else {
                this.cmbParqueadero.setAdapter(adapterComboParqueadero, camposCombo[0]);
            }
            this.cmbTipoServicio = (SearchableSpinner) findViewById(R.id.cmbServicio);
            this.cmbTipoServicio.setTitle("Tipo servicios");
            this.cmbTipoServicio.setPositiveButton("Cerrar");
            camposCombo = this.tipoServicioDao.queryBuilder().list().toArray();
            SpinnerAdapter adapterComboTipoServicio = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
            if (camposCombo == null || camposCombo.length <= 0) {
                this.cmbTipoServicio.setAdapter(adapterComboTipoServicio);
            } else {
                this.cmbTipoServicio.setAdapter(adapterComboTipoServicio, camposCombo[0]);
            }
            this.cmbClaseVehiculo = (SearchableSpinner) findViewById(R.id.cmbClase);
            this.cmbClaseVehiculo.setTitle("Clase vehiculos");
            this.cmbClaseVehiculo.setPositiveButton("Cerrar");
            camposCombo = this.claseVehiculoDao.queryBuilder().list().toArray();
            SpinnerAdapter adapterComboClaseVehiculo = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
            if (camposCombo == null || camposCombo.length <= 0) {
                this.cmbClaseVehiculo.setAdapter(adapterComboClaseVehiculo);
            } else {
                this.cmbClaseVehiculo.setAdapter(adapterComboClaseVehiculo, camposCombo[0]);
            }
            this.cmbColor = (SearchableSpinner) findViewById(R.id.cmbColor);
            this.cmbColor.setTitle("Color vehiculos");
            this.cmbColor.setPositiveButton("Cerrar");
            camposCombo = this.colorDao.queryBuilder().list().toArray();
            SpinnerAdapter adapterComboColor = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
            if (camposCombo == null || camposCombo.length <= 0) {
                this.cmbColor.setAdapter(adapterComboColor);
            } else {
                this.cmbColor.setAdapter(adapterComboColor, camposCombo[0]);
            }
            this.cmbTipoDocumento = (SearchableSpinner) findViewById(R.id.cmbTipoIdentificacion);
            this.cmbTipoDocumento.setTitle("Tipo documentos");
            this.cmbTipoDocumento.setPositiveButton("Cerrar");
            camposCombo = this.tipoIdentificacionDao.queryBuilder().list().toArray();
            SpinnerAdapter adapterComboTipoIdentificacion = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
            if (camposCombo == null || camposCombo.length <= 0) {
                this.cmbTipoDocumento.setAdapter(adapterComboTipoIdentificacion);
            } else {
                this.cmbTipoDocumento.setAdapter(adapterComboTipoIdentificacion, camposCombo[0]);
            }
            this.cmbAgente = (SearchableSpinner) findViewById(R.id.cmbAgente);
            this.cmbAgente.setTitle("Agentes");
            this.cmbAgente.setPositiveButton("Cerrar");
            SpinnerAdapter adapterComboAgentes = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.personaDao.queryBuilder().where(PersonaDao.Properties.Tipo.eq("A")).list().toArray());
            this.cmbAgente.setAdapter(adapterComboAgentes);
            this.cmbMunicipio = (SearchableSpinner) findViewById(R.id.cmbMunicipio);
            this.cmbMunicipio.setTitle("Municipio");
            this.cmbMunicipio.setPositiveButton("Cerrar");
            this.cmbDepartamento = (SearchableSpinner) findViewById(R.id.cmbDepartamento);
            this.cmbDepartamento.setTitle("Departamentos");
            this.cmbDepartamento.setPositiveButton("Cerrar");
            camposCombo = this.departamentoDao.queryBuilder().list().toArray();
            SpinnerAdapter adapterComboDepartamentos = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
            this.cmbDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Object[] municipios = municipioDao.queryBuilder().where(MunicipioDao.Properties.Id_departamento.eq(((Departamento) parent.getItemAtPosition(position)).getId_departamento())).orderAsc(MunicipioDao.Properties.Nom_municipio).list().toArray();
                        SpinnerAdapter adapterComboMunicipios = new ArrayAdapter(AgregarInformacionSIDGDUEActivity.this, android.R.layout.simple_spinner_dropdown_item, municipios);
                        if (municipios == null || municipios.length <= 0) {
                            cmbMunicipio.setAdapter(adapterComboMunicipios);
                        } else {
                            cmbMunicipio.setAdapter(adapterComboMunicipios, municipios[0]);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (camposCombo == null || camposCombo.length <= 0) {
                this.cmbDepartamento.setAdapter(adapterComboDepartamentos);
            } else {
                this.cmbDepartamento.setAdapter(adapterComboDepartamentos, camposCombo[0]);
            }
            this.mostrarCamposGrua = (LinearLayout) findViewById(R.id.otrosCamposGrua);
            this.buscarInfractor = (LinearLayout) findViewById(R.id.buscarInfractor);
            this.mostrarCamposInfractor = (LinearLayout) findViewById(R.id.otrosCamposInfractor);
            this.mostrarCamposVehiculo = (LinearLayout) findViewById(R.id.otrosCamposVehiculo);
            this.mostrarFotos = (LinearLayout) findViewById(R.id.mostrarFotos);
            this.mostrarVideo = (LinearLayout) findViewById(R.id.mostrarVideo);
            this.mImageView = (ImageView) findViewById(R.id.imagen);
            this.propietarioPresenteSiNo = (SwitchCompat) findViewById(R.id.propietarioPresenteSiNo);
            this.propietarioPresenteSiNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        buscarInfractor.setVisibility(View.VISIBLE);
                    } else {
                        buscarInfractor.setVisibility(View.GONE);
                    }
                }
            });
            this.gruaSiNo = (SwitchCompat) findViewById(R.id.gruaSiNo);
            this.gruaSiNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mostrarCamposGrua.setVisibility(View.VISIBLE);
                    } else {
                        mostrarCamposGrua.setVisibility(View.GONE);
                    }
                }
            });
            this.desengancheSiNo = (SwitchCompat) findViewById(R.id.desengancheSiNo);
            this.mTextureView = (TextureView) findViewById(R.id.video);
            this.mTextureView.setSurfaceTextureListener(this);
            Drawable bitmap = getResources().getDrawable(R.drawable.imagenes);
            this.mImageView.setImageDrawable(bitmap);
            this.mAttacher = new PhotoViewAttacher(this.mImageView);
            this.fotos = new ArrayList();
            this.inmovilizacion = new Inmovilizacion();
            this.inmovilizacion.__setDaoSession(this.daoSession);
            List<Inmovilizacion> inmovilizaciones = this.inmovilizacionDao.queryBuilder().orderDesc(InmovilizacionDao.Properties.Id_inmovilizacion).list();
            if (inmovilizaciones == null || inmovilizaciones.size() <= 0) {
                this.inmovilizacion.setId_inmovilizacion(1);
            } else {
                this.inmovilizacion.setId_inmovilizacion((inmovilizaciones.get(0)).getId_inmovilizacion() + 1);
            }
            this.inmovilizacion.setFec_ini_inm(new Date());
            this.fechaHoraComparendo = ((FloatLabel) findViewById(R.id.fecha)).getEditText();
            String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(this.inmovilizacion.getFec_ini_inm());
            this.fechaHoraComparendo.setText(fecha);
            this.fechaHoraComparendo.setClickable(false);
            this.fechaHoraComparendo.setKeyListener(null);
            this.fechaHoraComparendo.setFocusable(false);
            this.fechaHoraComparendo.setError(null);
            this.detalleZona = ((FloatLabel) findViewById(R.id.detalleZona)).getEditText();
            this.detalleZona.setClickable(false);
            this.detalleZona.setKeyListener(null);
            this.detalleZona.setFocusable(false);
            this.numeroComparendo = ((FloatLabel) findViewById(R.id.numeroInmovilizacion)).getEditText();
            this.numeroComparendo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        numeroComparendo.setError(null);
                    }
                }
            });
            this.direccion = ((FloatLabel) findViewById(R.id.direccion)).getEditText();
            this.direccion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        direccion.setError(null);
                    }
                }
            });
            this.numeroPlaca = ((FloatLabel) findViewById(R.id.placaVehiculo)).getEditText();
            this.numeroPlaca.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            this.numeroPlaca.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        numeroPlaca.setError(null);
                    }
                }
            });
            this.numeroChasis = ((FloatLabel) findViewById(R.id.numChasis)).getEditText();
            this.numeroMotor = ((FloatLabel) findViewById(R.id.numMotor)).getEditText();
            this.numeroSerie = ((FloatLabel) findViewById(R.id.numSerie)).getEditText();
            this.numeroDocumento = ((FloatLabel) findViewById(R.id.documentoInfractor)).getEditText();
            this.numeroDocumento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        numeroDocumento.setError(null);
                    }
                }
            });
            this.numeroDocumento.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            this.nombreUno = ((FloatLabel) findViewById(R.id.nombreUno)).getEditText();
            this.nombreUno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        nombreUno.setError(null);
                    }
                }
            });
            this.nombreDos = ((FloatLabel) findViewById(R.id.nombreDos)).getEditText();
            this.apellidoUno = ((FloatLabel) findViewById(R.id.apellidoUno)).getEditText();
            this.apellidoUno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        apellidoUno.setError(null);
                    }
                }
            });
            this.apellidoDos = ((FloatLabel) findViewById(R.id.apellidoDos)).getEditText();
            this.observaciones = ((FloatLabel) findViewById(R.id.observaciones)).getEditText();
            this.btnBuscarVehiculo = (ImageButton) findViewById(R.id.btnBuscarVehiculo);
            this.btnBuscarVehiculo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        UtilidadesGenerales.hideSoftKeyboard(v);
                        if (numeroPlaca.getText().toString().equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                            builder.setTitle("Información");
                            builder.setMessage("El numero de placa es requerido.");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            try {
                                if (UtilidadesGenerales.isOnline()) {
                                    dialogoBuscando = new ProgressDialog(AgregarInformacionSIDGDUEActivity.this);
                                    dialogoBuscando.setMessage("Buscando vehiculo remotamente...");
                                    dialogoBuscando.setIndeterminate(true);
                                    dialogoBuscando.setCancelable(false);
                                    dialogoBuscando.show();
                                    BuscadorAsyncTask mAsyncTask = new BuscadorAsyncTask();
                                    Vehiculo veh = new Vehiculo();
                                    veh.setPlaca(numeroPlaca.getText().toString());
                                    mAsyncTask.execute(2, veh);
                                } else {
                                    buscarVehiculoLocalmente();
                                }
                                AgregarInformacionSIDGDUEActivity.this.isBusquedaVehiculo = true;
                            } catch (Exception ex) {
                                if (dialogoBuscando != null) {
                                    dialogoBuscando.dismiss();
                                }
                                ex.printStackTrace();
                            }
                        }
                    } catch (Exception ex2) {
                        ex2.printStackTrace();
                    }
                }
            });
            this.btnBuscarInfractor = (ImageButton) findViewById(R.id.btnBuscarInfractor);
            this.btnBuscarInfractor.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        UtilidadesGenerales.hideSoftKeyboard(v);
                        if (numeroDocumento.getText().toString().equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                            builder.setTitle("Información");
                            builder.setMessage("El numero de documento es requerido.");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            try {
                                if (UtilidadesGenerales.isOnline()) {
                                    dialogoBuscando = new ProgressDialog(AgregarInformacionSIDGDUEActivity.this);
                                    dialogoBuscando.setMessage("Buscando infractor remotamente...");
                                    dialogoBuscando.setIndeterminate(true);
                                    dialogoBuscando.setCancelable(false);
                                    dialogoBuscando.show();
                                    BuscadorAsyncTask mAsyncTask = new BuscadorAsyncTask();
                                    Persona inf = new Persona();
                                    if (AgregarInformacionSIDGDUEActivity.this.cmbTipoDocumento.getSelectedItem() != null) {
                                        inf.setId_tipo_identificacion(((TipoIdentificacion) AgregarInformacionSIDGDUEActivity.this.cmbTipoDocumento.getSelectedItem()).getId_tipo_identificacion());
                                    }
                                    inf.setNo_identificacion(AgregarInformacionSIDGDUEActivity.this.numeroDocumento.getText().toString());
                                    mAsyncTask.execute(1, inf);
                                } else {
                                    buscarInfractorLocalmente();
                                }
                                isBusquedaInfractor = true;
                            } catch (Exception ex) {
                                if (dialogoBuscando != null) {
                                    dialogoBuscando.dismiss();
                                }
                                ex.printStackTrace();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
            this.btnGrabarVideo = (Button) findViewById(R.id.btnGrabarVideo);
            this.btnGrabarVideo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60*5);
                        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            this.btnEliminarVideo = (Button) findViewById(R.id.btnEliminarVideo);
            this.btnEliminarVideo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (rutaVideo == null || rutaVideo.equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                            builder.setTitle("Información");
                            builder.setMessage("El inmovilizacion no tiene fotos asociadas para eliminar.");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                            mImageView.setImageDrawable(AgregarInformacionSIDGDUEActivity.this.getResources().getDrawable(R.drawable.imagenes));
                            mAttacher.update();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                            builder.setTitle("Confirmación");
                            builder.setMessage("¿Está usted seguro de querer realizar esta operacion?");
                            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    File video = new File(AgregarInformacionSIDGDUEActivity.this.rutaVideo);
                                    if (video.exists()) {
                                        video.delete();
                                        mostrarVideo.setVisibility(View.GONE);
                                    }
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
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
                }
            });
            this.btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
            this.btnTomarFoto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        if (AgregarInformacionSIDGDUEActivity.this.fotos.size() < 6) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File imagesFolder = new File(Environment.getExternalStorageDirectory(), RUTAMULTIMEDIAINMOVILIZACION);
                            if (!imagesFolder.exists()) imagesFolder.mkdirs();
                            rutafoto = inmovilizacion.getId_inmovilizacion() + "_foto_" + numeroFoto + ".jpg";
                            Uri uriSavedImage = null;
                            File image = new File(imagesFolder, rutafoto);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uriSavedImage = FileProvider.getUriForFile(AgregarInformacionSIDGDUEActivity.this, BuildConfig.APPLICATION_ID + ".provider", image);

                            } else {
                                uriSavedImage = Uri.fromFile(image);
                            }
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                            startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                            return;
                        }
                        UtilidadesGenerales.mostrarMensaje("Solo se permite 6 fotos como maximo.", 1);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            this.btnAtras = (Button) findViewById(R.id.btnAtras);
            this.btnAtras.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        if (posFoto - 1 >= 0) {
                            posFoto--;
                        }
                        if (posFoto >= 0) {
                            desplegarFoto(posFoto);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            this.btnEliminar = (Button) findViewById(R.id.btnEliminar);
            this.btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (fotos != null && fotos.size() > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                            builder.setTitle("Confirmación");
                            builder.setMessage("¿Está usted seguro de querer eliminar la foto seleccionada?");
                            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    File filefoto = new File(fotos.get(posFoto));
                                    if (filefoto.exists()) {
                                        filefoto.delete();
                                    }
                                    fotos.remove(AgregarInformacionSIDGDUEActivity.this.posFoto);
                                    posFoto--;
                                    if (fotos.size() > 0 && posFoto >= 0 && posFoto < fotos.size()) {
                                        desplegarFoto(posFoto);
                                    } else if (fotos.size() == 0) {
                                        mImageView.setImageDrawable(AgregarInformacionSIDGDUEActivity.this.getResources().getDrawable(R.drawable.imagenes));
                                        mAttacher.update();
                                        mostrarFotos.setVisibility(View.GONE);
                                    }
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                            builder.setTitle("Información");
                            builder.setMessage("El inmovilizacion no tiene fotos asociadas para eliminar.");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                            Drawable bitmap = getResources().getDrawable(R.drawable.imagenes);
                            mImageView.setImageDrawable(bitmap);
                            mAttacher.update();
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            this.btnSiguiente = (Button) findViewById(R.id.btnAdelante);
            this.btnSiguiente.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        if (posFoto + 1 < fotos.size()) {
                            posFoto++;
                        }
                        if (posFoto < fotos.size()) {
                            desplegarFoto(posFoto);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            actualizarControles();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class BuscadorAsyncTask extends AsyncTask<Object, String, Object> {
        private int opcion = -1;

        @Override
        protected Object doInBackground(Object... params) {
            Object resultado = null;
            try {
                WSGruparInterface service = WSGruparClient.getClient();
                opcion = (int) params[0];
                if (opcion == 1) {
                    Persona persona = (Persona) params[1];
                    Call<WSGruparResult<Persona>> personasCall = service.buscarpersona(persona.getId_tipo_identificacion(), persona.getNo_identificacion());
                    Response<WSGruparResult<Persona>> response = personasCall.execute();
                    if (response != null && response.isSuccessful()) {
                        WSGruparResult<Persona> result = response.body();
                        List<Persona> personas = result.getItems();
                        if (personas != null && personas.size() > 0) {
                            resultado = personas.get(0);
                        }
                    }
                } else if (opcion == 2) {
                    Vehiculo vehiculo = (Vehiculo) params[1];
                    Call<WSGruparResult<Vehiculo>> vehiculoCall = service.buscarVehiculo(vehiculo.getPlaca());
                    Response<WSGruparResult<Vehiculo>> response = vehiculoCall.execute();
                    if (response != null && response.isSuccessful()) {
                        WSGruparResult<Vehiculo> result = response.body();
                        List<Vehiculo> vehiculos = result.getItems();
                        if (vehiculos != null && vehiculos.size() > 0) {
                            resultado = vehiculos.get(0);
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "AutenticarUsuarioAsyncTask.doInBackground: fallo buscar vehiculo o infractor");
                Log.e(TAG, "", ex);
                publishProgress("Error al realizar la consulta.");
                return null;
            }
            return resultado;
        }

        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {
                UtilidadesGenerales.mostrarMensaje(values[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Object resultado) {
            super.onPostExecute(resultado);
            try {
                if (opcion == 1) {
                    if (resultado != null) {
                        List<Municipio> municipioInfractor;
                        List<Departamento> departamentoMuniInfractor;
                        String nombreCompleto = "";
                        Persona infractor = (Persona) resultado;
                        if (inmovilizacion != null) {
                            inmovilizacion.setInfractor(infractor);
                        }

                        nombreUno.setText("");
                        nombreDos.setText("");
                        apellidoUno.setText("");
                        apellidoDos.setText("");

                        numeroDocumento.setError(null);
                        nombreUno.setError(null);
                        apellidoUno.setError(null);

                        try {
                            municipioInfractor = municipioDao.queryBuilder().where(MunicipioDao.Properties.Id_municipio.eq(infractor.getId_municipio())).list();
                            if (municipioInfractor != null && municipioInfractor.size() > 0) {
                                SpinnerAdapter adapterDepartamentos = new ArrayAdapter(AgregarInformacionSIDGDUEActivity.this, android.R.layout.simple_spinner_dropdown_item, departamentoDao.queryBuilder().list().toArray());
                                departamentoMuniInfractor = departamentoDao.queryBuilder().where(DepartamentoDao.Properties.Id_departamento.eq((municipioInfractor.get(0)).getId_departamento())).list();
                                if (departamentoMuniInfractor != null && departamentoMuniInfractor.size() > 0) {
                                    cmbDepartamento.setAdapter(adapterDepartamentos, departamentoMuniInfractor.get(0));
                                    Object[] MunicipiosDepartamento = municipioDao.queryBuilder().where(MunicipioDao.Properties.Id_departamento.eq((municipioInfractor.get(0)).getId_departamento())).orderAsc(MunicipioDao.Properties.Nom_municipio).list().toArray();
                                    cmbMunicipio.setAdapter(new ArrayAdapter(AgregarInformacionSIDGDUEActivity.this, android.R.layout.simple_spinner_dropdown_item, MunicipiosDepartamento), municipioInfractor.get(0));
                                } else cmbDepartamento.setAdapter(adapterDepartamentos);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        if (infractor.getNombre1() != null && !infractor.getNombre1().equals("")) {
                            nombreUno.setText(infractor.getNombre1());
                            nombreCompleto += " " + infractor.getNombre1();
                        }
                        if (infractor.getNombre2() != null && !infractor.getNombre2().equals("")) {
                            nombreDos.setText(infractor.getNombre2());
                            nombreCompleto += " " + infractor.getNombre2();
                        }
                        if (infractor.getApellido1() != null && !infractor.getApellido1().equals("")) {
                            apellidoUno.setText(infractor.getApellido1());
                            nombreCompleto += " " + infractor.getApellido1();
                        }
                        if (infractor.getApellido2() != null && !infractor.getApellido2().equals("")) {
                            apellidoDos.setText(infractor.getApellido2());
                            nombreCompleto += " " + infractor.getApellido2();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                        builder.setTitle("Información");
                        builder.setMessage("Se encontro a " + nombreCompleto + ", asociado(a) al numero de documento ingresado. Consulta ejecutada remotamente.");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                AgregarInformacionSIDGDUEActivity.this.numeroDocumento.setError(null);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        mostrarCamposInfractor.setVisibility(View.VISIBLE);
                        dialogoBuscando.dismiss();
                    } else {
                        buscarInfractorLocalmente();
                    }
                } else if (opcion == 2) {
                    if (resultado != null) {
                        Object[] camposCombo;
                        List<TipoServicio> listadoTipoServicios;
                        SpinnerAdapter adapterComboTipoServicio;
                        List<ClaseVehiculo> listadoClaseVehiculos;
                        SpinnerAdapter adapterComboClaseVehiculo;
                        List<Color> listadoColor;
                        SpinnerAdapter adapterComboColor;

                        Vehiculo vehiculo = (Vehiculo) resultado;
                        if (inmovilizacion != null) {
                            inmovilizacion.setVehiculo(vehiculo);
                        }

                        numeroChasis.setText("");
                        numeroMotor.setText("");
                        numeroSerie.setText("");

                        numeroPlaca.setError(null);
                        numeroChasis.setError(null);
                        numeroMotor.setError(null);
                        numeroSerie.setError(null);

                        if (vehiculo.getPlaca() != null && !vehiculo.getPlaca().equals("")) {
                            numeroPlaca.setText(vehiculo.getPlaca());
                        }
                        if (vehiculo.getNo_chasis() != null && !vehiculo.getNo_chasis().equals("")) {
                            numeroChasis.setText(vehiculo.getNo_chasis());
                        }
                        if (vehiculo.getNo_motor() != null && !vehiculo.getNo_motor().equals("")) {
                            numeroMotor.setText(vehiculo.getNo_motor());
                        }
                        if (vehiculo.getNo_serie() != null && !vehiculo.getNo_serie().equals("")) {
                            numeroSerie.setText(vehiculo.getNo_serie());
                        }

                        camposCombo = tipoServicioDao.queryBuilder().list().toArray();
                        listadoTipoServicios = tipoServicioDao.queryBuilder().where(TipoServicioDao.Properties.Id_tipo_servicio.eq(vehiculo.getId_tipo_servicio())).list();
                        adapterComboTipoServicio = new ArrayAdapter(AgregarInformacionSIDGDUEActivity.this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
                        if (listadoTipoServicios != null && listadoTipoServicios.size() > 0) {
                            cmbTipoServicio.setAdapter(adapterComboTipoServicio, listadoTipoServicios.get(0));
                        } else cmbTipoServicio.setAdapter(adapterComboTipoServicio);

                        camposCombo = claseVehiculoDao.queryBuilder().list().toArray();
                        listadoClaseVehiculos = claseVehiculoDao.queryBuilder().where(ClaseVehiculoDao.Properties.Id_clase_vehiculo.eq(vehiculo.getId_clase_vehiculo())).list();
                        adapterComboClaseVehiculo = new ArrayAdapter(AgregarInformacionSIDGDUEActivity.this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
                        if (listadoClaseVehiculos != null && listadoClaseVehiculos.size() > 0) {
                            cmbClaseVehiculo.setAdapter(adapterComboClaseVehiculo, listadoClaseVehiculos.get(0));
                        } else cmbClaseVehiculo.setAdapter(adapterComboClaseVehiculo);

                        camposCombo = colorDao.queryBuilder().list().toArray();
                        listadoColor = colorDao.queryBuilder().where(ColorDao.Properties.Id_color.eq(vehiculo.getId_color())).list();
                        adapterComboColor = new ArrayAdapter(AgregarInformacionSIDGDUEActivity.this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
                        if (listadoColor != null && listadoColor.size() > 0) {
                            cmbColor.setAdapter(adapterComboColor, listadoColor.get(0));
                        } else cmbColor.setAdapter(adapterComboColor);


                        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                        builder.setTitle("Información");
                        builder.setMessage("Vehiculo encontrado remotamente.");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                AgregarInformacionSIDGDUEActivity.this.numeroDocumento.setError(null);
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        mostrarCamposVehiculo.setVisibility(View.VISIBLE);
                        dialogoBuscando.dismiss();
                    } else {
                        buscarVehiculoLocalmente();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void buscarInfractorLocalmente() {
        try {
            String tipoDocumento = "";
            if (this.cmbTipoDocumento.getSelectedItem() != null) {
                tipoDocumento = ((TipoIdentificacion) this.cmbTipoDocumento.getSelectedItem()).getId_tipo_identificacion();
            }
            QueryBuilder queryBuilder = this.personaDao.queryBuilder();
            List<Persona> infractores = queryBuilder.where(PersonaDao.Properties.No_identificacion.eq(this.numeroDocumento.getText().toString()), PersonaDao.Properties.Id_tipo_identificacion.eq(tipoDocumento)).list();
            if (infractores == null || infractores.size() <= 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                builder.setTitle("Información");
                builder.setMessage("No se encontro una persona asociada al numero de documento ingresado.");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AgregarInformacionSIDGDUEActivity.this.numeroDocumento.setError(null);
                        dialogoBuscando.dismiss();
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                List<Municipio> municipioInfractor;
                List<Departamento> departamentoMuniInfractor;
                String nombreCompleto = "";
                Persona infractor = infractores.get(0);
                if (this.inmovilizacion != null) {
                    this.inmovilizacion.setInfractor(infractor);
                }

                nombreUno.setText("");
                nombreDos.setText("");
                apellidoUno.setText("");
                apellidoDos.setText("");

                numeroDocumento.setError(null);
                nombreUno.setError(null);
                apellidoUno.setError(null);

                try {
                    municipioInfractor = this.municipioDao.queryBuilder().where(MunicipioDao.Properties.Id_municipio.eq(infractor.getId_municipio())).list();
                    if (municipioInfractor != null && municipioInfractor.size() > 0) {
                        SpinnerAdapter adapterDepartamentos = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, this.departamentoDao.queryBuilder().list().toArray());
                        departamentoMuniInfractor = departamentoDao.queryBuilder().where(DepartamentoDao.Properties.Id_departamento.eq((municipioInfractor.get(0)).getId_departamento())).list();
                        if (departamentoMuniInfractor != null && departamentoMuniInfractor.size() > 0) {
                            cmbDepartamento.setAdapter(adapterDepartamentos, departamentoMuniInfractor.get(0));
                            Object[] MunicipiosDepartamento = municipioDao.queryBuilder().where(MunicipioDao.Properties.Id_departamento.eq((municipioInfractor.get(0)).getId_departamento())).orderAsc(MunicipioDao.Properties.Nom_municipio).list().toArray();
                            cmbMunicipio.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, MunicipiosDepartamento), municipioInfractor.get(0));
                        } else cmbDepartamento.setAdapter(adapterDepartamentos);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (infractor.getNombre1() != null && !infractor.getNombre1().equals("")) {
                    nombreUno.setText(infractor.getNombre1());
                    nombreCompleto += " " + infractor.getNombre1();
                }
                if (infractor.getNombre2() != null && !infractor.getNombre2().equals("")) {
                    nombreDos.setText(infractor.getNombre2());
                    nombreCompleto += " " + infractor.getNombre2();
                }
                if (infractor.getApellido1() != null && !infractor.getApellido1().equals("")) {
                    apellidoUno.setText(infractor.getApellido1());
                    nombreCompleto += " " + infractor.getApellido1();
                }
                if (infractor.getApellido2() != null && !infractor.getApellido2().equals("")) {
                    apellidoDos.setText(infractor.getApellido2());
                    nombreCompleto += " " + infractor.getApellido2();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                builder.setTitle("Información");
                builder.setMessage("Se encontro a " + nombreCompleto + ", asociado(a) al numero de documento ingresado. Consulta ejecutada localmente.");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AgregarInformacionSIDGDUEActivity.this.numeroDocumento.setError(null);
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            mostrarCamposInfractor.setVisibility(View.VISIBLE);
            dialogoBuscando.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buscarVehiculoLocalmente() {
        try {
            List<Vehiculo> vehiculos = this.vehiculoDao.queryBuilder().where(VehiculoDao.Properties.Placa.eq(numeroPlaca.getText().toString())).list();
            if (vehiculos == null || vehiculos.size() <= 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                builder.setTitle("Información");
                builder.setMessage("No se encontró un vehículo asociado al número de placa ingresado.");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AgregarInformacionSIDGDUEActivity.this.numeroDocumento.setError(null);
                        dialogoBuscando.dismiss();
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Object[] camposCombo;
                List<TipoServicio> listadoTipoServicios;
                SpinnerAdapter adapterComboTipoServicio;
                List<ClaseVehiculo> listadoClaseVehiculos;
                SpinnerAdapter adapterComboClaseVehiculo;
                List<Color> listadoColor;
                SpinnerAdapter adapterComboColor;

                Vehiculo vehiculo = (Vehiculo) vehiculos.get(0);
                if (this.inmovilizacion != null) {
                    this.inmovilizacion.setVehiculo(vehiculo);
                }

                numeroChasis.setText("");
                numeroMotor.setText("");
                numeroSerie.setText("");

                numeroPlaca.setError(null);
                numeroChasis.setError(null);
                numeroMotor.setError(null);
                numeroSerie.setError(null);

                if (vehiculo.getPlaca() != null && !vehiculo.getPlaca().equals("")) {
                    numeroPlaca.setText(vehiculo.getPlaca());
                }
                if (vehiculo.getNo_chasis() != null && !vehiculo.getNo_chasis().equals("")) {
                    numeroChasis.setText(vehiculo.getNo_chasis());
                }
                if (vehiculo.getNo_motor() != null && !vehiculo.getNo_motor().equals("")) {
                    numeroMotor.setText(vehiculo.getNo_motor());
                }
                if (vehiculo.getNo_serie() != null && !vehiculo.getNo_serie().equals("")) {
                    numeroSerie.setText(vehiculo.getNo_serie());
                }

                camposCombo = tipoServicioDao.queryBuilder().list().toArray();
                listadoTipoServicios = tipoServicioDao.queryBuilder().where(TipoServicioDao.Properties.Id_tipo_servicio.eq(vehiculo.getId_tipo_servicio())).list();
                adapterComboTipoServicio = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
                if (listadoTipoServicios != null && listadoTipoServicios.size() > 0) {
                    cmbTipoServicio.setAdapter(adapterComboTipoServicio, listadoTipoServicios.get(0));
                } else cmbTipoServicio.setAdapter(adapterComboTipoServicio);

                camposCombo = claseVehiculoDao.queryBuilder().list().toArray();
                listadoClaseVehiculos = claseVehiculoDao.queryBuilder().where(ClaseVehiculoDao.Properties.Id_clase_vehiculo.eq(vehiculo.getId_clase_vehiculo())).list();
                adapterComboClaseVehiculo = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
                if (listadoClaseVehiculos != null && listadoClaseVehiculos.size() > 0) {
                    cmbClaseVehiculo.setAdapter(adapterComboClaseVehiculo, listadoClaseVehiculos.get(0));
                } else cmbClaseVehiculo.setAdapter(adapterComboClaseVehiculo);

                camposCombo = colorDao.queryBuilder().list().toArray();
                listadoColor = colorDao.queryBuilder().where(ColorDao.Properties.Id_color.eq(vehiculo.getId_color())).list();
                adapterComboColor = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
                if (listadoColor != null && listadoColor.size() > 0) {
                    cmbColor.setAdapter(adapterComboColor, listadoColor.get(0));
                } else cmbColor.setAdapter(adapterComboColor);

                AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                builder.setTitle("Información");
                builder.setMessage("Vehiculo encontrado localmente.");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AgregarInformacionSIDGDUEActivity.this.numeroDocumento.setError(null);
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            mostrarCamposVehiculo.setVisibility(View.VISIBLE);
            dialogoBuscando.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (this.mPlayTask != null) {
            detenerVideo();
            this.mPlayTask.waitForStop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.agregar_comparendo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void almacenarInmovilizacion() {
        try {
            QueryBuilder queryBuilder;

            AlertDialog.Builder builder;
            AlertDialog alert;

            this.inmovilizacion.setNo_comparendo(!this.numeroComparendo.getText().toString().equals("") ? Long.valueOf(Long.parseLong(this.numeroComparendo.getText().toString())) : null);
            this.inmovilizacion.setDireccion(this.direccion.getText().toString());
            this.inmovilizacion.setPropietario_presente(this.propietarioPresenteSiNo.isChecked() ? "SI" : "NO");
            this.inmovilizacion.setDesenganche(this.desengancheSiNo.isChecked() ? "SI" : "NO");
            if (this.cmbInfraccion.getSelectedItem() != null) {
                this.inmovilizacion.setId_infraccion(((Infraccion) this.cmbInfraccion.getSelectedItem()).getId_infraccion());
            }
            if (this.cmbGrua.getSelectedItem() != null) {
                this.inmovilizacion.setId_grua(((Grua) this.cmbGrua.getSelectedItem()).getId_grua());
            }
            if (this.cmbZona.getSelectedItem() != null) {
                this.inmovilizacion.setId_zona(((Zonas) this.cmbZona.getSelectedItem()).getId_zona());
            }
            if (this.cmbParqueadero.getSelectedItem() != null) {
                this.inmovilizacion.setId_parqueadero(((Parqueadero) this.cmbParqueadero.getSelectedItem()).getId_parqueadero());
            }
            if (this.cmbAgente.getSelectedItem() != null) {
                this.inmovilizacion.setId_agente(((Persona) this.cmbAgente.getSelectedItem()).getId_persona());
            }
            Vehiculo vehiculo = new Vehiculo();
            List<Vehiculo> verificarVehiculos;
            Persona persona;
            String tipoDocumento;
            List<Persona> verificarPersonas;

            if (this.propietarioPresenteSiNo.isChecked()) {
                try {
                    persona = new Persona();
                    tipoDocumento = "";
                    if (this.cmbTipoDocumento.getSelectedItem() != null) {
                        tipoDocumento = ((TipoIdentificacion) this.cmbTipoDocumento.getSelectedItem()).getId_tipo_identificacion();
                    }
                    queryBuilder = this.personaDao.queryBuilder();
                    verificarPersonas = queryBuilder.where(PersonaDao.Properties.No_identificacion.eq(this.numeroDocumento.getText().toString()), PersonaDao.Properties.Id_tipo_identificacion.eq(tipoDocumento)).list();
                    if (verificarPersonas == null || (verificarPersonas != null && verificarPersonas.size() == 0)) {
                        List<Persona> personas = this.personaDao.queryBuilder().orderDesc(PersonaDao.Properties.Id_persona).list();
                        if (personas == null || personas.size() <= 0) {
                            persona.setId_persona(1);
                        } else {
                            persona.setId_persona(Math.abs((personas.get(0)).getId_persona()) + 1);
                        }
                        persona.setNo_identificacion((this.numeroDocumento != null && this.numeroDocumento.getText().equals("") ? "-3" : this.numeroDocumento.getText().toString()));
                        persona.setId_tipo_identificacion(tipoDocumento);
                        persona.setNombre1(this.nombreUno.getText().toString());
                        persona.setNombre2(this.nombreDos.getText().toString());
                        persona.setApellido1(this.apellidoUno.getText().toString());
                        persona.setApellido2(this.apellidoDos.getText().toString());
                        if (this.cmbMunicipio.getSelectedItem() != null) {
                            persona.setId_municipio(((Municipio) this.cmbMunicipio.getSelectedItem()).getId_municipio());
                        }
                        if (this.personaDao.insert(persona) > 0) {
                            this.inmovilizacion.setInfractor(persona);
                        }
                    } else if (verificarPersonas != null && verificarPersonas.size() > 0) {
                        this.inmovilizacion.setInfractor(verificarPersonas.get(0));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }


            verificarVehiculos = this.vehiculoDao.queryBuilder().where(VehiculoDao.Properties.Placa.eq(this.numeroPlaca.getText().toString())).list();
            if (verificarVehiculos == null || (verificarVehiculos != null && verificarVehiculos.size() == 0)) {
                List<Vehiculo> vehiculos = this.vehiculoDao.queryBuilder().orderDesc(VehiculoDao.Properties.Id_vehiculo).list();
                try {
                    if (vehiculos == null || vehiculos.size() <= 0) {
                        vehiculo.setId_vehiculo(1);
                    } else {
                        vehiculo.setId_vehiculo(Math.abs((vehiculos.get(0)).getId_vehiculo()) + 1);
                    }
                    vehiculo.setPlaca((this.numeroPlaca != null && this.numeroPlaca.getText().equals("") ? "000000" : this.numeroPlaca.getText().toString()));
                    vehiculo.setNo_serie(this.numeroSerie.getText().toString());
                    vehiculo.setNo_chasis(this.numeroChasis.getText().toString());
                    vehiculo.setNo_motor(this.numeroMotor.getText().toString());
                    if (this.cmbColor.getSelectedItem() != null) {
                        vehiculo.setId_color(((Color) this.cmbColor.getSelectedItem()).getId_color());
                    }
                    if (this.cmbClaseVehiculo.getSelectedItem() != null) {
                        vehiculo.setId_clase_vehiculo(((ClaseVehiculo) this.cmbClaseVehiculo.getSelectedItem()).getId_clase_vehiculo());
                    }
                    if (this.cmbTipoServicio.getSelectedItem() != null) {
                        vehiculo.setId_tipo_servicio(((TipoServicio) this.cmbTipoServicio.getSelectedItem()).getId_tipo_servicio());
                    }
                    if (this.vehiculoDao.insert(vehiculo) > 0) {
                        this.inmovilizacion.setVehiculo(vehiculo);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (verificarVehiculos != null && verificarVehiculos.size() > 0) {
                this.inmovilizacion.setVehiculo(verificarVehiculos.get(0));
            }

            this.inmovilizacion.setObservacion(this.observaciones.getText().toString());
            this.inmovilizacion.setEstado("G");
            this.inmovilizacion.setId_usuario(Long.valueOf(this.app.getIdPersona()));
            long idInmovilizacion = inmovilizacionDao.insert(this.inmovilizacion);
            if (idInmovilizacion < 0) {
                builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                builder.setTitle("Información");
                builder.setMessage("La inmovilizacion no pudo ser almacenada correctamente.");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert = builder.create();
                alert.show();
                return;
            } else {
                builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                builder.setTitle("Información");
                builder.setMessage("Inmovilizacion almacenada correctamente.");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AgregarInformacionSIDGDUEActivity.this.setResult(-1, new Intent());
                        try {
                            if (UtilidadesGenerales.isOnline()) {
                                AgregarInformacionSIDGDUEActivity.this.startService(new Intent(AgregarInformacionSIDGDUEActivity.this, EnviarInformacionSIGDUEService.class));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        dialog.dismiss();
                        finish();
                    }
                });
                alert = builder.create();
                alert.show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_comparendo_save:
                almacenarInmovilizacion();
                return true;
            case R.id.menu_comparendo_cancel:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void desplegarFoto(int pos) {
        try {
            if (this.fotos != null && this.fotos.size() > 0) {
                this.mImageView.setImageBitmap(BitmapFactory.decodeFile(this.fotos.get(pos)));
                this.mAttacher.update();
            }
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    File image = new File(Environment.getExternalStorageDirectory() + "/" + RUTAMULTIMEDIAINMOVILIZACION + "/" + this.rutafoto);
                    Bitmap bMap = BitmapFactory.decodeFile(image.getAbsolutePath());
                    try {
                        OutputStream stream = new FileOutputStream(image);
                        bMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        stream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.fotos.add(Environment.getExternalStorageDirectory() + "/" + RUTAMULTIMEDIAINMOVILIZACION + "/" + this.rutafoto);
                    this.posFoto = this.fotos.size() - 1;
                    this.numeroFoto++;
                    desplegarFoto(this.posFoto);
                    this.mostrarFotos.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE && resultCode == -1) {
            guardarVideo(data);
            this.mostrarVideo.setVisibility(View.VISIBLE);
            mostrarVideo();
        }
    }

    private void guardarVideo(Intent data) {
        try {
            File videosFolder = new File(Environment.getExternalStorageDirectory(), RUTAMULTIMEDIAINMOVILIZACION);
            if (!videosFolder.exists()) videosFolder.mkdirs();
            rutaVideo = videosFolder.getPath() + "/" + inmovilizacion.getId_inmovilizacion() + "_video_" + ".mp4";
            Uri videoUri = data.getData();
            AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(videoUri, "r");
            FileInputStream fis = videoAsset.createInputStream();

            FileOutputStream fos = new FileOutputStream(rutaVideo);

            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fis.close();
            fos.close();

            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(videoUri, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        this.mSurfaceTextureReady = true;
        actualizarControles();
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }


    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        this.mSurfaceTextureReady = false;
        return true;
    }


    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void mostrarVideo() {
        try {
            if (this.mMostrarDetenerVideo) {
                Log.d(this.TAG, "stopping movie");
                detenerVideo();
                return;
            }
            File video = new File(this.rutaVideo);
            if (this.mPlayTask == null || !video.exists()) {
                Log.d(this.TAG, "starting movie");
                SpeedControlCallback callback = new SpeedControlCallback();
                Surface surface = new Surface(this.mTextureView.getSurfaceTexture());
                try {
                    MoviePlayer player = new MoviePlayer(video, surface, callback);
                    adjustAspectRatio(player.getVideoWidth(), player.getVideoHeight());
                    this.mPlayTask = new MoviePlayer.PlayTask(player, this);
                    this.mMostrarDetenerVideo = true;
                    actualizarControles();
                    this.mPlayTask.execute();
                    return;
                } catch (IOException ioe) {
                    Log.e(this.TAG, "Unable to play movie", ioe);
                    surface.release();
                    return;
                }
            }
            Log.w(this.TAG, "movie already playing");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void clickPlayStop(View unused) {
        mostrarVideo();
    }

    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        try {
            int newWidth;
            int newHeight;
            int viewWidth = this.mTextureView.getWidth();
            int viewHeight = this.mTextureView.getHeight();
            double aspectRatio = ((double) videoHeight) / ((double) videoWidth);
            if (viewHeight > ((int) (((double) viewWidth) * aspectRatio))) {
                newWidth = viewWidth;
                newHeight = (int) (((double) viewWidth) * aspectRatio);
            } else {
                newWidth = (int) (((double) viewHeight) / aspectRatio);
                newHeight = viewHeight;
            }
            int xoff = (viewWidth - newWidth) / 2;
            int yoff = (viewHeight - newHeight) / 2;
            Log.v(this.TAG, "video=" + videoWidth + "x" + videoHeight + " view=" + viewWidth + "x" + viewHeight + " newView=" + newWidth + "x" + newHeight + " off=" + xoff + "," + yoff);
            Matrix txform = new Matrix();
            this.mTextureView.getTransform(txform);
            txform.setScale(((float) newWidth) / ((float) viewWidth), ((float) newHeight) / ((float) viewHeight));
            txform.postTranslate((float) xoff, (float) yoff);
            this.mTextureView.setTransform(txform);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void detenerVideo() {
        try {
            if (this.mPlayTask != null) {
                this.mPlayTask.requestStop();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playbackStopped() {
        try {
            Log.d(this.TAG, "playback stopped");
            this.mMostrarDetenerVideo = false;
            this.mPlayTask = null;
            actualizarControles();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void actualizarControles() {
        try {
            Button play = (Button) findViewById(R.id.btnReproducirDetener);
            if (this.mMostrarDetenerVideo) {
                play.setText("Detener video");
            } else {
                play.setText("Ver video");
            }
            play.setEnabled(this.mSurfaceTextureReady);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
