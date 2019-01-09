package com.example.author.timetracking.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Photo;
import com.example.author.timetracking.data.entity.Record;

import java.util.Date;
import java.util.List;

public class DataRepository {

    private static DataRepository sInstance;

    private final AppDatabase appDatabase;
    private MediatorLiveData<List<Record>> observableRecords;
    private MediatorLiveData<List<Category>> observableCategories;
    private MediatorLiveData<List<Photo>> observablePhotos;

    private DataRepository(final AppDatabase database) {
        appDatabase = database;
        observableRecords = new MediatorLiveData<>();
        observableCategories = new MediatorLiveData<>();

        observableRecords.addSource(appDatabase.getRecordDAO().getAll(), records -> {
            if (appDatabase.getDatabaseCreated().getValue() != null) {
                observableRecords.postValue(records);
            }
        });
        observableCategories.addSource(appDatabase.getCategoryDAO().getAll(), categories -> {
            if (appDatabase.getDatabaseCreated().getValue() != null) {
                observableCategories.postValue(categories);
            }
        });
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<Record>> getRecords() {
        return observableRecords;
    }

    public LiveData<Record> loadRecord(final int recordId) {
        return appDatabase.getRecordDAO().findById(recordId);
    }

    public List<Category> getMostSum() {
        return appDatabase.getCategoryDAO().getMostSum();
    }

    public List<Category> getMostSum(Date start, Date end) {
        return appDatabase.getCategoryDAO().getMostSum(start, end);
    }

    public List<Category> getSum() {
        return appDatabase.getCategoryDAO().getSum();
    }

    public List<Category> getSum(Date start, Date end) {
        return appDatabase.getCategoryDAO().getSum(start, end);
    }

    public LiveData<List<Category>> getCategories() {
        return observableCategories;
    }

    public LiveData<Category> loadCategory(long catId) {
        return appDatabase.getCategoryDAO().findById(catId);
    }

    public LiveData<List<Photo>> getPhotosByRecord(long recordId) {
        return appDatabase.getPhotoDAO().findByRecordId(recordId);
    }
}
