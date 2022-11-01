package pjdm.pjdm2022.radiolab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pjdm.pjdm2022.radiolab.entity.ListaStruttureElement;
import pjdm.pjdm2022.radiolab.R;

public class ListaStruttureAdapter extends RecyclerView.Adapter<ListaStruttureAdapter.LSViewHolder> {

    private ArrayList<ListaStruttureElement> dati;
    private Context context;

    private LayoutInflater inflater;

    private ListaStruttureElement selectedItem;
    public ListaStruttureAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);

        dati = new ArrayList<ListaStruttureElement>();
        dati.add(new ListaStruttureElement(1, "Radiolab Appio", "Viale Appio Claudio 1", "00178", "12345", "radiolabAppio@gmail.com", "lun-sab 8:00-19:00"));
        dati.add(new ListaStruttureElement(2, "Radiolab Eur", "Via dell'Oceano Atlantico 15", "00144", "23456", "radiolabEur@gmail.com", "lun-sab 8:00-19:00"));

    }

    @NonNull
    @Override
    public LSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_sede,parent,false);
        LSViewHolder vh = new LSViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LSViewHolder holder, int position) {
        holder.tvNomeStruttura.setText(dati.get(position).getNomeSede());
        holder.tvIndirizzo.setText(dati.get(position).getIndirizzoSede()+", "+dati.get(position).getCap());
        holder.tvOrarioApertura.setText(dati.get(position).getOrariApertura());
        holder.tvTelefono.setText(dati.get(position).getTelefono());
        holder.tvEmail.setText(dati.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return dati.size();
    }

    public void addAll(ArrayList<ListaStruttureElement> listaStruttureElements){
        dati.addAll(listaStruttureElements);
        notifyDataSetChanged();
    }

    public class LSViewHolder extends RecyclerView.ViewHolder {
            TextView tvNomeStruttura;
            TextView tvOrarioApertura;
            TextView tvTelefono;
            TextView tvIndirizzo;
            TextView tvEmail;

            public LSViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNomeStruttura = itemView.findViewById(R.id.tvNomeStruttura);
                tvOrarioApertura = itemView.findViewById(R.id.tvOrariStruttura);
                tvTelefono = itemView.findViewById(R.id.tvTelefonoStrutura);
                tvIndirizzo = itemView.findViewById(R.id.tvIndirizzo2);
                tvEmail = itemView.findViewById(R.id.tvEmailStruttura);
            }

    }
}
