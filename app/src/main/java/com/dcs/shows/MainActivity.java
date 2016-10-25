package com.dcs.shows;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.batch.android.Batch;
import com.batch.android.BatchUnlockListener;
import com.batch.android.Feature;
import com.batch.android.Offer;
import com.batch.android.Resource;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.phenotype.Configuration;

import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BatchUnlockListener {

    public final static int LAUNCH_MOVIES = 1;
    public final static int LAUNCH_TV = 2;
    public final static int LAUNCH_FAV = 3;
    public final static int LAUNCH_COMING_SOON = 4;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_movies);

        NavigationView navigationView1 = (NavigationView) findViewById(R.id.navigation_drawer_bottom);
        navigationView1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showAboutDialog();
                item.setChecked(false);
                return true;
            }
        });


        launchListFragment(LAUNCH_MOVIES);


        getSupportFragmentManager().addOnBackStackChangedListener(new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag("LIST_F_TAG");
                    if (listFragment != null) {
                        listFragment.resetToolbar();
                    }
                }
            }
        });


        SharedPreferences prefs = this.getSharedPreferences(
                "com.dcs.shows", Context.MODE_PRIVATE);
        boolean isPromoUser = prefs.getBoolean("appGratis_key", false);
        if(!isPromoUser) {
            /*
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-9909155562202230~4464471706");
            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            */
        }




    }

    public static String getSystemLanguage(){
        return Locale.getDefault().toString();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            launchListFragment(LAUNCH_MOVIES);
        } else if (id == R.id.nav_tv) {
            launchListFragment(LAUNCH_TV);
        } else if (id == R.id.nav_favs){
            launchListFragment(LAUNCH_FAV);
        } else if (id == R.id.nav_random){
            launchRandomFragment();
        } else if (id == R.id.nav_coming){
            launchListFragment(LAUNCH_COMING_SOON);
        } else if (id == R.id.nav_search){
            launchSuggestionFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAboutDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("About this app");
        alertDialog.setMessage(this.getResources().getString(R.string.about_dialog_text));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



    private void launchListFragment(int scope){

        getSupportFragmentManager().popBackStack ("detail", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Log.v(LOG_TAG, "launching listF with scope: " + scope);
        Fragment newDetail = ListFragment.newInstance(scope, "");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newDetail, "LIST_F_TAG")
                .commit();
    }
    private void launchRandomFragment(){
        getSupportFragmentManager().popBackStack ("detail", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment newDetail = RandomFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newDetail)
                .addToBackStack("random")
                .commit();
    }
    private void launchSuggestionFragment(){
        getSupportFragmentManager().popBackStack ("detail", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment newDetail = AdvancedSearchFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newDetail)
                .addToBackStack("suggestion")
                .commit();

        /*
        Fragment newDetail = AdvancedMovieFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newDetail)
                .addToBackStack("suggestion")
                .commit();

        */
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Batch.Unlock.setUnlockListener(this);
        Batch.onStart(this);
    }

    @Override
    public void onRedeemAutomaticOffer(Offer offer)
    {
        for(Feature feature : offer.getFeatures())
        {
            String featureRef = feature.getReference();
            String value = feature.getValue();

            if(featureRef.equals("AD_FREE")){
                Log.v(LOG_TAG, "hiding ads");
                AdView adView = (AdView) findViewById(R.id.adView);
                adView.setVisibility(View.GONE);

                SharedPreferences prefs = this.getSharedPreferences(
                        "com.dcs.shows", Context.MODE_PRIVATE);
                prefs.edit().putBoolean("appGratis_key", true).apply();
            }
        }

        Map<String, String> additionalParameters = offer.getOfferAdditionalParameters();
        String rewardMessage = additionalParameters.get("reward_message");

        // Build the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(rewardMessage).setTitle("Congratulations!");

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    @Override
    protected void onStop()
    {
        Batch.onStop(this);

        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Batch.onDestroy(this);

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Batch.onNewIntent(this, intent);

        super.onNewIntent(intent);
    }
}
