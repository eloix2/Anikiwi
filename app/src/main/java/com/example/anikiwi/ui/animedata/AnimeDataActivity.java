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
import com.example.anikiwi.utilities.FilterUtils;
import com.example.anikiwi.utilities.InputFilterMinMax;
import com.example.anikiwi.utilities.ToolbarUtil;

import java.util.Calendar;


public class AnimeDataActivity extends AppCompatActivity {
    private ActivityAnimeDataBinding binding;
    private AnimeDataViewModel animeDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimeDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolbarUtil.setCustomToolbar(this, binding.getRoot(), "Anime Data");
        ToolbarUtil.addBackButtonToCustomToolbar(this);

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

        TextView textViewRateMaxEpisodes = dialogView.findViewById(R.id.textViewRateMaxEpisodes);
        EditText editTextRateEpisodes = dialogView.findViewById(R.id.editTextRateEpisodes);
        EditText startingDateEditText = dialogView.findViewById(R.id.editTextStartingDate);
        EditText finishedDateEditText = dialogView.findViewById(R.id.editTextFinishedDate);
        EditText editTextRateScore = dialogView.findViewById(R.id.editTextRateScore);
        Spinner spinnerRateStatus = dialogView.findViewById(R.id.spinnerRateStatus);

        FilterUtils.setMaxEpisodes(animeData, textViewRateMaxEpisodes);
        FilterUtils.setEditTextFilters(editTextRateEpisodes, "0", animeData.getEpisodes());
        FilterUtils.setEditTextFilters(editTextRateScore, "0", "10");
        FilterUtils.setOnClickListeners(this, startingDateEditText);
        FilterUtils.setOnClickListeners(this, finishedDateEditText);

        ArrayAdapter<CharSequence> adapterRateStatus = createAdapterFromResource(R.array.rate_status_array);
        setSpinnerAdapter(spinnerRateStatus, adapterRateStatus);

        animeDataViewModel.getRatingData().observe(this, ratingData -> {
            if (ratingData != null) {
                updateDialogWithRatingData(adapterRateStatus, ratingData,
                        spinnerRateStatus, editTextRateEpisodes, editTextRateScore,
                        startingDateEditText, finishedDateEditText);
            }
        });

        setPositiveButton(builder, animeData, spinnerRateStatus, editTextRateEpisodes,
                editTextRateScore, startingDateEditText, finishedDateEditText);

        setNegativeButton(builder);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ArrayAdapter<CharSequence> createAdapterFromResource(int arrayResource) {
        return ArrayAdapter.createFromResource(this, arrayResource, R.layout.custom_spinner_item);
    }

    private void setSpinnerAdapter(Spinner spinner, ArrayAdapter<CharSequence> adapter) {
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner.setAdapter(adapter);
    }

    private void updateDialogWithRatingData(ArrayAdapter<CharSequence> adapterRateStatus, Rating ratingData,
                                            Spinner spinnerRateStatus, EditText editTextRateEpisodes,
                                            EditText editTextRateScore, EditText startingDateEditText,
                                            EditText finishedDateEditText) {
        spinnerRateStatus.setSelection(adapterRateStatus.getPosition(ratingData.getWatchStatus()));
        editTextRateEpisodes.setText(ratingData.getEpisodesWatched());
        editTextRateScore.setText(ratingData.getScore());
        startingDateEditText.setText(DateConverter.convertDateMongoToJava(ratingData.getStartingDate()));
        finishedDateEditText.setText(DateConverter.convertDateMongoToJava(ratingData.getFinishedDate()));
    }

    private void setPositiveButton(AlertDialog.Builder builder, Anime animeData,
                                   Spinner spinnerRateStatus, EditText editTextRateEpisodes,
                                   EditText editTextRateScore, EditText startingDateEditText,
                                   EditText finishedDateEditText) {
        builder.setPositiveButton("Rate", (dialog, which) -> {
            String userId = animeDataViewModel.getActiveUserId();
            String animeId = animeData.getId();
            String rateStatus = getSelectedItemAsString(spinnerRateStatus);
            String rateEpisodes = editTextRateEpisodes.getText().toString();
            String rateScore = editTextRateScore.getText().toString();
            String rateStartingDate = startingDateEditText.getText().toString();
            String rateFinishedDate = finishedDateEditText.getText().toString();

            rateStartingDate = DateConverter.convertDateJavaToMongo(rateStartingDate);
            rateFinishedDate = DateConverter.convertDateJavaToMongo(rateFinishedDate);

            if (rateEpisodes.isEmpty()) rateEpisodes = "0";
            if (rateScore.isEmpty()) rateScore = null;

            if (userId != null && !userId.isEmpty() && animeId != null && !animeId.isEmpty()) {
                Rating rating = new Rating(userId, animeId, rateStatus, rateEpisodes, rateScore, rateStartingDate, rateFinishedDate);
                animeDataViewModel.rateAnime(rating);
            }

            Toast.makeText(getApplicationContext(), "Anime rated", Toast.LENGTH_SHORT).show();
        });
    }


    private void setNegativeButton(AlertDialog.Builder builder) {
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
    }

    private String getSelectedItemAsString(Spinner spinner) {
        return spinner.getSelectedItem().toString();
    }

}
