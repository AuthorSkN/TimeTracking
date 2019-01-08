package com.example.author.timetracking.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.author.timetracking.data.entity.Record;

import java.util.List;

@Dao
public interface RecordDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Record record);

    @Delete
    void delete(Record record);

    @Update
    void update(Record record);

    @Query("SELECT * FROM RECORD")
    LiveData<List<Record>> getAll();

    @Query("SELECT * FROM Record WHERE recordId=:recordId")
    LiveData<Record> findById(long recordId);

    @Query("SELECT * FROM Record ORDER BY catId, duration DESC")
    LiveData<List<Record>> getAllSortByCat();

}
