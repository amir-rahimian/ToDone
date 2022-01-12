package com.amir.todone.DA;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.amir.todone.MainActivity;
import com.amir.todone.Objects.Field;
import com.amir.todone.Objects.FieldMap;
import com.amir.todone.R;

import java.util.ArrayList;
import java.util.List;

public class LocalDa {

    //SQLite obj
    private final SQLite sql;

    //share pref
    private final SharedPreferences sharedPref ;

    //editor
    private final SharedPreferences.Editor editor ;

    //context
    private Context context;

    //Instance
    private static LocalDa da;
    //constructor
    private LocalDa (Context context) {
        sql = new SQLite(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        this.context = context;
    }
    public static LocalDa getInstance(Context context) {
        if (da == null){
            da = new LocalDa(context);
        }
        return da;
    }


    //create tables
    private void createTables(){

    }

    // get string
    private String getStrRes (int res) {
        return context.getString(res);
    }
}
