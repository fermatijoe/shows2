package com.dcs.shows;

import android.app.Application;
import android.content.Context;

import com.orm.SugarContext;

//used to reference context
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SugarContext.init(this);
    }

    public static Context getContext(){
        return mContext;

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

}
