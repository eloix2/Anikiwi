package com.eloix.anikiwi.model;

import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("_id")
    String id;
    @SerializedName("userId")
    String userId;
    @SerializedName("animeId")
    String animeId;
    @SerializedName("watchStatus")
    String watchStatus;
    @SerializedName("episodesWatched")
    String episodesWatched;
    @SerializedName("score")
    String score;
    @SerializedName("startingDate")
    String startingDate;
    @SerializedName("finishedDate")
    String finishedDate;

    public Rating(String id, String userId, String animeId, String watchStatus, String episodesWatched, String score, String startingDate, String finishedDate) {
        this.id = id;
        this.userId = userId;
        this.animeId = animeId;
        this.watchStatus = watchStatus;
        this.episodesWatched = episodesWatched;
        this.score = score;
        this.startingDate = startingDate;
        this.finishedDate = finishedDate;
    }

    public Rating(String userId, String animeId, String watchStatus, String episodesWatched, String score, String startingDate, String finishedDate) {
        this.userId = userId;
        this.animeId = animeId;
        this.watchStatus = watchStatus;
        this.episodesWatched = episodesWatched;
        this.score = score;
        this.startingDate = startingDate;
        this.finishedDate = finishedDate;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getAnimeId() { return animeId; }
    public String getWatchStatus() { return watchStatus; }
    public String getEpisodesWatched() { return episodesWatched; }
    public String getScore() { return score; }
    public String getStartingDate() { return startingDate; }
    public String getFinishedDate() { return finishedDate; }

}
