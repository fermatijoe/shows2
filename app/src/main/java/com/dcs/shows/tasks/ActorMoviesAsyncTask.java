package com.dcs.shows.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dcs.shows.Show;
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

public class ActorMoviesAsyncTask extends AsyncTask<String, Void, List<Show>> {
    private static final String LOG_TAG = ActorMoviesAsyncTask.class.getSimpleName();

    private List<Show> getTrailersDataFromJson(String jsonStr) throws JSONException {

        try {
            List<Show> outPut = new ArrayList<>();
            JSONObject rootJSON = new JSONObject(jsonStr);

            JSONArray participatingArray = rootJSON.getJSONArray("cast");

            for(int i = 0; i<participatingArray.length(); i++){
                JSONObject movie = participatingArray.getJSONObject(i);
                if(movie.getString("media_type").equals("movie")
                        && movie.getString("poster_path") != null
                        && !movie.getString("poster_path").equals("null")){
                    Show newMovie = new Show(movie, "");
                    outPut.add(newMovie);
                } else if (movie.getString("media_type").equals("tv")
                        && movie.getString("poster_path") != null
                        && !movie.getString("poster_path").equals("null")){
                    Show newMovie = new Show(movie, "", 666);
                    outPut.add(newMovie);
                }
            }

            JSONArray directingArray = rootJSON.getJSONArray("crew");
            for(int n = 0; n<directingArray.length(); n++){
                JSONObject movie = directingArray.getJSONObject(n);
                if(movie.getString("media_type").equals("movie")
                        && movie.getString("poster_path") != null
                        && !movie.getString("poster_path").equals("null")){
                    Show newMovie = new Show(movie, "");
                    outPut.add(newMovie);
                } else if (movie.getString("media_type").equals("tv")
                        && movie.getString("poster_path") != null
                        && !movie.getString("poster_path").equals("null")){
                    Show newMovie = new Show(movie, "", 666);
                    outPut.add(newMovie);
                }
            }

            return outPut;
        }catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing JSON", e);
        }
        return null;
    }

    //params 0 is id
    @Override
    protected List<Show> doInBackground(String... params) {
        if (params[0] == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            //http://api.themoviedb.org/3/person/6885/combined_credits?api_key=480a9e79c0937c9f4e4a129fd0463f96
            final String BASE_URL = "http://api.themoviedb.org/3/person/" + params[0] + "/combined_credits";
            final String API_KEY_PARAM = "api_key";


            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, QueryUtils.API_KEY)
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
            return getTrailersDataFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }


}
