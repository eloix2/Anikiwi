package com.example.anikiwi.ui.animedata;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import jp.wasabeef.glide.transformations.BlurTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.anikiwi.R;
import com.example.anikiwi.databinding.ActivityAnimeDataBinding;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AnimeDataActivity extends AppCompatActivity {
    private ActivityAnimeDataBinding binding;
    private AnimeDataViewModel animeDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimeDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setActivityToolbar(binding.getRoot());
        // Add back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.textViewTitle.setText(getIntent().getStringExtra("anime_title"));

        String animeId = getIntent().getStringExtra("anime_id");

        // Initialize ViewModel
        animeDataViewModel = new ViewModelProvider(this).get(AnimeDataViewModel.class);
        animeDataViewModel.init(animeId); // Pass anime ID to ViewModel

        // Observe LiveData for anime data changes
        animeDataViewModel.getAnimeData().observe(this, animeData -> {
            if (animeData != null) {
                // Update UI with anime data
                binding.textViewTitle.setText(animeData.getTitle());

                // Add blurred background image with glide
                Glide.with(this)
                        .load(animeData.getImage_url())
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 3)))
                        .into(binding.imageViewBackground);

                // Add portrait image with glide
                Glide.with(this)
                        .load(animeData.getImage_url())
                        .into(binding.imageViewPortrait);
                // Update other UI elements with relevant data
                binding.textViewEpisodes.setText(animeData.getEpisodes());
                binding.textViewStatus.setText(animeData.getStatus());
                binding.textViewType.setText(animeData.getType());
                binding.textViewSeasonYear.setText(animeData.getSeasonAndYear());

                // Add button
                binding.buttonRateAnime.setOnClickListener(v -> {
                    showRateAnimeDialog();
                });


            }
        });
    }
    // Sets the toolbar for the fragment
    private void setActivityToolbar(View root) {
        // Find the Toolbar in the fragment's layout
        Toolbar toolbar = root.findViewById(R.id.custom_Toolbar);
        toolbar.setTitle("Anime Data");
        // Set the Toolbar as the ActionBar
        (this).setSupportActionBar(toolbar);
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

    public void showRateAnimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rate_anime, null);
        builder.setView(dialogView);

        EditText startingDayEditText = dialogView.findViewById(R.id.editTextStartingDay);
        EditText finishedDayEditText = dialogView.findViewById(R.id.editTextFinishedDay);

        startingDayEditText.setOnClickListener(v -> {
            // on below line we are getting
            // the instance of our calendar.
            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // on below line we are setting date to our edit text.
                        startingDayEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);

                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });

        finishedDayEditText.setOnClickListener(v -> {
            // on below line we are getting
            // the instance of our calendar.
            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // on below line we are setting date to our edit text.
                        finishedDayEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);

                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });

        //EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        //EditText editTextYear = dialogView.findViewById(R.id.editTextYear);

        // Lists of seasons, types and statuses
        Spinner spinnerRateStatus = dialogView.findViewById(R.id.spinnerRateStatus);
        Spinner spinnerRateScore = dialogView.findViewById(R.id.spinnerRateScore);

        // Creates ArrayAdapters to populate the Spinners with seasons, types and statuses
        ArrayAdapter<CharSequence> adapterRateStatus = ArrayAdapter.createFromResource(this,
                R.array.rate_status_array, android.R.layout.simple_spinner_item);

        adapterRateStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterRateScores = ArrayAdapter.createFromResource(this,
                R.array.scores_array, android.R.layout.simple_spinner_item);

        adapterRateScores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        spinnerRateStatus.setAdapter(adapterRateStatus);
        spinnerRateScore.setAdapter(adapterRateScores);

        //Todo: Llamar a la api para obtener los datos del rate activo y mostrarlos en el dialogo

        // Set the values of the fields that are saved from the last search
        //editTextTitle.setText(animeViewModel.getSavedQuery("title"));
        //editTextYear.setText(animeViewModel.getSavedQuery("year"));
        //spinnerSeasons.setSelection(adapterSeason.getPosition(animeViewModel.getSavedQuery("season")));
        //spinnerTypes.setSelection(adapterType.getPosition(animeViewModel.getSavedQuery("type")));
        //spinnerStatus.setSelection(adapterStatus.getPosition(animeViewModel.getSavedQuery("status")));

        builder.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
/*                String title = editTextTitle.getText().toString();
                String year = editTextYear.getText().toString();
                String selectedSeason = spinnerSeasons.getSelectedItem().toString();
                String selectedTypes = spinnerTypes.getSelectedItem().toString();
                String selectedStatus = spinnerStatus.getSelectedItem().toString();

                // TODO: Add tag filter functionality

                // Realiza acciones con los datos ingresados por el usuario
                // Llamar a la API
                Map<String, Object> queryParams = new HashMap<>();
                if(!title.isEmpty())
                    queryParams.put("title", title);
                if(!year.isEmpty())
                    queryParams.put("year", year);
                if(!selectedSeason.isEmpty())
                    queryParams.put("season", selectedSeason);
                if(!selectedTypes.isEmpty())
                    queryParams.put("type", selectedTypes);
                if(!selectedStatus.isEmpty())
                    queryParams.put("status", selectedStatus);

                animeViewModel.filterAnimes(queryParams);
                adapter.notifyDataSetChanged();*/
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Cierra el diálogo si se presiona "Cancelar"
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
