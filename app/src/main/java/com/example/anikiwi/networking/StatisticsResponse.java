package com.example.anikiwi.networking;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StatisticsResponse {
    @SerializedName("totalAnimesCompleted")
    private int totalAnimesCompleted;

    @SerializedName("totalEpisodesSeen")
    private int totalEpisodesSeen;

    @SerializedName("meanScoreCompleted")
    private double meanScoreCompleted;

    @SerializedName("animesCompletedPerScore")
    private List<AnimeScoreCount> animesCompletedPerScore;

    @SerializedName("episodesSeenPerScore")
    private List<AnimeScoreCount> episodesSeenPerScore;

    // Add getters for each field
    public int getTotalAnimesCompleted() {
        return totalAnimesCompleted;
    }

    public int getTotalEpisodesSeen() {
        return totalEpisodesSeen;
    }

    public double getMeanScoreCompleted() {
        return meanScoreCompleted;
    }

    public List<AnimeScoreCount> getAnimesCompletedPerScore() {
        return animesCompletedPerScore;
    }

    public List<AnimeScoreCount> getEpisodesSeenPerScore() {
        return episodesSeenPerScore;
    }
}
