package com.dcs.shows;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dcs.shows.utils.QueryUtils;
import com.dcs.shows.utils.SpacesItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private static final String LOG_TAG = "SearchActivity";
    private static final String EXTRA_QUERY = "com.dcs.shows.extra_query";
    private static final String EXTRA_SCOPE = "com.dcs.shows.extra_scope";
    private static final String EXTRA_DB = "com.dcs.shows.extra_db";
    private List<Show> db;
    private String query, scope, mLanguage;
    private RecyclerView mRecyclerView;
    private ShowAdapter mShowAdapter;
    private ProgressBar pb;

    public static SearchFragment newInstance(String query, String db, String s) {
        Bundle args = new Bundle();
        args.putString(EXTRA_QUERY, query);
        args.putString(EXTRA_DB, db);
        args.putString(EXTRA_SCOPE, s);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment(){

    }

    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Type listType = new TypeToken<ArrayList<Show>>(){}.getType();
            db = new Gson().fromJson(getArguments().getString(EXTRA_DB) , listType);
            query = getArguments().getString(EXTRA_QUERY).toLowerCase().trim();
            scope = getArguments().getString(EXTRA_SCOPE);
            mLanguage = MainActivity.getSystemLanguage();
            mLanguage = mLanguage.replace("_", "-");
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        getActivity().setTitle(query);
        pb = (ProgressBar)rootView.findViewById(R.id.progress_view);

        mShowAdapter = new ShowAdapter(new ArrayList<Show>());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 4);
        mRecyclerView.setLayoutManager(glm);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8, getActivity()));
        mRecyclerView.setAdapter(mShowAdapter);

        Log.v(LOG_TAG, "scope: " + scope);

        (new SearchMoviesTask()).execute(scope, query);
        pb.setVisibility(View.GONE);

        return rootView;
    }

    public class SearchMoviesTask extends AsyncTask<String, Void, List<Show>> {

        private final String LOG_TAG = SearchMoviesTask.class.getSimpleName();



        //Param 0 must be "movie" or "tv"
        //param 1 must be query
        @Override
        protected List<Show> doInBackground(String... params) {

            Uri baseUri = Uri.parse("http://api.themoviedb.org/3/search/");
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendPath(params[0]); //or "tv"
            uriBuilder.appendQueryParameter("query", params[1]);
            uriBuilder.appendQueryParameter("include_adult", "true");
            uriBuilder.appendQueryParameter("api_key", QueryUtils.API_KEY);
            uriBuilder.appendQueryParameter("language", mLanguage);


            String s = "";
            if(params[0].equals("movie")){
                s = "1";
            }else if(params[0].equals("tv")){
                s = "2";
            }

            //Bug report says s with value: "" was passed. From coming soon tab
            List<Show> shows = QueryUtils.fetchEarthquakeData(uriBuilder.toString(), s);
            return shows;
        }

        @Override
        protected void onPostExecute(List<Show> shows) {
            if (shows != null && !shows.isEmpty()) {
                Log.v(LOG_TAG, "Adding items to list");
                mShowAdapter.addItemsToList(shows, true);
                mShowAdapter.notifyDataSetChanged();
            }
            pb.setVisibility(View.GONE);
        }

        private String getLiteralScope(String scopeInt){
            switch (scopeInt){
                case "1":
                    //movies
                    return "movie";
                case "2":
                    //tv
                    return "tv";
                default:
                    return null;
            }
        }


    }

    private List<Show> getMatches(){
        //this method return a list to show on screen with pertinent movies
        List<Show> results = new ArrayList<>();
        for(Show full : db){
            String title = full.getTitle().toLowerCase().trim();
            String q = query.toLowerCase().trim();
            if(title.contains(q) || title.matches(q)){
                results.add(full);
                Log.v(LOG_TAG, "Result found and added");
            }
        }
        return results;
    }

    private class ShowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mImageView;
        public TextView mTextView;

        public ShowHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImageView = (ImageView) itemView.findViewById(R.id.grid_item_image);
            mTextView = (TextView) itemView.findViewById(R.id.grid_item_title);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = mRecyclerView.indexOfChild(view);

            Fragment newDetail = DetailFragment.newInstance(mShowAdapter.getList().get(itemPosition));
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_nested, newDetail)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private class ShowAdapter extends RecyclerView.Adapter<ShowHolder> {
        private List<Show> mShows;

        public ShowAdapter(List<Show> shows) {
            mShows = shows;
        }
        public void add(Show show){
            mShows.add(show);
            notifyDataSetChanged();
        }
        public void addItemsToList(List<Show> newShows, boolean append){
            if(append){
                mShows.addAll(newShows);
            }else {
                mShows = newShows;
            }
            notifyDataSetChanged();
        }

        public void removeItemsFromList(int index){
            mShows.remove(index);
            notifyItemRemoved(index);
        }


        public List<Show> getList(){
            return mShows;
        }

        @Override
        public ShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.list_item_row, parent, false);
            return new ShowHolder(rootView);
        }
        @Override
        public void onBindViewHolder(ShowHolder holder, int position) {
            Show currentShow = mShows.get(position);

            holder.mTextView.setText(currentShow.getTitle());
            String imageUrl = "http://image.tmdb.org/t/p/w185" + currentShow.getImage();
            Glide.with(getActivity()).load(imageUrl).into(holder.mImageView);

        }
        @Override
        public int getItemCount() {
            return mShows.size();
        }
    }


}
