package com.sigdue.listadapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sigdue.R;
import com.sigdue.db.Predial;
import com.sigdue.ui.AutoResizeTextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class InformacionSIGDUERecyclerView extends RecyclerView.Adapter<InformacionSIGDUERecyclerView.ComparendoViewHolder> {
    private List<Predial> data;
    private Context mContext;

    public InformacionSIGDUERecyclerView(List<Predial> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public ComparendoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_informacion_sigdue, parent, false);
        ComparendoViewHolder vh = new ComparendoViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ComparendoViewHolder holder, final int position) {
        holder.codigoDane.setText("Nº DANE: " + (this.data.get(position).getDane_sede() == null || (this.data.get(position)).getDane_sede().equals("") ? "" : this.data.get(position).getDane_sede()));
        holder.codigoPredio.setText("Cod Predio: " + (this.data.get(position).getCod_predio() == null || (this.data.get(position)).getCod_predio().equals("") ? "" : this.data.get(position).getCod_predio()));
        holder.clasePredio.setText("Clase Predio: " + (this.data.get(position).getClase_predio() == null || (this.data.get(position)).getCod_predio().equals("") ? "" : this.data.get(position).getCod_predio()));
        holder.tenencia.setText("Tenencia: " + (this.data.get(position).getTenencia() == null || (this.data.get(position)).getTenencia().equals("") ? "" : this.data.get(position).getTenencia()));
        holder.distanciaPoblado.setText("Distancia poblado: " + (this.data.get(position).getDist_km_centro_poblado() == null || (this.data.get(position)).getDist_km_centro_poblado().equals("") ? "" : this.data.get(position).getDist_km_centro_poblado()));
        holder.tipoDocumento.setText("Tipo documento: " + (this.data.get(position).getTipo_documento() == null || (this.data.get(position)).getTipo_documento().equals("") ? "" : this.data.get(position).getTipo_documento()));
        holder.topografia.setText("Topografía: " + (this.data.get(position).getTopografia() == null || (this.data.get(position)).getTopografia().equals("") ? "" : this.data.get(position).getTopografia()));

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
        AutoResizeTextView tipoDocumento;
        AutoResizeTextView codigoDane;
        AutoResizeTextView codigoPredio;
        AutoResizeTextView distanciaPoblado;
        AutoResizeTextView tenencia;
        AutoResizeTextView clasePredio;
        AutoResizeTextView topografia;

        ComparendoViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            codigoDane = (AutoResizeTextView) itemView.findViewById(R.id.codigo_dane);
            codigoPredio = (AutoResizeTextView) itemView.findViewById(R.id.codigo_predio);
            clasePredio = (AutoResizeTextView) itemView.findViewById(R.id.clase_predio);
            tenencia = (AutoResizeTextView) itemView.findViewById(R.id.tenencia);
            distanciaPoblado = (AutoResizeTextView) itemView.findViewById(R.id.distancia_poblado);
            tipoDocumento = (AutoResizeTextView) itemView.findViewById(R.id.tipo_documento);
            topografia = (AutoResizeTextView) itemView.findViewById(R.id.topografia);
            estado = (AutoResizeTextView) itemView.findViewById(R.id.estado);
        }
    }

}
