package com.amir.todone.Domain.Task;

public class SubTask {

    private String id;
    private String text;
    private boolean is_Done;
    private String task_id;

    public SubTask(String text) {
        this.id = null;
        this.text = text;
        this.is_Done = false;
        this.task_id = null;
    }

    public SubTask(String id, String text, boolean is_Done, String task_id) {
        this.id = id;
        this.text = text;
        this.is_Done = is_Done;
        this.task_id = task_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean is_Done() {
        return is_Done;
    }

    public String getTask_id() {
        return task_id;
    }

    public void editName(String name) {
        this.text = name;
    }

    public void set_Done(boolean is_Done) {
        this.is_Done = is_Done;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }
}
