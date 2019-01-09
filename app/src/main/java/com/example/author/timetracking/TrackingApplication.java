package com.example.author.timetracking;

import android.app.Application;

import com.example.author.timetracking.data.AppDatabase;
import com.example.author.timetracking.data.DataObservable;

public class TrackingApplication extends Application {

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public DataObservable getRepository() {
        return DataObservable.getInstance(getDatabase());
    }
}
