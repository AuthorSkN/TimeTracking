package com.example.author.timetracking.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.example.author.timetracking.data.entity.Category;
import com.example.author.timetracking.data.entity.Photo;
import com.example.author.timetracking.data.entity.Record;

import java.util.Date;
import java.util.List;

public class DataObservable {

    private static DataObservable sInstance;

    private final AppDatabase database;
    private MediatorLiveData<List<Record>> observableRecords;
    private MediatorLiveData<List<Category>> observableCategories;
    private MediatorLiveData<List<Photo>> observablePhotos;

    private DataObservable(final AppDatabase database) {
        this.database = database;
        this.observableRecords = new MediatorLiveData<>();
        this.observableCategories = new MediatorLiveData<>();

        this.observableRecords.addSource(this.database.getRecordDAO().getAll(), records -> {
            if (this.database.getDatabaseCreated().getValue() != null) {
                observableRecords.postValue(records);
            }
        });
        this.observableCategories.addSource(this.database.getCategoryDAO().getAll(), categories -> {
            if (this.database.getDatabaseCreated().getValue() != null) {
                observableCategories.postValue(categories);
            }
        });
    }

    public static DataObservable getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataObservable.class) {
                if (sInstance == null) {
                    sInstance = new DataObservable(database);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<Record>> getRecords() {
        return observableRecords;
    }

    public LiveData<Record> loadRecord(final int recordId) {
        return database.getRecordDAO().findById(recordId);
    }

    public List<Category> getMostSum() {
        return database.getCategoryDAO().getMostSum();
    }

    public List<Category> getMostSum(Date start, Date end) {
        return database.getCategoryDAO().getMostSum(start, end);
    }

    public List<Category> getSum() {
        return database.getCategoryDAO().getSum();
    }

    public List<Category> getSum(Date start, Date end) {
        return database.getCategoryDAO().getSum(start, end);
    }

    public LiveData<List<Category>> getCategories() {
        return observableCategories;
    }

    public LiveData<Category> loadCategory(long catId) {
        return database.getCategoryDAO().findById(catId);
    }

    public LiveData<List<Photo>> getPhotosByRecord(long recordId) {
        return database.getPhotoDAO().findByRecordId(recordId);
    }
}
