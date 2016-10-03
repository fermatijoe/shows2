package com.dcs.shows.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

public class CreditsAsyncTask extends AsyncTask<String, Void, List<CrewMember>> {
    private final String LOG_TAG = CreditsAsyncTask.class.getSimpleName();

    private List<CrewMember> getTrailersDataFromJson(String jsonStr) throws JSONException {
        List<CrewMember> results = new ArrayList<>();
        try {
            JSONObject rootJSON = new JSONObject(jsonStr);
            JSONArray castArray = rootJSON.getJSONArray("cast");

            int length;
            if (castArray.length() >= 10) {//there are more than 20 ppl in the movie, dont need to mention all
                length = 10;
            } else {//there are less than 10 ppl in the movie
                length = castArray.length();
            }

            for (int i = 0; i < length; i++) {
                JSONObject person = castArray.getJSONObject(i);
                CrewMember onePerson = new CrewMember(person);
                results.add(onePerson);
            }

            //then add director(s)

            JSONArray crewArray = rootJSON.getJSONArray("crew");

            if(crewArray != null && crewArray.length() != 0){
                for(int i = 0; i<crewArray.length(); i++){
                    JSONObject person = crewArray.getJSONObject(i);
                    if(person.getString("job").equals("Director")){
                        CrewMember onePerson = new CrewMember(person, "this guy is using a different constructor");
                        //irrelevant 2nd param
                        results.add(onePerson);
                    }
                }
            }


            return results;
        }catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing JSON", e);
        }
        return null;
    }

    //params 0 is id
    //params 1 is scope, can be "movie" or "tv"
    @Override
    protected List<CrewMember> doInBackground(String... params) {
        if (params[0] == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            //https://api.themoviedb.org/3/movie/271110/credits?api_key=480a9e79c0937c9f4e4a129fd0463f96
            final String BASE_URL = "http://api.themoviedb.org/3/" + params[1] + "/" + params[0];
            final String API_KEY_PARAM = "api_key";


            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath("credits")
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
