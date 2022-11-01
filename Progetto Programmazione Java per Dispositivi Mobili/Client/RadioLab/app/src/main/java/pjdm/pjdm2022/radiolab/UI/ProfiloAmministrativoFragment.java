package pjdm.pjdm2022.radiolab.UI;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.databinding.FragmentProfiloAmministrativoBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfiloAmministrativoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfiloAmministrativoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Executor executor = Executors.newSingleThreadExecutor();

    public ProfiloAmministrativoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfiloAmministrativoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfiloAmministrativoFragment newInstance( String param1, String param2 ) {
        ProfiloAmministrativoFragment fragment = new ProfiloAmministrativoFragment();
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

    private FragmentProfiloAmministrativoBinding binding;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        binding = FragmentProfiloAmministrativoBinding.inflate(inflater, container, false);
        recuperaInformazioniAmministrativo();
        binding.btnModificaIndirizzoAmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                modifica(binding.tvIndirizzoAmministrativo, binding.tvIndirizzoAmm.getText().toString());
            }
        });
        binding.btnModificaTelefonoAmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                modifica(binding.tvTelefonoAmministrativo, binding.tvTelefonoAmm.getText().toString());
            }
        });
        binding.btnModificaCapAmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                modifica(binding.tvCapAmministrativo, binding.tvCapAmm.getText().toString());
            }
        });
        binding.btnModificaPasswordAmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                modificaPassword();
            }
        });
        binding.btnDisconnetti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                MainActivity.datiUtente.reset();
                NavHostFragment.findNavController(ProfiloAmministrativoFragment.this)
                        .navigate(R.id.action_profiloAmministrativoFragment_to_AccessoAmministrativoFragment);
            }
        });

        return binding.getRoot();
    }

    private void recuperaInformazioniAmministrativo() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.profiloAmministrativo_urlPattern));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    Log.d("EGG", "run: " + MainActivity.datiUtente.getAccessToken());
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if (conn.getResponseCode() / 100 != 2) {
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
                                        NavHostFragment.findNavController(ProfiloAmministrativoFragment.this)
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
                                        Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();

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
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.tvNomeAmministrativo.setText(resJsonObject.getString("nomeImpiegato").toString());
                                    binding.tvCognomeAmministrativo.setText(resJsonObject.getString("cognomeImpiegato").toString());
                                    binding.tvTelefonoAmministrativo.setText(resJsonObject.getString("telefono").toString());
                                    binding.tvIndirizzoAmministrativo.setText(resJsonObject.getString("indirizzo").toString());
                                    binding.tvCapAmministrativo.setText(resJsonObject.getString("cap").toString());
                                    binding.tvCittaNascitaAmministrativo.setText(resJsonObject.getString("cittaNascita").toString());
                                    binding.tvDataNascitaAmministrativo.setText(resJsonObject.getString("dataNascita").toString());
                                    binding.tvGenereAmministrativo.setText(resJsonObject.getString("genere"));
                                    binding.tvCodiceFiscaleAmministrativo.setText(resJsonObject.getString("codiceFiscale").toString());
                                    binding.tvEmailAmministrativo.setText(resJsonObject.getString("emailLavorativa").toString());
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
                            Toast.makeText(getContext(), getContext().getString(R.string.fallito_recupero_info_imp) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void modifica( TextView tv, String tipo ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Modifica " + tipo);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.modifica_dialog,
                (ViewGroup) getView().getRootView(), false);
        final EditText etNuovoValore = rootView.findViewById(R.id.etNuovoValore);
        switch (tipo) {
            case "Telefono":
                etNuovoValore.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                break;
            case "Indirizzo":
                etNuovoValore.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
                break;
            case "C.A.P":
                etNuovoValore.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                break;
        }
        etNuovoValore.setText(tv.getText());
        builder.setView(rootView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialogInterface, int i ) {
                richiediModifica(tipo.toLowerCase().replace(".", ""), etNuovoValore.getText().toString(), tv);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialogInterface, int i ) {
                dialogInterface.dismiss();

            }
        });
        builder.show();

    }

    private void richiediModifica( String parametro, String nuovoValore, TextView tv ) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.profiloAmministrativo_urlPattern) + "?parametroDaModificare=" + parametro + "&nuovoValore=" + nuovoValore);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                                    tv.setText(nuovoValore);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
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
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.modifica_non_effettuata) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void modificaPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Modifica Password");
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.modifica_password_dialog,
                (ViewGroup) getView().getRootView(), false);
        final EditText etVecchiaPassword = rootView.findViewById(R.id.etVecchiaPassword);
        final EditText etNuovaPassword = rootView.findViewById(R.id.etNuovaPassword);
        final EditText etConfermaPassword = rootView.findViewById(R.id.etConfermaPassword);
        builder.setView(rootView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialogInterface, int i ) {
                if (checkPassword(etNuovaPassword.getText().toString(), etConfermaPassword.getText().toString())) {
                    richiediModificaPassword(etVecchiaPassword.getText().toString(), etNuovaPassword.getText().toString());
                    dialogInterface.dismiss();
                } else {
                    etConfermaPassword.setError("Le password non corrispondono");
                }
            }
        });
        builder.show();
    }

    private void richiediModificaPassword( String vecchiaPassword, String nuovaPassword ) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.accessoImpiegato_urlPattern) + "?precedentePassword=" + vecchiaPassword + "&nuovaPassword=" + nuovaPassword);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
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
                    } else {
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
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.modifica_non_effettuata) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
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

    private boolean checkPassword( String nuovaPassword, String confermaPassword ) {
        if (nuovaPassword.equals(confermaPassword)) {
            return true;
        }
        return false;
    }

}