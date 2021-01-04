package com.sigdue.asynctask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.sigdue.activity.LoginActivity;

public class ProgressDialogFragment extends DialogFragment {
    private final String TAG="ProgressDialogFragment";
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
