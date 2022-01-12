package com.amir.todone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.amir.todone.Dialogs.AddTaskBottomDialog;
import com.amir.todone.Fragments.CalenderFragment;
import com.amir.todone.Fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNav;
    private DrawerLayout drawer_layout;
    private FloatingActionButton addButton;
    private ImageView imgChangeTheme, imgMenu ,imgUser;
    private FragmentManager fm = getSupportFragmentManager();

    private final HomeFragment homeFragment = new HomeFragment();
    private final CalenderFragment calenderFragment = new CalenderFragment();

    private boolean back_pressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //---------
        bottomNav = findViewById(R.id.bottomNav);
        addButton = findViewById(R.id.addButton);
        drawer_layout = findViewById(R.id.drawer_layout);
        imgChangeTheme = findViewById(R.id.imgChangeTheme);
        imgMenu = findViewById(R.id.imgMenu);
        imgUser = findViewById(R.id.imgUser);


        imgMenu.setOnClickListener(view -> {
            drawer_layout.open();
        });
        imgUser.setOnClickListener(view -> {

        });
        addButton.setOnClickListener(view -> {
            showBottomSheetDialog();
            addButton.setClickable(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addButton.setClickable(true);
                }
            },1000);
        });
        bottomNav.setBackground(null);
        bottomNav.setItemRippleColor(null);
        bottomNav.getMenu().getItem(1).setEnabled(false);
        bottomNav.setOnItemSelectedListener(this);
        bottomNav.setSelectedItemId(R.id.Home);

        imgChangeTheme.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Change", Toast.LENGTH_SHORT).show();
//            imgChangeTheme.setImageResource(R.drawable.ic_moon);
        });


        fm.beginTransaction().replace(R.id.fragment_container, homeFragment, "Home").commit();
    }

    private void showBottomSheetDialog() {
        AddTaskBottomDialog taskBottomDialog = AddTaskBottomDialog.newInstance();
        taskBottomDialog.show(getSupportFragmentManager(),"add_task_dialog_fragment");
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.Home) { // Home
            if (bottomNav.getSelectedItemId() != R.id.Home)
                fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        } else { // Calender
            if (bottomNav.getSelectedItemId() != R.id.Calender)
                fm.beginTransaction().replace(R.id.fragment_container, calenderFragment).commit();
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (back_pressed) {
            super.onBackPressed();
        } else {
            back_pressed = true;
            Toast.makeText(this, getString(R.string.press_once_again_to_exit), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    back_pressed = false;
                }
            }, 2500);
        }

    }
}