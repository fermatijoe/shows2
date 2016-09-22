package com.dcs.shows.utils;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


    /* WHAT THIS CLASS DOES:
    Randomize between tv and movie
    Pick a random first_air_date_year or primary_release_year (depending on the previous result)
    Pick a random page and build the url
    */

public class RandomUtil {
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/";
    public static final String LOG_TAG = RandomUtil.class.getName();

    private RandomUtil(){}

    public static List<String> getRandomUrl(String language){
        String s = getRandomScope();

        //https://api.themoviedb.org/3/discover/tv?api_key=480a9e79c0937c9f4e4a129fd0463f96&page=1000&vote_average.gte=0.0

        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendPath(s); //movie or tv
        uriBuilder.appendQueryParameter("api_key", QueryUtils.API_KEY);
        uriBuilder.appendQueryParameter("language", language); //format: en-US, ru-RU
        uriBuilder.appendQueryParameter("vote_average.gte", "0.0");
        uriBuilder.appendQueryParameter("page", getRandomPage());

        /*String y = getRandomYear();
        if(s.equals("movie")){
            //add year query param for movies
            uriBuilder.appendQueryParameter("primary_release_year", y);
        }else if (s.equals("tv")){
            //add year query param for tv
            uriBuilder.appendQueryParameter("first_air_date_year", y);
        }
*/

        List<String> returns = new ArrayList<>();
        returns.add(0, uriBuilder.toString());
        returns.add(1, s);

        return returns;
    }

    private static String getRandomScope(){
        Random r = new Random();
        int Low = 1;
        int High = 3; //  generates between 1 and 2
        int res = r.nextInt(High-Low) + Low;
        switch (res){
            case 1:
                return "movie";
            case 2:
                return "tv";
            default:
                Log.e(LOG_TAG, "Error generating random scope");
                return "movie";
        }
    }

    private String getRandomYear(){
        Random r = new Random();
        int Low = 1971;
        int High = 2017; //  generates a year
        int res = r.nextInt(High-Low) + Low;
        return Integer.valueOf(res).toString();
    }

    private static String getRandomPage(){
        Random r = new Random();
        int Low = 1;
        int High = 1001; //  generates a year
        int res = r.nextInt(High-Low) + Low;
        return Integer.valueOf(res).toString();
    }
}
