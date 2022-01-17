package com.amir.todone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.amir.todone.Adapters.TaskRvAdapter;
import com.amir.todone.Dialogs.AppDialog;
import com.amir.todone.Domain.Category.Category;
import com.amir.todone.Domain.Category.CategoryManager;
import com.amir.todone.Domain.Task.Task;
import com.amir.todone.Domain.Task.TaskManager;

import java.util.List;

public class ShowTasksActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtTaskCount, txtHintShowTask;

    private List<Task> taskToShow;
    private RecyclerView tasksRV;
    private TaskRvAdapter tasksAdapter;

    enum activityState {
        Category, Done
    }

    private activityState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tasks);
        toolbar = findViewById(R.id.showTaskToolbar);
        txtTaskCount = findViewById(R.id.txtTaskCount);
        tasksRV = findViewById(R.id.RVShowTask);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            Bundle intent = getIntent().getExtras();
            if (intent.getBoolean("is_forCategory")) {
                Category category = (Category) intent.getSerializable("category");
                setTitle(category.getName());
                state = activityState.Category;
                int count = category.getTaskCount();
                txtTaskCount.setText(count + " Task" + (count > 1 ? "s" : ""));
                taskToShow = CategoryManager.getInstance(this).getCategoryTasks(category);
            }
            if (intent.getBoolean("is_forDone")) {
                setTitle("Done");
                state = activityState.Done;
                int count = intent.getInt("count");
                txtTaskCount.setText(count + " Task" + (count > 1 ? "s" : ""));
                taskToShow = TaskManager.getInstance(this).getDoneTasks();
            }
            tasksRV.setLayoutManager(new LinearLayoutManager(this));
            tasksAdapter = new TaskRvAdapter(this, taskToShow);
            tasksAdapter.setTaskListener(new TaskRvAdapter.TaskListener() {
                @Override
                public void onChangeTaskDone(int position, boolean isChecked) { }

                @Override
                public void onTaskClick(int position) {
                }

                @Override
                public void onCategoryClick(int position) {
                    Category category = CategoryManager.getInstance(ShowTasksActivity.this).getCategoryById(taskToShow.get(position).getCategory_id());
                    AppDialog dialog = new AppDialog();
                    dialog.setTitle(category.getName());
                    dialog.setMassage("Do you want to see all tasks of this category?");
                    dialog.setCancelButton("No",null);
                    dialog.setOkButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ShowTasksActivity.this, ShowTasksActivity.class);
                            intent.putExtra("is_forCategory", true);
                            intent.putExtra("category", category);
                            startActivity(intent);
                        }
                    });
                    dialog.show(getSupportFragmentManager(),"Open category");
                }
            });
            if (state == activityState.Category)
                tasksAdapter.setShow_category(false);
            tasksRV.setAdapter(tasksAdapter);
        } else {
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}