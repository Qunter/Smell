package com.yufa.smell.Activity.ChatCenter.RongTt;

import android.app.Application;

import io.rong.imkit.RongIM;

/**
 * Created by Administrator on 2016/11/15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this);
    }
}
