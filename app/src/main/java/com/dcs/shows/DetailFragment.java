package com.dcs.shows;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.view.menu.ActionMenuItemView;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dcs.shows.utils.FavoriteUtils;
import com.google.common.primitives.Shorts;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public final static String SHOW_TO_LOAD = "com.dcs.shows.show_to_load";
    public final static String SHOW_SCOPE = "com.dcs.shows.show_to_load";

    private Show mShow;
    private ImageView mImageView, mImageViewPoster, mCollpasingImageView;
    private TextView mTitleView, mVoteAverageView, mOverviewView, mDateView, mEmptyViewBackdrop, mEmptyViewPoster;
    private Button mTrailerButton;
    private FloatingActionButton mFloatingActionButton;
    private boolean fabChecked = false;
    private String mScope, mLanguage;


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        getActivity().setTitle(mShow.getTitle());

        mImageViewPoster = (ImageView) rootView.findViewById(R.id.detail_image_poster);
        mImageView = (ImageView) rootView.findViewById(R.id.detail_image);
        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mOverviewView = (TextView) rootView.findViewById(R.id.detail_overview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date);
        mVoteAverageView = (TextView) rootView.findViewById(R.id.detail_vote_average);
        mTrailerButton = (Button) rootView.findViewById(R.id.button_trailer);
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mEmptyViewBackdrop = (TextView) rootView.findViewById(R.id.empty_view_backdrop);
        mEmptyViewPoster = (TextView) rootView.findViewById(R.id.empty_view_poster);

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
            mEmptyViewBackdrop.setText("No image available");
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
            mEmptyViewPoster.setText("No image available");
        }else {
            //Loading poster
            String image_url_poster = "http://image.tmdb.org/t/p/w185" +  mShow.getImage();
            Glide
                    .with(this)
                    .load(image_url_poster)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(100,193) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            mImageViewPoster.setImageBitmap(resource);
                            // Asynchronous palette
                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette p) {
                                    if(p != null && p.getDarkVibrantSwatch() != null){
                                        setToolbarColor(p.getDarkVibrantSwatch().getRgb());

                                        ((AppCompatActivity) getActivity())
                                                .getSupportActionBar()
                                                .setBackgroundDrawable(new ColorDrawable(p.getDarkVibrantSwatch().getRgb()));
                                    }



                                }
                            });
                        }
                    });
        }


        mTitleView.setText(mShow.getTitle());
        mOverviewView.setText(mShow.getOverview());

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
        mTrailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentMovieId = Integer.valueOf(mShow.getShowId()).toString();
                startActivity(TrailerActivity.newIntent(getActivity(), currentMovieId, mScope));
            }
        });


        return rootView;
    }

    private void setToolbarColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_launch:
                String id = Integer.valueOf(mShow.getShowId()).toString();
                startActivity(IMDBActivity.newIntent(getActivity(), id, mScope));
                return true;
            default:
                break;
        }
        return false;
    }


}
