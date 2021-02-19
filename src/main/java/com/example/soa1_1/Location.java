package com.example.soa1_1;

import org.javalite.activejdbc.Model;

public class Location extends Model {
    public Location(Double x, Integer y, String name) {
        set("y", y, "x", x, "name", name);
    }
//    private Double x; //Поле не может быть null
//    private Integer y; //Поле не может быть null
//    private String name; //Строка не может быть пустой, Поле не может быть null
}