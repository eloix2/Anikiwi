package com.example.anikiwi.networking;

import com.google.gson.annotations.SerializedName;

public class Anime {
    @SerializedName("_id")
    String id;
    @SerializedName("title")
    String title;
    //todo: add serialized names for every field https://www.youtube.com/watch?v=n-dX4_p0630
    @SerializedName("picture")
    String image_url;
    @SerializedName("thumbnail")
    String thumbnail_url;
    //todo: add synopsis
    //String synopsis;
    @SerializedName("type")
    String type;
    @SerializedName("episodes")
    String episodes;
    @SerializedName("season")
    String season;
    @SerializedName("year")
    String year;
    @SerializedName("status")
    String status;
    @SerializedName("tags")
    String[] tags;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public String getType() {
        return type;
    }

    public String getEpisodes() {
        return episodes;
    }

    public String getSeason() {
        return season;
    }

    public String getYear() {
        return year;
    }

    public String getStatus() {
        return status;
    }

    public String[] getTags() {
        return tags;
    }

    public String getSeasonAndYear() {
        return season + " " + year;
    }
}
