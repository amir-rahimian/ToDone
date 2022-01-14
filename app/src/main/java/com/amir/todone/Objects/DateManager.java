package com.amir.todone.Objects;

import android.util.Log;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateManager {

    Calendar calendar ;

    public DateManager(Calendar calendarInstance) {
        this.calendar = calendarInstance;
    }


    public String getTodayDate(){
        return  new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime());
    }
    public String getTomorrowDate(){
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)+1);
        return  new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime());
    }
    public String formatDateForDateBase(int year,int month,int dayOfMonth){
        calendar.set(year, month, dayOfMonth);
        Date chosenDate = calendar.getTime();
        return new SimpleDateFormat("yyyy/MM/dd").format(chosenDate);
    }
    public String formatTimeForDateBase(int hourOfDay,int minute){
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        Date chosenDate = calendar.getTime();
        return new SimpleDateFormat("HH:mm").format(chosenDate);
    }
    public boolean isDatePast(@NonNull String date){
        int cy=calendar.get(Calendar.YEAR);
        int cm=calendar.get(Calendar.MONTH)+1;
        int cd=calendar.get(Calendar.DAY_OF_MONTH);
        String[] dates = date.split("/");
        int y=Integer.parseInt(dates[0]);
        int m=Integer.parseInt(dates[1]);
        int d=Integer.parseInt(dates[2]);

        if (cy>y)
            return true;
        else if (cm>m)
            return true;
        else return cd > d;
    }
    public boolean isTimePast(@NonNull String time){
        int ch=calendar.get(Calendar.HOUR_OF_DAY);
        int cm=calendar.get(Calendar.MINUTE);
        String[] dates = time.split(":");
        int h=Integer.parseInt(dates[0]);
        int m=Integer.parseInt(dates[1]);

        if (ch>h)
            return true;
        else return cm > m;
    }
}
