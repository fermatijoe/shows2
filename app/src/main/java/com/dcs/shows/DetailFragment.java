package com.dcs.shows;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dcs.shows.tasks.ActorAsyncTask;
import com.dcs.shows.tasks.CreditsAsyncTask;
import com.dcs.shows.tasks.IMDBAsyncTask;
import com.dcs.shows.tasks.ShowDetailAsyncTask;
import com.dcs.shows.tasks.SimilarShowAsyncTask;
import com.dcs.shows.utils.FavoriteUtils;
import com.dcs.shows.tasks.TrailerAsyncTask;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dcs.shows.R.id.cast_recyclerView;

public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public final static String SHOW_TO_LOAD = "com.dcs.shows.show_to_load";
    public final static String SHOW_SCOPE = "com.dcs.shows.show_to_load";

    private Show mShow;
    private ImageView mImageView, mImageViewPoster, mCollpasingImageView, mLoadMoreImageView;
    private TextView mTitleView, mVoteAverageView, mOverviewView, mDateView, mEmptyViewBackdrop,
            mEmptyViewPoster, mGenresTextView;
    private FloatingActionButton mTrailerFAB;
    private FloatingActionButton mFloatingActionButton;
    private boolean fabChecked = false;
    private String mScope, mLanguage;
    private static String mId;
    private CardView mLoadMorecardView, mMoreCardView1, mMoreCardView2, mMoreCardView3;

    private RecyclerView mRecyclerView, mSimilarRecyclerView;
    private DetailFragment.PersonAdapter mPersonAdapter;
    private SimilarAdapter mSimilarAdapter;


    public static DetailFragment newInstance(Show json) {
        String serializedShow = new Gson().toJson(json);
        Bundle args = new Bundle();
        args.putString(SHOW_TO_LOAD, serializedShow);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        String serializedShow = getArguments().getString(SHOW_TO_LOAD);
        mShow = (new Gson()).fromJson(serializedShow, Show.class);
        mScope = mShow.getScope();
        mLanguage = MainActivity.getSystemLanguage();
        mLanguage = mLanguage.replace("_", "-");
        getActivity().setTitle(mShow.getTitle());
        mId = Integer.valueOf(mShow.getShowId()).toString();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);



        mLoadMoreImageView = (ImageView) rootView.findViewById(R.id.load_more_imageView);
        mImageViewPoster = (ImageView) rootView.findViewById(R.id.detail_image_poster);
        mImageView = (ImageView) rootView.findViewById(R.id.detail_image);
        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mOverviewView = (TextView) rootView.findViewById(R.id.detail_overview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date);
        mVoteAverageView = (TextView) rootView.findViewById(R.id.detail_vote_average);
        mTrailerFAB = (FloatingActionButton) rootView.findViewById(R.id.trailer_fab);
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mEmptyViewBackdrop = (TextView) rootView.findViewById(R.id.empty_view_backdrop);
        mEmptyViewPoster = (TextView) rootView.findViewById(R.id.empty_view_poster);
        mLoadMorecardView = (CardView) rootView.findViewById(R.id.load_more_cardview);
        mMoreCardView1 = (CardView) rootView.findViewById(R.id.more_cardView_1);
        mMoreCardView2 = (CardView) rootView.findViewById(R.id.more_cardView_2);
        mMoreCardView3 = (CardView) rootView.findViewById(R.id.more_cardView_3);
        mGenresTextView = (TextView) rootView.findViewById(R.id.genres_text_view);

        if(FavoriteUtils.checkIfThisIsFavorite(mShow)) {
            fabChecked = true;
            mFloatingActionButton.setImageResource(R.drawable.ic_hearth_full);
        }else if(!FavoriteUtils.checkIfThisIsFavorite(mShow)) {
            fabChecked = false;
            mFloatingActionButton.setImageResource(R.drawable.ic_hearth_empty);
        }

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FavoriteUtils.checkIfThisIsFavorite(mShow)){
                    //movie is in fav, remove it
                    mFloatingActionButton.setImageResource(R.drawable.ic_hearth_empty);
                    FavoriteUtils.removeThisFromFavorites(mShow);
                    fabChecked = false;
                } else if(!FavoriteUtils.checkIfThisIsFavorite(mShow)){
                    //movie not in fav, add it
                    mFloatingActionButton.setImageResource(R.drawable.ic_hearth_full);
                    FavoriteUtils.addThisToFavorites(mShow);
                    fabChecked = true;
                }
            }
        });

        if(mShow.getImage2() == null
                || mShow.getImage2().contains("null")
                || mShow.getImage2().equals("null")){
            //movie has no backdrop.
            mImageView.setVisibility(View.GONE);
            mEmptyViewBackdrop.setVisibility(View.VISIBLE);
            mEmptyViewBackdrop.setText(R.string.error_no_overview);
        }else {
            //Loading backdrop
            String image_url = "http://image.tmdb.org/t/p/w300" + mShow.getImage2();
            Glide.with(this).load(image_url).into(mImageView);
        }

        if(mShow.getImage() == null
                || mShow.getImage().contains("null")
                || mShow.getImage().equals("null")){
            //movie has no backdrop.
            mImageViewPoster.setVisibility(View.GONE);
            mEmptyViewPoster.setVisibility(View.VISIBLE);
            mEmptyViewPoster.setText(R.string.error_no_overview);
        }else {
            //Loading poster
            String image_url_poster = "http://image.tmdb.org/t/p/w185" +  mShow.getImage();
            Glide
                    .with(this)
                    .load(image_url_poster)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(124,186) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            mImageViewPoster.setImageBitmap(resource);
                            // Asynchronous palette
                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette p) {
                                    if(p != null && p.getDarkVibrantSwatch() != null){
                                        int darkVibrantSwatch = p.getDarkVibrantSwatch().getRgb();

                                        setTrailerFABColor(darkVibrantSwatch);

                                        setToolbarColor(darkVibrantSwatch);
                                        ((AppCompatActivity) getActivity())
                                                .getSupportActionBar()
                                                .setBackgroundDrawable(new ColorDrawable(darkVibrantSwatch));
                                    }
                                }
                            });
                        }
                    });
        }


        mTitleView.setText(mShow.getTitle());

        if(mShow.getOverview() == null
                || mShow.getOverview().equals("")
                || mShow.getOverview().equals("null")){
            mOverviewView.setText(R.string.error_no_overview);
        }else {
            mOverviewView.setText(mShow.getOverview());
        }


        String movie_date = mShow.getDate();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String date = DateUtils.formatDateTime(getActivity(),
                    formatter.parse(movie_date).getTime(), DateUtils.FORMAT_SHOW_YEAR);
            mDateView.setText(date.substring(date.length() - 4));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mVoteAverageView.setText(Integer.toString(mShow.getRating()) + "/10");
        mTrailerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTrailer(mLanguage);
            }
        });


        /*
        Move info stuff below

         */

        mLoadMoreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMorecardView.setVisibility(View.GONE);
                if(mShow.getGenres() != null){

                    mMoreCardView1.setVisibility(View.VISIBLE);
                    String g = Arrays.toString(mShow.getGenres().toArray());
                    g = g.replace("[", "");
                    g = g.replace("]", "");
                    mGenresTextView.setText(g);

                    //pass movie id to asynctask
                    String showId = Integer.valueOf(mShow.getShowId()).toString();
                    String lang;
                    lang = MainActivity.getSystemLanguage();
                    lang = lang.replace("_", "-");
                    new Async1().execute(showId, mScope);
                    new Async5().execute(mScope, showId, lang);
                }else{
                    Toast.makeText(getActivity(), R.string.error_no_overview, Toast.LENGTH_SHORT).show();
                }



            }
        });



        mPersonAdapter = new PersonAdapter(new ArrayList<CrewMember>());
        mRecyclerView = (RecyclerView) rootView.findViewById(cast_recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mPersonAdapter);

        mSimilarAdapter = new SimilarAdapter(new ArrayList<Show>());
        mSimilarRecyclerView = (RecyclerView) rootView.findViewById(R.id.similar_recyclerView);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mSimilarRecyclerView.setLayoutManager(llm2);
        mSimilarRecyclerView.setAdapter(mSimilarAdapter);


        return rootView;
    }

    private void getTrailer(String lang){
        String currentMovieId = Integer.valueOf(mShow.getShowId()).toString();
        String scope = mShow.getScope();
        if(scope == null || currentMovieId == null){
            Log.e("TrailerActivity", "null arguments, s : " + scope + ", id: " + currentMovieId);
            Toast.makeText(getActivity(), "No trailers available", Toast.LENGTH_SHORT).show();
        }else {
            new Async3().execute(currentMovieId, scope, lang);
        }
    }

    private void setTrailerFABColor(int paletteColor){
        mTrailerFAB.setBackgroundTintList(ColorStateList.valueOf(paletteColor));
        if(isColorBright(paletteColor)){
            mTrailerFAB.setImageResource(R.drawable.ic_play_arrow_black);
        }else {
            mTrailerFAB.setImageResource(R.drawable.ic_play_arrow_white);
        }

    }

    public static boolean isColorBright(int color) {
        if (android.R.color.transparent == color)
            return true;

        boolean rtnValue = false;

        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };

        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);

        // color is light
        if (brightness >= 200) {
            rtnValue = true;
        }

        return rtnValue;
    }

    private void setToolbarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getActivity() != null){
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        //disable ordering and search
        MenuItem itemToHide1 = menu.findItem(R.id.action_sort_top);
        MenuItem itemToHide2 = menu.findItem(R.id.action_sort_popular);
        MenuItem itemToHide3 = menu.findItem(R.id.action_search);
        itemToHide1.setVisible(false);
        itemToHide2.setVisible(false);
        itemToHide3.setVisible(false);

        if(mScope.equals("movie")){
            inflater.inflate(R.menu.fragment_detail, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }



    private void launchBrowserForMovie(String id){
        Log.v(LOG_TAG, "id recieved by intent: " + id);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/title/" + id)));
    }

    private void launchBrowserForActor(String id){
        String trailerUrl = "http://www.imdb.com/name/" + id;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(trailerUrl));
        startActivity(intent);
    }

    private void launchYoutubeForTrailer(String urlEnd){
        String trailerUrl = "http://www.youtube.com/watch?v=" + urlEnd;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(trailerUrl));
        startActivity(intent);
    }

    private void fetchIMDBId(String mId){
        Log.v(LOG_TAG, "starting IMDBAsyncTask with id " + mId);
        new Async4().execute(mId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_launch:
                fetchIMDBId(this.mId);
                return true;
            default:
                break;
        }
        return false;
    }

    //shows the list of participating actors
    private class Async1 extends CreditsAsyncTask {
        @Override
        protected void onPostExecute(List<CrewMember> crewMembers) {
            super.onPostExecute(crewMembers);
            //show the data and remove the super
            if (crewMembers != null && !crewMembers.isEmpty()) {
                mPersonAdapter.addItemsToList(crewMembers, false);
                mPersonAdapter.notifyDataSetChanged();
                mMoreCardView2.setVisibility(View.VISIBLE);
            }
        }
    }

    //Fired when the user clicks on an actor's profile image
    //Should launch the ActorDetailFragment
    private class Async2 extends ActorAsyncTask {
        @Override
        protected void onPostExecute(CrewMember s) {
            if(s != null){
                //launch ActorDetailFragment where there will be another async task to fetch
                //participated movies
                String serializedPerson = new Gson().toJson(s);
                Fragment newDetail = CrewMemberDetailFragment.newInstance(serializedPerson);
                if(getActivity() != null && isAdded()){
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, newDetail)
                            .addToBackStack("crewDetail")
                            .commit();
                }
            }

        }
    }

    //opens the trailer (via intent)
    private class Async3 extends TrailerAsyncTask {
        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                Toast.makeText(getActivity(), "No trailers found", Toast.LENGTH_SHORT).show();
            }else {
                if (s.equals("no_locale_trailer")){
                    //retry with english trailer
                    getTrailer("en-US");
                }else {
                    launchYoutubeForTrailer(s);
                }

            }
        }
    }

    //launches IMDB page for current movie
    private class Async4 extends IMDBAsyncTask {
        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                Toast.makeText(getActivity(), "No IMDB page available", Toast.LENGTH_SHORT).show();
            }else{
                launchBrowserForMovie(s);
            }
        }
    }

    private class Async5 extends SimilarShowAsyncTask {
        @Override
        protected void onPostExecute(List<Show> shows) {
            if(shows != null && !shows.isEmpty()){
                mSimilarAdapter.addItemsToList(shows, false);
                mSimilarAdapter.notifyDataSetChanged();
                mMoreCardView3.setVisibility(View.VISIBLE);
            }else {
                Log.w(LOG_TAG, "No similar shows were found");
            }
        }
    }

    private class Async6 extends ShowDetailAsyncTask {
        @Override
        protected void onPostExecute(Show show) {
            if(getActivity() != null){
                Fragment newDetail = DetailFragment.newInstance(show);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_nested, newDetail)
                        .addToBackStack("detail")
                        .commit();
            }
        }
    }

    private class SimilarHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mPosterImageView;
        public TextView mTitleTextView;

        public SimilarHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.grid_item_image);
            mTitleTextView = (TextView) itemView.findViewById(R.id.grid_item_title);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = SimilarHolder.this.getAdapterPosition();
            String id = Integer.valueOf(mSimilarAdapter.getList().get(adapterPosition).getShowId())
                    .toString();
            new Async6().execute(mScope, id, mLanguage);
        }
    }

    private class SimilarAdapter extends RecyclerView.Adapter<SimilarHolder> {
        private List<Show> mShows;

        public SimilarAdapter(List<Show> shows) {
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
        public SimilarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.list_item_row_similar, parent, false);
            return new SimilarHolder(rootView);
        }
        @Override
        public void onBindViewHolder(SimilarHolder holder, int position) {
            Show currentShow = mShows.get(position);

            holder.mTitleTextView.setText(currentShow.getTitle());

            String imageUrl = "http://image.tmdb.org/t/p/w130" + currentShow.getImage();
            Glide.with(getActivity()).load(imageUrl).into(holder.mPosterImageView);

        }
        @Override
        public int getItemCount() {
            return mShows.size();
        }
    }

    private class PersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mImageView;
        public TextView mNameView, mRoleView;

        public PersonHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImageView = (ImageView) itemView.findViewById(R.id.profile_pic);
            mNameView = (TextView) itemView.findViewById(R.id.actor_name);
            mRoleView = (TextView) itemView.findViewById(R.id.actor_role);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = DetailFragment.PersonHolder.this.getAdapterPosition();
            //Open ActorAsyncTask and from its onPostExecute launch the IMDB page
            String lang;
            lang = MainActivity.getSystemLanguage();
            lang = lang.replace("_", "-");
            new Async2().execute(mPersonAdapter.getList().get(adapterPosition).getPersonId(), lang);
        }
    }

    private class PersonAdapter extends RecyclerView.Adapter<DetailFragment.PersonHolder> {
        private List<CrewMember> mActors;

        public PersonAdapter(List<CrewMember> actors) {
            mActors = actors;
        }

        public void add(CrewMember actor){
            mActors.add(actor);
            notifyDataSetChanged();
        }
        public void addItemsToList(List<CrewMember> newActors, boolean append){
            if(append){
                mActors.addAll(newActors);
            }else {
                mActors = newActors;
            }
            notifyDataSetChanged();
        }

        public List<CrewMember> getList(){
            return mActors;
        }

        @Override
        public DetailFragment.PersonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.list_item_row_crew, parent, false);
            return new DetailFragment.PersonHolder(rootView);
        }
        @Override
        public void onBindViewHolder(DetailFragment.PersonHolder holder, int position) {
            CrewMember currentPerson = mActors.get(position);

            if(currentPerson.getImage() != null && !currentPerson.getImage().equals("null")) {
                String imageUrl = "http://image.tmdb.org/t/p/w130" + currentPerson.getImage();
                Glide.with(getActivity()).load(imageUrl).into(holder.mImageView);
            }else {
                holder.mImageView.setImageResource(R.drawable.ic_person_placeholder);
            }



            holder.mNameView.setText(currentPerson.getName());

            if(currentPerson.getRole().equals("crew")){
                holder.mRoleView.setText(currentPerson.getJob());
            }else {
                holder.mRoleView.setText(currentPerson.getCharacter());
            }

        }
        @Override
        public int getItemCount() {
            return mActors.size();
        }
    }


}
