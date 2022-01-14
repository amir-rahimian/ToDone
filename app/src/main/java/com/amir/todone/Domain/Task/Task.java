package com.amir.todone.Domain.Task;

import java.util.List;

public class Task {

    private String id;
    private String taskText;
    private boolean is_Done;
    private String category_id;
    private String date ;
    private String time;
    private int subtasks_count;
    private List<SubTask> subtasks;

    public Task(String taskText,String category_id, String date, String time, List<SubTask> subtasks) {
        this.id = null;
        this.taskText = taskText;
        this.is_Done = false;
        this.category_id = category_id;
        this.date = date;
        this.time = time;
        this.subtasks= subtasks;
        this.subtasks_count = subtasks.size();
    }

    public Task(String id, String taskText, boolean is_Done, String category_id, String date, String time, List<SubTask> subtasks) {
        this.id = id;
        this.taskText = taskText;
        this.is_Done = is_Done;
        this.category_id = category_id;
        this.date = date;
        this.time = time;
        this.subtasks_count = subtasks.size();
        this.subtasks = subtasks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskText() {
        return taskText;
    }

    public boolean is_Done() {
        return is_Done;
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
        this.is_Done = is_Done;
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
