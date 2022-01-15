package com.amir.todone.Domain.Task;

import android.content.Context;

import com.amir.todone.DA.Da;
import com.amir.todone.Objects.DateManager;

import java.util.Calendar;
import java.util.List;

public class TaskManager {

    private final Context context;

    private static TaskManager taskManager;
    private TaskManager(Context context) {
        this.context = context;
    }
    public static TaskManager getInstance(Context context){
        if (taskManager==null)
            taskManager = new TaskManager(context);
        return taskManager;
    }

    public void createTask(Task task){
        List<String> ids = Da.getInstance(context).createTask(task);
        task.setId(ids.get(0));
        List<SubTask> subs  = task.getSubtasks();
        for (int i = 0 ; i<task.getSubtasks().size() ; i++){
            subs.get(i).setId(ids.get(i+1));
            subs.get(i).setTask_id(ids.get(0));
        }
    }

    public List<Task> getDoneTasks() {
        return Da.getInstance(context).getDoneTasks();
    }
    public int getDoneCount(){
        return Da.getInstance(context).getDoneCount();
    }

    public List<Task> geTaskByDate(String date){
            return Da.getInstance(context).getTaskByDate(date);
    }
    public List<Task> geTodayTasks(){
        return geTaskByDate(new DateManager(Calendar.getInstance()).getTodayDate());
    }
    public List<Task> geTomorrowTasks(){
        return geTaskByDate(new DateManager(Calendar.getInstance()).getTomorrowDate());
    }

    public List<Task> geTaskByDateNot(String notDate1 , String notDate2){
        return Da.getInstance(context).getTaskByDate(notDate1,true,notDate2,true,true);
    }
    public List<Task> geTaskByDateNot(String notDate){
        return Da.getInstance(context).getTaskByDate(notDate,true);
    }

    public List<Task> geOtherTasks(){
        return geTaskByDateNot(new DateManager(Calendar.getInstance()).getTodayDate(),new DateManager(Calendar.getInstance()).getTomorrowDate());
    }

    public void taskDone(Task task) {
        task.set_Done(true);
        Da.getInstance(context).doneTask(task);
    }
    public void taskUnDone(Task task){
        task.set_Done(false);
        Da.getInstance(context).unDoneTask(task);
    }
    public boolean subTaskDone(SubTask subTask){
        subTask.set_Done(true);
        return Da.getInstance(context).doneSubTask(subTask);
    }
    public boolean subTaskUnDone(SubTask subTask){
        subTask.set_Done(false);
        return Da.getInstance(context).ubDoneSubTask(subTask);
    }

}
