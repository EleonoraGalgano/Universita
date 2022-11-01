package pjdm.pjdm2022.radiolab.UI;

import android.app.DatePickerDialog;
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
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.databinding.FragmentRegistrazioneBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrazioneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrazioneFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Executor executor = Executors.newSingleThreadExecutor();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistrazioneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrazioneFragment newInstance( String param1, String param2 ) {
        RegistrazioneFragment fragment = new RegistrazioneFragment();
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

    private FragmentRegistrazioneBinding binding;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        binding = FragmentRegistrazioneBinding.inflate(inflater, container, false);
        binding.etDataNascita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet( android.widget.DatePicker view, int anno, int mese, int giorno ) {
                        binding.etDataNascita.setText(anno + "-" + (mese + 1) + "-" + giorno);
                    }
                }, 2000, 0, 1);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        binding.rbFemmina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                binding.rbFemmina.setChecked(true);
                binding.rbMaschio.setChecked(false);
            }
        });
        binding.rbMaschio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                binding.rbMaschio.setChecked(true);
                binding.rbFemmina.setChecked(false);
            }
        });
        binding.etPasswordConferma.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View v, boolean hasFocus ) {
                if (!hasFocus && !checkPassword()) {
                    binding.etPasswordConferma.setError("Le password non coincidono");
                }
            }
        });
        binding.etPasswordConferma.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange( View v, boolean hasFocus ) {
                if (!hasFocus && !checkPassword()) {
                    binding.etPasswordConferma.setError("Le password non coincidono");
                }
            }
        });
        binding.btConfermaRegistrazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if (checkPassword() && checkCampiObbligatori()) {
                    registraPaziente();
                    /*NavHostFragment.findNavController(RegistrazioneFragment.this)
                            .navigate(R.id.action_registrazioneFragment_to_accessoFragment);*/
                }
            }
        });
        binding.btAccesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                NavHostFragment.findNavController(RegistrazioneFragment.this)
                        .navigate(R.id.action_registrazioneFragment_to_accessoFragment);
            }
        });

        return binding.getRoot();
    }

    private void registraPaziente() {
        Log.d("EGG", "registraPaziente: ok");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.profiloPaziente_urlPattern)+"?nomePaziente=" + binding.etNome.getText().toString() +
                            "&cognomePaziente=" + binding.etCognome.getText().toString().trim() +
                            "&genere=" + (binding.rbMaschio.isChecked() ? "M" : "F") +
                            "&codiceFiscale=" + binding.etCodiceFiscaleReg.getText().toString() +
                            "&dataNascita=" + binding.etDataNascita.getText().toString() +
                            "&cittaNascita=" + binding.etCittaNascita.getText().toString() +
                            "&telefono=" + binding.etTelefono.getText().toString() +
                            "&indirizzo=" + binding.etIndirizzo.getText().toString() +
                            "&cap=" + binding.etCap.getText().toString() +
                            "&email=" + binding.etEmail.getText().toString() +
                            "&password=" + binding.etPassword.getText().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.connect();
                    if (conn.getResponseCode()/100 != 2) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getContext(), getContext().getString(R.string.fallita_registrazione) + " " + resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                    NavHostFragment.findNavController(RegistrazioneFragment.this)
                                            .navigate(R.id.action_registrazioneFragment_to_accessoFragment);
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
                            Toast.makeText(getContext(), getContext().getString(R.string.fallita_registrazione) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkCampiObbligatori() {
        for (int i = 0; i < binding.llRegistrazione.getChildCount(); i++) {
            View view = binding.llRegistrazione.getChildAt(i);
            Log.d("EGG", "CampiObbligatorio: " + view.getAccessibilityClassName());
            if (view instanceof android.widget.EditText) {
                android.widget.EditText editText = (android.widget.EditText) view;
                Log.d("EGG", "CampiObbligatorio: " + editText.getHint() + " " + editText.getText());
                if (editText.length() == 0) {
                    editText.setError("Campo obbligatorio");
                    return false;
                }
            } else if (view instanceof android.widget.TableRow) {
                Log.d("EGG", "checkCampiObbligatori: " + binding.rbMaschio.isChecked() + " " + binding.rbFemmina.isChecked());
                if (!binding.rbMaschio.isChecked() && !binding.rbFemmina.isChecked()) {
                    binding.rbMaschio.setError("Campo obbligatorio");
                    return false;
                }
            } else if (view instanceof android.widget.TextView) {
                android.widget.TextView textView = (android.widget.TextView) view;
                Log.d("EGG", "CampiObbligatorio: " + textView.getHint() + " " + textView.getText());
                if (textView.length() == 0) {
                    textView.setError("Campo obbligatorio");
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkPassword() {
        if (binding.etPassword.getText().toString().equals(binding.etPasswordConferma.getText().toString())) {
            return true;
        }
        return false;
    }

}