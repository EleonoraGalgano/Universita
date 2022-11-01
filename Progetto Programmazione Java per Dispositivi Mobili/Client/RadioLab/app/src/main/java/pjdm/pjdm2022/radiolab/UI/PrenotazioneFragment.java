package pjdm.pjdm2022.radiolab.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.databinding.FragmentPrenotazioneBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrenotazioneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrenotazioneFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Executor executor = Executors.newSingleThreadExecutor();

    public PrenotazioneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrenotazioneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrenotazioneFragment newInstance( String param1, String param2 ) {
        PrenotazioneFragment fragment = new PrenotazioneFragment();
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

    private FragmentPrenotazioneBinding binding;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        binding = FragmentPrenotazioneBinding.inflate(inflater, container, false);
        binding.tvNomePrestazioneRiepilogo.setText(getArguments().getString("nomePrestazione"));
        binding.tvDataRiepilogo.setText(getArguments().getString("data"));
        binding.tvOraRiepilogo.setText(getArguments().getString("ora"));
        binding.tvLuogoRiepilogo.setText(getArguments().getString("luogo"));
        binding.btAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                NavHostFragment.findNavController(PrenotazioneFragment.this)
                        .navigate(R.id.action_prenotazioneFragment_to_homeFragment);
            }
        });
        binding.btConfermaPrenotazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                richiediPrenotazione();
            }
        });
        return binding.getRoot();
    }

    private void richiediPrenotazione() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.gestionePrenotazioni_urlPattern)
                            + "?codicePrestazione=" + getArguments().getString("codicePrestazione")
                            + "&data=" + getArguments().getString("data")
                            + "&ora=" + getArguments().getString("ora")
                            + "&nomeSede=" + getArguments().getString("luogo")
                            + "&idSala=" + getArguments().getInt("idSala"));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if (conn.getResponseCode()/100 != 2) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String line = bufferedReader.readLine();
                        conn.disconnect();
                        bufferedReader.close();
                        JSONObject resJsonObject = new JSONObject(line);
                        if (conn.getResponseCode() == 401) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                        MainActivity.datiUtente.reset();
                                        NavHostFragment.findNavController(PrenotazioneFragment.this)
                                                .navigate(R.id.action_prenotazioneFragment_to_AccessoFragment);
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
                                        Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    NavHostFragment.findNavController(PrenotazioneFragment.this)
                                            .navigate(R.id.action_prenotazioneFragment_to_disponibilitaFragment);
                                }
                            });
                        }
                    } else {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = bufferedReader.readLine();
                        conn.disconnect();
                        bufferedReader.close();
                        JSONObject resJsonObject = new JSONObject(line);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                NavHostFragment.findNavController(PrenotazioneFragment.this)
                                        .navigate(R.id.action_prenotazioneFragment_to_appuntamentiFragment);
                            }
                        });
                    }
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.fallita_prenotazione) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
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