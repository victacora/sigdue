package com.sigdue.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sigdue.R;
import com.sigdue.aplication.AplicacionSIGDUE;
import com.sigdue.asynctask.AsyncTaskSIGDUE;
import com.sigdue.asynctask.AutenticarUsuarioAsyncTask;
import com.sigdue.asynctask.ParametrosAsyncTask;
import com.sigdue.asynctask.ProgressDialogFragment;
import com.sigdue.db.DaoSession;
import com.sigdue.db.Usuario;
import com.sigdue.db.UsuarioDao;
import com.sigdue.utilidadesgenerales.UtilidadesGenerales;

public class LoginActivity extends AppCompatActivity implements AsyncTaskSIGDUE {

    private static final String TAG = "LoginActivity";
    private AplicacionSIGDUE app;
    private AutenticarUsuarioAsyncTask mAuthTask = null;
    private ParametrosAsyncTask parametrosAsyncTask = null;
    private ProgressDialogFragment mProgressDialog = null;
    private TextView mMessage;
    private TextView version;
    private String mPassword;
    private EditText mPasswordEdit;
    private String mUsername;
    private EditText mUsernameEdit;
    private DaoSession daoSession;
    private UsuarioDao usuarioDao;
    private ImageView btnConfiguracion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UtilidadesGenerales.context = this;
            app = (AplicacionSIGDUE) getApplicationContext();
            daoSession = app.getDaoSession();
            usuarioDao = daoSession.getUsuarioDao();
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
            version = findViewById(R.id.version);
            version.setText(UtilidadesGenerales.getVersionApp());
            if (app.getIdUsuario() != -1) {
                Usuario persona = usuarioDao.queryBuilder().where(UsuarioDao.Properties.Id_usuario.eq(app.getIdUsuario())).unique();
                {
                    if (persona != null) {
                        app.setUsuario(persona.getUsuario());
                        Intent listarInmovilizaciones = new Intent(LoginActivity.this, ListarInformacionSIGDUEActivity.class);
                        startActivity(listarInmovilizaciones);
                        finish();
                    } else {
                        app.setUsuario("");
                        app.setIdUsuario(-1);
                    }
                }
            }
            btnConfiguracion = findViewById(R.id.btnConfiguracion);
            btnConfiguracion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, ConfiguracionActivity.class);
                    startActivity(intent);
                }
            });
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
                parametrosAsyncTask = null;
                mAuthTask = new AutenticarUsuarioAsyncTask(LoginActivity.this, daoSession, mProgressDialog);
                mAuthTask.execute(mUsername, mPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void showProgress(String mensaje) {
        mProgressDialog = ProgressDialogFragment.newInstance(mAuthTask != null ? mAuthTask : parametrosAsyncTask, LoginActivity.this, mensaje);
        mProgressDialog.show(getSupportFragmentManager(), "dialog_authetication");
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void onAuthenticationCancel() {
        mAuthTask = null;
        parametrosAsyncTask = null;
        hideProgress();
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
            } else if (requestCode == UtilidadesGenerales.RECORD_AUDIO
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso para almacenar audio desde el equipo.";
            } else if (requestCode == UtilidadesGenerales.ACCESS_WIFI_STATE_CODE
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
            } else if (requestCode == UtilidadesGenerales.ACCESS_COARSE_LOCATION_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso de consulta GPS, ubicación aproximada.";
            } else if (requestCode == UtilidadesGenerales.WRITE_EXTERNAL_STORAGE_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso de almacenamiento en memoria externa.";
            } else if (requestCode == UtilidadesGenerales.READ_EXTERNAL_STORAGE_CODE
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                mensaje = "Error al activar permiso de lectura de memoria externa.";
            } else if (requestCode == UtilidadesGenerales.VIBRATE_CODE
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
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere tener habilitado el permiso para almacenar audio desde el equipo.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                                UtilidadesGenerales.RECORD_AUDIO);
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.i(TAG, "Solicitar permiso para uso de audio.");
            } else if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Información");
                builder.setCancelable(false);
                builder.setMessage("Esta aplicación requiere tener habilitado el permiso para almacenar audio desde el equipo.");
                builder.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA},
                                UtilidadesGenerales.RECORD_AUDIO);
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
                Log.i(TAG, "Solicitar permiso para uso de audio.");
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
            } else if (ActivityCompat.checkSelfPermission(this,
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
            } else if (ActivityCompat.checkSelfPermission(this,
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
            } else if (ActivityCompat.checkSelfPermission(this,
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
            } else if (ActivityCompat.checkSelfPermission(this,
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

    @Override
    public void onPostExecute(Object result) {
        mAuthTask = null;
        hideProgress();
        if (result != null) {
            try {
                Usuario usuario = (Usuario) result;
                UtilidadesGenerales.escribirSharedPreferences(R.string.pref_usuario_key, String.valueOf(usuario.getId_usuario()), UtilidadesGenerales.STRING_TYPE);
                app.setUsuario(usuario.getUsuario());
                app.setIdUsuario(usuario.getId_usuario());
                irAListarInformacionSIGDUE();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (mAuthTask != null) {
            mMessage.setText(getText(R.string.login_activity_loginfail_text_both));
        } else if (parametrosAsyncTask != null) {
            irAListarInformacionSIGDUE();
        }
    }

    private void irAListarInformacionSIGDUE() {
        try {
            limpiarFormulario();
            Intent listarInmovilizaciones = new Intent(LoginActivity.this, ListarInformacionSIGDUEActivity.class);
            startActivity(listarInmovilizaciones);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        mMessage.setText("");
        mPasswordEdit.setText("");
        mUsernameEdit.setText("");
    }

    @Override
    public void onCancelled() {
        onAuthenticationCancel();
    }
}