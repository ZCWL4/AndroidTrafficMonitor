package com.example.yp.androidtrafficmonitor.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by yp on 2016/10/10.
 */
public class AppApplication extends Application{
    private static Application application = new AppApplication();
    private Context context;
    public static boolean isSwitchFloat = false;

    public static Context getContext() {
        return application.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
}
