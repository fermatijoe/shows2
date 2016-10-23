package com.dcs.shows;

import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.dcs.shows.utils.GenreList;
import com.dcs.shows.utils.MultiSelectionSpinner;
import com.dcs.shows.utils.QueryUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static java.lang.Integer.parseInt;

/*
    This should be considered RandomFragment v2.
    It will include widgets to select a date range, genre, rating and so on.
 */
public class AdvancedMovieFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final static String LOG_TAG = AdvancedMovieFragment.class.getSimpleName();
    private Spinner mScopeSpinner, mStartYearSpinner, mEndYearSpinner;
    private MultiSelectionSpinner mGenreSpinner;
    private String mScope, mGenre, mStartYear, mEndYear;
    private ArrayList<String> mYears;


    public AdvancedMovieFragment(){}

    public static AdvancedMovieFragment newInstance(){
        Bundle args = new Bundle();
        AdvancedMovieFragment fragment = new AdvancedMovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_advanced_movie, container, false);

        mScopeSpinner = (Spinner) rootView.findViewById(R.id.scope_spinner);
        mGenreSpinner = (MultiSelectionSpinner) rootView.findViewById(R.id.genre_spinner);
        mStartYearSpinner = (Spinner) rootView.findViewById(R.id.startYear_spinner);
        mEndYearSpinner = (Spinner) rootView.findViewById(R.id.endYear_spinner);

        GenreList gL = new GenreList();
        gL.initGenreListMovie();
        List<String> genresToLoad = gL.getGenresList();

        if(genresToLoad != null){
            Log.v(LOG_TAG, genresToLoad.toString());
            mGenreSpinner.setItems(genresToLoad);
        }



        mScopeSpinner.setOnItemSelectedListener(this);
        mStartYearSpinner.setOnItemSelectedListener(this);
        mEndYearSpinner.setOnItemSelectedListener(this);


        mYears = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1970; i <= thisYear; i++) {
            mYears.add(Integer.toString(i));
        }
        Collections.reverse(mYears);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, mYears);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStartYearSpinner.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGenre = mGenreSpinner.getSelectedItemsAsString();
                mGenre = mGenre.trim();
                Log.v(LOG_TAG, mScope + mGenre + mStartYear + mEndYear);
                launchListFragment(mScope, mGenre, mStartYear, mEndYear);
            }
        });


        return rootView;
    }

    private void launchListFragment(String scope, String genres, String startYear, String endYear){

        if(scope.equals(getActivity().getResources().getString(R.string.nav_movies))){
            scope = "movie";
        } else if(scope.equals(getActivity().getResources().getString(R.string.nav_tv))){
            scope = "tv";
        }

        Uri baseUri = Uri.parse("https://api.themoviedb.org/3");
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendPath("discover");
        uriBuilder.appendPath(scope);
        uriBuilder.appendQueryParameter("api_key", QueryUtils.API_KEY); // api key
        uriBuilder.appendQueryParameter("page", "1"); //load a new page?
        uriBuilder.appendQueryParameter("language", MainActivity.getSystemLanguage());//user language
        uriBuilder.appendQueryParameter("sort_by", "popularity.desc");

        if(scope.equals("movie")){
            uriBuilder.appendQueryParameter("primary_release_date.gte", startYear);
            uriBuilder.appendQueryParameter("primary_release_date.lte", endYear);
        }else if(scope.equals("tv")){
            uriBuilder.appendQueryParameter("first_air_date.gte", startYear);
            uriBuilder.appendQueryParameter("first_air_date.lte", endYear);
        }

        if(genres != null && genres.length() != 0){
            String[] genresArray = genres.split(",");
            GenreList gL = new GenreList();
            gL.initGenreListMovie();
            String genresQuery = gL.getIdGenreWithMovie(genresArray);
            uriBuilder.appendQueryParameter("with_genres", genresQuery);
        }
        Log.v(LOG_TAG, "Built url: " + uriBuilder.toString().toLowerCase());



        getActivity().getSupportFragmentManager().popBackStack ("detail", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        int s = 0;
        if(mScope.equals(getActivity().getResources().getString(R.string.nav_movies))){
            s = 1;
        } else if(mScope.equals(getActivity().getResources().getString(R.string.nav_tv))){
            s = 2;
        }

        Fragment newDetail = ListFragment.newInstance(s, uriBuilder.toString().toLowerCase());
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newDetail, "LIST_F_TAG")
                .commit();
    }

    private void populateEndYearSpinner(String startYear){
        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = Integer.parseInt(startYear); i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        Collections.reverse(years);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEndYearSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        parent.getItemAtPosition(pos);

        switch (parent.getId())
        {
            case R.id.scope_spinner:
                mScope = mScopeSpinner.getSelectedItem().toString();
                break;

            case R.id.genre_spinner:
                mGenre = mGenreSpinner.getSelectedItem().toString();
                break;

            case R.id.startYear_spinner:
                mStartYear = mStartYearSpinner.getSelectedItem().toString();
                populateEndYearSpinner(mStartYear);
                break;

            case R.id.endYear_spinner:
                mEndYear = mEndYearSpinner.getSelectedItem().toString();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
