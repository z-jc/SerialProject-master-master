package com.zjc.serialport.app;

import android.app.Application;
import android.content.Context;

/**
 * author cowards
 * created on 2019\3\6 0006
 **/
public class SerialApp extends Application {
    public static Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = getContext();
    }

    public Context getContext() {
        return this.getApplicationContext();
    }
}