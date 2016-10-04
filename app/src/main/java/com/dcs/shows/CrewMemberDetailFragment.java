package com.dcs.shows;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dcs.shows.tasks.ActorMoviesAsyncTask;
import com.dcs.shows.tasks.ActorPicturesAsyncTask;
import com.dcs.shows.tasks.ShowDetailAsyncTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.dcs.shows.R.id.cast_recyclerView;
import static com.dcs.shows.R.id.movies_recyclerView;


public class CrewMemberDetailFragment extends Fragment {
    private static final String LOG_TAG = CrewMemberDetailFragment.class.getSimpleName();
    public static final String ARG_PERSON = "com.dcs.shows.person";

    private CrewMember mCrewMember;
    private ImageView mProfileImageView, mHeaderImageView;
    private TextView mNametextView, mAgeTextView, mBioTextView, mEmptyHeaderTextView;
    private CardView mBioCardView;
    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;

    public static CrewMemberDetailFragment newInstance(String  json) {
        Bundle args = new Bundle();
        args.putString(ARG_PERSON, json);
        CrewMemberDetailFragment fragment = new CrewMemberDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public CrewMemberDetailFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        try {
            String serializedPerson = getArguments().getString(ARG_PERSON);
            mCrewMember = (new Gson()).fromJson(serializedPerson, CrewMember.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_person, container, false);

        mProfileImageView = (ImageView) rootView.findViewById(R.id.profile_imageview);
        mHeaderImageView = (ImageView) rootView.findViewById(R.id.header_imageview);
        mNametextView = (TextView) rootView.findViewById(R.id.name_textview);
        mAgeTextView = (TextView) rootView.findViewById(R.id.age_textview);
        mBioTextView = (TextView) rootView.findViewById(R.id.biography_textview);
        mEmptyHeaderTextView = (TextView) rootView.findViewById(R.id.empty_view_header);
        mBioCardView = (CardView) rootView.findViewById(R.id.biography_cardview);


        if(mCrewMember.getImage() != null
                && !mCrewMember.getImage().equals("")
                && !mCrewMember.getImage().equals("null")) {
            Glide.with(getActivity()).load(mCrewMember.getImage()).into(mProfileImageView);
        }else {
            //load placehodler profile pic
            Glide.with(getActivity()).load(R.drawable.ic_person_placeholder).into(mProfileImageView);
        }

        mNametextView.setText(mCrewMember.getName());
        if(mCrewMember.getBirthDay() != null
                && !mCrewMember.getBirthDay().equals("")
                && !mCrewMember.getBirthDay().equals("null")
                && mCrewMember.getBirthDay().length() >= 4) {
            String bYear = mCrewMember.getBirthDay().substring(0, 4);
            mAgeTextView.setText(bYear);
        }else {
            mAgeTextView.setVisibility(View.GONE);
        }

        if(mCrewMember.getBiography() != null
                && !mCrewMember.getBiography().equals("")
                && !mCrewMember.getBiography().equals("null")
                && mCrewMember.getBiography().length() >= 4) {
            mBioTextView.setText(mCrewMember.getBiography());
        }else {
            mBioCardView.setVisibility(View.GONE);
        }

        mMovieAdapter = new MovieAdapter(new ArrayList<Show>());
        mRecyclerView = (RecyclerView) rootView.findViewById(movies_recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mMovieAdapter);

        // fetch tagged_images and show it
        new Async1().execute(mCrewMember.getPersonId());

        // fetch this actor's movies
        new Async2().execute(mCrewMember.getPersonId());



        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setHeader(String urlPath){
        String completeUrl = "http://image.tmdb.org/t/p/w500" + urlPath;
        Glide.with(getActivity()).load(completeUrl).into(mHeaderImageView);
    }

    private class Async1 extends ActorPicturesAsyncTask {
        @Override
        protected void onPostExecute(String s) {
            if(s != null
                    && !s.equals("")
                    && !s.equals("null")){
                setHeader(s);
            }else {
                //header image not available
                mEmptyHeaderTextView.setVisibility(View.VISIBLE);
                mEmptyHeaderTextView.setText(R.string.error_no_overview);
            }
        }
    }

    private class Async2 extends ActorMoviesAsyncTask {
        @Override
        protected void onPostExecute(List<Show> list) {
            if(list != null && list.size() != 0){
                //give list to adapter
                mMovieAdapter.addItemsToList(list, false);
                mMovieAdapter.notifyDataSetChanged();
            }
        }
    }

    private class Async3 extends ShowDetailAsyncTask {
        @Override
        protected void onPostExecute(Show show) {
            if(show != null){
                launchDetailFragment(show);
            }
        }
    }

    private void launchDetailFragment(Show s){
        Fragment newDetail = DetailFragment.newInstance(s);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newDetail)
                .addToBackStack("detail")
                .commit();
    }

    private class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mImageView;
        public TextView mEmptyView;

        public MovieHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImageView = (ImageView) itemView.findViewById(R.id.poster);
            mEmptyView = (TextView) itemView.findViewById(R.id.empty_poster);

        }

        @Override
        public void onClick(View view) {
            int adapterPosition = MovieHolder.this.getAdapterPosition();
            Show s = mMovieAdapter.getList().get(adapterPosition);
            String stringId = Integer.valueOf(s.getShowId()).toString();
            new Async3().execute(s.getScope(), stringId);
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {
        private List<Show> mMovies;

        public MovieAdapter(List<Show> movies) {
            mMovies = movies;
        }

        public void add(Show movie){
            mMovies.add(movie);
            notifyDataSetChanged();
        }
        public void addItemsToList(List<Show> newMovies, boolean append){
            if(append){
                mMovies.addAll(newMovies);
            }else {
                mMovies = newMovies;
            }
            notifyDataSetChanged();
        }

        public List<Show> getList(){
            return mMovies;
        }

        @Override
        public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.list_item_row_movie, parent, false);
            return new MovieHolder(rootView);
        }
        @Override
        public void onBindViewHolder(MovieHolder holder, int position) {
            Show currentMovie = mMovies.get(position);

            if(currentMovie.getImage() != null
                    && !currentMovie.getImage().equals("null")) {
                Log.v(LOG_TAG, "loading featured movie image: " + currentMovie.getImage());
                String imageUrl = "http://image.tmdb.org/t/p/w185" + currentMovie.getImage();
                Glide.with(getActivity()).load(imageUrl).into(holder.mImageView);
            }else {
                holder.mEmptyView.setText(R.string.error_no_overview);
            }
        }
        @Override
        public int getItemCount() {
            return mMovies.size();
        }
    }
}
