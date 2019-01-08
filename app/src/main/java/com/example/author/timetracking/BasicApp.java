package com.example.author.timetracking;

import android.app.Application;

import com.example.author.timetracking.data.AppDatabase;
import com.example.author.timetracking.data.DataRepository;

public class BasicApp extends Application {

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }
}
