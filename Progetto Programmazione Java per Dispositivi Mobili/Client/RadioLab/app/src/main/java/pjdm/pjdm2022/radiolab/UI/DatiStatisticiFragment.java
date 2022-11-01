package pjdm.pjdm2022.radiolab.UI;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.adapter.ListaStatisticaAdapter;
import pjdm.pjdm2022.radiolab.databinding.FragmentDatiStatisticiBinding;
import pjdm.pjdm2022.radiolab.entity.ListaStatisticaElement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DatiStatisticiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatiStatisticiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Executor executor = Executors.newSingleThreadExecutor();
    private RecyclerView rv;
    private ListaStatisticaAdapter adapter;
    private Spinner spinner;
    private BarChart barChart;

    public DatiStatisticiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatiStatisticiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatiStatisticiFragment newInstance( String param1, String param2 ) {
        DatiStatisticiFragment fragment = new DatiStatisticiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private FragmentDatiStatisticiBinding binding;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        binding = FragmentDatiStatisticiBinding.inflate(inflater, container, false);
        binding.rbPrenotazioniEseguiteStatistica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                binding.rbPrenotazioniEseguiteStatistica.setChecked(true);
                binding.rbPrestazioniNonFruiteStatistica.setChecked(false);
            }
        });
        binding.rbPrestazioniNonFruiteStatistica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                binding.rbPrestazioniNonFruiteStatistica.setChecked(true);
                binding.rbPrenotazioniEseguiteStatistica.setChecked(false);
            }
        });

        binding.tvDataInizioStatistica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet( android.widget.DatePicker view, int anno, int mese, int giorno ) {
                        binding.tvDataInizioStatistica.setText(anno + "-" + (mese + 1) + "-" + giorno);
                    }
                }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
                datePickerDialog.getDatePicker().setMinDate(LocalDate.of(2022, 1, 1).plusMonths(0).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
                datePickerDialog.getDatePicker().setMaxDate(LocalDate.now().plusMonths(0).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
                datePickerDialog.show();
            }
        });

        binding.tvDataFineStatistica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet( android.widget.DatePicker view, int anno, int mese, int giorno ) {
                        binding.tvDataFineStatistica.setText(anno + "-" + (mese + 1) + "-" + giorno);
                    }
                }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth());
                datePickerDialog.getDatePicker().setMinDate(LocalDate.of(2022, 1, 1).plusMonths(0).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
                datePickerDialog.getDatePicker().setMaxDate(LocalDate.now().plusMonths(0).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
                datePickerDialog.show();
            }
        });
        spinner = binding.spinnerMensileAnnualeStatistica;
        ArrayAdapter<CharSequence> adapterPeriodo = ArrayAdapter.createFromResource(getContext(), R.array.raggruppamento, android.R.layout.simple_spinner_item);
        adapterPeriodo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterPeriodo);
        rv = binding.rvStatistica;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListaStatisticaAdapter(getContext());
        rv.setAdapter(adapter);
        binding.swCambiaVisualizzazione1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                if (isChecked) {
                    binding.rvStatistica.setVisibility(View.INVISIBLE);
                    binding.istoDatiStatistici.setVisibility(View.VISIBLE);
                } else {
                    binding.rvStatistica.setVisibility(View.VISIBLE);
                    binding.istoDatiStatistici.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.btEseguiStatistiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                if (!binding.rbPrenotazioniEseguiteStatistica.isChecked() && !binding.rbPrestazioniNonFruiteStatistica.isChecked()) {
                    Toast.makeText(getContext(), "Selezionare un parametro da valutare", Toast.LENGTH_SHORT).show();
                } else if (binding.tvDataInizioStatistica.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Selezionare una data di inizio", Toast.LENGTH_SHORT).show();
                } else if (binding.tvDataFineStatistica.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Selezionare una data di fine", Toast.LENGTH_SHORT).show();
                } else if (!Date.valueOf(binding.tvDataFineStatistica.getText().toString()).after(Date.valueOf(binding.tvDataInizioStatistica.getText().toString()))) {
                    Toast.makeText(getContext(), "La data di fine deve essere successiva alla data di inizio", Toast.LENGTH_SHORT).show();
                } else {
                    binding.swCambiaVisualizzazione1.setChecked(false);
                    richiediStatiche();
                }
            }
        });
        return binding.getRoot();
    }

    private void estrapolaGraficoDatiStatistica( ArrayList<ListaStatisticaElement> statisticaPrestazioniRisultato ) {
        barChart = binding.istoDatiStatistici;
        final ArrayList<BarEntry> ammontare = new ArrayList<>();
        final ArrayList<String> mese_anno = new ArrayList<>();
        for (int i = 0; i < statisticaPrestazioniRisultato.size(); i++) {
            ammontare.add(new BarEntry(i, statisticaPrestazioniRisultato.get(i).getAmmontare()));
            mese_anno.add(statisticaPrestazioniRisultato.get(i).getMeseAnno());
        }
        Log.d("EGG", "estrapolaGraficoStatistica: " + mese_anno.toString());
        BarDataSet dataSet = new BarDataSet(ammontare, "Statistica Prenotazione");
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setDrawLabels(true);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(mese_anno));
        barChart.getXAxis().setLabelRotationAngle(-45);
        BarData data = new BarData(dataSet);
        dataSet.setColors(ColorTemplate.getHoloBlue());

        barChart.getDescription().setEnabled(false);
        barChart.setData(data);
    }

    private void richiediStatiche() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.esecuzioneStatistica_urlPattern)
                            + "?statoPrenotazione=" + (binding.rbPrenotazioniEseguiteStatistica.isChecked() ? "2" : "1")
                            + "&inizioPeriodo=" + binding.tvDataInizioStatistica.getText()
                            + "&finePeriodo=" + binding.tvDataFineStatistica.getText()
                            + "&raggruppamento=" + (binding.spinnerMensileAnnualeStatistica.getSelectedItem().toString().equals("ANNO") ? "anno" : "mese")
                    );
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if (conn.getResponseCode() / 100 != 2) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        JSONObject resJsonObject = new JSONObject(line);
                        if (conn.getResponseCode() == 401) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                        MainActivity.datiUtente.reset();
                                        NavHostFragment.findNavController(DatiStatisticiFragment.this)
                                                .navigate(R.id.action_profiloAmministrativoFragment_to_AccessoAmministrativoFragment);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        JSONArray resJsonArray = new JSONArray(line);
                        ArrayList<ListaStatisticaElement> listaStatisticaPrestazioni = new ArrayList<>();
                        for (int i = 0; i < resJsonArray.length(); i++) {
                            JSONObject resJsonObject = resJsonArray.getJSONObject(i);
                            String mese_anno = null;
                            if (binding.spinnerMensileAnnualeStatistica.getSelectedItem().toString().equals("ANNO")) {
                                mese_anno = String.valueOf(resJsonObject.getInt("raggruppamento"));
                            } else {
                                mese_anno = new DateFormatSymbols().getMonths()[resJsonObject.getInt("raggruppamento") - 1];
                            }
                            ListaStatisticaElement statisticaPrestazione = new ListaStatisticaElement(
                                    mese_anno,
                                    resJsonObject.getInt("ammontare"));
                            listaStatisticaPrestazioni.add(statisticaPrestazione);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.addAll(listaStatisticaPrestazioni);
                                estrapolaGraficoDatiStatistica(listaStatisticaPrestazioni);
                                binding.swCambiaVisualizzazione1.setEnabled(true);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.fallita_statistica) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
                            NavHostFragment.findNavController(DatiStatisticiFragment.this)
                                    .navigate(R.id.action_datiStatisticiFragment_to_homeAmministrativoFragment);
                        }
                    });
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}