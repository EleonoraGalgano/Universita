package pjdm.pjdm2022.radiolab.UI;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.adapter.ListaAppuntamentiAdapter;
import pjdm.pjdm2022.radiolab.databinding.FragmentAppuntamentiBinding;
import pjdm.pjdm2022.radiolab.entity.ListaAppuntamentiElement;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppuntamentiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppuntamentiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rv;
    private ListaAppuntamentiAdapter adapter;
    private ListaAppuntamentiElement selectedItem = null;

    public AppuntamentiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppuntamentiFragment.
     */
    private Executor executor = Executors.newSingleThreadExecutor();

    // TODO: Rename and change types and number of parameters
    public static AppuntamentiFragment newInstance( String param1, String param2 ) {
        AppuntamentiFragment fragment = new AppuntamentiFragment();
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(AppuntamentiFragment.this).navigate(R.id.action_appuntamentiFragment_to_homeFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private FragmentAppuntamentiBinding binding;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        binding = FragmentAppuntamentiBinding.inflate(inflater, container, false);
        rv = binding.rvElencoAppuntamenti;
        adapter = new ListaAppuntamentiAdapter(getContext());
        recuperaAppuntamentiPaziente();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove( @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target ) {
                return false;
            }

            @Override
            public void onSwiped( @NonNull RecyclerView.ViewHolder viewHolder, int direction ) {
                int position = viewHolder.getAdapterPosition();
                int itemWidth = rv.getChildAt(position).getWidth();
                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Elimina appuntamento");
                    builder.setMessage("Sei sicuro di voler eliminare l'appuntamento?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {
                            richiediCancellazioneAppuntamento(position);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {
                            adapter.notifyItemChanged(position);
                        }
                    });

                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel( DialogInterface dialog ) {
                            adapter.notifyItemChanged(position);
                        }
                    });

                    builder.show();
                }
            }

            @Override
            public void onChildDraw( Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                ColorDrawable background = new ColorDrawable(getContext().getColor(R.color.bg_selected));
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
            }
        });
        itemTouchHelper.attachToRecyclerView(rv);
        return binding.getRoot();
    }

    private void richiediCancellazioneAppuntamento( int position ) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.gestionePrenotazioni_urlPattern) + "?idPrenotazione=" + adapter.getItem(position).getIdPrenotazione());
                    Log.d("EGG", "run: " + url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("DELETE");
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
                                        NavHostFragment.findNavController(AppuntamentiFragment.this)
                                                .navigate(R.id.action_appuntamentiFragment_to_AccessoFragment);
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
                                adapter.notifyItemChanged(position);
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
                                adapter.removeItem(position);
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
                            Toast.makeText(getContext(), getContext().getString(R.string.fallita_cancellazione) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
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

    private void recuperaAppuntamentiPaziente() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(getContext().getString(R.string.host) + getContext().getString(R.string.listaAppuntamenti_urlPattern));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
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
                                        NavHostFragment.findNavController(AppuntamentiFragment.this)
                                                .navigate(R.id.action_appuntamentiFragment_to_AccessoFragment);
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
                        ArrayList<ListaAppuntamentiElement> listaAppuntamenti = new ArrayList<>();
                        Log.d("EGG", "appuntamenti: " + resJsonArray.toString());
                        for (int i = 0; i < resJsonArray.length(); i++) {
                            JSONObject resJsonObject = resJsonArray.getJSONObject(i);
                            ListaAppuntamentiElement appuntamento = new ListaAppuntamentiElement(
                                    resJsonObject.getString("idPrenotazione"),
                                    resJsonObject.getString("nomePrestazione"),
                                    resJsonObject.getString("dataApp"),
                                    resJsonObject.getString("oraApp"),
                                    resJsonObject.getString("nomeSede"));
                            listaAppuntamenti.add(appuntamento);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.addAll(listaAppuntamenti);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getContext().getString(R.string.fallita_ricerca_appuntamento) + " " + getContext().getString(R.string.connessione_errore), Toast.LENGTH_LONG).show();
                            NavHostFragment.findNavController(AppuntamentiFragment.this)
                                    .navigate(R.id.action_appuntamentiFragment_to_homeFragment);
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