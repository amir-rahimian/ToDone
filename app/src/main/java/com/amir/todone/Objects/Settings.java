package com.amir.todone.Objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    SharedPreferences sharedPreferences;
    public static final int theme_default = -1;
    public static final int theme_light = 1;
    public static final int theme_dark = 2;

    private static Settings Settings;
    private Settings(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static Settings getInstance(Context context){
        if (Settings == null)
            Settings = new Settings(context);
        return Settings;
    }
    public void setTheme (int theme){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Theme",theme);
        editor.apply();
    }
    public int getTheme (){
        return sharedPreferences.getInt("Theme",theme_default);
    }
    public void setLanguage(Languages language){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Language" , language==Languages.En?"En":"Fa");
        editor.apply();
    }
   public Languages getLanguage(){
        return sharedPreferences.getString("Language", "En").equals("En") ?Languages.En:Languages.Fa;
   }

}

