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
}