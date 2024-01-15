package com.eloix.anikiwi.model;

import com.google.gson.annotations.SerializedName;

public class AnimeScoreCount {
    @SerializedName("score")
    private int score;

    @SerializedName("count")
    private int count;

    public int getCount() {
        return count;
    }
}

