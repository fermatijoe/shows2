package com.dcs.shows;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import java.util.UUID;

public class TrailerActivity extends AppCompatActivity {

    public final static String EXTRA_MOVIE_ID = "com.dcs.shows.extra_movie_id";
    public final static String EXTRA_SCOPE = "com.dcs.shows.scope";

    public static Intent newIntent(Context packageContext, String id, String scopeLiteral) {
        Intent intent = new Intent(packageContext, TrailerActivity.class);
        intent.putExtra(EXTRA_MOVIE_ID, id);
        intent.putExtra(EXTRA_SCOPE, scopeLiteral);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getIntent().getStringExtra(EXTRA_MOVIE_ID);
        String s = getIntent().getStringExtra(EXTRA_SCOPE);
        if(s == null || id == null){
            Log.e("TrailerActivity", "null arguments, s : " + s + ", id: " + id);
            Toast.makeText(getApplicationContext(), "No trailers available", Toast.LENGTH_SHORT).show();
        }
        (new FetchTrailersTask()).execute(id, s);

    }

    private void launchBrowser(String urlEnd){
        String trailerUrl = "http://www.youtube.com/watch?v=" + urlEnd;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(trailerUrl));
        startActivity(intent);
    }

    private class FetchTrailersTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private String getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            JSONObject trailer = trailerArray.getJSONObject(0);
            if (trailer.getString("site").contentEquals("YouTube")) {
                //return trailer
                String url = trailer.getString("key");
                return url;
            }else{
                return null;
            }
        }

        @Override
        protected String doInBackground(String... params) {

            if (params[0] == null || params[1] == null) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/" + params[1] + "/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";


                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, QueryUtils.API_KEY)
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

        @Override
        protected void onPostExecute(String url) {
            if (url == null) {
                Toast.makeText(getApplicationContext(), "No trailers available", Toast.LENGTH_SHORT).show();
            }else {
                launchBrowser(url);
            }

        }
    }

}
