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
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.databinding.FragmentGestioneProfiloBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GestioneProfiloFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GestioneProfiloFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Executor executor = Executors.newSingleThreadExecutor();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GestioneProfiloFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GestioneProfiloFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GestioneProfiloFragment newInstance( String param1, String param2 ) {
        GestioneProfiloFragment fragment = new GestioneProfiloFragment();
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

    private FragmentGestioneProfiloBinding binding;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        binding = FragmentGestioneProfiloBinding.inflate(inflater, container, false);
        recuperaInformazioniPaziente();
        binding.btnModificaTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                modifica(binding.tvTelefonoPaziente, binding.tvTelefono.getText().toString());
            }
        });
        binding.btnModificaIndirizzo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                modifica(binding.tvIndirizzoPaziente, binding.tvIndirizzo.getText().toString());
            }
        });
        binding.btnModificaCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                modifica(binding.tvCapPaziente, binding.tvCap.getText().toString());
            }
        });
        binding.btnModificaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                modifica(binding.tvEmailPaziente, binding.tvEmail.getText().toString());
            }
        });
        binding.btnModificaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                modificaPassword();
            }
        });
        binding.btnDisconnetti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                MainActivity.datiUtente.reset();
                NavHostFragment.findNavController(GestioneProfiloFragment.this)
                        .navigate(R.id.action_gestioneProfiloFragment_to_accessoFragment);
            }
        });
        binding.btRimuoviAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Log.d("EGG", "onClick: rimuovi account");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Rimuovi account");
                builder.setMessage("Sei sicuro di voler rimuovere l'account?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        richiediRimoazioneAccount();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        return binding.getRoot();
    }

    private void richiediRimoazioneAccount() {
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.accessoPaziente_urlPattern));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.setRequestMethod("DELETE");
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
                                        NavHostFragment.findNavController(GestioneProfiloFragment.this)
                                                .navigate(R.id.action_gestioneProfiloFragment_to_accessoFragment);
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.datiUtente.reset();
                                Toast.makeText(getContext(), "Account rimosso", Toast.LENGTH_SHORT).show();
                                NavHostFragment.findNavController(GestioneProfiloFragment.this)
                                        .navigate(R.id.action_gestioneProfiloFragment_to_accessoFragment);
                            }
                        });
                    }
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void recuperaInformazioniPaziente() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.profiloPaziente_urlPattern));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if (conn.getResponseCode() / 100 != 2) {
                        BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(conn.getErrorStream()));
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
                                        NavHostFragment.findNavController(GestioneProfiloFragment.this)
                                                .navigate(R.id.action_gestioneProfiloFragment_to_accessoFragment);
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
                        BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    binding.tvNomePaziente.setText(resJsonObject.getString("nomePaziente").toString());
                                    binding.tvCognomePaziente.setText(resJsonObject.getString("cognomePaziente").toString());
                                    binding.tvTelefonoPaziente.setText(resJsonObject.getString("telefono").toString());
                                    binding.tvIndirizzoPaziente.setText(resJsonObject.getString("indirizzo").toString());
                                    binding.tvCapPaziente.setText(resJsonObject.getString("cap").toString());
                                    binding.tvCittaNascitaPaziente.setText(resJsonObject.getString("cittaNascita").toString());
                                    binding.tvDataNascitaPaziente.setText(resJsonObject.getString("dataNascita").toString());
                                    binding.tvGenerePaziente.setText(resJsonObject.getString("genere"));
                                    binding.tvCodiceFiscalePaziente.setText(resJsonObject.getString("codiceFiscale").toString());
                                    binding.tvEmailPaziente.setText(resJsonObject.getString("email").toString());
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
                            Toast.makeText(getContext(), getContext().getString(R.string.fallito_recupero_info_pz) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
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
            case "Email":
                etNuovoValore.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.profiloPaziente_urlPattern) + "?parametroDaModificare=" + parametro + "&nuovoValore=" + nuovoValore);
                    Log.d("EGG", "run: " + url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if (conn.getResponseCode() / 100 != 2) {
                        BufferedReader bufferedReader = new BufferedReader(new java.io.InputStreamReader(conn.getErrorStream()));
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
                                        NavHostFragment.findNavController(GestioneProfiloFragment.this)
                                                .navigate(R.id.action_gestioneProfiloFragment_to_accessoFragment);
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
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                        Toast.makeText(getContext(), getContext().getString(R.string.modifica_successo), Toast.LENGTH_SHORT).show();
                                        tv.setText(nuovoValore);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.modifica_non_effettuata) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_SHORT).show();
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
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.accessoPaziente_urlPattern) + "?precedentePassword=" + vecchiaPassword + "&nuovaPassword=" + nuovaPassword);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
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
                                        NavHostFragment.findNavController(GestioneProfiloFragment.this)
                                                .navigate(R.id.action_gestioneProfiloFragment_to_accessoFragment);
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
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.modifica_non_effettuata) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_SHORT).show();
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