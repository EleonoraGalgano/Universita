package pjdm.pjdm2022.radiolab.UI;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.MalformedJsonException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.adapter.ListaPrestazioniAdapter;
import pjdm.pjdm2022.radiolab.databinding.FragmentListaPrestazioniBinding;
import pjdm.pjdm2022.radiolab.entity.ListaPrestazioniElement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaPrestazioniFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaPrestazioniFragment extends Fragment {
    private Executor executor = Executors.newSingleThreadExecutor();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView rv;
    private ListaPrestazioniAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListaPrestazioniFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaEsamiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaPrestazioniFragment newInstance( String param1, String param2 ) {
        ListaPrestazioniFragment fragment = new ListaPrestazioniFragment();
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

    private FragmentListaPrestazioniBinding binding;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        binding = FragmentListaPrestazioniBinding.inflate(inflater, container, false);

        rv = binding.rvElencoEsami;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListaPrestazioniAdapter(getContext());
        richiediPrestazioni();
        rv.setAdapter(adapter);
        binding.btVisualizzaDisponibilita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if (adapter.getSelectedItem() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("nomePrestazioneScelta", adapter.getSelectedItem().getNome());
                    bundle.putString("codicePrestazioneScelta", adapter.getSelectedItem().getCodicePrestazione());
                    NavHostFragment.findNavController(ListaPrestazioniFragment.this)
                            .navigate(R.id.action_listaPrestazioniFragment_to_disponibilitaFragment, bundle);
                } else {
                    Toast.makeText(getContext(), "Selezionare una prestazione", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }

    private void richiediPrestazioni() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.listaPrestazioni_urlPattern));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if (conn.getResponseCode() / 100 != 2) {
                        BufferedReader bufferdReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String line = bufferdReader.readLine();
                        bufferdReader.close();
                        JSONObject resJsonObject = new JSONObject(line);
                        if (conn.getResponseCode() == 401) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                        MainActivity.datiUtente.reset();
                                        NavHostFragment.findNavController(ListaPrestazioniFragment.this)
                                                .navigate(R.id.action_listaEsamiFragment_to_AccessoFragment);
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
                        Log.d("EGG", "run: " + conn.getResponseCode());
                        BufferedReader bufferdReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = bufferdReader.readLine();
                        bufferdReader.close();
                        JSONArray jsonArray = new JSONArray(line);
                        ArrayList<ListaPrestazioniElement> listaPrestazioni = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            listaPrestazioni.add(new ListaPrestazioniElement(jsonObject.getString("nomePrestazione"),
                                    jsonObject.getString("codicePrestazione"), (float) jsonObject.getDouble("costo")));
                        }
                        Log.d("EGG", "run: " + listaPrestazioni.toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.progressBarListaPrestazioni.setVisibility(View.INVISIBLE);
                                adapter.addAll(listaPrestazioni);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    Log.e(TAG, "run: " + e.getMessage());
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.fallito_recupero_prestazioni) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                } catch (MalformedJsonException e) {
                    Log.e(TAG, "run: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "run: " + e.getMessage());
                }
            }
        });
    }
}