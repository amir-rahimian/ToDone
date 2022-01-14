package com.amir.todone.Domain.Category;

import java.io.Serializable;

public class Category implements Serializable {

    private String id;
    private String name ;
    private int taskCount;

    //for create
    public Category(String name){
        this.id = null;
        this.name = name;
        taskCount = 0;
    }

    // for show data
    public Category(String id, String name, int taskCount) {
        this.id = id;
        this.name = name;
        this.taskCount = taskCount;
    }

    public String getName() {
        return name;
    }

    public void editName(String newName){ name = newName;}

    public int getTaskCount() {
        return taskCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }
}
