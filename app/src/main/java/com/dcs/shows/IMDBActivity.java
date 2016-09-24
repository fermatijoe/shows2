package com.dcs.shows;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class IMDBActivity extends AppCompatActivity {

    private static final String LOG_TAG = IMDBActivity.class.getSimpleName();
    public final static String EXTRA_MOVIE_ID = "com.dcs.shows.extra_movie_id";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getIntent().getStringExtra(EXTRA_MOVIE_ID);
        (new FetchIMDBTask()).execute(id);


    }

    private class FetchIMDBTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchIMDBTask.class.getSimpleName();

        private String getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject imdbJson = new JSONObject(jsonStr);

            return imdbJson.getString("imdb_id");
        }

        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                //http://api.themoviedb.org/3/tv/62560?api_key=480a9e79c0937c9f4e4a129fd0463f96&page=1
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0];
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
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
        protected void onPostExecute(String id) {
            if (id == null) {
                Toast.makeText(getApplicationContext(), "No IMDB page available", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Intent output = new Intent();
                output.putExtra(EXTRA_MOVIE_ID, id);
                setResult(RESULT_OK, output);
                finish();

            }

        }
    }

}
