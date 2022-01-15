package com.amir.todone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amir.todone.DA.Da;
import com.amir.todone.Dialogs.AddTaskBottomDialog;
import com.amir.todone.Domain.Category.CategoryManager;
import com.amir.todone.Domain.Task.TaskManager;
import com.amir.todone.Fragments.CalenderFragment;
import com.amir.todone.Fragments.HomeFragment;
import com.amir.todone.Objects.Settings;
import com.amir.todone.Objects.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNav;
    private DrawerLayout drawer_layout;
    private FloatingActionButton addButton;
    private ImageView imgChangeTheme, imgMenu, imgUser;
    private ConstraintLayout drawerCategoryOp,drawerDoneOp;
    private TextView txtCategoryCount ,txtDoneCount;
    private FragmentManager fm = getSupportFragmentManager();

    private  HomeFragment homeFragment;
    private  CalenderFragment calenderFragment;
    private boolean back_pressed = false;
    private int themeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //---------
        bottomNav = findViewById(R.id.bottomNav);
        addButton = findViewById(R.id.addButton);
        drawer_layout = findViewById(R.id.drawer_layout);
        imgChangeTheme = findViewById(R.id.imgChangeTheme);
        txtCategoryCount = findViewById(R.id.txtCategoryCount);
        drawerCategoryOp = findViewById(R.id.drawerCategoryOp);
        txtDoneCount = findViewById(R.id.txtDoneCount);
        drawerDoneOp = findViewById(R.id.drawerDoneOp);
        imgMenu = findViewById(R.id.imgMenu);
        imgUser = findViewById(R.id.imgUser);

        Da.getInstance(this).createTables();
        setTheme();
        updateDrawer();

        homeFragment = new HomeFragment();
        calenderFragment = new CalenderFragment();

        imgMenu.setOnClickListener(view -> {
            drawer_layout.open();
        });
        imgUser.setOnClickListener(view -> {

        });
        drawerDoneOp.setOnClickListener(view -> {
            if (!txtDoneCount.getText().toString().equals("0")) {
                Intent intent = new Intent(MainActivity.this, ShowTasksActivity.class);
                intent.putExtra("is_forDone", true);
                intent.putExtra("count", TaskManager.getInstance(MainActivity.this).getDoneCount());
                startActivity(intent);
                closeDrawer();
            }else {
                Toast.makeText(MainActivity.this, "You have not done any task!", Toast.LENGTH_SHORT).show();
            }
        });
        drawerCategoryOp.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,CategoriesActivity.class));
            closeDrawer();
        });
        addButton.setOnClickListener(view -> {
            showBottomSheetDialog();
            addButton.setClickable(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addButton.setClickable(true);
                }
            }, 1000);
        });
        bottomNav.setBackground(null);
        bottomNav.setItemRippleColor(null);
        bottomNav.getMenu().getItem(1).setEnabled(false);
        bottomNav.getMenu().getItem(2).setEnabled(false);
        bottomNav.setOnItemSelectedListener(this);
        bottomNav.setSelectedItemId(R.id.Home);

        imgChangeTheme.setOnClickListener(view -> {
            ThemeManager.getInstance(this).checkTheme(new ThemeManager.onResult() {
                @Override
                public void light() {
                    imgChangeTheme.setImageResource(R.drawable.ic_moon);
                    ThemeManager.getInstance(MainActivity.this).changeThemeTo(Settings.theme_dark);
                }

                @Override
                public void dark() {
                    imgChangeTheme.setImageResource(R.drawable.ic_sun);
                    ThemeManager.getInstance(MainActivity.this).changeThemeTo(Settings.theme_light);
                }
            });
        });

        fm.beginTransaction().replace(R.id.fragment_container, homeFragment, "Home").commit();

        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                updateDrawer();
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }
            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == 0){
                    updateDrawer();
                }
            }
        });
    }

    private void closeDrawer() {
        new Handler().postDelayed(() -> drawer_layout.close(),500);
    }

    private void updateDrawer() {
        txtCategoryCount.setText(CategoryManager.getInstance(MainActivity.this).getCategoriesCount()+"");
        txtDoneCount.setText(TaskManager.getInstance(MainActivity.this).getDoneCount()+"");
    }

    private void setTheme() {
        ThemeManager.getInstance(this).checkTheme( new ThemeManager.onResult() {
            @Override
            public void light() {
                imgChangeTheme.setImageResource(R.drawable.ic_sun);
            }

            @Override
            public void dark() {
                imgChangeTheme.setImageResource(R.drawable.ic_moon);
            }
        });

    }

    private void showBottomSheetDialog() {
        AddTaskBottomDialog taskBottomDialog = AddTaskBottomDialog.newInstance();
        taskBottomDialog.setListener(new AddTaskBottomDialog.BottomSheetDialogListener() {
            @Override
            public void onDismiss() {
                homeFragment.reLoadTasks();
            }
        });
        taskBottomDialog.show(getSupportFragmentManager(), "add_task_dialog_fragment");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.Home) { // Home
            if (bottomNav.getSelectedItemId() != R.id.Home)
                fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        }
        else { // Calender
            if (bottomNav.getSelectedItemId() != R.id.Calender)
                fm.beginTransaction().replace(R.id.fragment_container, calenderFragment).commit();
        }
        // Todo : add Calender view
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