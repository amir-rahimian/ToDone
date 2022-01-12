package com.amir.todone.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FieldMap extends HashMap<String, String> {

    //constructor
    public FieldMap(){super();}
    public FieldMap(Field... fields){
        put(fields);
    }

    //put params
    public FieldMap put(Field... fields){
        for (Field f:fields) {
        put(f);
        }
        return this;
    }
    //put params
    public FieldMap put(FieldMap fieldMap, Field... fields){
        for (Field f:fields) {
            put(f);
        }
        for (Field f:fieldMap.getAll()) {
            put(f);
        }
        return this;
    }

    //put one
    private FieldMap put(Field field){super.put(field.getKey(),field.getValue());return this;}

    //get
    public Field getField(String key){
        Field field = new Field(key,super.get(key));
        return field;
    }
    public List<Field> getAll(){
        Set<String> set = keySet();
        ArrayList<Field> result = new ArrayList<>();
        for (String s:set) {
            result.add(new Field(s,super.get(s)));
        }
        return result;
    }
    //toString
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        Set<String> keys = keySet();
        for (String key:keys) {
            sb.append(getField(key)).append("&");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

}
