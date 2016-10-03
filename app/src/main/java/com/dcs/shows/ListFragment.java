package com.dcs.shows;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.dcs.shows.utils.EndlessRecyclerViewScrollListener;
import com.dcs.shows.utils.FavoriteUtils;
import com.dcs.shows.utils.QueryUtils;

import com.dcs.shows.utils.SpacesItemDecoration;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment{
    private static final String LOG_TAG = ListFragment.class.getSimpleName();
    private static final String ARG_SCOPE = "com.dcs.shows.activity_to_launch";
    private static final String BASE_URL = "http://api.themoviedb.org/3";


    private String mCurrentSortPreference = "popular", mLanguage;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    public int mScope;
    private RecyclerView mRecyclerView;
    private ShowAdapter mShowAdapter;
    private SearchView mSearchView;



    public static ListFragment newInstance(int target) {
        Bundle args = new Bundle();
        args.putInt(ARG_SCOPE, target);
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ListFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScope = getArguments().getInt(ARG_SCOPE);
        setHasOptionsMenu(true);
        mLanguage = MainActivity.getSystemLanguage();
        mLanguage = mLanguage.replace("_", "-");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);



        mShowAdapter = new ShowAdapter(new ArrayList<Show>());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 4);
        mRecyclerView.setLayoutManager(glm);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8, getActivity()));
        mRecyclerView.setAdapter(mShowAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(glm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                anotherOne(page, mScope, mCurrentSortPreference, false);
            }
        });

        mTextView = (TextView) rootView.findViewById(R.id.empty_view);
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_view);

        switch (mScope){
            case 1:
                getActivity().setTitle(R.string.nav_movies);
                if(checkConnectivity()){
                    anotherOne(1, mScope, mCurrentSortPreference, false);
                }else {
                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.setText("No internet connection");
                    mProgressBar.setVisibility(View.GONE);
                }
                break;
            case 2:
                getActivity().setTitle(R.string.nav_tv);
                if(checkConnectivity()){
                    anotherOne(1, mScope, mCurrentSortPreference, false);
                }else {
                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.setText("No internet connection");
                    mProgressBar.setVisibility(View.GONE);
                }
                break;
            case 3:
                getActivity().setTitle(R.string.nav_fav);
                List<Show> favList = FavoriteUtils.getAllFavorites();

                if(favList.size() == 0){
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.setText("No favorites");
                }else {
                    mShowAdapter.addItemsToList(favList, false);
                    mProgressBar.setVisibility(View.GONE);
                }
                break;
            case 4:
                //coming soon list
                getActivity().setTitle(R.string.nav_coming_soon);

                if(checkConnectivity()){
                    anotherOne(1, mScope, mCurrentSortPreference, false);
                }else {
                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.setText("No internet connection");
                    mProgressBar.setVisibility(View.GONE);
                }
                break;

        }


        return rootView;
    }

    private boolean checkConnectivity(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void anotherOne(int currPage, int scope, String sort, boolean o){
        String override = Boolean.valueOf(o).toString();
        (new FetchMoviesTask()).execute(Integer.valueOf(currPage).toString(),
                Integer.valueOf(scope).toString(), sort, override);

    }

    public void resetToolbar(){
        //reset title
        resetTitle();

        //and toolbar color
        resetColor();
    }

    private void resetTitle(){
        switch (mScope){
            case 1:
                getActivity().setTitle(R.string.nav_movies);
                break;
            case 2:
                getActivity().setTitle(R.string.nav_tv);
                break;
            case 3:
                getActivity().setTitle(R.string.nav_fav);
                break;
            default:
                getActivity().setTitle(R.string.nav_movies);
                Log.e(LOG_TAG, "Error resetting toolbar title");
                break;
        }
    }

    private void resetColor(){
        if(getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null){
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
        }else {
            Log.e(LOG_TAG, "Error resetting toolbar color");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Show>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private boolean override = false;

        @Override
        protected List<Show> doInBackground(String... params) {
            //params 0 = page to load. should be always >1 and !=0
            //params 1 = mScope. should be 1(movies) or 2(tv shows) or 4(coming soon list)
            //params 2 = order to load. should be "popular" or "top_rated".
            if(params[3].equals("true")){
                override = true;
            }
            Uri baseUri = Uri.parse(BASE_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();;

            if(params[1].equals("4")){
                uriBuilder.appendPath("movie");
                uriBuilder.appendPath("upcoming");
                uriBuilder.appendQueryParameter("api_key", QueryUtils.API_KEY); // api key
                uriBuilder.appendQueryParameter("page", params[0]); //load a new page?
                uriBuilder.appendQueryParameter("language", mLanguage); //user language
            }else{
                uriBuilder.appendPath(getLiteralScope(params[1])); //movie or tv?
                uriBuilder.appendPath(params[2]); //sort order
                uriBuilder.appendQueryParameter("api_key", QueryUtils.API_KEY); // api key
                uriBuilder.appendQueryParameter("page", params[0]); //load a new page?
                uriBuilder.appendQueryParameter("language", mLanguage); //user language
            }



            Log.v(LOG_TAG, "onCreateLoader@URL built: " + uriBuilder.toString());

            return QueryUtils.fetchEarthquakeData(uriBuilder.toString(), params[1]);
        }

        @Override
        protected void onPostExecute(List<Show> shows) {
            if (shows != null && !shows.isEmpty()) {
                mShowAdapter.addItemsToList(shows, !override);
                mShowAdapter.notifyDataSetChanged();
            }
            mProgressBar.setVisibility(View.GONE);
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



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.main, menu);

        if(mScope == 3){
            //disable ordering
            MenuItem itemToHide1 = menu.findItem(R.id.action_sort_top);
            MenuItem itemToHide2 = menu.findItem(R.id.action_sort_popular);
            MenuItem itemToHide3 = menu.findItem(R.id.action_search);
            itemToHide1.setVisible(false);
            itemToHide2.setVisible(false);
            itemToHide3.setVisible(false);
        }

        if(mScope != 3){
            //enable search
            final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
            mSearchView = (SearchView) myActionMenuItem.getActionView();
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if(s != null || !s.isEmpty()) {
                        String listSerializedToJson = new Gson().toJson(mShowAdapter.getList());

                        String scopeToSend = "";
                        if(mScope == 1){
                            scopeToSend = "movie";
                        }else if(mScope == 2){
                            scopeToSend = "tv";
                        } else if (mScope == 4){ //we're in coming soon tab
                            scopeToSend = "movie";
                        }
                        Fragment newDetail = SearchFragment.newInstance(s, listSerializedToJson, scopeToSend);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.container_nested, newDetail)
                                .addToBackStack(null)
                                .commit();

                    }
                    if(!mSearchView.isIconified()) {
                        mSearchView.setIconified(true);
                    }
                    myActionMenuItem.collapseActionView();
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_popular:
                mCurrentSortPreference = "popular";
                anotherOne(1, mScope, mCurrentSortPreference, true);
                return true;
            case R.id.action_sort_top:
                mCurrentSortPreference = "top_rated";
                anotherOne(1, mScope, mCurrentSortPreference, true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
            int adapterPosition = ShowHolder.this.getAdapterPosition();
            Show s = mShowAdapter.getList().get(adapterPosition);

            Fragment newDetail = DetailFragment.newInstance(s);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_nested, newDetail)
                    .addToBackStack("detail")
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
