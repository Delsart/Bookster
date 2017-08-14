package com.delsart.bookdownload;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.delsart.bookdownload.handler.MyCrashHandler;

/**
 * Created by Delsart on 2017/7/30.
 */
public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
//        LeakCanary.install(this);
        context = getApplicationContext();
//        MyCrashHandler myCrashHandler = MyCrashHandler.getInstance();
//        myCrashHandler.init(getApplicationContext());
    }
}