package com.example.author.timetracking.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.example.author.timetracking.TrackingApplication;
import com.example.author.timetracking.data.DataObservable;
import com.example.author.timetracking.data.entity.Category;

import java.util.Date;
import java.util.List;

public class CategoryListViewModel extends AndroidViewModel {

    private final DataObservable dataObservable;
    private final MediatorLiveData<List<Category>> observableCategories;

    public CategoryListViewModel(Application application) {
        super(application);

        observableCategories = new MediatorLiveData<>();
        observableCategories.setValue(null);
        dataObservable = ((TrackingApplication) application).getDataObservable();
        LiveData<List<Category>> records = dataObservable.getCategories();
        observableCategories.addSource(records, records1 -> observableCategories.setValue(records1));
    }

    public LiveData<List<Category>> getCategories() {
        return observableCategories;
    }

    public List<Category> getMostSum() {
        return dataObservable.getMostSum();
    }

    public List<Category> getMostSum(Date start, Date end) {
        return dataObservable.getMostSum(start, end);
    }

    public List<Category> getSum() {
        return dataObservable.getSum();
    }

    public List<Category> getSum(Date start, Date end) {
        return dataObservable.getSum(start, end);
    }

}
