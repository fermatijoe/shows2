package com.dcs.shows.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.speech.tts.Voice;
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
import java.util.Random;

public class ActorPicturesAsyncTask extends AsyncTask<String, Void, String>{
    private final String LOG_TAG = ActorPicturesAsyncTask.class.getSimpleName();

    private int getRandomResult(int arrayLength){
        Random r = new Random();
        int Low = 1;
        int High = arrayLength;
        return r.nextInt(High-Low) + Low;
    }

    private String getTrailersDataFromJson(String jsonStr) throws JSONException {

        try {
            String outPut;
            JSONObject rootJSON = new JSONObject(jsonStr);
            //parse here poster image
            JSONArray resultsArray = rootJSON.getJSONArray("results");
            try {
                JSONObject result = resultsArray.getJSONObject(getRandomResult(resultsArray.length()));
                outPut = result.getString("file_path");
                return outPut;
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }


            return null;
        }catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing JSON", e);
        }
        return null;
    }

    //params 0 is id
    @Override
    protected String doInBackground(String... params) {
        if (params[0] == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            //http://api.themoviedb.org/3/person/6885/tagged_images?api_key=480a9e79c0937c9f4e4a129fd0463f96
            final String BASE_URL = "http://api.themoviedb.org/3/person/" + params[0] + "/tagged_images";
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
