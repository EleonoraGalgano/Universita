package pjdm.pjdm2022.radiolab.UI;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.MalformedJsonException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.adapter.ListaStruttureAdapter;
import pjdm.pjdm2022.radiolab.databinding.FragmentSediBinding;
import pjdm.pjdm2022.radiolab.entity.ListaStruttureElement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SediFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SediFragment extends Fragment{ //implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //private MapView mMapView;

    private RecyclerView rv;
    private ListaStruttureAdapter adapter;

    private FragmentSediBinding binding;

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final String TAG = "StruttureFragment";
    private static final int CHANGE_IMAGE = 1;

    private ImageView ivStrutture2;
    private Bitmap bmp;
    private int index = 0;

    private ArrayList<Bitmap> imgList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SediFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StruttureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SediFragment newInstance(String param1, String param2) {
        SediFragment fragment = new SediFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSediBinding.inflate(inflater, container, false);
        rv = binding.rvStrutture;
        adapter = new ListaStruttureAdapter(getContext());
        richiediSedi();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        return binding.getRoot();
    }

    private void richiediSedi() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host)+getContext().getString(R.string.listaPrestazioni_urlPattern));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "Bearer " + MainActivity.datiUtente.getAccessToken());
                    conn.connect();
                    if(conn.getResponseCode()/100 != 200){
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
                                        NavHostFragment.findNavController(SediFragment.this)
                                                .navigate(R.id.action_sediFragment_to_AccessoFragment);
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
                    }else {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line = bufferedReader.readLine();
                        bufferedReader.close();
                        JSONArray jsonArray = new JSONArray(line);
                        ArrayList<ListaStruttureElement> listaStrutture = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            listaStrutture.add(new ListaStruttureElement(
                                    jsonObject.getInt("codiceSede"),
                                    jsonObject.getString("nomeSede"),
                                    jsonObject.getString("indirizzoSede"),
                                    jsonObject.getString("cap"),
                                    jsonObject.getString("telefono"),
                                    jsonObject.getString("email"),
                                    jsonObject.getString("orariApertura")
                            ));
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.addAll(listaStrutture);
                                rv.setAdapter(adapter);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
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