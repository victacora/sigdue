package com.sigdue.asynctask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ProgressDialogFragment extends DialogFragment {
    private final String TAG = "ProgressDialogFragment";
    private ProgressDialog dialog;
    private String mensaje;
    private AsyncTask asyncTask;
    private Context context;

    public void inicializarDialogo(AsyncTask asyncTask, Context context, String mensaje) {
        try {
            this.asyncTask = asyncTask;
            this.context = context;
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

    public static ProgressDialogFragment newInstance(AsyncTask asyncTask, Context context, String mensaje) {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        frag.inicializarDialogo(asyncTask, context, mensaje);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setMessage(mensaje);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Log.i(TAG, "Dialogo cancelado.");
                asyncTask.cancel(true);
            }
        });
        return dialog;
    }
}
