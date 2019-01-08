package com.example.author.timetracking.data;


import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timeStamp) {
        if (timeStamp == null) {
            return null;
        } else {
            return new Date(timeStamp);
        }
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }


}
