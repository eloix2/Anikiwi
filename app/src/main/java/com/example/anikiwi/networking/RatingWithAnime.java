package com.example.anikiwi.networking;

import com.google.gson.annotations.SerializedName;

public class RatingWithAnime {
    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("animeId")
    private Anime anime;

    @SerializedName("watchStatus")
    private String watchStatus;

    @SerializedName("episodesWatched")
    private int episodesWatched;

    @SerializedName("score")
    private int score;

    @SerializedName("startingDate")
    private String startingDate;

    @SerializedName("finishedDate")
    private String finishedDate;


    public static class Anime {
        @SerializedName("_id")
        private String id;

        @SerializedName("season")
        private String season;

        @SerializedName("title")
        private String title;

        @SerializedName("type")
        private String type;

        @SerializedName("year")
        private int year;

        @SerializedName("episodes")
        private int episodes;

        @SerializedName("picture")
        private String picture;

        // Add other properties of Anime
        public String getId() {
            return id;
        }

        public String getSeason() {
            return season;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public int getYear() {
            return year;
        }

        public int getEpisodes() {
            return episodes;
        }

        public String getPicture() {
            return picture;
        }
        // Add appropriate getters and setters here
    }
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Anime getAnime() {
        return anime;
    }

    public String getWatchStatus() {
        return watchStatus;
    }

    public int getEpisodesWatched() {
        return episodesWatched;
    }

    public int getScore() {
        return score;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public String getFinishedDate() {
        return finishedDate;
    }
}