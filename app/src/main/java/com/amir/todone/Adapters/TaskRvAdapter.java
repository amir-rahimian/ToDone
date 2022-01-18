package com.amir.todone.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.amir.todone.Domain.Category.Category;
import com.amir.todone.Domain.Category.CategoryManager;
import com.amir.todone.Domain.Task.SubTask;
import com.amir.todone.Domain.Task.Task;
import com.amir.todone.Domain.Task.TaskManager;
import com.amir.todone.Objects.DateManager;
import com.amir.todone.R;
import com.amir.todone.TaskActivity;

import java.util.Calendar;
import java.util.List;


public class TaskRvAdapter extends RecyclerView.Adapter<TaskVH> {

    public interface TaskListener {
        void onChangeTaskDone(int position, boolean isChecked);

        void onTaskClick(int position);

        void onCategoryClick(int position);
    }

    private List<Task> tasks;
    private Context context;
    private boolean isShow_category = true;
    private boolean isShow_date = true;
    private TaskListener taskListener;

    public TaskRvAdapter(Context context, List<Task> tasks) {
        this.tasks = tasks;
        this.context = context;
        setTaskListener(null);
    }

    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = new TaskListener() {
            @Override
            public void onChangeTaskDone(int position, boolean isChecked) {
                if (taskListener != null)
                    taskListener.onChangeTaskDone(position, isChecked);
                if (isChecked) {
                    TaskManager.getInstance(context).taskDone(tasks.get(position));
                } else {
                    TaskManager.getInstance(context).taskUnDone(tasks.get(position));
                }
            }

            @Override
            public void onTaskClick(int position) {
                if (taskListener != null)
                    taskListener.onTaskClick(position);
            }

            @Override
            public void onCategoryClick(int position) {
                if (taskListener != null)
                    taskListener.onCategoryClick(position);
            }
        };
    }

    public void setShow_category(boolean show_category) {
        isShow_category = show_category;
    }

    public void setShow_date(boolean show_date) {
        isShow_date = show_date;
    }

    @NonNull
    @Override
    public TaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.task_layout, parent, false);
        return new TaskVH(v, taskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskVH holder, int position) {
        Task task = tasks.get(position);
        CardView taskCard = holder.getTaskCard();
        TextView txtTaskText = holder.getTxtTaskText();
        taskCard.setOnClickListener(v -> {
            taskListener.onTaskClick(position);
            Intent intent =new Intent(context, TaskActivity.class);
            intent.putExtra("Task",task);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, taskCard,"task");
            context.startActivity(intent, optionsCompat.toBundle());
        });
        LottieAnimationView ConfettiLayout = holder.getConfettiLayout();
        txtTaskText.setText(task.getTaskText());
        CheckBox checkBox = holder.getTaskCheckbox();
        checkBox.setChecked(task.isDone());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtTaskText.setPaintFlags(txtTaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                txtTaskText.setAlpha(0.8f);
                ConfettiLayout.setAnimation(R.raw.confetti);
                ConfettiLayout.playAnimation();
            } else {
                txtTaskText.setPaintFlags(0);
                txtTaskText.setAlpha(1.0f);
            }
        });
        if (isShow_category) {
            Category category = CategoryManager.getInstance(context).getCategoryById(task.getCategory_id());
            if (category != null) {
                holder.getCategoryBadge().setVisibility(View.VISIBLE);
                holder.getTxtCategory().setText(category.getName());
            }else holder.getCategoryBadge().setVisibility(View.GONE);
        } else holder.getCategoryBadge().setVisibility(View.GONE);

        if (!task.getDate().equals("-1") && isShow_date) {
            holder.getDateBadge().setVisibility(View.VISIBLE);
            if (new DateManager(Calendar.getInstance()).isDatePast(task.getDate()))
                holder.getTxtDate().setTextColor(context.getResources().getColor(R.color.error));
            holder.getTxtDate().setText(task.getDate());
        } else
            holder.getDateBadge().setVisibility(View.GONE);
        if (!task.getTime().equals("-1")) {
            holder.getTimeBadge().setVisibility(View.VISIBLE);
            if (new DateManager(Calendar.getInstance()).isTimePast(task.getDate(), task.getTime())) {
                holder.getTxtTime().setTextColor(context.getResources().getColor(R.color.error));
                holder.getImgClock().setColorFilter(context.getResources().getColor(R.color.error), PorterDuff.Mode.SRC_IN);
            }
            holder.getTxtTime().setText(task.getTime());
        } else
            holder.getTimeBadge().setVisibility(View.GONE);
        LinearLayout subTaskLayout = holder.getSubTasksLayout();
        subTaskLayout.removeAllViews();
        if (task.getSubtasks_count() > 0) {
            holder.getImgShowSubTask().setVisibility(View.VISIBLE);
            holder.getTxtSubTasksState().setVisibility(View.VISIBLE);
            holder.getSubTasksLayout().setVisibility(View.VISIBLE);
            List<SubTask> subTasks = task.getSubtasks();
            for (SubTask sub : subTasks) {
                View subAddView = View.inflate(context, R.layout.subtask_layout, null);
                CheckBox subCheckBox = subAddView.findViewById(R.id.subtaskCheckbox);
                subCheckBox.setText(sub.getText());
                subCheckBox.setChecked(sub.isDone());
                subCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        task.incrementDoneSubtasks_count();
                        if (TaskManager.getInstance(context).subTaskDone(sub)) {
                            holder.getTaskCheckbox().setChecked(true);
                            TaskManager.getInstance(context).taskDone(task);
                        }
                    } else {
                        task.decrementDoneSubtasks_count();
                        if (TaskManager.getInstance(context).subTaskUnDone(sub)) {
                            holder.getTaskCheckbox().setChecked(false);
                            TaskManager.getInstance(context).taskUnDone(task);
                        }
                    }
                    holder.getTxtSubTasksState().setText(task.getDoneSubtasks_count() + "/" + subTasks.size());
                });
                subTaskLayout.addView(subAddView);
            }
            holder.getTxtSubTasksState().setText(task.getDoneSubtasks_count() + "/" + subTasks.size());
        } else {
            holder.getImgShowSubTask().setVisibility(View.GONE);
            holder.getTxtSubTasksState().setVisibility(View.GONE);
            holder.getSubTasksLayout().setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}

class TaskVH extends RecyclerView.ViewHolder {

    private CardView taskCard;
    private CheckBox taskCheckbox;
    private TextView txtTaskText, txtSubTasksState, txtCategory, txtDate, txtTime;
    private ImageView imgShowSubTask, imgClock;
    private CardView categoryBadge, dateBadge, timeBadge;
    private LinearLayout subTasksLayout;
    private LottieAnimationView ConfettiLayout;

    public TaskVH(@NonNull View itemView, TaskRvAdapter.TaskListener taskListener) {
        super(itemView);
        taskCard = itemView.findViewById(R.id.taskCard);

        txtTaskText = itemView.findViewById(R.id.txtTaskText);

        ConfettiLayout = itemView.findViewById(R.id.ConfettiLayout);

        taskCheckbox = itemView.findViewById(R.id.subTaskCheckbox);
        txtSubTasksState = itemView.findViewById(R.id.txtSubTasksState);
        imgShowSubTask = itemView.findViewById(R.id.imgShowSubTask);
        imgClock = itemView.findViewById(R.id.imgClock);

        categoryBadge = itemView.findViewById(R.id.categoryBadge);
        dateBadge = itemView.findViewById(R.id.dateBadge);
        timeBadge = itemView.findViewById(R.id.timeBadge);

        txtCategory = itemView.findViewById(R.id.txtCategory);
        txtDate = itemView.findViewById(R.id.txtDate);
        txtTime = itemView.findViewById(R.id.txtTime);

        subTasksLayout = itemView.findViewById(R.id.subTasksLayout);

        categoryBadge.setOnClickListener(v -> taskListener.onCategoryClick(getAdapterPosition()));

        taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtTaskText.setPaintFlags(txtTaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                txtTaskText.setAlpha(0.8f);
            } else {
                txtTaskText.setPaintFlags(0);
                txtTaskText.setAlpha(1.0f);
            }

        });
        taskCheckbox.setOnClickListener(v -> {
            boolean isChecked = taskCheckbox.isChecked();
            taskListener.onChangeTaskDone(getAdapterPosition(), isChecked);
            for (int i = 0; i < subTasksLayout.getChildCount(); i++) {
                CheckBox checkBox = subTasksLayout.getChildAt(i).findViewById(R.id.subtaskCheckbox);
                if (isChecked) {
                    if (!checkBox.isChecked()) checkBox.setChecked(true);
                } else {
                    if (checkBox.isChecked()) checkBox.setChecked(false);
                }
            }
        });

        imgShowSubTask.setOnClickListener(v -> {
            if (subTasksLayout.getVisibility() == View.VISIBLE) {
                imgShowSubTask.animate().rotation(0.0f);
                subTasksLayout.setVisibility(View.GONE);
            } else {
                imgShowSubTask.animate().rotation(180.0f);
                subTasksLayout.setVisibility(View.VISIBLE);
            }
        });
        subTasksLayout.setVisibility(View.GONE);
    }


    public LottieAnimationView getConfettiLayout() {
        return ConfettiLayout;
    }

    public ImageView getImgClock() {
        return imgClock;
    }

    public CardView getTaskCard() {
        return taskCard;
    }

    public TextView getTxtTaskText() {
        return txtTaskText;
    }

    public CheckBox getTaskCheckbox() {
        return taskCheckbox;
    }

    public TextView getTxtSubTasksState() {
        return txtSubTasksState;
    }

    public TextView getTxtCategory() {
        return txtCategory;
    }

    public TextView getTxtDate() {
        return txtDate;
    }

    public TextView getTxtTime() {
        return txtTime;
    }

    public ImageView getImgShowSubTask() {
        return imgShowSubTask;
    }

    public CardView getCategoryBadge() {
        return categoryBadge;
    }

    public CardView getDateBadge() {
        return dateBadge;
    }

    public CardView getTimeBadge() {
        return timeBadge;
    }

    public LinearLayout getSubTasksLayout() {
        return subTasksLayout;
    }
}
