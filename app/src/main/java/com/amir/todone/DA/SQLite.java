package com.amir.todone.DA;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.amir.todone.Objects.Field;
import com.amir.todone.Objects.FieldMap;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SQLite {

    //base obj
    SQLiteDatabase myDataBase;

    //constructor
    public SQLite(Context context) {
        myDataBase = context.openOrCreateDatabase("database.db", 0, null);
    }

    //create
    public void createTable(String tableName, FieldMap columns, FieldMap foreignIds) {
        createTable(tableName, columns, foreignIds, true);
    }

    public void createTable(String tableName, FieldMap columns, FieldMap foreignIds, boolean isIdUnique) {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(tableName).append(" ( ");
        if (isIdUnique)
            sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,");
        else
            sb.append("id INTEGER,");
        Set<String> columnKeys = columns.keySet();
        for (String key : columnKeys)
            sb.append(key).append(" ").append(columns.get(key)).append(",");
        if (foreignIds != null) {
            Set<String> foreignKeys = foreignIds.keySet();
            for (String key : foreignKeys)
                sb.append(key).append(" ").append(foreignIds.get(key)).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        Log.e("Q : ", sb.toString());
        myDataBase.execSQL(sb.toString());
    }

    //insert
    public String insert(String tableName, FieldMap data) {
        StringBuilder sb = new StringBuilder("INSERT INTO " + tableName + " ( ");
        Set<String> keys = data.keySet();
        for (String key : keys) {
            sb.append(key).append(", ");
        }
        sb.deleteCharAt(sb.length() - 2);
        sb.append(" ) VALUES ( ");
        Collection<String> values = data.values();
        for (String val : values)
            sb.append("'").append(val.replace("'", "''")).append("', ");
        sb.deleteCharAt(sb.length() - 2);
        sb.append(")");
        myDataBase.execSQL(sb.toString());
        Cursor c = myDataBase.rawQuery("select last_insert_rowid()", null);
        c.moveToNext();
        return c.getString(c.getColumnIndexOrThrow("last_insert_rowid()"));
    }

    //update
    public void update(String tableName, Field filter, Field... data) {
        StringBuilder sb = new StringBuilder("UPDATE " + tableName + " SET " );
        String s = "UPDATE " + tableName + " SET "
                + data[0].getKey() + " = '" + data[0].getValue() + "'" +

                " WHERE " + filter.getKey() + " = '" + filter.getValue() + "'";
        for (Field d :
                data) {
           sb.append(d.getKey()).append(" = '").append(d.getValue()).append("',");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(" WHERE ").append(filter.getKey()).append(" = '").append(filter.getValue()).append( "'");
        myDataBase.execSQL(sb.toString());
    }

    //increment by num
    public void incrementBy(int increment, String column, String tableName, Field filter) {
        String sb = "UPDATE " + tableName + " SET " + column + " = " + column + ((increment<0)?" - " +(-1*increment):" + "+ + increment)+
                " WHERE " + filter.getKey() + " = '" + filter.getValue() + "'";
        myDataBase.execSQL(sb);
    }

    //select
    public Cursor select(String tableName, List<String> projection, Field filter1, boolean is1_forNot, Field filter2, boolean is2_forNot, boolean is_and, String order, String limit) {
        StringBuilder sb = new StringBuilder();
        if (projection != null && projection.size() != 0) {
            for (String a :
                    projection) {
                sb.append(a).append(", ");
            }
            sb.deleteCharAt(sb.length() - 2);
        } else {
            sb.append("*");
        }
        sb = new StringBuilder("SELECT " + sb.toString() + " FROM " + tableName);

        if (filter1 != null) {
            sb.append(" WHERE ").append(filter1.getKey());
            if (is1_forNot) sb.append(" NOT LIKE '");else sb.append(" LIKE '");
            sb.append(filter1.getValue()).append("' ");
        }
        if (filter1 != null && filter2 != null)
            if (is_and) sb.append(" AND ");
            else sb.append(" OR ");
        if (filter2 != null) {
            sb.append(filter2.getKey());
            if (is2_forNot) sb.append(" NOT LIKE '");else sb.append(" LIKE '");
            sb.append(filter2.getValue()).append("' ");
        }
        if (order != null)
            sb.append(" ORDER BY ").append(order);

        if (limit != null)
            sb.append(" LIMIT ").append(limit);

        Cursor cursor;
        try {
            cursor = myDataBase.rawQuery(sb.toString(), null);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return null;
        }
        Log.e("SELECT", sb.toString());
        return cursor;
    }
    public Cursor select(String tableName, List<String> projection, Field filter1, boolean is1_forNot, Field filter2, boolean is2_forNot, boolean is_and, String order) {
        return select(tableName, projection, filter1, is1_forNot, filter2, is2_forNot, is_and, order, null);
    }
    public Cursor select(String tableName, List<String> projection, Field filter1, boolean is1_forNot, Field filter2, boolean is2_forNot, boolean is_and) {
        return select(tableName, projection, filter1, is1_forNot, filter2, is2_forNot, is_and, null);
    }
    public Cursor select(String tableName, Field filter1, boolean is1_forNot, Field filter2, boolean is2_forNot, boolean is_and) {
        return select(tableName, null, filter1, is1_forNot, filter2, is2_forNot, is_and, null);
    }
    public Cursor select(String tableName, List<String> projection, Field filter, boolean is_forNot){
        return select(tableName, projection,filter, is_forNot, null, false, false);
    }
    public Cursor select(String tableName, Field filter, boolean is_forNot){
        return select(tableName, filter, is_forNot, null, false, false);
    }
    public Cursor select(String tableName){
        return select(tableName, null, null, false, null, false, false, null, null);
    }

    //get count
    public int getCount(String tableName, Field filter , boolean is_fotNot) {
        Cursor cursor = select(tableName, filter,is_fotNot);

        return cursor != null ? cursor.getCount() : 0;
    }
    public int getCount(String tableName) {

        return getCount(tableName,null,false);
    }
    //delete
    public void delete(String tableName, Field filter) {
        myDataBase.execSQL("DELETE FROM " + tableName + " WHERE " + filter.getKey() + "= '" + filter.getValue() + "'");
    }

    //truncate
    public void truncate(String tableName) {
        myDataBase.execSQL("DELETE FROM " + tableName);
    }

    //drop
    public void drop(String tableName) {
        myDataBase.execSQL("DROP TABLE" + tableName);
    }
}
