package com.sigdue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.iangclifton.android.floatlabel.FloatLabel;
import com.sigdue.Constants;
import com.sigdue.R;
import com.sigdue.aplication.AplicacionSIGDUE;
import com.sigdue.asynctask.ActualizarInformacionPredioAsyncTask;
import com.sigdue.asynctask.ActualizarUbiGeoAsyncTask;
import com.sigdue.asynctask.AsyncTaskSIGDUE;
import com.sigdue.asynctask.CargarMultimediaAsyncTask;
import com.sigdue.asynctask.ProgressDialogFragment;
import com.sigdue.db.Archivo;
import com.sigdue.db.ArchivoDao;
import com.sigdue.db.DaoSession;
import com.sigdue.db.Parametro;
import com.sigdue.db.ParametroDao;
import com.sigdue.db.Predial;
import com.sigdue.db.PredialDao;
import com.sigdue.db.Usuario;
import com.sigdue.db.UsuarioDao;
import com.sigdue.service.LocationTrack;
import com.sigdue.ui.SearchableSpinner;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class AgregarInformacionSIDGDUEActivity extends AppCompatActivity implements AsyncTaskSIGDUE {

    private static final int PICK_FILES = 400;
    public static final int FORMULARIO_INFO_PREDIO = 3;
    public static final int FORMULARIO_CARGAR_MULTIMEDIA = 2;
    public static final int FORMULARIO_ACTUALIZAR_UBICACION = 1;
    private String TAG = "AgregarInmActivity";
    private AplicacionSIGDUE app;
    private Button btnAtras;
    private Button btnEliminar;
    private Button btnCargarArchivos;
    private Button btnSiguiente;
    private Button btnAdjuntarUrlVideo;
    private SearchableSpinner cmbClasePredio;
    private SearchableSpinner cmbClima;
    private SearchableSpinner cmbConQuienTieneTenencia;
    private SearchableSpinner cmbDistanciaMetrosSedePrincipal;
    private SearchableSpinner cmbDistanciaKilometrosCentroPoblado;
    private SearchableSpinner cmbPropiedadLote;
    private SearchableSpinner cmbTenencia;
    private SearchableSpinner cmbTipoDocumento;
    private SearchableSpinner cmbTopografia;
    private SearchableSpinner cmbZonaAislamiento;
    private SearchableSpinner cmbZonaAltoRiesgo;
    private SearchableSpinner cmbZonaProteccion;
    private DaoSession daoSession;
    private ParametroDao parametroDao;
    private PredialDao predialDao;
    private ArchivoDao archivoDao;
    private EditText fechaAvaluoCatastral;
    private DatePicker dp;
    private PhotoViewAttacher mAttacher;
    private ImageView mImageView;
    int numeroArchivo;
    int posArchivo;
    String rutaArchivo;
    private double longitude;
    private double latitude;
    private LocationTrack locationTrack;
    private Predial predial;
    private EditText avaluoCatastral;
    private EditText codigoDane;
    private EditText codigoPredio;
    private EditText cualTipoDocumento;
    private EditText numeroDocumentoLegalizacion;
    private EditText notariaDependenciaOrigen;
    private EditText lugarExpedicion;
    private EditText registroCatastral;
    private EditText matriculaInmobiliaria;
    private EditText propietarios;
    private EditText nombreTenencia;
    private EditText avaluoComercial;
    private EditText fechaAvaluoComercial;
    private EditText fechaExpedicion;
    private EditText fechaTenencia;
    private EditText urlVideo;
    private EditText descripcion;
    private List<Archivo> archivos;
    private MapView map = null;
    private UsuarioDao usuarioDao;
    private Usuario usuario;
    private int task;
    private EditText longitudEditext;
    private EditText latitudEditext;
    private LinearLayout formularioUbicacion;
    private LinearLayout formularioPredio;
    private LinearLayout formularioMultimedia;
    private LinearLayout mostrarArchivos;
    private LinearLayout mostrarMapa;
    private LinearLayout mostrarUrl;
    private LinearLayout mostrarCualTipoDocumento;
    private TextView titluloformulario;
    private ActualizarUbiGeoAsyncTask actualizarUbiGeoAsyncTask;
    private ActualizarInformacionPredioAsyncTask actualizarInformacionPredioAsyncTask;
    private CargarMultimediaAsyncTask cargarMultimediaAsyncTask;
    private ProgressDialogFragment mProgressDialogConsultaInfoSIGDUE = null;

    private BroadcastReceiver mHabilitarGPS = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                UtilidadesGenerales.habilitarGPS(context, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    private Marker startMarker;
    private IMapController mapController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UtilidadesGenerales.context = this;
            setContentView(R.layout.agregar_informacion_sigdue_activity);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                task = extras.getInt(Constants.TASK);
                titluloformulario = findViewById(R.id.datosInformacion);
                formularioMultimedia = findViewById(R.id.formularioMultimedia);
                formularioUbicacion = findViewById(R.id.formularioUbicacion);
                formularioPredio = findViewById(R.id.formularioInfoPredio);
                if (task == 1) {
                    titluloformulario.setText("Actualizar ubicación");
                    formularioUbicacion.setVisibility(View.VISIBLE);
                } else if (task == 2) {
                    titluloformulario.setText("Cargar multimedia");
                    formularioMultimedia.setVisibility(View.VISIBLE);
                } else if (task == 3) {
                    titluloformulario.setText("Información del predio");
                    formularioPredio.setVisibility(View.VISIBLE);
                }
            }

            Context ctx = getApplicationContext();
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

            locationTrack = new LocationTrack(AgregarInformacionSIDGDUEActivity.this);
            mostrarMapa = findViewById(R.id.mostrarMapa);
            mostrarMapa.setVisibility(UtilidadesGenerales.isOnline() ? View.VISIBLE : View.GONE);
            map = (MapView) findViewById(R.id.map);
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);
            map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

            latitudEditext = ((FloatLabel) findViewById(R.id.latitud)).getEditText();
            setSoloLectura(latitudEditext, false);

            longitudEditext = ((FloatLabel) findViewById(R.id.longitud)).getEditText();
            setSoloLectura(longitudEditext, false);

            numeroArchivo = 0;
            posArchivo = 0;
            rutaArchivo = "";

            this.app = (AplicacionSIGDUE) getApplication();
            this.daoSession = this.app.getDaoSession();
            this.predialDao = this.daoSession.getPredialDao();
            this.parametroDao = this.daoSession.getParametroDao();
            this.archivoDao = this.daoSession.getArchivoDao();
            this.usuarioDao = this.daoSession.getUsuarioDao();

            List<Usuario> usuarios = usuarioDao.queryBuilder().where(UsuarioDao.Properties.Id_usuario.eq(app.getIdUsuario())).list();
            if (usuarios != null && usuarios.size() > 0) {
                usuario = usuarios.get(0);
            }

            cmbClasePredio = (SearchableSpinner) findViewById(R.id.cmbClasePredio);
            setComboConf(cmbClasePredio, "Clase predio", Constants.CLASE_PREDIO);

            cmbClima = (SearchableSpinner) findViewById(R.id.cmbClima);
            setComboConf(cmbClima, "Clima", Constants.CLIMA);

            cmbConQuienTieneTenencia = (SearchableSpinner) findViewById(R.id.cmbConQuienTenencia);
            setComboConf(cmbConQuienTieneTenencia, "Con quien tiene tenencia", Constants.CON_QUIEN_TIENE_TENENCIA);

            cmbDistanciaMetrosSedePrincipal = (SearchableSpinner) findViewById(R.id.cmbDitanciaSedePrincipal);
            setComboConf(cmbDistanciaMetrosSedePrincipal, "Distancia en metros sede principal", Constants.DISTANCIA_METROS_SEDE_PRINCIPAL);

            cmbDistanciaKilometrosCentroPoblado = (SearchableSpinner) findViewById(R.id.cmbDistanciaPoblado);
            setComboConf(cmbDistanciaKilometrosCentroPoblado, "Distancia en kilometros centro poblado", Constants.DISTANCIA_EN_KILOMETROS_CENTRO_POBLADO);

            cmbPropiedadLote = (SearchableSpinner) findViewById(R.id.cmbPropiedadLote);
            setComboConf(cmbPropiedadLote, "Propiedad del lote", Constants.PROPIEDAD_LOTE);

            cmbTenencia = (SearchableSpinner) findViewById(R.id.cmbTenencia);
            setComboConf(cmbTenencia, "Tenencia", Constants.TENENCIA);

            cmbTopografia = (SearchableSpinner) findViewById(R.id.cmbTopografia);
            setComboConf(cmbTopografia, "Topografía", Constants.TOPOGRAFIA);

            mostrarCualTipoDocumento = findViewById(R.id.mostrarCualTipoDocumento);
            mostrarCualTipoDocumento.setVisibility(View.GONE);

            cmbTipoDocumento = (SearchableSpinner) findViewById(R.id.cmbTipoDocumento);
            setComboConf(cmbTipoDocumento, "Tipo documento", Constants.TIPO_DOCUMENTO);
            this.cmbTipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (((Parametro) parent.getItemAtPosition(position)).getParametro().equals("09 - Otro Documento"))
                        mostrarCualTipoDocumento.setVisibility(View.VISIBLE);
                    else mostrarCualTipoDocumento.setVisibility(View.GONE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            cmbZonaAislamiento = (SearchableSpinner) findViewById(R.id.cmbZonaAislamiento);
            setComboConf(cmbZonaAislamiento, "Zona de aislamiento", Constants.ZONA_AISLAMIENTO);

            cmbZonaAltoRiesgo = (SearchableSpinner) findViewById(R.id.cmbZonaAltoRiesgo);
            setComboConf(cmbZonaAltoRiesgo, "Zona de alto riesgo", Constants.ZONA_ALTO_RIESGO);

            cmbZonaProteccion = (SearchableSpinner) findViewById(R.id.cmbZonaProteccion);
            setComboConf(cmbZonaProteccion, "Zona de protección", Constants.ZONA_PROTECCION);

            this.mImageView = (ImageView) findViewById(R.id.imagen);
            Drawable bitmap = getResources().getDrawable(R.drawable.imagenes);
            this.mImageView.setImageDrawable(bitmap);
            this.mAttacher = new PhotoViewAttacher(this.mImageView);
            this.predial = null;

            codigoDane = ((FloatLabel) findViewById(R.id.dane_sede)).getEditText();
            codigoDane.setText(app.getUsuario());
            setSoloLectura(codigoDane, false);
            codigoPredio = ((FloatLabel) findViewById(R.id.codigo_predio)).getEditText();
            avaluoCatastral = ((FloatLabel) findViewById(R.id.avaluo_catastral)).getEditText();
            avaluoCatastral.setText("0");
            avaluoComercial = ((FloatLabel) findViewById(R.id.avaluo_comercial)).getEditText();
            avaluoComercial.setText("0");
            cualTipoDocumento = ((FloatLabel) findViewById(R.id.cual_tipo_documento)).getEditText();
            numeroDocumentoLegalizacion = ((FloatLabel) findViewById(R.id.nro_documento_legalizacion)).getEditText();
            notariaDependenciaOrigen = ((FloatLabel) findViewById(R.id.notaria_dependencia_origen)).getEditText();
            lugarExpedicion = ((FloatLabel) findViewById(R.id.lugar_expedicion)).getEditText();
            registroCatastral = ((FloatLabel) findViewById(R.id.registro_catastral)).getEditText();
            matriculaInmobiliaria = ((FloatLabel) findViewById(R.id.matricula_inmobiliaria)).getEditText();
            propietarios = ((FloatLabel) findViewById(R.id.propietarios)).getEditText();
            nombreTenencia = ((FloatLabel) findViewById(R.id.nombre_tiene_tenencia)).getEditText();
            urlVideo = ((FloatLabel) findViewById(R.id.url_video)).getEditText();
            urlVideo.addTextChangedListener(
                    new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            try {
                                if (archivos.size() > 0 && posArchivo >= 0 && posArchivo < archivos.size()) {
                                    archivos.get(posArchivo).setNombre(urlVideo.getText().toString());
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
            );

            mostrarUrl = findViewById(R.id.mostrarURlVideo);
            mostrarUrl.setVisibility(View.GONE);
            mostrarArchivos = findViewById(R.id.mostrarArchivos);
            mostrarArchivos.setVisibility(View.GONE);

            descripcion = ((FloatLabel) findViewById(R.id.descripcion)).getEditText();
            descripcion.addTextChangedListener(
                    new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            try {
                                if (archivos.size() > 0 && posArchivo >= 0 && posArchivo < archivos.size()) {
                                    archivos.get(posArchivo).setDescripcion(descripcion.getText().toString());
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
            );
            fechaAvaluoCatastral = ((FloatLabel) findViewById(R.id.fecha_avaluo)).getEditText();
            setFechaConf(fechaAvaluoCatastral, "Fecha avalúo catastral", avaluoComercial);

            fechaAvaluoComercial = ((FloatLabel) findViewById(R.id.fecha_avaluo_comercial)).getEditText();
            setFechaConf(fechaAvaluoComercial, "Fecha avalúo comercial", cualTipoDocumento);

            fechaExpedicion = ((FloatLabel) findViewById(R.id.fecha_expedicion)).getEditText();
            setFechaConf(fechaExpedicion, "Fecha expedición", notariaDependenciaOrigen);

            fechaTenencia = ((FloatLabel) findViewById(R.id.fecha_tenencia)).getEditText();
            setFechaConf(fechaTenencia, "Fecha tenencia", urlVideo);

            incializarInformacionDelPredio();
            this.btnAdjuntarUrlVideo = (Button) findViewById(R.id.btnCargarVideo);
            btnAdjuntarUrlVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Archivo archivo = new Archivo();
                    archivo.setId_usuario(usuario.getId_usuario());
                    archivo.setRuta("");
                    archivo.setTipo("Video");
                    archivo.setMedia_type("text/uri-list");
                    archivo.setNombre("");
                    setIdArchivo(archivo);
                    archivo.setEstado("P");
                    archivos.add(archivo);
                    urlVideo.setText("");
                    posArchivo = archivos.size() - 1;
                    desplegarArchivo(posArchivo);
                }
            });
            this.btnCargarArchivos = (Button) findViewById(R.id.btnCargarArchivos);
            this.btnCargarArchivos.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        final String[] ACCEPT_MIME_TYPES = {
                                "application/pdf",
                                "image/*"
                        };
                        Intent intent = new Intent();
                        if (Build.VERSION.SDK_INT < 19) {
                            intent.setType("*/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, ACCEPT_MIME_TYPES);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(Intent.createChooser(intent, "Adjuntar archivos"), PICK_FILES);
                        } else {
                            intent = new Intent();
                            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setType("*/*");
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, ACCEPT_MIME_TYPES);
                            startActivityForResult(Intent.createChooser(intent, "Adjuntar archivos"), PICK_FILES);

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            this.btnAtras = (Button) findViewById(R.id.btnAtras);
            this.btnAtras.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        if (posArchivo - 1 >= 0) {
                            posArchivo--;
                        }
                        if (posArchivo >= 0) {
                            desplegarArchivo(posArchivo);
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
                        if (archivos != null && archivos.size() > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                            builder.setTitle("Confirmación");
                            builder.setMessage("¿Está usted seguro de eliminar el archivo seleccionado?");
                            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Archivo archivo = archivoDao.queryBuilder().where(ArchivoDao.Properties.Id_archivo.eq(archivos.get(posArchivo).getId_archivo())).unique();
                                    if (archivo != null && archivo.getId_archivo() > 0) {
                                        archivoDao.delete(archivo);
                                    }
                                    archivos.remove(posArchivo);
                                    posArchivo = 0;
                                    if (!archivos.isEmpty()) {
                                        desplegarArchivo(posArchivo);
                                    } else {
                                        mostrarArchivos.setVisibility(View.GONE);
                                        mImageView.setImageDrawable(AgregarInformacionSIDGDUEActivity.this.getResources().getDrawable(R.color.actionbar_divider));
                                        mAttacher.update();
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
                            builder.setMessage("No existen archivos pendientes por eliminar.");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                            mImageView.setImageDrawable(AgregarInformacionSIDGDUEActivity.this.getResources().getDrawable(R.color.actionbar_divider));
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
                        if (posArchivo + 1 < archivos.size()) {
                            posArchivo++;
                        }
                        if (posArchivo < archivos.size()) {
                            desplegarArchivo(posArchivo);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            List<Archivo> archivosBd = archivoDao.queryBuilder().where(ArchivoDao.Properties.Id_usuario.eq(app.getIdUsuario())).list();
            if (archivosBd != null && !archivosBd.isEmpty() && task == FORMULARIO_CARGAR_MULTIMEDIA) {
                archivos = archivosBd;
                mostrarArchivos.setVisibility(View.VISIBLE);
                desplegarArchivo(posArchivo);
            } else {
                archivos = new ArrayList<>();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void confMapa() {
        if (locationTrack.canGetLocation()) {
            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();

            latitudEditext.setText(String.valueOf(latitude));
            longitudEditext.setText(String.valueOf(longitude));

            mapController = map.getController();
            mapController.setZoom(19.0);
            GeoPoint startPoint = new GeoPoint(latitude, longitude);
            mapController.setCenter(startPoint);
            startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setTitle("Establezca su ubicación");
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            MapEventsReceiver mReceive = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
                    mapController.animateTo(p);
                    actualizarPosicionMarcador(p.getLatitude(), p.getLongitude());
                    return false;
                }

                @Override
                public boolean longPressHelper(GeoPoint p) {
                    return false;
                }
            };
            map.getOverlays().add(new MapEventsOverlay(mReceive));
            map.getOverlays().add(startMarker);

        }
    }

    private void actualizarPosicionMarcador(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        latitudEditext.setText(String.valueOf(latitude));
        longitudEditext.setText(String.valueOf(longitude));
        this.startMarker.setPosition(new GeoPoint(latitude, longitude));
    }

    private void incializarInformacionDelPredio() {
        List<Predial> infoPredial = predialDao.queryBuilder().where(PredialDao.Properties.Dane_sede.eq(app.getUsuario())).list();
        if (infoPredial != null && !infoPredial.isEmpty()) {
            predial = infoPredial.get(0);
            this.codigoDane.setText(this.predial.getDane_sede() != null ? this.predial.getDane_sede() : "");
            this.codigoPredio.setText(this.predial.getCod_predio() != null ? this.predial.getCod_predio() : "");
            setParametroCombo(this.predial.getClima(), cmbClima, Constants.CLIMA);
            setParametroCombo(this.predial.getDistancia_mts_sede_ppal(), cmbDistanciaMetrosSedePrincipal, Constants.DISTANCIA_METROS_SEDE_PRINCIPAL);
            setParametroCombo(this.predial.getDist_km_centro_poblado(), cmbDistanciaKilometrosCentroPoblado, Constants.DISTANCIA_EN_KILOMETROS_CENTRO_POBLADO);
            setParametroCombo(this.predial.getClase_predio(), cmbClasePredio, Constants.CLASE_PREDIO);
            this.avaluoCatastral.setText(this.predial.getAvaluo_catastral());
            this.fechaAvaluoCatastral.setText(this.predial.getFec_avaluo_catastral());
            this.avaluoComercial.setText(this.predial.getAvaluo_comercial());
            this.fechaAvaluoComercial.setText(this.predial.getFec_avaluo_comercial());
            setParametroCombo(this.predial.getZona_aislamiento(), cmbZonaAislamiento, Constants.ZONA_AISLAMIENTO);
            setParametroCombo(this.predial.getZona_alto_riesgo(), cmbZonaAltoRiesgo, Constants.ZONA_ALTO_RIESGO);
            setParametroCombo(this.predial.getZona_proteccion(), cmbZonaProteccion, Constants.ZONA_PROTECCION);
            setParametroCombo(this.predial.getTopografia(), cmbTopografia, Constants.TOPOGRAFIA);
            setParametroCombo(this.predial.getPropiedad_lote(), cmbPropiedadLote, Constants.PROPIEDAD_LOTE);
            setParametroCombo(this.predial.getTipo_documento(), cmbTipoDocumento, Constants.TIPO_DOCUMENTO);
            this.cualTipoDocumento.setText(this.predial.getCual_tipo_documento());
            this.numeroDocumentoLegalizacion.setText(this.predial.getNro_documento_legalizacion());
            this.fechaExpedicion.setText(this.predial.getFec_expedicion());
            this.notariaDependenciaOrigen.setText(this.predial.getNotaria_dependencia_origen());
            this.lugarExpedicion.setText(this.predial.getLugar_expedicion());
            this.registroCatastral.setText(this.predial.getRegistro_catastral());
            this.matriculaInmobiliaria.setText(this.predial.getMatricula_inmobiliaria());
            this.propietarios.setText(this.predial.getPropietarios());
            this.nombreTenencia.setText(this.predial.getNom_quien_tenencia());
            setParametroCombo(this.predial.getTenencia(), cmbTenencia, Constants.TENENCIA);
            setParametroCombo(this.predial.getCon_quien_tenencia(), cmbConQuienTieneTenencia, Constants.CON_QUIEN_TIENE_TENENCIA);
            this.fechaTenencia.setText(this.predial.getFecha_tenencia_lote());
        } else {
            predial = new Predial();
        }
    }

    private void setParametroCombo(String parametro, SearchableSpinner searchableSpinner, long tipoParametro) {
        if (parametro == null) return;
        List<Parametro> parametros = parametroDao.queryBuilder().where(ParametroDao.Properties.Parametro.eq(parametro), ParametroDao.Properties.Tipo.eq(tipoParametro)).list();
        Object[] camposCombo = this.parametroDao.queryBuilder().where(ParametroDao.Properties.Tipo.eq(tipoParametro)).list().toArray();
        SpinnerAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
        if (parametros != null && !parametros.isEmpty()) {
            searchableSpinner.setAdapter(adapter, parametros.get(0));
        }
    }

    private void setSoloLectura(EditText editText, boolean soloLectura) {
        editText.setClickable(soloLectura);
        editText.setKeyListener(null);
        editText.setFocusable(soloLectura);
        editText.setError(null);
    }

    private void setFechaConf(final EditText fechaEditText, final String titulo, final EditText editTextNext) {
        final SimpleDateFormat formatFechaHora = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = formatFechaHora.format(new Date());
        fechaEditText.setText(fecha);
        setSoloLectura(fechaEditText, true);
        fechaEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try {
                    Dialog dialog = null;
                    if (hasFocus) {
                        UtilidadesGenerales.hideSoftKeyboard(v);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.datetimepicker_dialog, null);
                        dp = dialogView.findViewById(R.id.datePicker);
                        String fecha = fechaEditText.getText().toString();
                        if (fecha != null && !fecha.equals("")) {
                            try {
                                Date d = formatFechaHora.parse(fecha);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(d);
                                int year = cal.get(Calendar.YEAR);
                                int month = cal.get(Calendar.MONTH);
                                int day = cal.get(Calendar.DAY_OF_MONTH);
                                dp.updateDate(year, month, day);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        dialogBuilder.setView(dialogView);
                        dialogBuilder.setTitle(titulo);
                        dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    int currentApiVersion = Build.VERSION.SDK_INT;
                                    if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                                        fechaEditText.setText((dp.getDayOfMonth() < 10 ? "0" + dp.getDayOfMonth() : dp.getDayOfMonth()) + "/" + ((dp.getMonth() + 1) < 10 ? "0" + (dp.getMonth() + 1) : (dp.getMonth() + 1)) + "/" + dp.getYear());
                                    } else {
                                        fechaEditText.setText((dp.getDayOfMonth() < 10 ? "0" + dp.getDayOfMonth() : dp.getDayOfMonth()) + "/" + ((dp.getMonth() + 1) < 10 ? "0" + (dp.getMonth() + 1) : (dp.getMonth() + 1)) + "/" + dp.getYear());
                                    }
                                    if (editTextNext != null) editTextNext.requestFocus();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (editTextNext != null) editTextNext.requestFocus();
                            }
                        });
                        dialog = dialogBuilder.create();
                        dialog.show();
                    } else {
                        if (dialog != null) dialog.cancel();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void setComboConf(SearchableSpinner cmb, String titulo, int tipoParametro) {
        cmb.setTitle(titulo);
        cmb.setPositiveButton("Cerrar");
        Object[] camposCombo = this.parametroDao.queryBuilder().where(ParametroDao.Properties.Tipo.eq(tipoParametro)).list().toArray();
        SpinnerAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
        cmb.setAdapter(adapter);
    }


    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
        if (task == FORMULARIO_ACTUALIZAR_UBICACION) {
            unregisterReceiver(mHabilitarGPS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (task == FORMULARIO_ACTUALIZAR_UBICACION) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.location.PROVIDERS_CHANGED");
            registerReceiver(mHabilitarGPS, filter);
            confMapa();
        }
        map.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        confirmarSalida();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.agregar_comparendo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void almacenarInformacionSIGDUE() {
        try {
            if (task == FORMULARIO_INFO_PREDIO) {
                boolean actualizar = !(this.predial.getDane_sede() == null || this.predial.getDane_sede().isEmpty());
                this.predial.setDane_sede(this.codigoDane.getText().toString());
                this.predial.setCod_predio(this.codigoPredio.getText().toString());
                if (this.cmbClima.getSelectedItem() != null) {
                    this.predial.setClima(((Parametro) this.cmbClima.getSelectedItem()).getParametro());
                }
                if (this.cmbDistanciaMetrosSedePrincipal.getSelectedItem() != null) {
                    this.predial.setDistancia_mts_sede_ppal(((Parametro) this.cmbDistanciaMetrosSedePrincipal.getSelectedItem()).getParametro());
                }
                if (this.cmbDistanciaKilometrosCentroPoblado.getSelectedItem() != null) {
                    this.predial.setDist_km_centro_poblado(((Parametro) this.cmbDistanciaKilometrosCentroPoblado.getSelectedItem()).getParametro());
                }
                if (this.cmbClasePredio.getSelectedItem() != null) {
                    this.predial.setClase_predio(((Parametro) this.cmbClasePredio.getSelectedItem()).getParametro());
                }
                this.predial.setAvaluo_catastral(this.avaluoCatastral.getText().toString());
                this.predial.setFec_avaluo_catastral(this.fechaAvaluoCatastral.getText().toString());
                this.predial.setAvaluo_comercial(this.avaluoComercial.getText().toString());
                this.predial.setFec_avaluo_comercial(this.fechaAvaluoComercial.getText().toString());

                if (this.cmbZonaAislamiento.getSelectedItem() != null) {
                    this.predial.setZona_aislamiento(((Parametro) this.cmbZonaAislamiento.getSelectedItem()).getParametro());
                }
                if (this.cmbZonaAltoRiesgo.getSelectedItem() != null) {
                    this.predial.setZona_alto_riesgo(((Parametro) this.cmbZonaAltoRiesgo.getSelectedItem()).getParametro());
                }
                if (this.cmbZonaProteccion.getSelectedItem() != null) {
                    this.predial.setZona_proteccion(((Parametro) this.cmbZonaProteccion.getSelectedItem()).getParametro());
                }
                if (this.cmbTopografia.getSelectedItem() != null) {
                    this.predial.setTopografia(((Parametro) this.cmbTopografia.getSelectedItem()).getParametro());
                }
                if (this.cmbPropiedadLote.getSelectedItem() != null) {
                    this.predial.setPropiedad_lote(((Parametro) this.cmbPropiedadLote.getSelectedItem()).getParametro());
                }
                if (this.cmbTipoDocumento.getSelectedItem() != null) {
                    this.predial.setTipo_documento(((Parametro) this.cmbTipoDocumento.getSelectedItem()).getParametro());
                }
                this.predial.setCual_tipo_documento(this.cualTipoDocumento.getText().toString());
                this.predial.setNro_documento_legalizacion(this.numeroDocumentoLegalizacion.getText().toString());
                this.predial.setFec_expedicion(this.fechaExpedicion.getText().toString());
                this.predial.setNotaria_dependencia_origen(this.notariaDependenciaOrigen.getText().toString());
                this.predial.setLugar_expedicion(this.lugarExpedicion.getText().toString());
                this.predial.setRegistro_catastral(this.registroCatastral.getText().toString());
                this.predial.setMatricula_inmobiliaria(this.matriculaInmobiliaria.getText().toString());
                this.predial.setPropietarios(this.propietarios.getText().toString());
                this.predial.setNom_quien_tenencia(this.nombreTenencia.getText().toString());
                if (this.cmbTenencia.getSelectedItem() != null) {
                    this.predial.setTenencia(((Parametro) this.cmbTenencia.getSelectedItem()).getParametro());
                }
                if (this.cmbConQuienTieneTenencia.getSelectedItem() != null) {
                    this.predial.setCon_quien_tenencia(((Parametro) this.cmbConQuienTieneTenencia.getSelectedItem()).getParametro());
                }
                this.predial.setFecha_tenencia_lote(this.fechaTenencia.getText().toString());
                if (archivos != null) archivoDao.insertInTx(archivos);
                if (actualizar) predialDao.update(this.predial);
                else predialDao.insert(this.predial);
                if (UtilidadesGenerales.isOnline()) {
                    showProgress("Actualizando información del predio");
                    actualizarInformacionPredioAsyncTask = new ActualizarInformacionPredioAsyncTask(this, mProgressDialogConsultaInfoSIGDUE);
                    actualizarInformacionPredioAsyncTask.execute(predial);
                }
            } else if (task == FORMULARIO_CARGAR_MULTIMEDIA) {
                archivoDao.insertOrReplaceInTx(archivos);
                if (UtilidadesGenerales.isOnline()) {
                    showProgress("Actualizando multimedia");
                    cargarMultimediaAsyncTask = new CargarMultimediaAsyncTask(this, AgregarInformacionSIDGDUEActivity.this, daoSession, mProgressDialogConsultaInfoSIGDUE);
                    cargarMultimediaAsyncTask.execute(String.valueOf(usuario.getId_usuario()), usuario.getUsuario());
                }
            } else if (task == FORMULARIO_ACTUALIZAR_UBICACION) {
                this.usuario.setLongitude(String.valueOf(longitude));
                this.usuario.setLatitude(String.valueOf(latitude));
                this.usuarioDao.update(usuario);
                if (UtilidadesGenerales.isOnline()) {
                    showProgress("Actualizando ubicación");
                    actualizarUbiGeoAsyncTask = new ActualizarUbiGeoAsyncTask(this, mProgressDialogConsultaInfoSIGDUE);
                    actualizarUbiGeoAsyncTask.execute(usuario);
                }
            }
        } catch (Exception ex) {
            mostrarError();
            ex.printStackTrace();
        }
    }

    protected void showProgress(String mensaje) {
        mProgressDialogConsultaInfoSIGDUE = ProgressDialogFragment.newInstance(null, this, mensaje);
        mProgressDialogConsultaInfoSIGDUE.show(getSupportFragmentManager(), "dialog_loading");
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

    private void confirmarAlmacenamientoExitoso() {
        AlertDialog.Builder builder;
        AlertDialog alert;
        builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
        builder.setTitle("Información");
        builder.setMessage("Información almacenada correctamente.");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AgregarInformacionSIDGDUEActivity.this.setResult(-1, new Intent());
                dialog.dismiss();
                finish();
            }
        });
        alert = builder.create();
        alert.show();
    }

    private void mostrarError() {
        AlertDialog.Builder builder;
        AlertDialog alert;
        builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
        builder.setTitle("Información");
        builder.setMessage("La información no pudo ser almacenada correctamente.");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert = builder.create();
        alert.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_comparendo_save:
                almacenarInformacionSIGDUE();
                return true;
            case R.id.menu_comparendo_cancel:
                confirmarSalida();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void confirmarSalida() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarInformacionSIDGDUEActivity.this);
        builder.setTitle("Confirmación");
        builder.setCancelable(false);
        builder.setMessage("¿Está usted seguro de salir, sin antes almacenar la información?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                usuario = null;
                predial = null;
                dialog.dismiss();
                finish();
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


    public void desplegarArchivo(int pos) {
        try {
            if (this.archivos != null && this.archivos.size() > 0) {
                mostrarArchivos.setVisibility(View.VISIBLE);
                this.descripcion.setText(this.archivos.get(pos).getDescripcion());
                mostrarUrl.setVisibility(View.GONE);

                if (this.archivos.get(pos).getTipo().equals("Imagen")) {
                    final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(Uri.parse(this.archivos.get(pos).getRuta()), takeFlags);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(this.archivos.get(pos).getRuta()));
                    this.mImageView.setImageBitmap(bitmap);
                    this.mAttacher.update();
                } else if (this.archivos.get(pos).getTipo().equals("Pdf")) {
                    mImageView.setImageDrawable(AgregarInformacionSIDGDUEActivity.this.getResources().getDrawable(R.drawable.pdf));
                    this.mAttacher.update();
                } else {
                    mostrarUrl.setVisibility(View.VISIBLE);
                    this.urlVideo.setText(this.archivos.get(pos).getNombre());
                    mImageView.setImageDrawable(AgregarInformacionSIDGDUEActivity.this.getResources().getDrawable(R.drawable.video));
                    this.mAttacher.update();
                }
            }
        } catch (Exception ex) {
            mImageView.setImageDrawable(AgregarInformacionSIDGDUEActivity.this.getResources().getDrawable(R.color.actionbar_divider));
            this.mAttacher.update();
            ex.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver cR = getContentResolver();
        if (requestCode == PICK_FILES) {
            if (null != data) {
                if (null != data.getClipData()) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        Archivo archivo = new Archivo();
                        setIdArchivo(archivo);
                        archivo.setId_usuario(usuario.getId_usuario());
                        archivo.setRuta(uri.toString());
                        archivo.setMedia_type(cR.getType(uri));
                        archivo.setTipo(archivo.getMedia_type().equals("image/jpeg") ? "Imagen" : "Pdf");
                        archivo.setNombre(getFileName(uri));
                        archivo.setDescripcion(archivo.getNombre());
                        archivo.setEstado("P");
                        archivos.add(archivo);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    Archivo archivo = new Archivo();
                    archivo.setId_usuario(usuario.getId_usuario());
                    archivo.setRuta(uri.toString());
                    archivo.setMedia_type(cR.getType(uri));
                    archivo.setTipo(archivo.getMedia_type().equals("image/jpeg") ? "Imagen" : "Pdf");
                    archivo.setNombre(getFileName(uri));
                    archivo.setDescripcion(archivo.getNombre());
                    setIdArchivo(archivo);
                    archivo.setEstado("P");
                    archivos.add(archivo);
                }
                if (!archivos.isEmpty()) {
                    posArchivo = 0;
                    desplegarArchivo(posArchivo);
                }
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void setIdArchivo(Archivo archivo) {
        List<Archivo> archivosBD = this.archivoDao.queryBuilder().orderDesc(ArchivoDao.Properties.Id_archivo).list();
        if (archivosBD == null || archivosBD.size() <= 0) {
            archivo.setId_archivo(1 + archivos.size());
        } else {
            archivo.setId_archivo((archivosBD.get(0)).getId_archivo() + 1 + archivos.size());
        }
    }

    @Override
    public void onPostExecute(Object result) {
        actualizarUbiGeoAsyncTask = null;
        actualizarInformacionPredioAsyncTask = null;
        cargarMultimediaAsyncTask = null;
        hideProgress();
        if (result != null && ((Boolean) result)) {
            confirmarAlmacenamientoExitoso();
        } else {
            mostrarError();
        }
    }

    @Override
    public void onCancelled() {
        hideProgress();
    }


}
