package com.example.anikiwi.networking;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecommendationResponse {
    @SerializedName("recommendations")
    private List<Anime> recommendations;

    @SerializedName("isRandom")
    private boolean isRandom;

    public List<Anime> getRecommendations() {
        return recommendations;
    }

    public boolean isRandom() {
        return isRandom;
    }
}
