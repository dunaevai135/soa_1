package com.example.soa1_1;

import org.javalite.activejdbc.Model;

public class Coordinate extends Model {
    public Coordinate(Double x, Double y) {
        set("y", y, "x", x);
    }

    public Coordinate() {
    }
//    private int x;
//    private double y; //Значение поля должно быть больше -575
}