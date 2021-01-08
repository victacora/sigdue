package com.sigdue.listadapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sigdue.Constants;
import com.sigdue.R;
import com.sigdue.activity.ExecuteTaskSIGDUE;
import com.sigdue.asynctask.AsyncTaskSIGDUE;
import com.sigdue.db.ArchivoDao;
import com.sigdue.db.DaoSession;
import com.sigdue.db.ParametroDao;
import com.sigdue.db.Usuario;
import com.sigdue.ui.AutoResizeTextView;

import java.util.List;

public class InformacionSIGDUERecyclerView extends RecyclerView.Adapter<InformacionSIGDUERecyclerView.ComparendoViewHolder> {
    private List<Usuario> data;
    private Context mContext;
    private ExecuteTaskSIGDUE executeTaskSIGDUE;
    private DaoSession daoSession;

    public InformacionSIGDUERecyclerView(List<Usuario> data, DaoSession daoSession, Context mContext, ExecuteTaskSIGDUE executeTaskSIGDUE) {
        this.data = data;
        this.mContext = mContext;
        this.daoSession = daoSession;
        this.executeTaskSIGDUE = executeTaskSIGDUE;
    }

    @Override
    public ComparendoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_informacion_sigdue, parent, false);
        ComparendoViewHolder vh = new ComparendoViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ComparendoViewHolder holder, final int position) {
        holder.codigoDane.setText("DANE SEDE " + (this.data.get(position).getUsuario() == null || (this.data.get(position)).getUsuario().equals("") ? "" : this.data.get(position).getUsuario()));
        holder.municipio.setText("Municipio: " + (this.data.get(position).getNombre_municipio() == null || (this.data.get(position)).getNombre_municipio().equals("") ? "" : this.data.get(position).getNombre_municipio()));
        holder.establecimiento.setText("Establecimiento: " + (this.data.get(position).getNombre_establecimiento() == null || (this.data.get(position)).getNombre_establecimiento().equals("") ? "" : this.data.get(position).getNombre_establecimiento()));
        holder.rector.setText("Rector: " + (this.data.get(position).getRector_establecimiento() == null || (this.data.get(position)).getRector_establecimiento().equals("") ? "" : this.data.get(position).getRector_establecimiento()));
        holder.posicion.setText(String.format("Posición: (%s , %s)",
                this.data.get(position).getLatitude() == null || (this.data.get(position)).getLatitude().equals("") ? "" : this.data.get(position).getLatitude(),
                this.data.get(position).getLongitude() == null || (this.data.get(position)).getLongitude().equals("") ? "" : this.data.get(position).getLongitude()));
        holder.sede.setText("Sede: " + (this.data.get(position).getNombre_sede() == null || (this.data.get(position)).getNombre_sede().equals("") ? "" : this.data.get(position).getNombre_sede()));
        holder.zona.setText("Zona: " + (this.data.get(position).getZona_sede() == null || (this.data.get(position)).getZona_sede().equals("") ? "" : this.data.get(position).getZona_sede()));
        holder.estado.setText("Estado: " + (this.data.get(position).getEst_sede() == null || (this.data.get(position)).getEst_sede().equals("") ? "" : this.data.get(position).getEst_sede()));
        holder.archivos.setText("Archivos adjuntos: " + (this.daoSession.getArchivoDao().queryBuilder().where(ArchivoDao.Properties.Id_usuario.eq(this.data.get(position).getId_usuario())).count()));
        holder.actualizarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                executeTaskSIGDUE.onCallActivity(1);
            }
        });
        holder.cargarMultimedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                executeTaskSIGDUE.onCallActivity(2);
            }
        });
        holder.actualizarInfoPredio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (validarParametrizacion().equals("")) {
                    executeTaskSIGDUE.onCallActivity(3);
                }
            }
        });

    }

    public String validarParametrizacion() {
        String parametrosComparendos = "";
        try {
            for (Integer tipoParametro = 1; tipoParametro <= 12; tipoParametro++) {
                if (this.daoSession.getParametroDao().queryBuilder().where(ParametroDao.Properties.Tipo.eq(tipoParametro)).count() == 0) {
                    parametrosComparendos = parametrosComparendos + "-No existen " + Constants.tiposParametros.get(tipoParametro) + " registrados.\n";
                }
            }
            if (!parametrosComparendos.equals("")) {
                parametrosComparendos = parametrosComparendos + "\nEjecute la opci\u00f3n: sincronizar maestros.";
            }
            if (!parametrosComparendos.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return (data != null ? data.size() : 0);
    }

    public static class ComparendoViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        AutoResizeTextView zona;
        AutoResizeTextView codigoDane;
        AutoResizeTextView municipio;
        AutoResizeTextView sede;
        AutoResizeTextView rector;
        AutoResizeTextView establecimiento;
        AutoResizeTextView estado;
        AutoResizeTextView posicion;
        AutoResizeTextView archivos;
        Button actualizarUbicacion;
        Button cargarMultimedia;
        Button actualizarInfoPredio;

        ComparendoViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            codigoDane = (AutoResizeTextView) itemView.findViewById(R.id.codigo_dane);
            municipio = (AutoResizeTextView) itemView.findViewById(R.id.nombre_municipio);
            establecimiento = (AutoResizeTextView) itemView.findViewById(R.id.nombre_establecimiento);
            rector = (AutoResizeTextView) itemView.findViewById(R.id.rector_establecimiento);
            sede = (AutoResizeTextView) itemView.findViewById(R.id.nombre_sede);
            zona = (AutoResizeTextView) itemView.findViewById(R.id.zona_sede);
            estado = (AutoResizeTextView) itemView.findViewById(R.id.est_sede);
            archivos = (AutoResizeTextView) itemView.findViewById(R.id.archivos);
            posicion = (AutoResizeTextView) itemView.findViewById(R.id.posicion);
            estado = (AutoResizeTextView) itemView.findViewById(R.id.est_sede);
            actualizarUbicacion = (Button) itemView.findViewById(R.id.btnActualizarUbiGeo);
            cargarMultimedia = (Button) itemView.findViewById(R.id.btnAdjuntarArchivos);
            actualizarInfoPredio = (Button) itemView.findViewById(R.id.btnInformacionPredio);

        }
    }

}
