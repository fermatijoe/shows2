package com.dcs.shows;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/*
    This should be considered RandomFragment v2.
    It will include widgets to select a date range, genre, rating and so on.
 */
public class SuggestionFragment extends Fragment {
    private final static String LOG_TAG = SuggestionFragment.class.getSimpleName();

    public SuggestionFragment(){}

    public static SuggestionFragment newInstance(){
        Bundle args = new Bundle();
        SuggestionFragment fragment = new SuggestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_suggestion, container, false);
        return rootView;
    }
}
