package com.example.author.timetracking.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import android.support.annotation.NonNull;


import java.io.Serializable;
import java.util.Date;


@Entity
public class Record implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long recordId;
    @NonNull
    private String title;
    private Date startTime;
    private Date endTime;
    private long duration;
    @ForeignKey(entity = Category.class,
            parentColumns = "categoryId",
            childColumns = "catId",
            onDelete = ForeignKey.NO_ACTION)
    private long catId;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
        if (startTime != null && endTime != null) {
            this.duration = (endTime.getTime() - startTime.getTime()) / 3600;
        }
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            this.duration = (endTime.getTime() - startTime.getTime()) / 3600;
        }
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

}
