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


@Database(entities = {Photo.class, Category.class, Record.class},
        version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    private final MutableLiveData<Boolean> isDatabaseCreated = new MutableLiveData<>();


    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    public abstract CategoryDAO getCategoryDAO();

    public abstract PhotoDAO getPhotoDAO();

    public abstract RecordDAO getRecordDAO();

    private static AppDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class,
                "my-database")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                long[] ids = getInstance(context).getPhotoDAO().insert(getCategoriesPhotos());
                                getInstance(context).getCategoryDAO().insert(getInitCategories(ids));
                                getInstance(context).setDatabaseCreated();
                            }

                            private Category[] getInitCategories(long[] phIds) {
                                return new Category[]{
                                        new Category("Cleaning", phIds[0]),
                                        new Category("Dinner", phIds[1]),
                                        new Category("Dream", phIds[2]),
                                        new Category("Rest", phIds[3]),
                                        new Category("Work", phIds[4]),
                                };
                            }

                            private Photo[] getCategoriesPhotos() {
                                return new Photo[]{
                                        new Photo("android.resource://com.todo.androidtodo/" + R.mipmap.clean_category),
                                        new Photo("android.resource://com.todo.androidtodo/" + R.mipmap.dinner_category),
                                        new Photo("android.resource://com.todo.androidtodo/" + R.mipmap.dream_category),
                                        new Photo("android.resource://com.todo.androidtodo/" + R.mipmap.rest_category),
                                        new Photo("android.resource://com.todo.androidtodo/" + R.mipmap.work_category)
                                };
                            }
                        });
                    }
                })
                .build();
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return isDatabaseCreated;
    }

    private void setDatabaseCreated() {
        isDatabaseCreated.postValue(true);
    }


}

