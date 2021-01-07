package com.sigdue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;

import com.iangclifton.android.floatlabel.FloatLabel;
import com.sigdue.BuildConfig;
import com.sigdue.Constants;
import com.sigdue.R;
import com.sigdue.aplication.AplicacionSIGDUE;
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
import com.sigdue.utilidadesgenerales.Filtro;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.sigdue.Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.sigdue.Constants.RUTAMULTIMEDIASIGDUE;

public class AgregarInformacionSIDGDUEActivity extends AppCompatActivity {

    private static final int PICK_FILES = 400;
    private String TAG = "AgregarInmActivity";
    private AplicacionSIGDUE app;
    private Button btnAtras;
    private Button btnEliminar;
    private Button btnCargarArchivos;
    private Button btnSiguiente;
    private Button btnTomarFoto;
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
    ArrayList<String> fotos;
    private PhotoViewAttacher mAttacher;
    private ImageView mImageView;
    private LinearLayout mostrarFotos;
    int numeroFoto;
    int posFoto;
    String rutafoto;
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
    private LinearLayout formularioFotos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UtilidadesGenerales.context = this;
            setContentView(R.layout.agregar_informacion_sigdue_activity);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                task = extras.getInt(Constants.TASK);
                formularioFotos = findViewById(R.id.formularioFotos);
                formularioMultimedia = findViewById(R.id.formularioMultimedia);
                formularioUbicacion = findViewById(R.id.formularioUbicacion);
                formularioPredio = findViewById(R.id.formularioInfoPredio);
                if (task == 1) formularioUbicacion.setVisibility(View.VISIBLE);
                else if (task == 2) {
                    formularioFotos.setVisibility(View.VISIBLE);
                    formularioMultimedia.setVisibility(View.VISIBLE);
                } else if (task == 3) {
                    formularioMultimedia.setVisibility(View.VISIBLE);
                    formularioPredio.setVisibility(View.VISIBLE);
                }
            }

            Context ctx = getApplicationContext();
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

            locationTrack = new LocationTrack(AgregarInformacionSIDGDUEActivity.this);
            map = (MapView) findViewById(R.id.map);
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);

            latitudEditext = ((FloatLabel) findViewById(R.id.latitud)).getEditText();
            setSoloLectura(latitudEditext, false);

            longitudEditext = ((FloatLabel) findViewById(R.id.longitud)).getEditText();
            setSoloLectura(longitudEditext, false);

            if (locationTrack.canGetLocation()) {
                longitude = locationTrack.getLongitude();
                latitude = locationTrack.getLatitude();

                latitudEditext.setText(String.valueOf(latitude));
                longitudEditext.setText(String.valueOf(longitude));

                IMapController mapController = map.getController();
                mapController.setZoom(19.0);
                GeoPoint startPoint = new GeoPoint(latitude, longitude);
                mapController.setCenter(startPoint);
                Marker startMarker = new Marker(map);
                startMarker.setPosition(startPoint);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(startMarker);
            }

            fotos = new ArrayList();
            numeroFoto = 0;
            posFoto = 0;
            rutafoto = "";

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

            cmbTipoDocumento = (SearchableSpinner) findViewById(R.id.cmbTipoDocumento);
            setComboConf(cmbTipoDocumento, "Tipo documento", Constants.TIPO_DOCUMENTO);

            cmbZonaAislamiento = (SearchableSpinner) findViewById(R.id.cmbZonaAislamiento);
            setComboConf(cmbZonaAislamiento, "Zona de aislamiento", Constants.ZONA_AISLAMIENTO);

            cmbZonaAltoRiesgo = (SearchableSpinner) findViewById(R.id.cmbZonaAltoRiesgo);
            setComboConf(cmbZonaAltoRiesgo, "Zona de alto riesgo", Constants.ZONA_ALTO_RIESGO);

            cmbZonaProteccion = (SearchableSpinner) findViewById(R.id.cmbZonaProteccion);
            setComboConf(cmbZonaProteccion, "Zona de protección", Constants.ZONA_PROTECCION);

            this.mostrarFotos = (LinearLayout) findViewById(R.id.mostrarFotos);
            this.mImageView = (ImageView) findViewById(R.id.imagen);
            Drawable bitmap = getResources().getDrawable(R.drawable.imagenes);
            this.mImageView.setImageDrawable(bitmap);
            this.mAttacher = new PhotoViewAttacher(this.mImageView);
            this.fotos = new ArrayList();
            this.predial = new Predial();
            List<Predial> informacionSIGDUE = this.predialDao.queryBuilder().orderDesc(PredialDao.Properties.Id_predial).list();
            if (informacionSIGDUE == null || informacionSIGDUE.size() <= 0) {
                this.predial.setId_predial(1);
            } else {
                this.predial.setId_predial((informacionSIGDUE.get(0)).getId_predial() + 1);
            }
            codigoDane = ((FloatLabel) findViewById(R.id.dane_sede)).getEditText();
            codigoDane.setText(app.getUsuario());
            setSoloLectura(codigoDane, false);
            codigoPredio = ((FloatLabel) findViewById(R.id.codigo_predio)).getEditText();
            avaluoCatastral = ((FloatLabel) findViewById(R.id.avaluo_catastral)).getEditText();
            avaluoComercial = ((FloatLabel) findViewById(R.id.avaluo_comercial)).getEditText();
            cualTipoDocumento = ((FloatLabel) findViewById(R.id.cual_tipo_documento)).getEditText();
            numeroDocumentoLegalizacion = ((FloatLabel) findViewById(R.id.nro_documento_legalizacion)).getEditText();
            notariaDependenciaOrigen = ((FloatLabel) findViewById(R.id.notaria_dependencia_origen)).getEditText();
            lugarExpedicion = ((FloatLabel) findViewById(R.id.lugar_expedicion)).getEditText();
            registroCatastral = ((FloatLabel) findViewById(R.id.registro_catastral)).getEditText();
            matriculaInmobiliaria = ((FloatLabel) findViewById(R.id.matricula_inmobiliaria)).getEditText();
            propietarios = ((FloatLabel) findViewById(R.id.propietarios)).getEditText();
            nombreTenencia = ((FloatLabel) findViewById(R.id.nombre_tiene_tenencia)).getEditText();
            urlVideo = ((FloatLabel) findViewById(R.id.url_video)).getEditText();
            fechaAvaluoCatastral = ((FloatLabel) findViewById(R.id.fecha_avaluo)).getEditText();
            setFechaConf(fechaAvaluoCatastral, "Fecha avalúo catastral", avaluoComercial);

            fechaAvaluoComercial = ((FloatLabel) findViewById(R.id.fecha_avaluo_comercial)).getEditText();
            setFechaConf(fechaAvaluoComercial, "Fecha avalúo comercial", cualTipoDocumento);

            fechaExpedicion = ((FloatLabel) findViewById(R.id.fecha_expedicion)).getEditText();
            setFechaConf(fechaExpedicion, "Fecha expedición", notariaDependenciaOrigen);

            fechaTenencia = ((FloatLabel) findViewById(R.id.fecha_tenencia)).getEditText();
            setFechaConf(fechaTenencia, "Fecha tenencia", urlVideo);

            this.btnCargarArchivos = (Button) findViewById(R.id.btnCargarArchivos);
            this.btnCargarArchivos.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        final String[] ACCEPT_MIME_TYPES = {
                                "application/pdf",
                                "image/*"
                        };
                        Intent intent = new Intent();
                        intent.setType("*/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, ACCEPT_MIME_TYPES);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(Intent.createChooser(intent, "Adjuntar archivos"), PICK_FILES);

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
                            File imagesFolder = new File(Environment.getExternalStorageDirectory(), RUTAMULTIMEDIASIGDUE);
                            if (!imagesFolder.exists()) imagesFolder.mkdirs();
                            rutafoto = usuario.getId_usuario() + "_foto_" + numeroFoto + ".jpg";
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
        } catch (Exception ex) {
            ex.printStackTrace();
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
        SpinnerAdapter adapterComboZona = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
        cmb.setAdapter(adapterComboZona);
    }


    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        AlertDialog.Builder builder;
        AlertDialog alert;
        try {

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
            if (this.cmbTenencia.getSelectedItem() != null) {
                this.predial.setTenencia(((Parametro) this.cmbTenencia.getSelectedItem()).getParametro());
            }
            if (this.cmbConQuienTieneTenencia.getSelectedItem() != null) {
                this.predial.setCon_quien_tenencia(((Parametro) this.cmbConQuienTieneTenencia.getSelectedItem()).getParametro());
            }
            this.predial.setFecha_tenencia_lote(this.fechaTenencia.getText().toString());
            if (this.urlVideo.getText() != null && !this.urlVideo.getText().toString().isEmpty()) {
                archivos = new ArrayList<>();
                Archivo archivo = new Archivo();
                archivo.setId_usuario(usuario.getId_usuario());
                archivo.setRuta(this.urlVideo.getText().toString());
                archivo.setTipo("video");
                archivo.setEstado("P");
                setIdArchivo(archivo);
                archivos.add(archivo);
            }

            this.usuario.setLongitude(String.valueOf(longitude));
            this.usuario.setLatitude(String.valueOf(latitude));
            this.usuarioDao.update(usuario);

            archivoDao.insertInTx(archivos);
            predialDao.insert(this.predial);

            confirmarAlmacenamientoExitoso();


        } catch (Exception ex) {
            mostrarError();
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
                try {
                    if (usuario != null) {
                        File carpetaArchivos = new File(Environment.getExternalStorageDirectory(), RUTAMULTIMEDIASIGDUE);
                        //borrar fotos
                        String inicio = usuario.getId_usuario() + "_foto_";
                        String fin = ".jpg";
                        File[] archivos = carpetaArchivos.listFiles(new Filtro(inicio, fin));
                        if (archivos != null && archivos.length > 0) {
                            for (int i = 0; i < archivos.length && i < 6; i++) {
                                File tempf = archivos[i];
                                if (tempf.exists()) {
                                    tempf.delete();
                                }
                            }
                        }
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
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
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver cR = getContentResolver();
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    File image = new File(Environment.getExternalStorageDirectory() + "/" + RUTAMULTIMEDIASIGDUE + "/" + this.rutafoto);
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
                    this.fotos.add(Environment.getExternalStorageDirectory() + "/" + RUTAMULTIMEDIASIGDUE + "/" + this.rutafoto);
                    this.posFoto = this.fotos.size() - 1;
                    this.numeroFoto++;
                    desplegarFoto(this.posFoto);
                    this.mostrarFotos.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == PICK_FILES) {
            if (null != data) {
                if (null != data.getClipData()) {
                    archivos = new ArrayList<>();
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        Archivo archivo = new Archivo();
                        setIdArchivo(archivo);
                        archivo.setId_usuario(usuario.getId_usuario());
                        archivo.setRuta(uri.getPath());
                        archivo.setTipo(cR.getType(uri));
                        archivo.setEstado("P");
                        archivos.add(archivo);
                    }
                    this.btnCargarArchivos.setText("Adjuntar archivos (" + data.getClipData().getItemCount() + ")");
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    this.btnCargarArchivos.setText("Adjuntar archivos (1)");
                    archivos = new ArrayList<>();
                    Archivo archivo = new Archivo();
                    archivo.setId_usuario(usuario.getId_usuario());
                    archivo.setRuta(uri.getPath());
                    archivo.setTipo(cR.getType(uri));
                    setIdArchivo(archivo);
                    archivo.setEstado("P");
                    archivos.add(archivo);
                } else {
                    this.btnCargarArchivos.setText("Adjuntar archivos");
                    archivos = new ArrayList<>();
                }
            } else {
                this.btnCargarArchivos.setText("Adjuntar archivos");
                archivos = new ArrayList<>();
            }
        }
    }

    public void setIdArchivo(Archivo archivo) {
        List<Archivo> archivosBD = this.archivoDao.queryBuilder().orderDesc(ArchivoDao.Properties.Id_archivo).list();
        if (archivosBD == null || archivosBD.size() <= 0) {
            archivo.setId_archivo(1 + archivos.size());
        } else {
            archivo.setId_archivo((archivosBD.get(0)).getId_archivo() + 1 + archivos.size());
        }
    }
}
