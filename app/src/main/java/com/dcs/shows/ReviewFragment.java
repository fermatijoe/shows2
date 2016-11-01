package com.dcs.shows;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dcs.shows.tasks.ReviewAsyncTask;
import com.dcs.shows.utils.SpacesItemDecoration;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.dcs.shows.DetailFragment.SHOW_TO_LOAD;

public class ReviewFragment extends Fragment {
    private Show mShow;
    private String mScope, mLanguage, mId;
    private RecyclerView mRecyclerView;
    private ReviewAdapter mReviewAdapter;
    private TextView mEmptyView;

    public static ReviewFragment newInstance(Show json) {
        String serializedShow = new Gson().toJson(json);
        Bundle args = new Bundle();
        args.putString(SHOW_TO_LOAD, serializedShow);
        ReviewFragment fragment = new ReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        String serializedShow = getArguments().getString(SHOW_TO_LOAD);
        mShow = (new Gson()).fromJson(serializedShow, Show.class);
        mScope = mShow.getScope();
        mLanguage = MainActivity.getSystemLanguage();
        mId = Integer.valueOf(mShow.getShowId()).toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        ((ProgressBar) rootView.findViewById(R.id.progress_view)).setVisibility(View.GONE);

        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);

        mReviewAdapter = new ReviewAdapter(new ArrayList<Show>());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mReviewAdapter);

        new Async1().execute(mScope, mId, mLanguage);

        return rootView;
    }

    private class Async1 extends ReviewAsyncTask {
        @Override
        protected void onPostExecute(List<Show> reviews) {
            if (reviews != null && !reviews.isEmpty()) {
                mReviewAdapter.addItemsToList(reviews, false);
                mReviewAdapter.notifyDataSetChanged();
            } else {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(getActivity().getResources().getString(R.string.error_no_overview));
            }
        }
    }

    private class ReviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mNameTextView, mReviewTextView;

        public ReviewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mNameTextView = (TextView) itemView.findViewById(R.id.review_name);
            mReviewTextView = (TextView) itemView.findViewById(R.id.review_text);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = ReviewHolder.this.getAdapterPosition();
            Show s = mReviewAdapter.getList().get(adapterPosition);
        }
    }

    //reuseing the show model for less code
    //getTitle returns the reviewer's name
    //getOverview returns the review text
    private class ReviewAdapter extends RecyclerView.Adapter<ReviewHolder> {
        private List<Show> mReviews;

        public ReviewAdapter(List<Show> shows) {
            mReviews = shows;
        }

        public void add(Show review){
            mReviews.add(review);
            notifyDataSetChanged();
        }
        public void addItemsToList(List<Show> newReviews, boolean append){
            if(append){
                mReviews.addAll(newReviews);
            }else {
                mReviews = newReviews;
            }
            notifyDataSetChanged();
        }

        public List<Show> getList(){
            return mReviews;
        }

        @Override
        public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.list_item_review, parent, false);
            return new ReviewHolder(rootView);
        }
        @Override
        public void onBindViewHolder(ReviewHolder holder, int position) {
            Show currentShow = mReviews.get(position);
            holder.mNameTextView.setText(currentShow.getTitle());
            holder.mReviewTextView.setText(currentShow.getOverview());

        }
        @Override
        public int getItemCount() {
            return mReviews.size();
        }
    }

}
