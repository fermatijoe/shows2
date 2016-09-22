package com.dcs.shows;




import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;



public class Show extends SugarRecord {
    int showId;
    String title; // original_title
    String image; // poster_path
    String image2; // backdrop_path used in DetailActivity
    String overview;
    int rating; // vote_average
    String date; // release_date
    String scope;

    public Show() {

    }

    //it looks like that the Show object gets auto filled with these values from the JSONObject

    public Show(JSONObject movie) throws JSONException {
        this.showId = movie.getInt("id");
        this.title = movie.getString("original_title");
        this.image = movie.getString("poster_path");
        this.image2 = movie.getString("backdrop_path");
        this.overview = movie.getString("overview");
        this.rating = movie.getInt("vote_average");
        this.date = movie.getString("release_date");
        this.scope = "movie";
    }

    public Show(JSONObject movie, int notNeeded) throws JSONException {
        this.showId = movie.getInt("id");
        this.title = movie.getString("original_name");
        this.image = movie.getString("poster_path");
        this.image2 = movie.getString("backdrop_path");
        this.overview = movie.getString("overview");
        this.rating = movie.getInt("vote_average");
        this.date = movie.getString("first_air_date");
        this.scope = "tv";
    }

    public int getShowId() {
        return showId;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getImage2() {
        return image2;
    }

    public String getOverview() {
        return overview;
    }

    public int getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    public String getScope() {
        return scope;
    }
}
