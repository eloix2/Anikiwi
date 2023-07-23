package com.example.anikiwi.ui.animedata;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anikiwi.R;
import com.example.anikiwi.databinding.ActivityAnimeDataBinding;
import com.example.anikiwi.databinding.ActivityMainBinding;


public class AnimeDataActivity extends AppCompatActivity {

    //puedo hacer la carga de datos de dos formas:
    //pasando los datos desde la view anterior
    //pasando el id y cargando los datos en la activity
    //el problema es que si se duerme la api he de hacer otro boton para recargar los datos y puede ser repetitivo
    //1. con un intent, pasando el id del anime y cargando los datos en la activity

    private ActivityAnimeDataBinding binding;
    private AnimeDataViewModel animeDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimeDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //add back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.textViewTitle.setText(getIntent().getStringExtra("anime_title"));


        //animeDataViewModel = new ViewModelProvider(this).get(AnimeDataViewModel.class);
        //animeDataViewModel.init();
        //animeDataViewModel.getAnimeData().observe(this, new Observer<AnimeData>() {
          //  @Override
            //public void onChanged(AnimeData animeData) {

            //}
        //});
    }

    //add back button functionality replicating the behaviour of real android back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // This is the ID of the back button in the ActionBar/Toolbar
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
