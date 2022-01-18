package com.amir.todone;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.amir.todone.Dialogs.AppDialog;
import com.amir.todone.Dialogs.CategoryPickerDialog;
import com.amir.todone.Domain.Category.Category;
import com.amir.todone.Domain.Category.CategoryManager;
import com.amir.todone.Domain.Task.SubTask;
import com.amir.todone.Domain.Task.Task;
import com.amir.todone.Domain.Task.TaskManager;
import com.amir.todone.Objects.DateManager;
import com.amir.todone.Objects.Settings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CheckBox taskCheckbox;
    private EditText edtTaskText;
    private LinearLayout subList, addASubtaskOption;
    private TextView txtSelectedCategory, txtSelectedDate, txtSelectedTime;
    private ConstraintLayout categoryOp, dateOp, timeOp;


    private Category taskCategory = null;
    private String taskDate = null;
    private String taskTime = null;
    private Task task;
    private List<SubTask> subTasks;
    private boolean haveChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        toolbar = findViewById(R.id.taskToolbar);
        edtTaskText = findViewById(R.id.edtTaskText);
        taskCheckbox = findViewById(R.id.subTaskCheckbox);
        subList = findViewById(R.id.subList);
        addASubtaskOption = findViewById(R.id.addASubtaskOption);
        txtSelectedCategory = findViewById(R.id.txtSelectedCategory);
        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        txtSelectedTime = findViewById(R.id.txtSelectedTime);
        categoryOp = findViewById(R.id.categoryOp);
        dateOp = findViewById(R.id.dateOp);
        timeOp = findViewById(R.id.timeOp);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            implementActivity();
            Bundle intent = getIntent().getExtras();
            task = (Task) intent.getSerializable("Task");
            getSupportActionBar().setTitle(task.getTaskText());
            edtTaskText.setText(task.getTaskText());
            taskCheckbox.setChecked(task.isDone());
            taskCategory = CategoryManager.getInstance(TaskActivity.this).getCategoryById(task.getCategory_id());
            if (taskCategory != null)
                txtSelectedCategory.setText(taskCategory.getName());
            setTimeAndDate();
            subTasks = task.getSubtasks();
            for (SubTask sub :
                    subTasks) {
                addSubTask(sub);
            }
            haveChange = false;
        } else {
            finish();
        }
    }

    private void implementActivity() {
        addASubtaskOption.setOnClickListener(v -> {
            haveChange = true;
            if (subList.getChildCount() < 5) {
                int emptyTextView = notEmpty();
                if (emptyTextView == -1) {
                    addSubTask();
                } else {
                    Toast.makeText(TaskActivity.this, R.string.have_empty_subtask, Toast.LENGTH_SHORT).show();
                    subList.getChildAt(emptyTextView).findViewById(R.id.edtAddCategory).requestFocus();
                }
            }
        });
        edtTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((null != edtTaskText.getLayout() && edtTaskText.getLayout().getLineCount() > 5)) {
                    edtTaskText.getText().delete(edtTaskText.getText().length() - 1, edtTaskText.getText().length());
                }
                haveChange = true;
            }
        });
        taskCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo : Done Task
                haveChange = true;
                boolean isChecked = taskCheckbox.isChecked();
                changeAllSubsTo(isChecked);
            }
        });
        taskCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtTaskText.setPaintFlags(edtTaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    edtTaskText.setAlpha(0.8f);
                } else {
                    edtTaskText.setPaintFlags(0);
                    edtTaskText.setAlpha(1.0f);
                }
            }
        });
        ////////////////////
        categoryOp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(TaskActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.edit_delete_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                            haveChange = true;
                            taskCategory = null;
                            txtSelectedCategory.setText(R.string.set);
                        } else {
                            categoryOp.callOnClick();
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });
        categoryOp.setOnClickListener(v -> {
            List<Category> categories = CategoryManager.getInstance(TaskActivity.this).getAllCategories();
            new CategoryPickerDialog(TaskActivity.this, categories,
                    new CategoryPickerDialog.onSelectListener() {
                        @Override
                        public void done(Category category) {
                            haveChange = true;
                            taskCategory = category;
                            txtSelectedCategory.setText(taskCategory.getName());
                        }
                    })
                    .show(getSupportFragmentManager(), "CategoryPicker");
            categoryOp.setClickable(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    categoryOp.setClickable(true);
                }
            }, 1000);
        });

        ///////////////////////
        dateOp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(TaskActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.edit_delete_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                            haveChange = true;
                            taskDate = null;
                            txtSelectedDate.setText(R.string.set);
                            txtSelectedDate.setTextColor(getResources().getColor(R.color.textColor_hint));
                            timeOp.animate().alpha(0.5f);
                        } else {
                            dateOp.callOnClick();
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });
        dateOp.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(TaskActivity.this,
                    (DatePickerDialog.OnDateSetListener) (view12, year, month, dayOfMonth) -> {
                        taskDate = new DateManager(Calendar.getInstance()).formatDateForDateBase(year, month, dayOfMonth);
                        haveChange = true;
                        if (new DateManager(Calendar.getInstance()).isDatePast(taskDate)) {
                            txtSelectedDate.setTextColor(getResources().getColor(R.color.error));
                            Toast.makeText(TaskActivity.this, R.string.wanado_in_past, Toast.LENGTH_SHORT).show();
                            txtSelectedDate.setText(taskDate);
                            if (timeOp.getAlpha() == 1.0f) timeOp.animate().alpha(0.5f);
                            if (taskTime != null)
                                txtSelectedTime.setTextColor(getResources().getColor(R.color.error));
                        } else {
                            txtSelectedDate.setTextColor(getResources().getColor(R.color.textColor_hint));
                            txtSelectedDate.setText(taskDate);
                            if (timeOp.getAlpha() != 1.0f) timeOp.animate().alpha(1.0f);
                            if (taskTime != null)
                                if (new DateManager(Calendar.getInstance()).isTimePast(taskDate, taskTime)) {
                                    txtSelectedTime.setTextColor(getResources().getColor(R.color.error));
                                } else {
                                    txtSelectedTime.setTextColor(getResources().getColor(R.color.textColor_hint));
                                }
                        }
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
            dateOp.setClickable(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dateOp.setClickable(true);
                }
            }, 1000);
        });
        /////////
        timeOp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (timeOp.getAlpha() == 1.0f) {
                    PopupMenu popup = new PopupMenu(TaskActivity.this, v);
                    popup.getMenuInflater().inflate(R.menu.edit_delete_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.delete) {
                                haveChange = true;
                                taskTime = null;
                                txtSelectedTime.setText(R.string.set);
                                txtSelectedTime.setTextColor(getResources().getColor(R.color.textColor_hint));
                            } else {
                                timeOp.callOnClick();
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
                return false;
            }
        });
        timeOp.setOnClickListener(v -> {
            if (timeOp.getAlpha() == 1.0f) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(TaskActivity.this,
                        (TimePickerDialog.OnTimeSetListener) (view13, hourOfDay, minute) -> {
                            taskTime = new DateManager(Calendar.getInstance()).formatTimeForDateBase(hourOfDay, minute);
                            haveChange = true;
                            txtSelectedTime.setText(taskTime);
                            if (new DateManager(Calendar.getInstance()).isTimePast(taskDate, taskTime)) {
                                txtSelectedTime.setTextColor(getResources().getColor(R.color.error));
                                Toast.makeText(TaskActivity.this, R.string.wanado_in_past, Toast.LENGTH_SHORT).show();
                            } else {
                                txtSelectedTime.setTextColor(getResources().getColor(R.color.textColor_hint));
                            }
                            txtSelectedTime.setText(taskTime);
                        },
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        true);
                timePickerDialog.show();
                timeOp.setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timeOp.setClickable(true);
                    }
                }, 1000);
            } else {
                Toast.makeText(TaskActivity.this, R.string.date_first, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setTimeAndDate() {
        taskDate = task.getDate().equals("-1") ? null : task.getDate();
        taskTime = task.getTime().equals("-1") ? null : task.getTime();
        if (taskDate != null) {
            if (new DateManager(Calendar.getInstance()).isDatePast(taskDate)) {
                txtSelectedDate.setTextColor(getResources().getColor(R.color.error));
            }
            txtSelectedDate.setText(task.getDate());
            timeOp.setAlpha(1.0f);
        }
        if (taskTime != null) {
            if (new DateManager(Calendar.getInstance()).isTimePast(taskDate, taskTime)) {
                txtSelectedTime.setTextColor(getResources().getColor(R.color.error));
            }
            txtSelectedTime.setText(task.getTime());
        }
    }

    private void addSubTask(@Nullable SubTask subTask) {
        View subListAddView = View.inflate(TaskActivity.this, R.layout.subtask_add_layout, null);
        EditText sub = subListAddView.findViewById(R.id.edtAddCategory);
        CheckBox check = subListAddView.findViewById(R.id.subTaskCheckbox);
        if (subTask == null) sub.requestFocus();
        else {
            sub.setText(subTask.getText());
            check.setChecked(subTask.isDone());
        }
        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            haveChange = true;
            int doneCount = checkAllSubtasksDone();
            if (isChecked) {
                if (doneCount == subTasks.size()) {
                    taskCheckbox.setChecked(true);
                }
            } else {
                if (doneCount == subTasks.size() - 1) {
                    taskCheckbox.setChecked(false);
                }
            }
        });
        sub.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                haveChange = true;
                if (s.length() == 30)
                    Toast.makeText(TaskActivity.this, R.string.max_subtask_len, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        subListAddView.findViewById(R.id.imgDel).setOnClickListener(delView -> {
            haveChange = true;
            subList.removeView(subListAddView);
            if (addASubtaskOption.getVisibility() != View.VISIBLE)
                addASubtaskOption.setVisibility(View.VISIBLE);
        });
        subList.addView(subListAddView);
        if (subList.getChildCount() == 5) {
            addASubtaskOption.setVisibility(View.GONE);
        }
    }

    private void addSubTask() {
        addSubTask(null);
    }

    private int checkAllSubtasksDone() {
        int doneCount = 0;
        for (int i = 0; i < subList.getChildCount(); i++) {
            if (subList.getChildAt(i) != null) {
                CheckBox checkBox = subList.getChildAt(i).findViewById(R.id.subTaskCheckbox);
                if (checkBox.isChecked()) {
                    doneCount++;
                }
            }
        }

        return doneCount;

    }

    private void changeAllSubsTo(boolean isCheck) {
        for (int i = 0; i < subList.getChildCount(); i++) {
            if (subList.getChildAt(i) != null) {
                CheckBox checkBox = subList.getChildAt(i).findViewById(R.id.subTaskCheckbox);
                checkBox.setChecked(isCheck);
            }
        }
    }


    private int notEmpty() {
        for (int i = 0; i < subList.getChildCount(); i++) {
            if (subList.getChildAt(i) != null) {
                EditText editText = subList.getChildAt(i).findViewById(R.id.edtAddCategory);
                if (editText.getText().toString().trim().matches("")) return i;
            }
        }
        return -1;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.deleteTask) {
            AppDialog dialog = new AppDialog();
            dialog.setTitle(getString(R.string.delete_task));
            dialog.setMassage(getString(R.string.sure_delete_task));
            dialog.setOkButton(getResources().getString(R.string.delete), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskManager.getInstance(TaskActivity.this).deleteTask(task);
                    Settings.getInstance(TaskActivity.this).notifyTaskChanged();
                    finish();
                }
            });
            dialog.setCancelButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show(getSupportFragmentManager(), "Sure");
        } else {
            if (!haveChange) {
                finish();
            } else {
                updateTask();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateTask() {
        task.set_Done(taskCheckbox.isChecked());
        task.editTaskText(edtTaskText.getText().toString().trim());
        if (taskDate != null) {
            if (new DateManager(Calendar.getInstance()).isDatePast(taskDate)) {
                Toast.makeText(TaskActivity.this, R.string.wrong_date, Toast.LENGTH_SHORT).show();
                txtSelectedTime.setTextColor(getResources().getColor(R.color.error));
                txtSelectedDate.setTextColor(getResources().getColor(R.color.error));
                return;
            } else
                task.setDate(taskDate);
        } else {
            task.setDate("-1");
        }
        if (taskTime != null) {
            if (new DateManager(Calendar.getInstance()).isTimePast(taskDate, taskTime)) {
                Toast.makeText(TaskActivity.this, R.string.wrong_time, Toast.LENGTH_SHORT).show();
                txtSelectedTime.setTextColor(getResources().getColor(R.color.error));
                return;
            } else
                task.setTime(taskTime);
        } else {
            task.setTime("-1");
        }
        if (!task.getCategory_id().equals("-1"))
        CategoryManager.getInstance(TaskActivity.this).aTaskLeftThisCategory(task.getCategory_id());
        if (taskCategory != null)
            task.setCategory_id(taskCategory.getId());
        else {
            task.setCategory_id("-1");
        }
        int doneSubs = checkAllSubtasksDone();
        for (SubTask s :
                subTasks) {
            TaskManager.getInstance(TaskActivity.this).deleteSubTask(s);
        }
        subTasks.clear();
        subTasks.addAll(subTaskData());
        task.setSubtasks_count(subTasks.size());
        task.setDoneSubtasks_count(doneSubs);
        TaskManager.getInstance(TaskActivity.this).updateTask(task);
        Settings.getInstance(TaskActivity.this).notifyTaskChanged();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private List<SubTask> subTaskData() {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (int i = 0; i < subList.getChildCount(); i++) {
            if (subList.getChildAt(i) != null) {
                EditText editText = subList.getChildAt(i).findViewById(R.id.edtAddCategory);
                CheckBox checkBox = subList.getChildAt(i).findViewById(R.id.subTaskCheckbox);
                if (!editText.getText().toString().trim().matches("")) {
                    subTasks.add(new SubTask(editText.getText().toString().trim(), checkBox.isChecked()));
                }
            }
        }
        return subTasks;
    }

    @Override
    public void onBackPressed() {
        if (haveChange) {
            AppDialog dialog = new AppDialog();
            dialog.setTitle(getString(R.string.discard_change));
            dialog.setMassage(getString(R.string.sure_discard));
            dialog.setOkButton(getString(R.string.discard), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            dialog.setCancelButton(getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show(getSupportFragmentManager(), "Sure");
        } else
            finish();
    }
}