package pjdm.pjdm2022.radiolab.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pjdm.pjdm2022.radiolab.entity.ListaPrestazioniElement;
import pjdm.pjdm2022.radiolab.R;

public class ListaPrestazioniAdapter extends RecyclerView.Adapter<ListaPrestazioniAdapter.LPViewHolder> {
    private ArrayList<ListaPrestazioniElement> dati;
    private Context context;

    private ListaPrestazioniElement selectedItem;

    private LayoutInflater inflater;

    public ListaPrestazioniAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.selectedItem = null;
        this.dati = new ArrayList<ListaPrestazioniElement>();
    }

    @NonNull
    @Override
    public ListaPrestazioniAdapter.LPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_prestazione, parent, false);
        LPViewHolder vh = new LPViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ListaPrestazioniAdapter.LPViewHolder holder, int position) {
        holder.tvNomePrestazione.setText(dati.get(position).getNome());
        if(dati.get(position).isSelected()) {
            holder.tvNomePrestazione.setBackground(context.getDrawable(R.drawable.selected_border));
        } else {
            holder.tvNomePrestazione.setBackground(context.getDrawable(R.drawable.et_bg));
        }
    }

    @Override
    public int getItemCount() {
        return dati.size();
    }

    public void addItem(ListaPrestazioniElement element) {
        dati.add(element);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<ListaPrestazioniElement> listaPrestazioni) {
        dati = listaPrestazioni;
        Log.d("EGG", "addItems: "+dati);
        notifyDataSetChanged();
    }

    public ListaPrestazioniElement
    getSelectedItem() {
        return selectedItem;
    }

    class LPViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNomePrestazione;

        public LPViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectedItem != null) {
                        selectedItem.toogleSelected();
                    }
                    selectedItem = dati.get(getAdapterPosition());
                    selectedItem.toogleSelected();
                    notifyDataSetChanged();
                }
            });
            tvNomePrestazione = itemView.findViewById(R.id.tvNomePrestazioneRiepilogo);
        }
    }
}
