package com.amir.todone.Objects;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    public interface onResult {
        void light();
        void dark();
    }
    private Context context;

    private static ThemeManager themeManager ;
    private ThemeManager(Context context) {
        this.context = context;
    }
    public static ThemeManager getInstance(Context context){
        if (themeManager == null)
            themeManager = new ThemeManager(context);
        return themeManager;
    }

    public void checkTheme(onResult onResult){
        switch (Settings.getInstance(context).getTheme()) {
            case Settings.theme_default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                int nightModeFlags =
                        context.getResources().getConfiguration().uiMode &
                                Configuration.UI_MODE_NIGHT_MASK;
                switch (nightModeFlags){
                    case Configuration.UI_MODE_NIGHT_YES:
                        onResult.dark();
                        break;
                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    case Configuration.UI_MODE_NIGHT_NO:
                        onResult.light();
                        break;
                }
                break;
            case Settings.theme_dark:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                onResult.dark();
                break;
            case Settings.theme_light:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                onResult.light();
                break;
        }
    }

    public void changeThemeTo(int themeState){ // Todo : animation
        switch (themeState) {
            case Settings.theme_default:
                Settings.getInstance(context).setTheme(Settings.theme_default);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case Settings.theme_dark:
                Settings.getInstance(context).setTheme(Settings.theme_dark);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case Settings.theme_light:
                Settings.getInstance(context).setTheme(Settings.theme_light);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }
}
