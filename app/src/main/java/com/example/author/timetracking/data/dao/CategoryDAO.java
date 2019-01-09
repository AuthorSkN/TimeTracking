package com.example.author.timetracking.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.author.timetracking.data.entity.Category;

import java.util.Date;
import java.util.List;

@Dao
public interface CategoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category... category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("select * from Category")
    LiveData<List<Category>> getAll();

    @Query("select * from Category where categoryId=:categoryId limit 1")
    LiveData<Category> findById(long categoryId);

    @Query("select sum(duration) as sum, categoryId, phid, category.title from category join record on catId = categoryid group by categoryid, phid order by sum desc")
    List<Category> getMostSum();

    @Query("select sum(duration) as sum,categoryId, phid, category.title from category join record on catId = categoryid and startTime >= :start and endTime <=:end group by categoryid, phid order by sum desc")
    List<Category> getMostSum(Date start, Date end);

    @Query("select sum(duration) as sum,categoryId, phid, category.title from category join record on categoryid = catId group by catId, phid")
    List<Category> getSum();

    @Query("select sum(duration) as sum,categoryId, phid, category.title from category join record on categoryid = catId and startTime >= :start and endTime <=:end group by catId, phid")
    List<Category> getSum(Date start, Date end);

}
