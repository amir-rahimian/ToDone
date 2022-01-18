package com.amir.todone;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.amir.todone.Objects.Languages;
import com.amir.todone.Objects.Settings;
import com.amir.todone.Objects.ThemeManager;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ConstraintLayout accountOp, themesOp, languageOp;
    private TextView txtSelectedTheme, txtSelectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbarSetting);
        accountOp = findViewById(R.id.accountOp);
        themesOp = findViewById(R.id.themesOp);
        languageOp = findViewById(R.id.languageOp);
        txtSelectedTheme = findViewById(R.id.txtSelectedTheme);
        txtSelectedLanguage = findViewById(R.id.txtSelectedLanguage);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.settings));

        setThemeVal();
        setLangVal();

        accountOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo : open account sync activity
            }
        });
        themesOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(SettingsActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.theme_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.themeDefault) {
                            ThemeManager.getInstance(SettingsActivity.this).changeThemeTo(Settings.theme_default);
                            txtSelectedTheme.setText(R.string.systemDefault);
                        } else if (item.getItemId() == R.id.light) {
                            ThemeManager.getInstance(SettingsActivity.this).changeThemeTo(Settings.theme_light);
                            txtSelectedTheme.setText(R.string.light);
                        } else {
                            ThemeManager.getInstance(SettingsActivity.this).changeThemeTo(Settings.theme_dark);
                            txtSelectedTheme.setText(R.string.dark);
                        }
                        return false;
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    popup.setGravity(Gravity.END);
                }
                popup.show();
            }
        });
        languageOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo : lang Change !
                PopupMenu popup = new PopupMenu(SettingsActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.language_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.en) {
                            if (Settings.getInstance(SettingsActivity.this).getLanguage()!=Languages.En) {
                                setLocal("en");
                                Settings.getInstance(SettingsActivity.this).setLanguage(Languages.En);
                            }
                        } else {
                            if (Settings.getInstance(SettingsActivity.this).getLanguage()!=Languages.Fa){
                                setLocal("fa");
                                Settings.getInstance(SettingsActivity.this).setLanguage(Languages.Fa);
                            }
                        }
                        return false;
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    popup.setGravity(Gravity.END);
                }
                popup.show();
            }
        });
    }

    private void setThemeVal() {
        switch (Settings.getInstance(SettingsActivity.this).getTheme()) {
            case Settings.theme_default:
                txtSelectedTheme.setText(R.string.systemDefault);
                break;
            case Settings.theme_light:
                txtSelectedTheme.setText(R.string.light);
                break;
            case Settings.theme_dark:
                txtSelectedTheme.setText(R.string.dark);
                break;
        }
    }

    private void setLangVal() {
        if (Settings.getInstance(SettingsActivity.this).getLanguage() == Languages.En) {
            txtSelectedLanguage.setText(R.string.en_lang_name);
        } else {
            txtSelectedLanguage.setText(R.string.fa_lang_name);
        }
    }

    public void setLocal(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        recreate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}