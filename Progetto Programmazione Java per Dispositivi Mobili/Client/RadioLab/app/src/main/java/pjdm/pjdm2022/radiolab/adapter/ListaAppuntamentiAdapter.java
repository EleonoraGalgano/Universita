package pjdm.pjdm2022.radiolab.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pjdm.pjdm2022.radiolab.entity.ListaAppuntamentiElement;
import pjdm.pjdm2022.radiolab.R;

public class ListaAppuntamentiAdapter extends RecyclerView.Adapter<ListaAppuntamentiAdapter.LAViewHolder>{
    private ArrayList<ListaAppuntamentiElement> dati;
    private Context context;
    private ListaAppuntamentiElement selectedItem;

    private LayoutInflater inflater;

    public ListaAppuntamentiAdapter(Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        dati = new ArrayList<ListaAppuntamentiElement>();
    }

    @NonNull
    @Override
    public LAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_appuntamento, parent, false);
        LAViewHolder vh = new LAViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LAViewHolder holder, int position) {
        holder.tvIdPrenotazione.setText(dati.get(position).getIdPrenotazione());
        holder.tvNomePrestazione.setText(dati.get(position).getNomePrestazione());
        holder.tvData.setText(dati.get(position).getData().toString());
        holder.tvOra.setText(dati.get(position).getOra().toString());
        holder.tvNomeSede.setText(dati.get(position).getNomeSede());
    }

    @Override
    public int getItemCount() {
        return dati.size();
    }

    public ListaAppuntamentiElement getItem(int position){
        return dati.get(position);
    }

    public void removeItem(int position){
        dati.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(ListaAppuntamentiElement selectedItem, int position) {
        dati.add(position, selectedItem);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<ListaAppuntamentiElement> listaAppuntamenti) {
        dati.clear();
        dati=listaAppuntamenti;
        notifyDataSetChanged();
    }

    class LAViewHolder extends RecyclerView.ViewHolder{
        TextView tvIdPrenotazione;
        TextView tvNomePrestazione;
        TextView tvData;
        TextView tvOra;
        TextView tvNomeSede;

        public LAViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdPrenotazione = itemView.findViewById(R.id.tvIdPrenotazionePaziente);
            tvNomePrestazione = itemView.findViewById(R.id.tvNomePrestazionePaziente);
            tvData = itemView.findViewById(R.id.tvDataAppuntamentoPrenotato);
            tvOra = itemView.findViewById(R.id.tvOraAppuntamentoPrenotato);
            tvNomeSede = itemView.findViewById(R.id.tvLuogoAppuntamentoPrenotato);
        }
    }
}
