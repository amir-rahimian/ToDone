package com.amir.todone.DA;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.airbnb.lottie.L;
import com.amir.todone.Domain.Category.Category;
import com.amir.todone.Domain.Task.SubTask;
import com.amir.todone.Domain.Task.Task;
import com.amir.todone.Objects.Field;
import com.amir.todone.Objects.FieldMap;
import com.amir.todone.R;

import java.util.ArrayList;
import java.util.List;

public class Da {

    //SQLite obj
    private final SQLite sql;

    //share pref
    private final SharedPreferences sharedPref;

    //editor
    private final SharedPreferences.Editor editor;

    //context
    private Context context;

    //Instance
    private static Da da;

    //constructor
    private Da(Context context) {
        sql = new SQLite(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        this.context = context;
    }

    public static Da getInstance(Context context) {
        if (da == null)
            da = new Da(context);
        return da;
    }


    //create tables
    public void createTables() {
        boolean is_databaseBuild = sharedPref.getBoolean("is_databaseBuild", false);
        Log.e("TAble", is_databaseBuild + "");
        if (!is_databaseBuild) {
            SharedPreferences.Editor editor = sharedPref.edit();

            //Category table
            sql.createTable(getStrRes(R.string.tableName_Categories), new FieldMap(
                    new Field(getStrRes(R.string.category_Name), "VARCHAR(30)"),
                    new Field(getStrRes(R.string.category_Count), "INTEGER DEFAULT 0")), null);

            createCategory(new Category("Work"));
            createCategory(new Category("Personal"));
            createCategory(new Category("Study"));

            //Task table
            sql.createTable(getStrRes(R.string.tableName_Tasks), new FieldMap(
                            new Field(getStrRes(R.string.task_text), "TEXT"),
                            new Field(getStrRes(R.string.task_isDone), "INTEGER DEFAULT 0"),
                            new Field(getStrRes(R.string.task_Date), "VARCHAR(30)"),
                            new Field(getStrRes(R.string.task_Time), "VARCHAR(30)"),
                            new Field(getStrRes(R.string.task_subTasks_Count), "INTEGER DEFAULT 0"),
                            new Field(getStrRes(R.string.task_doneSubTasks_Count), "INTEGER DEFAULT 0")),
                    new FieldMap(new Field(getStrRes(R.string.task_Category_id), "INTEGER ," +
                            " FOREIGN KEY (" + getStrRes(R.string.task_Category_id) +
                            ") REFERENCES " + getStrRes(R.string.tableName_Categories) + "(id)")));

            //subTask table
            sql.createTable(getStrRes(R.string.tableName_SubTasks), new FieldMap(
                            new Field(getStrRes(R.string.subTask_name), "VARCHAR(30)"),
                            new Field(getStrRes(R.string.subTask_isDone), "INTEGER DEFAULT 0")),
                    new FieldMap(new Field(getStrRes(R.string.subTask_Task_id), "INTEGER ," +
                            " FOREIGN KEY (" + getStrRes(R.string.subTask_Task_id) +
                            ") REFERENCES " + getStrRes(R.string.tableName_Tasks) + "(id)")));

            editor.putBoolean("is_databaseBuild", true);
            editor.apply();

        }
    }

    //create a Category
    public String createCategory(Category category) {
        return sql.insert(getStrRes(R.string.tableName_Categories), new FieldMap(
                new Field(getStrRes(R.string.category_Name), category.getName()),
                new Field(getStrRes(R.string.category_Count), "0")));
    }

    //edit a Category
    public void editCategoryName(Category category, String newName) {
        sql.update(getStrRes(R.string.tableName_Categories),
                new Field("id", category.getId()), new Field(getStrRes(R.string.category_Name), newName)
        );
    }

    //a task add or deleted that had this category
    public void editCategoryCount(Category category, int newCount) {
        sql.update(getStrRes(R.string.tableName_Categories),
                new Field("id", category.getId()), new Field(getStrRes(R.string.category_Count), newCount + "")
        );
    }

    //delete Category
    public void deleteCategory(Category category) {
        sql.delete(getStrRes(R.string.tableName_Categories),
                new Field("id", category.getId()));
    }

    // get all Categories
    public List<Category> getAllCategories() {
        Cursor cursor = sql.select(getStrRes(R.string.tableName_Categories));
        List<Category> result = new ArrayList<Category>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(
                    cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(getStrRes(R.string.category_Name)));
            int count = cursor.getInt(
                    cursor.getColumnIndexOrThrow(getStrRes(R.string.category_Count)));
            result.add(new Category(id, name, count));
        }
        return result;
    }

    // get count of Categories
    public int getCategoriesCount() {
        return sql.getCount(getStrRes(R.string.tableName_Categories));
    }

    //get category bu passing Id
    public Category getCategoryById(String catId) {
        Cursor cursor = sql.select(getStrRes(R.string.tableName_Categories), new Field("id", catId), false);
        Category category = null;
        if (cursor != null)
            while (cursor.moveToNext()) {
                String id = cursor.getString(
                        cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.category_Name)));
                int count = cursor.getInt(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.category_Count)));
                category = new Category(id, name, count);
            }
        return category;
    }

    //get Category's tasks
    public List<Task> getCategoryTasks(Category category) {
        Cursor cursor = sql.select(getStrRes(R.string.tableName_Tasks),
                new Field(getStrRes(R.string.task_Category_id), category.getId()), false);
        List<Task> result = new ArrayList<Task>();
        if (cursor != null)
            while (cursor.moveToNext()) {
                String id = cursor.getString(
                        cursor.getColumnIndexOrThrow("id"));
                String text = cursor.getString(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_text)));
                boolean isDone = cursor.getInt(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_isDone))) == 1;
                String categoryId = cursor.getString(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_Category_id)));
                int doneSubCount = cursor.getInt(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_doneSubTasks_Count)));
                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_Date)));
                String time = cursor.getString(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_Time)));

                Cursor subcursor = sql.select(getStrRes(R.string.tableName_SubTasks),
                        new Field(getStrRes(R.string.subTask_Task_id), id), false);
                List<SubTask> subs = new ArrayList<SubTask>();
                while (subcursor.moveToNext()) {
                    String s_id = subcursor.getString(
                            subcursor.getColumnIndexOrThrow("id"));
                    String s_name = subcursor.getString(
                            subcursor.getColumnIndexOrThrow(getStrRes(R.string.subTask_name)));
                    boolean s_isDone = subcursor.getInt(
                            subcursor.getColumnIndexOrThrow(getStrRes(R.string.subTask_isDone))) == 1;
                    String s_taskId = subcursor.getString(
                            subcursor.getColumnIndexOrThrow(getStrRes(R.string.subTask_Task_id)));
                    subs.add(new SubTask(s_id, s_name, s_isDone, s_taskId));
                }
                result.add(new Task(id, text, isDone, categoryId, date, time, subs, doneSubCount));
            }
        return result;
    }

    // create Task
    public List<String> createTask(Task task) {
        List<String> ids = new ArrayList<>();
        String id = sql.insert(getStrRes(R.string.tableName_Tasks), new FieldMap(
                new Field(getStrRes(R.string.task_text), task.getTaskText()),
                new Field(getStrRes(R.string.task_Date), task.getDate()),
                new Field(getStrRes(R.string.task_Time), task.getTime()),
                new Field(getStrRes(R.string.task_subTasks_Count), task.getSubtasks_count() + ""),
                new Field(getStrRes(R.string.task_doneSubTasks_Count), "0"),
                new Field(getStrRes(R.string.task_Category_id), task.getCategory_id())));
        ids.add(id);
        for (SubTask s :
                task.getSubtasks()) {
            ids.add(sql.insert(getStrRes(R.string.tableName_SubTasks), new FieldMap(
                    new Field(getStrRes(R.string.subTask_name), s.getText()),
                    new Field(getStrRes(R.string.subTask_Task_id), id))));
        }
        sql.incrementBy(1, getStrRes(R.string.category_Count),
                getStrRes(R.string.tableName_Categories),
                new Field("id", task.getCategory_id()));
        return ids;
    }

    private List<Task> getTaskFilterBy(Field filter1, boolean is1_forNot, Field filter2, boolean is2_forNot, boolean is_and) {
        Cursor cursor = sql.select(getStrRes(R.string.tableName_Tasks), filter1, is1_forNot, filter2, is2_forNot, is_and);
        List<Task> result = new ArrayList<Task>();
        if (cursor != null)
            while (cursor.moveToNext()) {
                String id = cursor.getString(
                        cursor.getColumnIndexOrThrow("id"));
                String text = cursor.getString(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_text)));
                boolean isDone = cursor.getInt(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_isDone))) == 1;
                String categoryId = cursor.getString(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_Category_id)));
                int doneSubCount = cursor.getInt(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_doneSubTasks_Count)));
                String date = cursor.getString(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_Date)));
                String time = cursor.getString(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_Time)));

                Cursor subcursor = sql.select(getStrRes(R.string.tableName_SubTasks),
                        new Field(getStrRes(R.string.subTask_Task_id), id), false);
                List<SubTask> subs = new ArrayList<SubTask>();
                while (subcursor.moveToNext()) {
                    String s_id = subcursor.getString(
                            subcursor.getColumnIndexOrThrow("id"));
                    String s_name = subcursor.getString(
                            subcursor.getColumnIndexOrThrow(getStrRes(R.string.subTask_name)));
                    boolean s_isDone = subcursor.getInt(
                            subcursor.getColumnIndexOrThrow(getStrRes(R.string.subTask_isDone))) == 1;
                    String s_taskId = subcursor.getString(
                            subcursor.getColumnIndexOrThrow(getStrRes(R.string.subTask_Task_id)));
                    subs.add(new SubTask(s_id, s_name, s_isDone, s_taskId));
                }
                result.add(new Task(id, text, isDone, categoryId, date, time, subs, doneSubCount));
            }
        return result;
    }

    private List<Task> getTaskFilterBy(Field filter, boolean is_forNot) {
        return getTaskFilterBy(filter, is_forNot, null, false, false);
    }

    //get Done tasks
    public List<Task> getDoneTasks() {
        return getTaskFilterBy(new Field(getStrRes(R.string.task_isDone), "1"), false);
    }

    //get Task by Date
    public List<Task> getTaskByDate(String inDate1, boolean isDate1_forNot, String inDate2, boolean isDate2_forNot, boolean is_and) {
        return getTaskFilterBy(new Field(getStrRes(R.string.task_Date), inDate1), isDate1_forNot, new Field(getStrRes(R.string.task_Date), inDate2), isDate2_forNot, is_and);
    }

    public List<Task> getTaskByDate(String inDate, boolean is_forNot) {
        return getTaskFilterBy(new Field(getStrRes(R.string.task_Date), inDate), is_forNot);
    }

    public List<Task> getTaskByDate(String inDate) {
        return getTaskFilterBy(new Field(getStrRes(R.string.task_Date), inDate), false);
    }

    // get count of done Tasks;
    public int getDoneCount() {
        return sql.getCount(getStrRes(R.string.tableName_Tasks),
                new Field(getStrRes(R.string.task_isDone), "1"), false);
    }

    public void doneTask(Task task) {
        sql.update(getStrRes(R.string.tableName_Tasks),
                new Field("id", task.getId()),
                new Field(getStrRes(R.string.task_isDone), "1")
        );
    }

    public void unDoneTask(Task task) {
        sql.update(getStrRes(R.string.tableName_Tasks),
                new Field("id", task.getId()),
                new Field(getStrRes(R.string.task_isDone), "0")
        );
    }

    public boolean doneSubTask(SubTask subTask) {
        sql.update(getStrRes(R.string.tableName_SubTasks),
                new Field("id", subTask.getId()), new Field(getStrRes(R.string.subTask_isDone), "1")
        );
        sql.incrementBy(1, getStrRes(R.string.task_doneSubTasks_Count),
                getStrRes(R.string.tableName_Tasks),
                new Field("id", subTask.getTask_id()));
        if (checkAllDone(subTask.getTask_id())){
            return true;
        }
        return false;
    }

    private boolean checkAllDone(String taskId) {
        List<String> projection = new ArrayList<>();
        projection.add(getStrRes(R.string.task_subTasks_Count));
        projection.add(getStrRes(R.string.task_doneSubTasks_Count));
        Cursor cursor = sql.select(getStrRes(R.string.tableName_Tasks), projection,
                new Field("id", taskId), false);
        if (cursor != null)
            while (cursor.moveToNext()) {
                int subC = cursor.getInt(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_subTasks_Count)));
                int subDC = cursor.getInt(
                        cursor.getColumnIndexOrThrow(getStrRes(R.string.task_doneSubTasks_Count)));
                if (subDC==subC)
                    return true;
            }
        return false;
    }

    public boolean ubDoneSubTask(SubTask subTask) {
        sql.update(getStrRes(R.string.tableName_SubTasks),
                new Field("id", subTask.getId()), new Field(getStrRes(R.string.subTask_isDone), "0")
        );
        sql.incrementBy(-1, getStrRes(R.string.task_doneSubTasks_Count),
                getStrRes(R.string.tableName_Tasks),
                new Field("id", subTask.getTask_id()));
        if (!checkAllDone(subTask.getTask_id())){
            return true;
        }
        return false;
    }

    public void deleteTask(Task task) {
        sql.delete(getStrRes(R.string.tableName_Tasks),
                new Field("id",task.getId()));
        sql.delete(getStrRes(R.string.tableName_SubTasks),
                new Field(getStrRes(R.string.subTask_Task_id),task.getId()));
        sql.incrementBy(-1, getStrRes(R.string.category_Count),
                getStrRes(R.string.tableName_Categories),
                new Field("id", task.getCategory_id()));
    }

    public void deleteSubtask(SubTask subTask) {
        sql.delete(getStrRes(R.string.tableName_SubTasks),
                new Field("id",subTask.getId()));
    }

    public List<String> updateTask(Task task) {
        List<String> ids = new ArrayList<>();
        sql.update(getStrRes(R.string.tableName_Tasks),
                new Field("id",task.getId()),
                new Field(getStrRes(R.string.task_text),task.getTaskText()),
                new Field(getStrRes(R.string.task_isDone),task.isDone()?"1":"0"),
                new Field(getStrRes(R.string.task_Category_id),task.getCategory_id()),
                new Field(getStrRes(R.string.task_Date),task.getDate()),
                new Field(getStrRes(R.string.task_Time),task.getTime()),
                new Field(getStrRes(R.string.task_subTasks_Count),task.getSubtasks_count()+""),
                new Field(getStrRes(R.string.task_doneSubTasks_Count),task.getDoneSubtasks_count()+"")
        );
        ids.add(task.getId());
        for (SubTask s :
                task.getSubtasks()) {
            ids.add(sql.insert(getStrRes(R.string.tableName_SubTasks), new FieldMap(
                    new Field(getStrRes(R.string.subTask_name), s.getText()),
                    new Field(getStrRes(R.string.subTask_isDone), s.isDone()?"1":"0"),
                    new Field(getStrRes(R.string.subTask_Task_id), ids.get(0)))));
        }
        return ids;
    }

    // get string
    private String getStrRes(int res) {
        return context.getString(res);
    }


}
