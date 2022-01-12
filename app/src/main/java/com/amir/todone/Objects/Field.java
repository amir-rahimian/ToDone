package com.amir.todone.Objects;

public class Field {

    // fields
    private final String key ;
    private final String value ;

    //constructor
    public Field(String key,String value){
        this.key = key;
        this.value = value;
    }

    //getter
    public String getKey(){return key;}
    public String getValue(){return value;}

    //toString
    @Override
    public String toString(){return key+"="+value;}
}
