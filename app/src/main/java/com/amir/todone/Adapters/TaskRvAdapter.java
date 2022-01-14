package com.amir.todone.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amir.todone.Domain.Category.Category;
import com.amir.todone.Domain.Category.CategoryManager;
import com.amir.todone.Domain.Task.SubTask;
import com.amir.todone.Domain.Task.Task;
import com.amir.todone.Objects.DateManager;
import com.amir.todone.R;

import java.util.Calendar;
import java.util.List;

public class TaskRvAdapter extends RecyclerView.Adapter<TaskVH> {

    interface TaskListener {
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
        setListener(null);
    }

    public void setTaskListener(TaskListener taskListener) {
        setListener(taskListener);
    }

    private void setListener(TaskListener taskListener) {
        this.taskListener = new TaskListener() {
            @Override
            public void onChangeTaskDone(int position, boolean isChecked) {
                if (taskListener != null)
                    taskListener.onChangeTaskDone(position, isChecked);
                Log.e("Adapter", tasks.get(position).getTaskText() + " Task Changed to" + isChecked);
            }

            @Override
            public void onTaskClick(int position) {
                if (taskListener != null)
                    taskListener.onTaskClick(position);
                Log.e("Adapter", tasks.get(position).getTaskText() + " Task clicked");
            }

            @Override
            public void onCategoryClick(int position) {
                if (taskListener != null)
                    taskListener.onCategoryClick(position);
                Category category = CategoryManager.getInstance(context).getCategoryById(tasks.get(position).getCategory_id());
                Log.e("Adapter", category.getName() + " Category clicked");
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
        holder.getTxtTaskText().setText(task.getTaskText());
        if (isShow_category) {
            Category category = CategoryManager.getInstance(context).getCategoryById(task.getCategory_id());
            if (category != null)
                holder.getTxtCategory().setText(category.getName());
            else holder.getCategoryBadge().setVisibility(View.GONE);
        } else holder.getCategoryBadge().setVisibility(View.GONE);

        if (!task.getDate().equals("-1") && isShow_date) {
            if (new DateManager(Calendar.getInstance()).isDatePast(task.getDate()))
                holder.getTxtDate().setTextColor(context.getResources().getColor(R.color.error));
            holder.getTxtDate().setText(task.getDate());
        } else
            holder.getDateBadge().setVisibility(View.GONE);
        if (!task.getTime().equals("-1")) {
            if (new DateManager(Calendar.getInstance()).isTimePast(task.getTime())) {
                holder.getTxtTime().setTextColor(context.getResources().getColor(R.color.error));
                holder.getImgClock().setColorFilter(context.getResources().getColor(R.color.error), PorterDuff.Mode.SRC_IN);
            }
            holder.getTxtTime().setText(task.getTime());
        } else
            holder.getTimeBadge().setVisibility(View.GONE);
        if (task.getSubtasks_count() > 0) {
            List<SubTask> subTasks = task.getSubtasks();
            int subDone = 0;
            for (SubTask sub : subTasks) {
                if (sub.is_Done()) subDone++;
                View subAddView = View.inflate(context, R.layout.subtask_layout, null);
                CheckBox checkBox = subAddView.findViewById(R.id.subtaskCheckbox);
                checkBox.setText(sub.getText());
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Toast.makeText(context, sub.getText() + " changed to " + isChecked, Toast.LENGTH_SHORT).show();
                    }
                });
                holder.getSubTasksLayout().addView(subAddView);
            }
            holder.getTxtSubTasksState().setText(subDone + "/" + subTasks.size());

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
    private ImageView imgShowSubTask , imgClock;
    private CardView categoryBadge, dateBadge, timeBadge;
    private LinearLayout subTasksLayout;

    public TaskVH(@NonNull View itemView, TaskRvAdapter.TaskListener taskListener) {
        super(itemView);
        taskCard = itemView.findViewById(R.id.taskCard);

        txtTaskText = itemView.findViewById(R.id.txtTaskText);

        taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
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

        taskCard.setOnClickListener(v -> taskListener.onTaskClick(getAdapterPosition()));

        categoryBadge.setOnClickListener(v -> taskListener.onCategoryClick(getAdapterPosition()));

        taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtTaskText.setPaintFlags(txtTaskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                txtTaskText.setPaintFlags(0);
            }
            taskListener.onChangeTaskDone(getAdapterPosition(), isChecked);
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
