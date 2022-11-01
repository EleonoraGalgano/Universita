package pjdm.pjdm2022.radiolab.UI;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import pjdm.pjdm2022.radiolab.databinding.FragmentAccessoAmministrativoBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccessoAmministrativoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccessoAmministrativoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Executor executor = Executors.newSingleThreadExecutor();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccessoAmministrativoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccessoAmministrativoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccessoAmministrativoFragment newInstance(String param1, String param2) {
        AccessoAmministrativoFragment fragment = new AccessoAmministrativoFragment();
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(AccessoAmministrativoFragment.this)
                        .navigate(R.id.action_AccessoAmministrativoFragment_to_AccessoFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
    private FragmentAccessoAmministrativoBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccessoAmministrativoBinding.inflate(inflater, container, false);
        binding.btAccediAmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPasswordAmm.getText().toString();
                connetti(username, password);
            }
        });
        return binding.getRoot();
    }

    private void connetti( String username, String password ) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host)+getContext().getString(R.string.accessoImpiegato_urlPattern)+"?username=" + username + "&password=" + password);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.connect();
                    if(conn.getResponseCode()/100 != 2) {
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
                    }else{
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        conn.disconnect();
                        JSONObject resJsonObject = new JSONObject(line);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MainActivity.datiUtente.saveData(getContext().getString(R.string.impiegato), resJsonObject.getString("accessToken"));
                                    binding.etUsername.setText("");
                                    binding.etPasswordAmm.setText("");
                                    Toast.makeText(getContext(), resJsonObject.getString("result"), Toast.LENGTH_LONG).show();
                                    NavHostFragment.findNavController(AccessoAmministrativoFragment.this).navigate(R.id.action_AccessoAmministrativoFragment_to_homeAmministrativoFragment);
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
                            Toast.makeText(getContext(), getContext().getString(R.string.fallito_accesso)+" " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
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