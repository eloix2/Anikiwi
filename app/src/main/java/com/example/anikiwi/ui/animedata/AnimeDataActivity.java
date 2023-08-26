package com.example.anikiwi.ui.animedata;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import jp.wasabeef.glide.transformations.BlurTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.anikiwi.R;
import com.example.anikiwi.databinding.ActivityAnimeDataBinding;
import com.example.anikiwi.databinding.ActivityMainBinding;


public class AnimeDataActivity extends AppCompatActivity {
    private ActivityAnimeDataBinding binding;
    private AnimeDataViewModel animeDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimeDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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


            }
        });
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
