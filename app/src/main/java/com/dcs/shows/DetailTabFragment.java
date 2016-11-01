package com.dcs.shows;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dcs.shows.tasks.ReviewAsyncTask;
import com.google.gson.Gson;

import java.util.List;

import static com.dcs.shows.DetailFragment.SHOW_TO_LOAD;
import static com.dcs.shows.R.id.tabLayout;

/*
    this is launched after clicking on a poster image
    this handles tabs between reviews/detail page/similar movies
    and any other tab that might be related
 */
public class DetailTabFragment extends Fragment {
    // TODO: 29/10/2016 refactor every DetailFrag launch to this new frag instead

    private Show mShow;

    public DetailTabFragment(){}

    public static DetailTabFragment newInstance(Show json){
        String serializedShow = new Gson().toJson(json);
        Bundle args = new Bundle();
        args.putString(DetailFragment.SHOW_TO_LOAD, serializedShow);
        DetailTabFragment fragment = new DetailTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        String serializedShow = getArguments().getString(DetailFragment.SHOW_TO_LOAD);
        mShow = (new Gson()).fromJson(serializedShow, Show.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_tablayout, container, false);

        if(mShow.getScope().equals("tv")){
            //no reviews for tv shows, just launch regular detialf
            Fragment newDetail = DetailFragment.newInstance(mShow);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_nested, newDetail)
                    .addToBackStack("detail")
                    .commit();
        }


        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.Details)));
        tabLayout.addTab(tabLayout.newTab().setText(getActivity().getResources().getString(R.string.Reviews)));
        final ViewPager viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return inflatedView;
    }



    private class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Fragment detailFragment = DetailFragment.newInstance(mShow);
                    return detailFragment;
                case 1:
                    Fragment reviewFragment = ReviewFragment.newInstance(mShow);
                    return reviewFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
