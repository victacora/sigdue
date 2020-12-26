package com.sigdue.listadapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sigdue.R;
import com.sigdue.db.Infraccion;
import com.sigdue.db.Inmovilizacion;
import com.sigdue.db.Persona;
import com.sigdue.db.Vehiculo;
import com.sigdue.ui.AutoResizeTextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class InmovilizacionRecyclerView extends RecyclerView.Adapter<InmovilizacionRecyclerView.ComparendoViewHolder> {
    private List<Inmovilizacion> data;
    private Context mContext;

    public InmovilizacionRecyclerView(List<Inmovilizacion> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public ComparendoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_inmovilizacion, parent, false);
        ComparendoViewHolder vh = new ComparendoViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ComparendoViewHolder holder, final int position) {
        String nombreInfractor;
        String nombreUsuario;
        SimpleDateFormat formatFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        AutoResizeTextView autoResizeTextView = holder.numeroComp;
        autoResizeTextView.setText("Nº Comp: " + (this.data.get(position).getNo_comparendo() == null || (this.data.get(position)).getNo_comparendo().equals("") ? "" : this.data.get(position).getNo_comparendo()));
        String fecha = "";
        try {
            fecha = formatFechaHora.format(this.data.get(position).getFec_ini_inm());
        } catch (Exception ex) {
            fecha = "";
            ex.printStackTrace();
        }
        holder.fechaComp.setText("Fecha: " + fecha);
        Persona infractor = (this.data.get(position)).getInfractor();
        Vehiculo vehiculo = (this.data.get(position)).getVehiculo();
        Infraccion infraccion = (this.data.get(position)).getInfraccion();
        autoResizeTextView = holder.vehiculo;
        String codigo = (infraccion == null || infraccion.getCodigo().equals("")) ? "" : infraccion.getCodigo();
        autoResizeTextView.setText("Placa veh: " + (vehiculo != null ? vehiculo.getPlaca() : "") + " Cod Inf: " + codigo);
        holder.numeroCedula.setText("N° documento: " + (infractor != null ? infractor.getNo_identificacion() : ""));
        if (infractor != null) {
            nombreInfractor = (infractor.getNombre1() != null ? infractor.getNombre1() : "") + " " + (infractor.getNombre2() != null ? infractor.getNombre2() : "") + " " + (infractor.getApellido1() != null ? infractor.getApellido1() : "") + " " + (infractor.getApellido2() != null ? infractor.getApellido2() : "");
        } else {
            nombreInfractor = "";
        }
        holder.nombreInfractor.setText("Infractor: " + nombreInfractor);
        Persona agente = (this.data.get(position)).getAgente();
        holder.placaAgente.setText("Placa agente: " + (agente != null ? agente.getPlaca() : ""));
        Persona usuario = (this.data.get(position)).getUsuario();
        if (usuario != null) {
            nombreUsuario = (usuario.getNombre1() != null ? usuario.getNombre1() : "") + " " + (usuario.getNombre2() != null ? usuario.getNombre2() : "") + " " + (usuario.getApellido1() != null ? usuario.getApellido1() : "") + " " + (usuario.getApellido2() != null ? usuario.getApellido2() : "");
        } else {
            nombreUsuario = "";
        }
        holder.usuario.setText("Usuario: " + (usuario != null ? usuario.getNo_identificacion() : "") + " - " + nombreUsuario);
        if ((this.data.get(position)).getEstado() == null || !(this.data.get(position)).getEstado().equals("E")) {
            holder.estado.setText("Inmovilizacion no enviada.");
            holder.estado.setTextColor(this.mContext.getResources().getColor(R.color.primary_color));
            return;
        }
        holder.estado.setText("Inmovilizacion enviada");
        holder.estado.setTextColor(this.mContext.getResources().getColor(R.color.primary_color));

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
        AutoResizeTextView estado;
        AutoResizeTextView fechaComp;
        AutoResizeTextView nombreInfractor;
        AutoResizeTextView numeroCedula;
        AutoResizeTextView numeroComp;
        AutoResizeTextView placaAgente;
        AutoResizeTextView usuario;
        AutoResizeTextView vehiculo;

        ComparendoViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            nombreInfractor = (AutoResizeTextView) itemView.findViewById(R.id.nombreInfractor);
            numeroCedula = (AutoResizeTextView) itemView.findViewById(R.id.documentoInfractor);
            usuario = (AutoResizeTextView) itemView.findViewById(R.id.usuario);
            placaAgente = (AutoResizeTextView) itemView.findViewById(R.id.placaAgente);
            numeroComp = (AutoResizeTextView) itemView.findViewById(R.id.numeroInmovilizacion);
            fechaComp = (AutoResizeTextView) itemView.findViewById(R.id.fechaComparendo);
            this.vehiculo = (AutoResizeTextView) itemView.findViewById(R.id.vehiculo);
            this.estado = (AutoResizeTextView) itemView.findViewById(R.id.estado);
        }
    }

}
