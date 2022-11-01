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
import pjdm.pjdm2022.radiolab.adapter.ListaDisponibilitaAdapter;
import pjdm.pjdm2022.radiolab.databinding.FragmentDisponibilitaBinding;
import pjdm.pjdm2022.radiolab.entity.ListaDisponibilitaElement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisponibilitaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisponibilitaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Executor executor = Executors.newSingleThreadExecutor();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rv;
    private ListaDisponibilitaAdapter adapter;

    public DisponibilitaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisponibilitaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisponibilitaFragment newInstance(String param1, String param2) {
        DisponibilitaFragment fragment = new DisponibilitaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private FragmentDisponibilitaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String prestazioneScelta = getArguments().getString("nomePrestazioneScelta");
        Log.d(TAG, "Selezionata prestazione: " + prestazioneScelta);
        binding = FragmentDisponibilitaBinding.inflate(inflater, container, false);
        binding.btHome1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(DisponibilitaFragment.this)
                        .navigate(R.id.action_disponibilitaFragment_to_homeFragment);
            }
        });
        rv = binding.rvElencoDisponibilita;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListaDisponibilitaAdapter(getContext());
        richiediDisponibilita(getArguments().getString("codicePrestazioneScelta"));
        rv.setAdapter(adapter);
        binding.tvPrestazioneInteresse.setText(prestazioneScelta);
        binding.btPrenotaOra2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getSelectedItem() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("codicePrestazione", getArguments().getString("codicePrestazioneScelta"));
                    bundle.putString("nomePrestazione", getArguments().getString("nomePrestazioneScelta"));
                    bundle.putString("ora", adapter.getSelectedItem().getOra());
                    bundle.putString("data", adapter.getSelectedItem().getData());
                    bundle.putString("luogo", adapter.getSelectedItem().getNomeSede());
                    bundle.putInt("idSala", adapter.getSelectedItem().getIdSala());
                    Log.d("EGG", "idSala: " + adapter.getSelectedItem().getIdSala());
                    NavHostFragment.findNavController(DisponibilitaFragment.this).navigate(R.id.action_disponibilitaFragment_to_prenotazioneFragment, bundle);
                } else {
                    Toast.makeText(getContext(), "Selezionare una disponibilità", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }

    private void richiediDisponibilita(String codicePrestazioneScelta) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.gestionePrenotazioni_urlPattern) + "?codicePrestazione=" + codicePrestazioneScelta);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    Log.d("EGG", "check "+ MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if(conn.getResponseCode()/100 != 2) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
                        if (conn.getResponseCode() == 401) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                        MainActivity.datiUtente.reset();
                                        NavHostFragment.findNavController(DisponibilitaFragment.this)
                                                .navigate(R.id.action_disponibilitaFragment_to_AccessoFragment);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.progressBarAppuntamentiDisponibili.setVisibility(View.INVISIBLE);
                                try {
                                    Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                NavHostFragment.findNavController(DisponibilitaFragment.this).navigate(R.id.action_disponibilitaFragment_to_homeFragment);
                            }
                        });
                    }
                    }else{
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        conn.disconnect();
                        JSONArray jsonArray = new JSONArray(line);
                        ArrayList<ListaDisponibilitaElement> listaDisponibilita = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            listaDisponibilita.add(new ListaDisponibilitaElement(
                                    jsonObject.getString("data"),
                                    jsonObject.getString("ora"),
                                    jsonObject.getString("nomeSede"),
                                    jsonObject.getInt("idSala")
                            ));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.progressBarAppuntamentiDisponibili.setVisibility(View.INVISIBLE);
                                adapter.addAll(listaDisponibilita);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.fallito_disponibilita)+" " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
                            NavHostFragment.findNavController(DisponibilitaFragment.this).navigate(R.id.action_disponibilitaFragment_to_homeFragment);
                        }
                    });
                    e.printStackTrace();
                } catch (MalformedJsonException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}