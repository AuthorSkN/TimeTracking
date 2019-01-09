package com.example.author.timetracking.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.example.author.timetracking.TrackingApplication;
import com.example.author.timetracking.data.DataObservable;
import com.example.author.timetracking.data.entity.Record;

import java.util.List;

public class RecordsListViewModel extends AndroidViewModel {

    private final DataObservable dataObservable;
    private final MediatorLiveData<List<Record>> observableRecords;

    public RecordsListViewModel(Application application) {
        super(application);

        observableRecords = new MediatorLiveData<>();
        observableRecords.setValue(null);
        dataObservable = ((TrackingApplication)application).getDataObservable();
        LiveData<List<Record>> records = dataObservable.getRecords();
        observableRecords.addSource(records, observableRecords::setValue);
    }

    public LiveData<List<Record>> getRecords() {
        return observableRecords;
    }
}
