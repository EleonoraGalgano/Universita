package pjdm.pjdm2022.radiolab.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import pjdm.pjdm2022.radiolab.R;
import pjdm.pjdm2022.radiolab.data.DatiUtente;
import pjdm.pjdm2022.radiolab.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    public static DatiUtente datiUtente;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        datiUtente = new DatiUtente(this);
    }
}