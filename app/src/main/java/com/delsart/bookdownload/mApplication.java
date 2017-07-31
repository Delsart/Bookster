package com.delsart.bookdownload;

import android.app.Application;
import android.content.Context;

/**
 * Created by Delsart on 2017/7/30.
 */
public class mApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}