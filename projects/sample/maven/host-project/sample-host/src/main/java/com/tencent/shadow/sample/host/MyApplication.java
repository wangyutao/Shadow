package com.jpyy001.tools.sample.host;

import android.app.Application;

import com.jpyy001.tools.sample.introduce_shadow_lib.InitApplication;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        InitApplication.onApplicationCreate(this);
    }
}
