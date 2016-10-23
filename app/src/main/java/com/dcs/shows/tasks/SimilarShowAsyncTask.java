package com.dcs.shows.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dcs.shows.Show;
import com.dcs.shows.utils.GenreList;
import com.dcs.shows.utils.QueryUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SimilarShowAsyncTask extends AsyncTask<String, Void, List<Show>> {
    private static final String LOG_TAG = SimilarShowAsyncTask.class.getSimpleName();

    private List<Show> getTrailersDataFromJson(String jsonStr, String scope) throws JSONException {

        try {
            List<Show> outPut = new ArrayList<>();
            JSONObject rootJSON = new JSONObject(jsonStr);
            JSONArray resultsArray = rootJSON.getJSONArray("results");

            if(resultsArray.length() > 0) {
                if (scope.equals("movie")) {
                    for(int i = 0; i<resultsArray.length(); i++){
                        JSONObject movie = resultsArray.getJSONObject(i);
                        Show s = new Show(movie);
                        outPut.add(s);
                    }
                    return outPut;
                } else if (scope.equals("tv")) {
                    for(int i = 0; i<resultsArray.length(); i++){
                        JSONObject tv = resultsArray.getJSONObject(i);
                        Show s = new Show(tv, 69);
                        outPut.add(s);
                    }
                    return outPut;
                }
            }


            return null;
        }catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing JSON", e);
        }
        return null;
    }

    //params 0 is scope
    //params 1 is id
    //params 2 is language
    @Override
    protected List<Show> doInBackground(String... params) {
        if (params[0] == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            //https://api.themoviedb.org/3/movie/{movie_id}/similar?api_key=<<api_key>>&language=en-US
            final String BASE_URL = "http://api.themoviedb.org/3";
            final String API_KEY_PARAM = "api_key";


            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0]) //movie or tv
                    .appendPath(params[1]) //id
                    .appendPath("similar")
                    .appendQueryParameter(API_KEY_PARAM, QueryUtils.API_KEY)
                    .appendQueryParameter("language", params[2])
                    .build();
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "built url: " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getTrailersDataFromJson(jsonStr, params[0]);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
