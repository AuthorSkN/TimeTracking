package com.example.author.timetracking.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.author.timetracking.R;
import com.example.author.timetracking.data.dao.*;
import com.example.author.timetracking.data.entity.*;

import java.util.concurrent.Executors;


@Database(entities = {Photo.class, Category.class, Record.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "time-racking-db";

    private static AppDatabase instance;
    private final MutableLiveData<Boolean> isCreated = new MutableLiveData<>();


    public synchronized static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = buildDatabase(context);
        }
        return instance;
    }

    public abstract CategoryDAO getCategoryDAO();

    public abstract PhotoDAO getPhotoDAO();

    public abstract RecordDAO getRecordDAO();

    private static AppDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class,
                DATABASE_NAME)
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);

                        Executors.newSingleThreadExecutor().execute(() -> {
                            Photo[] startPhotos = getStartPhotos();
                            long[] photoIds = getInstance(context).getPhotoDAO().insert(startPhotos);

                            Category[] startCategories = getStartCategories(photoIds);
                            getInstance(context).getCategoryDAO().insert(startCategories);

                            getInstance(context).setDatabaseCreated();
                        });
                    }
                })
                .build();
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return isCreated;
    }

    private void setDatabaseCreated() {
        isCreated.postValue(true);
    }

    private static Category[] getStartCategories(long[] photoIds) {
        return new Category[]{
                new Category("Cleaning", photoIds[0]),
                new Category("Dinner", photoIds[1]),
                new Category("Dream", photoIds[2]),
                new Category("Rest", photoIds[3]),
                new Category("Work", photoIds[4]),
        };
    }

    private static Photo[] getStartPhotos() {
        return new Photo[]{
                new Photo("android.resource://com.example.author.timetracking//" + R.mipmap.clean_category),
                new Photo("android.resource://com.example.author.timetracking/" + R.mipmap.dinner_category),
                new Photo("android.resource://com.example.author.timetracking/" + R.mipmap.dream_category),
                new Photo("android.resource://com.example.author.timetracking/" + R.mipmap.rest_category),
                new Photo("android.resource://com.example.author.timetracking/" + R.mipmap.work_category)
        };
    }


}

