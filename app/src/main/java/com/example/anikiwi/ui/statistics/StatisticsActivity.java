package com.example.anikiwi.ui.statistics;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.anikiwi.databinding.ActivityStatisticsBinding;
import com.example.anikiwi.networking.StatisticsResponse;
import com.example.anikiwi.utilities.BarChartCustomizer;
import com.example.anikiwi.utilities.ToolbarUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private ActivityStatisticsBinding binding;
    private StatisticsViewModel statisticsViewModel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolbarUtil.setCustomToolbar(this, binding.getRoot(), "Statistics");
        ToolbarUtil.addBackButtonToCustomToolbar(this);

        userId = getIntent().getStringExtra("user_id");

        statisticsViewModel = new StatisticsViewModel();

        // Observe changes in the statistics data
        statisticsViewModel.getStatistics().observe(this, new Observer<StatisticsResponse>() {
            @Override
            public void onChanged(StatisticsResponse statistics) {
                // Update UI with statistics data
                updateUI(statistics);
            }
        });

        // Observe loading state
        statisticsViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                // Show/hide loading indicator
            }
        });

        // Observe error messages
        statisticsViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                // Handle and display error
            }
        });

        // Fetch user statistics
        statisticsViewModel.getUserStatistics(userId);

    }

    private List<BarEntry> getDummyBarEntries() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 2f));
        entries.add(new BarEntry(1f, 6f));
        entries.add(new BarEntry(2f, 4f));
        entries.add(new BarEntry(3f, 1f));
        entries.add(new BarEntry(4f, 13f));
        entries.add(new BarEntry(5f, 14f));
        entries.add(new BarEntry(6f, 4f));
        entries.add(new BarEntry(7f, 3f));
        entries.add(new BarEntry(8f, 55f));
        entries.add(new BarEntry(9f, 23f));
        entries.add(new BarEntry(10f, 5f));
        return entries;
    }

    private void updateUI(StatisticsResponse statistics) {
        // Update Animes Completed
        binding.textViewAnimesCompleted.setText("Animes Completed: " + statistics.getTotalAnimesCompleted());

        // Update Episodes Watched
        binding.textViewEpisodesWatched.setText("Episodes Watched: " + statistics.getTotalEpisodesSeen());

        // Update Mean Score
        binding.textViewMeanScore.setText("Mean Score: " + statistics.getMeanScoreCompleted());

        // Update the BarChart for Animes Ratings / Scores
        BarChart barChartAnimeRatingsScores = binding.barChartAnimeRatingsScores;
        BarChartCustomizer customizer = new BarChartCustomizer(this);
        List<BarEntry> animeRatingsEntries = BarChartCustomizer.convertAnimeScoreCountToBarEntries(statistics.getAnimesCompletedPerScore());
        customizer.customizeBarChart(barChartAnimeRatingsScores, animeRatingsEntries);

        // Update the BarChart for Episodes Seen / Score
        BarChart barChartEpisodesSeenScore = binding.barChartEpisodesSeenScore;
        List<BarEntry> episodesSeenEntries = BarChartCustomizer.convertAnimeScoreCountToBarEntries(statistics.getEpisodesSeenPerScore());
        customizer.customizeBarChart(barChartEpisodesSeenScore, episodesSeenEntries);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
