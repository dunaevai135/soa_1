package com.example.soa1_1;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.*;

//@HasMany(child = Product.class, foreignKeyName = "owner")
@Table("people")
//@IdName("id")
public class Person extends Model {
    public Person(String name, Color eyeColor) {
        set("name", name, "eyeColor", eyeColor);
    }

    public Person() {
    }

//    private String name; //Поле не может быть null, Строка не может быть пустой
//    private String passportID; //Длина строки не должна быть больше 25, Значение этого поля должно быть уникальным, Поле может быть null
//    private Color eyeColor; //Поле может быть null
//    private Color hairColor; //Поле не может быть null
//    private Country nationality; //Поле не может быть null
//    private Location location; //Поле не может быть null
}