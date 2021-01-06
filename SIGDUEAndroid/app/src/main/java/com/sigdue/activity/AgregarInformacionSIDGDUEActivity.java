package com.sigdue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.sigdue.BuildConfig;
import com.sigdue.R;
import com.sigdue.aplication.AplicacionSIGDUE;
import com.sigdue.db.DaoSession;
import com.sigdue.db.Parametro;
import com.sigdue.db.ParametroDao;
import com.sigdue.db.Predial;
import com.sigdue.db.PredialDao;
import com.sigdue.service.EnviarInformacionSIGDUEService;
import com.sigdue.service.LocationTrack;
import com.sigdue.ui.MoviePlayer;
import com.sigdue.ui.SearchableSpinner;
import com.sigdue.ui.SpeedControlCallback;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileInputStream;
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
import static com.sigdue.Constants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE;
import static com.sigdue.Constants.RUTAMULTIMEDIASIGDUE;

public class AgregarInformacionSIDGDUEActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MoviePlayer.PlayerFeedback {

    private String TAG = "AgregarInmActivity";
    private AplicacionSIGDUE app;
    private Button btnAtras;
    private Button btnEliminar;
    private Button btnEliminarVideo;
    private Button btnGrabarVideo;
    private Button btnSiguiente;
    private Button btnTomarFoto;
    private SearchableSpinner cmbZona;
    private DaoSession daoSession;
    private ParametroDao parametroDao;
    private PredialDao predialDao;
    private EditText fechaAluo;
    private DatePicker dp;
    ArrayList<String> fotos;
    private PhotoViewAttacher mAttacher;
    private ImageView mImageView;
    private boolean mMostrarDetenerVideo;
    private MoviePlayer.PlayTask mPlayTask;
    private boolean mSurfaceTextureReady;
    private TextureView mTextureView;
    private LinearLayout mostrarFotos;
    private LinearLayout mostrarVideo;
    int numeroFoto;
    int posFoto;
    String rutaVideo;
    String rutafoto;
    private double longitude;
    private double latitude;
    private LocationTrack locationTrack;
    private Predial predial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UtilidadesGenerales.context = this;
            locationTrack = new LocationTrack(AgregarInformacionSIDGDUEActivity.this);

            if (locationTrack.canGetLocation()) {
                longitude = locationTrack.getLongitude();
                latitude = locationTrack.getLatitude();
                Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
            }

            mSurfaceTextureReady = false;
            fotos = new ArrayList();
            numeroFoto = 0;
            posFoto = 0;
            rutafoto = "";
            rutaVideo = "";
            this.app = (AplicacionSIGDUE) getApplication();
            this.daoSession = this.app.getDaoSession();
            this.predialDao = this.daoSession.getPredialDao();
            this.parametroDao = this.daoSession.getParametroDao();
            setContentView(R.layout.agregar_informacion_sigdue_activity);

            this.cmbZona = (SearchableSpinner) findViewById(R.id.cmbClima);
            this.cmbZona.setTitle("Zonas");
            this.cmbZona.setPositiveButton("Cerrar");
            Object[] camposCombo = this.parametroDao.queryBuilder().list().toArray();
            SpinnerAdapter adapterComboZona = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, camposCombo);
            if (camposCombo == null || camposCombo.length <= 0) {
                this.cmbZona.setAdapter(adapterComboZona);
            } else {
                this.cmbZona.setAdapter(adapterComboZona, camposCombo[0]);
            }
            this.mostrarFotos = (LinearLayout) findViewById(R.id.mostrarFotos);
            this.mostrarVideo = (LinearLayout) findViewById(R.id.mostrarVideo);
            this.mImageView = (ImageView) findViewById(R.id.imagen);
            this.mTextureView = (TextureView) findViewById(R.id.video);
            this.mTextureView.setSurfaceTextureListener(this);
            Drawable bitmap = getResources().getDrawable(R.drawable.imagenes);
            this.mImageView.setImageDrawable(bitmap);
            this.mAttacher = new PhotoViewAttacher(this.mImageView);
            this.fotos = new ArrayList();
            this.predial = new Predial();
            List<Predial> inmovilizaciones = this.predialDao.queryBuilder().orderDesc(PredialDao.Properties.Id_predial).list();
            if (inmovilizaciones == null || inmovilizaciones.size() <= 0) {
                this.predial.setId_predial(1);
            } else {
                this.predial.setId_predial((inmovilizaciones.get(0)).getId_predial() + 1);
            }
            fechaAluo = findViewById(R.id.fecha_avaluo);
            final SimpleDateFormat formatFechaHora = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = formatFechaHora.format(new Date());
            fechaAluo.setText(fecha);
            fechaAluo.setClickable(false);
            fechaAluo.setKeyListener(null);
            fechaAluo.setFocusable(false);
            fechaAluo.setError(null);
            fechaAluo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                            String fecha = fechaAluo.getText().toString();
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
                            dialogBuilder.setTitle("Fecha");
                            dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                                        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                                            fechaAluo.setText((dp.getDayOfMonth() < 10 ? "0" + dp.getDayOfMonth() : dp.getDayOfMonth()) + "/" + ((dp.getMonth() + 1) < 10 ? "0" + (dp.getMonth() + 1) : (dp.getMonth() + 1)) + "/" + dp.getYear());
                                        } else {
                                            fechaAluo.setText((dp.getDayOfMonth() < 10 ? "0" + dp.getDayOfMonth() : dp.getDayOfMonth()) + "/" + ((dp.getMonth() + 1) < 10 ? "0" + (dp.getMonth() + 1) : (dp.getMonth() + 1)) + "/" + dp.getYear());
                                        }
                                        //direccion.requestFocus();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                            dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //direccion.requestFocus();
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

            this.btnGrabarVideo = (Button) findViewById(R.id.btnGrabarVideo);
            this.btnGrabarVideo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60 * 5);
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
                            File imagesFolder = new File(Environment.getExternalStorageDirectory(), RUTAMULTIMEDIASIGDUE);
                            if (!imagesFolder.exists()) imagesFolder.mkdirs();
                            rutafoto = predial.getId_predial() + "_foto_" + numeroFoto + ".jpg";
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


            this.predial.setEstado("G");
            long idInmovilizacion = predialDao.insert(this.predial);
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
        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE && resultCode == -1) {
            guardarVideo(data);
            this.mostrarVideo.setVisibility(View.VISIBLE);
            mostrarVideo();
        }
    }

    private void guardarVideo(Intent data) {
        try {
            File videosFolder = new File(Environment.getExternalStorageDirectory(), RUTAMULTIMEDIASIGDUE);
            if (!videosFolder.exists()) videosFolder.mkdirs();
            rutaVideo = videosFolder.getPath() + "/" + predial.getId_predial() + "_video_" + ".mp4";
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }
}
