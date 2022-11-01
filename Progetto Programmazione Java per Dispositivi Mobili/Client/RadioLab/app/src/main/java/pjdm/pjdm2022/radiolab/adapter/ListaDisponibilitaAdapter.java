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

import pjdm.pjdm2022.radiolab.entity.ListaDisponibilitaElement;
import pjdm.pjdm2022.radiolab.R;

public class ListaDisponibilitaAdapter extends RecyclerView.Adapter<ListaDisponibilitaAdapter.LDViewHolder> {
    private ArrayList<ListaDisponibilitaElement> dati;
    private Context context;

    private ListaDisponibilitaElement selectedItem;

    private LayoutInflater inflater;

    public ListaDisponibilitaAdapter(Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        dati = new ArrayList<ListaDisponibilitaElement>();
    }

    @NonNull
    @Override
    public LDViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_disponibilita, parent, false);
        LDViewHolder vh = new LDViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LDViewHolder holder, int position) {
        holder.tvDataAppuntamentoDisponibile.setText(dati.get(position).getData().toString());
        holder.tvOraAppuntamentoDisponibile.setText(dati.get(position).getOra().toString());
        holder.tvLuogoAppuntamentoDisponibile.setText(dati.get(position).getNomeSede());
        if(dati.get(position).isSelected()) {
            holder.itemView.setBackground(context.getDrawable(R.drawable.selected_border));
        } else {
            holder.itemView.setBackground(context.getDrawable(R.drawable.et_bg));
        }
    }

    @Override
    public int getItemCount() {
        return this.dati.size();
    }

    public ListaDisponibilitaElement getSelectedItem() {
        return selectedItem;
    }

    public void addAll(ArrayList<ListaDisponibilitaElement> listaDisponibilita) {
        dati=listaDisponibilita;
        notifyDataSetChanged();
    }

    class LDViewHolder extends RecyclerView.ViewHolder {
        TextView tvDataAppuntamentoDisponibile;
        TextView tvOraAppuntamentoDisponibile;
        TextView tvLuogoAppuntamentoDisponibile;

        public LDViewHolder(@NonNull View itemView) {
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
                    notifyDataSetChanged();
                }
            });
            tvDataAppuntamentoDisponibile= itemView.findViewById(R.id.tvDataAppuntamentoDisponibile);
            tvOraAppuntamentoDisponibile = itemView.findViewById(R.id.tvOraAppuntamentoDisponibile);
            tvLuogoAppuntamentoDisponibile= itemView.findViewById(R.id.tvLuogoAppuntamentoDisponibile);
        }
    }
}
