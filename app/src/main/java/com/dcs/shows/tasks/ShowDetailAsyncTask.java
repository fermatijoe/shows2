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

public class ShowDetailAsyncTask extends AsyncTask<String, Void, Show> {
    private static final String LOG_TAG = ShowDetailAsyncTask.class.getSimpleName();

    private Show getTrailersDataFromJson(String jsonStr, String scope) throws JSONException {

        try {
            String outPut;
            JSONObject rootJSON = new JSONObject(jsonStr);
            if(scope.equals("movie")){
                Show movie = new Show(rootJSON);

                List<String> genres = new ArrayList<>();
                JSONArray genreArray = rootJSON.getJSONArray("genres");
                GenreList gl = new GenreList();
                gl.initGenreListMovie();
                for (int n = 0; n<genreArray.length(); n++){
                    JSONObject genre = genreArray.getJSONObject(n);

                    int genreId = genre.getInt("id");
                    String genreName = gl.getMovieGenreWithId(genreId);
                    genres.add(genreName);
                }

                movie.setGenres(genres);

                return movie;

            }else if (scope.equals("tv")){
                Show tv = new Show(rootJSON, 11);

                List<String> genres = new ArrayList<>();
                JSONArray genreArray = rootJSON.getJSONArray("genres");
                GenreList gl = new GenreList();
                gl.initGenreListMovie();
                for (int n = 0; n<genreArray.length(); n++){
                    JSONObject genre = genreArray.getJSONObject(n);

                    int genreId = genre.getInt("id");
                    String genreName = gl.getTvGenreWithId(genreId);
                    if(!genreName.equals("")){
                        genres.add(genreName);
                    }

                }

                tv.setGenres(genres);

                return tv;
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
    protected Show doInBackground(String... params) {
        if (params[0] == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            //http://api.themoviedb.org/3/movie/333484?api_key=API KEY HERE&page=1
            final String BASE_URL = "http://api.themoviedb.org/3";
            final String API_KEY_PARAM = "api_key";


            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(params[1])
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
