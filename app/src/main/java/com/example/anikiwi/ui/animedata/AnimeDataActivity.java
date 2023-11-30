package com.example.anikiwi.ui.animedata;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.anikiwi.networking.Anime;
import com.example.anikiwi.networking.Rating;
import com.example.anikiwi.utilities.DateConverter;
import com.example.anikiwi.utilities.InputFilterMinMax;

import java.util.Calendar;


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
                        .load(animeData.getImageUrl())
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 3)))
                        .into(binding.imageViewBackground);

                // Add portrait image with glide
                Glide.with(this)
                        .load(animeData.getImageUrl())
                        .into(binding.imageViewPortrait);
                // Update other UI elements with relevant data
                binding.textViewEpisodes.setText(animeData.getEpisodes());
                binding.textViewStatus.setText(animeData.getStatus());
                binding.textViewType.setText(animeData.getType());
                binding.textViewSeasonYear.setText(animeData.getSeasonAndYear());
                binding.textViewTags.setText(formatTags(animeData.getTags()));

                binding.cardViewTags.setOnClickListener(v -> {
                    toggleTagsVisibility();
                });
                // Add button
                binding.buttonRateAnime.setOnClickListener(v -> {
                    showRateAnimeDialog(animeData);
                });

            }
        });
    }

    // TODO: Move this to a utility class
    private String formatTags(String[] tags) {
        if (tags == null || tags.length == 0) {
            return "";
        }

        return TextUtils.join(", ", tags);
    }

    // Function to toggle visibility of tags
    private void toggleTagsVisibility() {
        TextView textViewTags = binding.textViewTags;
        TextView textViewTouchTags = binding.textViewTouchTags;

        if (textViewTags.getVisibility() == View.VISIBLE) {
            // Tags are visible, collapse them
            textViewTags.setVisibility(View.GONE);
            textViewTouchTags.setVisibility(View.VISIBLE);
        } else {
            // Tags are collapsed, expand them
            textViewTags.setVisibility(View.VISIBLE);
            textViewTouchTags.setVisibility(View.GONE);
        }
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

    public void showRateAnimeDialog(Anime animeData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rate_anime, null);
        builder.setView(dialogView);

        // Set the max Episodes using animeData
        TextView textViewRateMaxEpisodes = dialogView.findViewById(R.id.textViewRateMaxEpisodes);
        textViewRateMaxEpisodes.setText(animeData.getEpisodes());

        EditText editTextRateEpisodes  = dialogView.findViewById(R.id.editTextRateEpisodes);
        EditText startingDateEditText = dialogView.findViewById(R.id.editTextStartingDate);
        EditText finishedDateEditText = dialogView.findViewById(R.id.editTextFinishedDate);
        EditText editTextRateScore = dialogView.findViewById(R.id.editTextRateScore);

        // Set the filters for the number EditTexts

        editTextRateEpisodes.setFilters(new InputFilter[]{ new InputFilterMinMax("0", animeData.getEpisodes()) });
        editTextRateScore.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "10") });

        // Set the onClickListeners for the Date EditTexts

        startingDateEditText.setOnClickListener(v -> {
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
                        startingDateEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);

                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });

        finishedDateEditText.setOnClickListener(v -> {
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
                        finishedDateEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);

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

        // Lists of statuses
        Spinner spinnerRateStatus = dialogView.findViewById(R.id.spinnerRateStatus);

        // Creates ArrayAdapters to populate the Spinners with statuses
        ArrayAdapter<CharSequence> adapterRateStatus = ArrayAdapter.createFromResource(this,
                R.array.rate_status_array, android.R.layout.simple_spinner_item);

        adapterRateStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the Spinner
        spinnerRateStatus.setAdapter(adapterRateStatus);

        // Set the values of the fields with the data from the active rating
        animeDataViewModel.getRatingData().observe(this, ratingData -> {
            if (ratingData != null) {
                // Update dialog with rating data
                spinnerRateStatus.setSelection(adapterRateStatus.getPosition(ratingData.getWatchStatus()));
                editTextRateEpisodes.setText(ratingData.getEpisodesWatched());
                editTextRateScore.setText(ratingData.getScore());
                startingDateEditText.setText(DateConverter.convertDateMongoToJava(ratingData.getStartingDate()));
                finishedDateEditText.setText(DateConverter.convertDateMongoToJava(ratingData.getFinishedDate()));
            }
        });

        builder.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the active user Id and the anime Id
                String userId = animeDataViewModel.getActiveUserId();
                String animeId = animeData.getId();

                // Get the values of the fields
                String rateStatus = spinnerRateStatus.getSelectedItem().toString();
                String rateEpisodes = editTextRateEpisodes.getText().toString();
                String rateScore = editTextRateScore.getText().toString();
                String rateStartingDate = startingDateEditText.getText().toString();
                String rateFinishedDate = finishedDateEditText.getText().toString();

                //Convert date formats to the one used in the API
                rateStartingDate = DateConverter.convertDateJavaToMongo(rateStartingDate);
                rateFinishedDate = DateConverter.convertDateJavaToMongo(rateFinishedDate);

                // Llamar a la API

                if(rateEpisodes.isEmpty())
                    rateEpisodes = "0";

                if(rateScore.isEmpty())
                    rateScore = null;

                if(userId != null && !userId.isEmpty() && animeId != null && !animeId.isEmpty()){
                    Rating rating = new Rating(userId, animeId, rateStatus, rateEpisodes, rateScore, rateStartingDate, rateFinishedDate);
                    animeDataViewModel.rateAnime(rating);
                }

                Toast.makeText(getApplicationContext(), "Anime rated", Toast.LENGTH_SHORT).show();
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
