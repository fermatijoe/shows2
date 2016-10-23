package com.dcs.shows;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dcs.shows.tasks.ActorAsyncTask;
import com.dcs.shows.tasks.ActorSearchAsyncTask;
import com.dcs.shows.utils.SpacesItemDecoration;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

//should launch crewMemberDetailFragment
public class AdvancedPersonFragment extends Fragment {
    private final static String LOG_TAG = AdvancedPersonFragment.class.getSimpleName();
    private EditText mQueryEditText;
    private FloatingActionButton mSearchFAB;
    private CrewMemberAdapter mCrewMemberAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmptyView;

    public AdvancedPersonFragment(){}

    public static AdvancedPersonFragment newInstance(){
        Bundle args = new Bundle();
        AdvancedPersonFragment fragment = new AdvancedPersonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_advanced_person, container, false);
        mQueryEditText = (EditText) rootView.findViewById(R.id.query_name_edittext);
        mSearchFAB = (FloatingActionButton) rootView.findViewById(R.id.search_fab);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_view);
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);

        mProgressBar.setVisibility(GONE);


        mSearchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = mQueryEditText.getText().toString();
                if(query != null && query.length() > 0){
                    mProgressBar.setVisibility(View.VISIBLE);
                    String lang;
                    lang = MainActivity.getSystemLanguage();
                    lang = lang.replace("_", "-");
                    if (checkConnectivity()) {
                        new Async1().execute(query, lang);
                    } else {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mEmptyView.setText("No internet connection");
                        mProgressBar.setVisibility(View.GONE);
                    }


                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity()
                            .getCurrentFocus()
                            .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });


        mCrewMemberAdapter = new CrewMemberAdapter(new ArrayList<CrewMember>());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mCrewMemberAdapter);

        return rootView;
    }

    private boolean checkConnectivity(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class Async1 extends ActorSearchAsyncTask{
        @Override
        protected void onPostExecute(List<CrewMember> l) {
            if(l != null && l.size() > 0){
                mCrewMemberAdapter.addItemsToList(new ArrayList<CrewMember>(), false);
                Log.v(LOG_TAG, "Found results: " + l.toString());
                mCrewMemberAdapter.addItemsToList(l, true);
                mCrewMemberAdapter.notifyDataSetChanged();
            }
            mProgressBar.setVisibility(GONE);
        }
    }

    private class Async2 extends ActorAsyncTask{
        @Override
        protected void onPostExecute(CrewMember crewMember) {
            if(crewMember != null){
                //launch ActorDetailFragment where there will be another async task to fetch
                //participated movies
                String serializedPerson = new Gson().toJson(crewMember);
                Fragment newDetail = CrewMemberDetailFragment.newInstance(serializedPerson);
                if(getActivity() != null && isAdded()){
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, newDetail)
                            .commit();
                }
            }
        }
    }

    private class CrewMemberHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mProfileImageView;
        public TextView mNameTextView, mRoleTextView;

        public CrewMemberHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mProfileImageView = (ImageView) itemView.findViewById(R.id.profile_pic);
            mNameTextView = (TextView) itemView.findViewById(R.id.actor_name);
            mRoleTextView = (TextView) itemView.findViewById(R.id.known_for);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = CrewMemberHolder.this.getAdapterPosition();
            CrewMember s = mCrewMemberAdapter.getList().get(adapterPosition);
            //fetch full show with AsyncTask
            String lang;
            lang = MainActivity.getSystemLanguage();
            lang = lang.replace("_", "-");
            new Async2().execute(s.getPersonId(), lang);


        }
    }

    private class CrewMemberAdapter extends RecyclerView.Adapter<CrewMemberHolder> {
        private List<CrewMember> mCrewMembers;

        public CrewMemberAdapter(List<CrewMember> crewMembers) {
            mCrewMembers = crewMembers;
        }

        public void add(CrewMember member){
            mCrewMembers.add(member);
            notifyDataSetChanged();
        }
        public void addItemsToList(List<CrewMember> newMembers, boolean append){
            if(append){
                mCrewMembers.addAll(newMembers);
            }else {
                mCrewMembers = newMembers;
            }
            notifyDataSetChanged();
        }

        public List<CrewMember> getList(){
            return mCrewMembers;
        }

        @Override
        public CrewMemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.list_item_row_actorsearch, parent, false);
            return new CrewMemberHolder(rootView);
        }
        @Override
        public void onBindViewHolder(CrewMemberHolder holder, int position) {
            CrewMember currentActor = mCrewMembers.get(position);


            holder.mNameTextView.setText(currentActor.getName());

            holder.mRoleTextView.setText(currentActor.getKnownFor());
            Log.v(LOG_TAG, "known movies: " + currentActor.getKnownFor());

            Glide.with(getActivity()).load(currentActor.getImage()).into(holder.mProfileImageView);


        }
        @Override
        public int getItemCount() {
            return mCrewMembers.size();
        }
    }



}

