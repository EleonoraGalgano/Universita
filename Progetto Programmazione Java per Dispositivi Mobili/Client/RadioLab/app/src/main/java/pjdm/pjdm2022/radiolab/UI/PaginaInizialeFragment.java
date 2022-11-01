package pjdm.pjdm2022.radiolab.UI;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.databinding.FragmentPaginaInizialeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaginaInizialeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaginaInizialeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PaginaInizialeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaginaInizialeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaginaInizialeFragment newInstance(String param1, String param2) {
        PaginaInizialeFragment fragment = new PaginaInizialeFragment();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Exit");
                builder.setMessage("Sei sicuro di voler uscire?");
                builder.setPositiveButton("Si", (dialog, which) -> {
                    dialog.dismiss();
                    getActivity().finish();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
    private FragmentPaginaInizialeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaginaInizialeBinding.inflate(inflater, container, false);
        binding.btEntraApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                NavHostFragment.findNavController(PaginaInizialeFragment.this).navigate(R.id.action_paginaInizialeFragment_to_AccessoFragment);
            }
        });
        return binding.getRoot();
    }
}