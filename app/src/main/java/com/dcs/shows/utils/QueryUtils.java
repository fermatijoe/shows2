package com.dcs.shows.utils;

import android.text.TextUtils;
import android.util.Log;

import com.dcs.shows.BuildConfig;
import com.dcs.shows.Show;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getName();
    public final static String API_KEY = BuildConfig.TMDB_API_KEY;
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    public static List<Show> fetchEarthquakeData(String address, String scope){

        // Create URL object
        URL url = createUrl(address);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        int s = Integer.parseInt(scope);
        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Show> shows = extractFeatureFromJson(jsonResponse, s);

        // Return the {@link Event}
        return shows;

    }


    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Show} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
    public static List<Show> extractFeatureFromJson(String earthquakeJSON, int scope) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        List<Show> shows = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(earthquakeJSON);
            JSONArray movieArray = root.getJSONArray("results");

            List<Show> results = new ArrayList<>();

            if(scope == 1 || scope == 4){
                for(int i = 0; i < movieArray.length(); i++) {
                    JSONObject movie = movieArray.getJSONObject(i);
                    Show movieModel = new Show(movie);

                    List<String> genres = new ArrayList<>();
                    JSONArray genreArray = movie.getJSONArray("genre_ids");
                    GenreList gl = new GenreList();
                    gl.initGenreListMovie();
                    for (int n = 0; n<genreArray.length(); n++){


                        int genreId = genreArray.getInt(n);
                        String genreName = gl.getMovieGenreWithId(genreId);
                        genres.add(genreName);
                    }

                    movieModel.setGenres(genres);

                    results.add(movieModel);
                }
            } else if(scope == 2){
                for(int i = 0; i < movieArray.length(); i++) {
                    JSONObject movie = movieArray.getJSONObject(i);
                    Show movieModel = new Show(movie, 1010);

                    List<String> genres = new ArrayList<>();
                    JSONArray genreArray = movie.getJSONArray("genre_ids");
                    GenreList gl = new GenreList();
                    gl.initGenreListMovie();
                    for (int n = 0; n<genreArray.length(); n++){

                        int genreId = genreArray.getInt(n);
                        String genreName = gl.getTvGenreWithId(genreId);
                        if(!genreName.equals("")){
                            genres.add(genreName);
                        }

                    }

                    movieModel.setGenres(genres);

                    Log.v(LOG_TAG, "GENRES SAVED " + Arrays.toString(genres.toArray()));
                    results.add(movieModel);
                }


            }


            return results;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }

}
