package com.yufa.smell.Activity;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/12.
 */

public class AgentApplication extends Application {

    private  List<Activity> activities = new ArrayList<Activity>();

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (Activity activity : activities) {
            activity.finish();
        }
        System.exit(0);
    }
    public List<Activity> getActivities(){
        return activities;
    }
}