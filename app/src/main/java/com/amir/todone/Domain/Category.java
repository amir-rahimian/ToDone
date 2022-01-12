package com.amir.todone.Domain;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String name ;
    private int count;
    private List<String> Tasks;

    public Category(String name) throws Exception {
        if (have()) throw new Exception("already have this category name");
        this.name = name;
        count = 0 ;
        Tasks = new ArrayList<>();
    }
    public Category(String name, List<String> tasks) throws Exception {
        if (have()) throw new Exception("already have this category name");
        this.name = name;
        this.count = tasks.size();
        Tasks = tasks;
    }
    private boolean have(){
        return false;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void editname(String newName) {
        name = newName;
    }
}
