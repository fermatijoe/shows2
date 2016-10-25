package com.dcs.shows.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.dcs.shows.CrewMember;
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

/*
    returns a list of search results
    then full info about that actor are fetched with ActorAsyncTask
 */
public class ActorSearchAsyncTask extends AsyncTask<String, Void, List<CrewMember>> {
    private final static String LOG_TAG = ActorSearchAsyncTask.class.getSimpleName();


    private List<CrewMember> getTrailersDataFromJson(String jsonStr) throws JSONException {

        try {
            List<CrewMember> results = new ArrayList<>();
            JSONObject rootJSON = new JSONObject(jsonStr);
            JSONArray resultsArray = rootJSON.getJSONArray("results");

            if(resultsArray.length() > 0){
                for (int i = 0; i<resultsArray.length(); i++){
                    JSONObject actor = resultsArray.getJSONObject(i);
                    if(actor.getString("profile_path") != null
                            && !actor.getString("profile_path").equals("null")){
                        CrewMember result = new CrewMember(actor, true);

                        JSONArray knownArray = actor.getJSONArray("known_for");
                        for (int n = 0; n<knownArray.length(); n++){
                            JSONObject knownMovie = knownArray.getJSONObject(n);
                            if(knownMovie.getString("media_type").equals("movie")){
                                String title = knownMovie.getString("title");
                                result.setKnownFor(title + ", ");

                            }else if(knownMovie.getString("media_type").equals("tv")){
                                String title = knownMovie.getString("name");
                                result.setKnownFor(title + ", ");
                            }

                        }


                        results.add(result);
                    }

                }
                return results;
            }else {
                return null;
            }

        }catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing JSON", e);
        }
        return null;
    }

    //params 0 is query
    //params 1 is lang
    @Override
    protected List<CrewMember> doInBackground(String... params) {
        if (params[0] == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/search/person";
            final String API_KEY_PARAM = "api_key";

            //https://api.themoviedb.org/3/search/person
            // ?api_key=xxxxxxxxxxx
            // &language=en-US
            // &query=robert%20downey

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, QueryUtils.API_KEY)
                    .appendQueryParameter("language", params[1])
                    .appendQueryParameter("query", params[0])
                    .build();
            URL url = new URL(builtUri.toString());

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
