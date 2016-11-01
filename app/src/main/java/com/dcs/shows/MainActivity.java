package com.dcs.shows;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;
import java.util.Map;

import static com.dcs.shows.App.getContext;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

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


    }

    @Override
    protected void onResume() {
        super.onResume();
        //show ads if user did opt-in
        if(adsEnabledPref()){
            showAds();
        }else {
            AdView adView = (AdView) findViewById(R.id.adView);
            adView.setVisibility(View.GONE);
        }
    }

    private boolean adsEnabledPref(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showAds = preferences.getBoolean("checkbox_pref_adshow", false);
        return showAds;
    }

    private void showAds(){
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9909155562202230~4464471706");
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public static String getSystemLanguage(){
        String locale = Locale.getDefault().toString();
        locale = locale.replace("_", "-");
        return locale;
    }

    @Override
    public void onBackPressed() {
        ListFragment listFragment = (ListFragment) getSupportFragmentManager()
                .findFragmentByTag("LIST_F_TAG");
        if (listFragment != null) {
            listFragment.resetToolbar();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                launchSettingsFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
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

        resetColor();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void resetColor(){
        if(getSupportActionBar() != null){
            getSupportActionBar()
                    .setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(),
                            R.color.colorPrimary)));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void showAboutDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage(this.getResources().getString(R.string.about_dialog_text));
        alertDialog.setTitle(this.getResources().getString(R.string.about));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



    private void launchListFragment(int scope){
        switch (scope){
            case LAUNCH_MOVIES:
                setTitle(this.getResources().getString(R.string.nav_movies));
                break;
            case LAUNCH_TV:
                setTitle(this.getResources().getString(R.string.nav_tv));
                break;
            case LAUNCH_FAV:
                setTitle(this.getResources().getString(R.string.nav_fav));
                break;
        }
        getSupportFragmentManager().popBackStack ("detail", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Fragment newDetail = ListFragment.newInstance(scope, "");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newDetail, "LIST_F_TAG")
                .commit();
    }
    private void launchRandomFragment(){
        setTitle(this.getResources().getString(R.string.nav_random));
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
    }

    private void launchSettingsFragment(){
        Intent i = new Intent(this, Preferences.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
