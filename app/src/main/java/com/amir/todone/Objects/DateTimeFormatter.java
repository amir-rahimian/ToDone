package com.amir.todone.Objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeFormatter {

    private final Date dateTime;
    private String date ;
    private String time ;

    public DateTimeFormatter(String dateTime) {
        Date dateTime1;
        DateFormat dateFormatUtc = new SimpleDateFormat("HH:mm'&'yyyy/MM/dd Z");
        try {
            dateTime1 = dateFormatUtc.parse(dateTime+" -5:00");
        } catch (ParseException e) {
            dateTime1 = new Date();
        }
        this.dateTime = dateTime1;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
