package com.amir.todone.Domain.Task;

import java.io.Serializable;

public class SubTask implements Serializable {

    private String id;
    private String text;
    private boolean isDone;
    private String task_id;

    public SubTask(String text) {
        this.id = null;
        this.text = text;
        this.isDone = false;
        this.task_id = null;
    }

    public SubTask(String id, String text, boolean isDone, String task_id) {
        this.id = id;
        this.text = text;
        this.isDone = isDone;
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

    public boolean isDone() {
        return isDone;
    }

    public String getTask_id() {
        return task_id;
    }

    public void editName(String name) {
        this.text = name;
    }

    public void set_Done(boolean is_Done) {
        this.isDone = is_Done;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }
}
