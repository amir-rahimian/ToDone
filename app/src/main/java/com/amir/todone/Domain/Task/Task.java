package com.amir.todone.Domain.Task;

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {

    private String id;
    private String taskText;
    private boolean isDone;
    private String category_id;
    private String date ;
    private String time;
    private int subtasks_count;
    private int doneSubtasks_count;
    private List<SubTask> subtasks;

    public Task(String taskText,String category_id, String date, String time, List<SubTask> subtasks) {
        this.id = null;
        this.taskText = taskText;
        this.isDone = false;
        this.category_id = category_id;
        this.date = date;
        this.time = time;
        this.subtasks= subtasks;
        this.subtasks_count = subtasks.size();
        this.doneSubtasks_count = 0;
    }

    public Task(String id, String taskText, boolean isDone, String category_id, String date, String time, List<SubTask> subtasks, int doneSubtasks_count) {
        this.id = id;
        this.taskText = taskText;
        this.isDone = isDone;
        this.category_id = category_id;
        this.date = date;
        this.time = time;
        this.subtasks = subtasks;
        this.subtasks_count = subtasks.size();
        this.doneSubtasks_count = doneSubtasks_count;
    }

    public void incrementDoneSubtasks_count() {
        this.doneSubtasks_count++;
    }
    public void decrementDoneSubtasks_count() {
        this.doneSubtasks_count--;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDoneSubtasks_count() {
        return doneSubtasks_count;
    }

    public String getTaskText() {
        return taskText;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getSubtasks_count() {
        return subtasks_count;
    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    public void editTaskText(String taskText) {
        this.taskText = taskText;
    }

    public void set_Done(boolean is_Done) {
        this.isDone = is_Done;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
