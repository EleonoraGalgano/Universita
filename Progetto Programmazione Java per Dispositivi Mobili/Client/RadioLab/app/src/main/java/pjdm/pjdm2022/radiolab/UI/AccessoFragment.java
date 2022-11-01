package pjdm.pjdm2022.radiolab.UI;

import android.os.Bundle;
import android.util.Log;
import android.util.MalformedJsonException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.databinding.FragmentAccessoBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccessoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccessoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Executor executor = Executors.newSingleThreadExecutor();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccessoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccessoFragment newInstance( String param1, String param2 ) {
        AccessoFragment fragment = new AccessoFragment();
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
        if (MainActivity.datiUtente.isLogged() && MainActivity.datiUtente.getRole().equals(getString(R.string.paziente))) {
            NavHostFragment.findNavController(AccessoFragment.this).navigate(R.id.action_accessoFragment_to_homeFragment);
        } else if (MainActivity.datiUtente.isLogged() && MainActivity.datiUtente.getRole().equals(getString(R.string.impiegato))) {
            NavHostFragment.findNavController(AccessoFragment.this).navigate(R.id.action_AccessoFragment_to_homeAmministrativoFragment);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(AccessoFragment.this).navigate(R.id.action_AccessoFragment_to_paginaInizialeFragment);
            }
        };

        requireActivity().

                getOnBackPressedDispatcher().

                addCallback(this, callback);

    }

    private FragmentAccessoBinding binding;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        binding = FragmentAccessoBinding.inflate(inflater, container, false);

        binding.btAccediPaziente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                String codiceFiscale = binding.etCodiceFiscale.getText().toString();
                String password = binding.etPassword.getText().toString();
                connetti(codiceFiscale, password);
            }
        });
        binding.btRegistrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                NavHostFragment.findNavController(AccessoFragment.this)
                        .navigate(R.id.action_accessoFragment_to_registrazioneFragment);
            }
        });

        binding.btOpzioneAmmnistrativo.setOnClickListener(new View.OnClickListener() {
            public void onClick( View v ) {
                NavHostFragment.findNavController(AccessoFragment.this)
                        .navigate(R.id.action_AccessoFragment_to_AccessoAmministrativoFragment);
            }
        });

        return binding.getRoot();
    }

    private void connetti( String codiceFiscale, String password ) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.accessoPaziente_urlPattern)+"?codiceFiscale=" + codiceFiscale + "&password=" + password);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.connect();

                    if (conn.getResponseCode() / 100 != 2) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
                        getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        NavHostFragment.findNavController(AccessoFragment.this).navigate(R.id.action_accessoFragment_to_homeFragment);
                                        MainActivity.datiUtente.saveData(getContext().getString(R.string.paziente), resJsonObject.getString("accessToken"));
                                        binding.etCodiceFiscale.setText("");
                                        binding.etPassword.setText("");
                                        Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.fallito_accesso) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                } catch (MalformedJsonException e) {
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