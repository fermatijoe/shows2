package com.dcs.shows;

import android.app.Application;
import android.content.Context;

import com.batch.android.Batch;
import com.batch.android.Config;
import com.orm.SugarContext;

//used to reference context
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SugarContext.init(this);
        Batch.Push.setGCMSenderId("727360497987");

        // TODO : switch to live Batch Api Key before shipping
        //Batch.setConfig(new Config("DEV57FD31DA060C60CDF1E094AF483")); // devloppement
        Batch.setConfig(new Config("57FD31DA05D2188B7AE5B65B291127")); // live
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
