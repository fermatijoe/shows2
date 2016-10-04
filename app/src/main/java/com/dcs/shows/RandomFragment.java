package com.dcs.shows;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dcs.shows.utils.GenreList;
import com.dcs.shows.utils.QueryUtils;
import com.dcs.shows.utils.RandomUtil;

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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomFragment extends Fragment {
    private static final int LATEST_MOVIE_ID_AVAILABLE = 416565;
    private static final int LATEST_TV_ID_AVAILABLE = 67897;
    private static final int MINIMUM_ID_AVAILABLE = 2;
    private static final String LOG_TAG = RandomFragment.class.getSimpleName();

    private ProgressBar mProgressBar;
    private String mLanguage;

    public RandomFragment(){    }

    public static RandomFragment newInstance() {
        Bundle args = new Bundle();
        RandomFragment fragment = new RandomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLanguage = MainActivity.getSystemLanguage();
        mLanguage = mLanguage.replace("_", "-");
        runTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_random, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_view);
        return rootView;
    }

    private void runTask(){
        List<String> urlAndScope = RandomUtil.getRandomUrl(mLanguage);

        (new FetchMovieTask()).execute(urlAndScope.get(0), urlAndScope.get(1));
    }

    private int getRandomId(int min, int max){
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private int getRandomScope(int min, int max){
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private void launchDetail(Show s){
        mProgressBar.setVisibility(View.GONE);
        Fragment newDetail = DetailFragment.newInstance(s);
        if(getActivity() != null && isAdded()){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_nested, newDetail) // add to back stack?
                    .commit();
        }

    }

    /*
    PARAMS[0] is the url
    PARAMS[1] is the scope
     */
    private class FetchMovieTask extends AsyncTask<String, Void, Show> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private int getRandomResult(){
            Random r = new Random();
            int Low = 1;
            int High = 20;
            return r.nextInt(High-Low) + Low;
        }

        private Show getTrailersDataFromJson(String jsonStr, String scope) throws JSONException {
            try{
                JSONObject root = new JSONObject(jsonStr);
                JSONArray movieArray = root.getJSONArray("results");


                if(scope.equals("movie")){
                    for(int i = 0; i < 20; i++){
                        Log.v(LOG_TAG, "trying object at index");
                        JSONObject movie = movieArray.getJSONObject(getRandomResult());
                        Show movieModel = new Show(movie);
                        if(movieModel.getOverview().length() == 0
                                || movieModel.getOverview().equals("null")
                                || movieModel.getOverview().length() == 4){
                            Log.v(LOG_TAG, "object had no overview");
                            //get another object
                            continue;
                        }else{
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
                            return movieModel;
                        }
                    }


                } else if(scope.equals("tv")){

                    for(int i = 0; i < 20; i++){
                        JSONObject movie = movieArray.getJSONObject(getRandomResult());
                        Show movieModel = new Show(movie, 101010);
                        if(movieModel.getOverview().length() == 0
                                || movieModel.getOverview().equals("null")
                                || movieModel.getOverview().length() == 4){
                            //get another object
                            continue;
                        }else{
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
                            return movieModel;

                        }
                    }
                }

                return null;

            }catch (JSONException e){
                e.printStackTrace();
                Log.e(LOG_TAG, "Couldn't create Show object from parsed json");
            }

            return null;
        }

        @Override
        protected Show doInBackground(String... params) {

            if (params[0] == null || params[1] == null) {
                Log.e(LOG_TAG, "A parameter is null");
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                URL url = new URL(params[0]);
                Log.v(LOG_TAG, "" + url);

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
                runTask();
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
                return getTrailersDataFromJson(jsonStr, params[1]);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Show show) {
            if(show != null){
                if(show.getOverview().length() == 0
                        || show.getOverview().equals("null")
                        || show.getOverview().length() == 4){
                    runTask();
                } else{
                    if(isAdded() && getActivity() != null){
                        launchDetail(show);
                    }

                }

            }else if(show == null){
                Log.e(LOG_TAG, "Show was null");
                runTask();
            }
            //mProgressBar.setVisibility(View.GONE);
        }
    }


}



