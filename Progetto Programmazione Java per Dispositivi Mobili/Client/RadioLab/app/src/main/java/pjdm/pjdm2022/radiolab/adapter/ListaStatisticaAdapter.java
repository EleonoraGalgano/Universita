package pjdm.pjdm2022.radiolab.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.entity.ListaStatisticaElement;

public class ListaStatisticaAdapter extends RecyclerView.Adapter<ListaStatisticaAdapter.LFViewHolder>{
    private ArrayList<ListaStatisticaElement> dati;

    Context context;

    private LayoutInflater inflater;

    public ListaStatisticaAdapter( Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.dati = new ArrayList<>();
    }

    @NonNull
    @Override
    public LFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_statistica, parent, false);
        ListaStatisticaAdapter.LFViewHolder vh = new ListaStatisticaAdapter.LFViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LFViewHolder holder, int position) {
        ListaStatisticaElement element = dati.get(position);
        holder.tvMeseAnno.setText(element.getMeseAnno());
        holder.tvAmmontare.setText(element.getAmmontare().toString());
    }

    @Override
    public int getItemCount() {
        return dati.size();
    }

    public void addItem( ListaStatisticaElement element) {
        dati.add(element);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<ListaStatisticaElement> listaFatturato) {
        dati = listaFatturato;
        Log.d("EGG", "addItems: "+dati);
        notifyDataSetChanged();
    }

    public ArrayList<ListaStatisticaElement> getDati() {
        return dati;
    }

    public class LFViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMeseAnno;
        private TextView tvAmmontare;

        public LFViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMeseAnno = itemView.findViewById(R.id.tvMeseAnnoCalcolato);
            tvAmmontare = itemView.findViewById(R.id.tvAmmontareCalcolato);
        }
    }
}
