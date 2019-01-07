package com.example.author.timetracking.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class Category implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long categoryId;
    private String title;
    private String description;
    @ForeignKey(entity = Photo.class,
                parentColumns = "phId",
                childColumns = "phId",
                onDelete = ForeignKey.CASCADE)
    private long phId;
    private int sum;

    public Category(@NonNull String title, long phId) {
        this.title = title;
        this.phId = phId;
    }

    public Category(int sum, long catId, long phId, String title) {
        this.sum = sum;
        this.categoryId = catId;
        this.phId = phId;
        this.title = title;
    }

    public Category() {
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPhId() {
        return phId;
    }

    public void setPhId(long phId) {
        this.phId = phId;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }



    @Override
    public String toString() {
        return title;
    }
}
