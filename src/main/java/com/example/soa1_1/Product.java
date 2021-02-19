package com.example.soa1_1;


import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.BelongsToParents;
import org.javalite.activejdbc.annotations.Table;

@Table("products")
@BelongsToParents({
        @BelongsTo(foreignKeyName="owner",parent=Person.class)
})
public class Product extends Model {
    public Product(String name, java.util.Date creationDate){
        set("name", name, "creationDate", creationDate);
    }

    public Product() {
    }
//    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
//    private String name; //Поле не может быть null, Строка не может быть пустой
//    private Coordinates coordinates; //Поле не может быть null
//    private java.util.Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
//    private Long price; //Поле может быть null, Значение поля должно быть больше 0
//    private Integer manufactureCost; //Поле может быть null
//    private UnitOfMeasure unitOfMeasure; //Поле может быть null
//    private Person owner; //Поле может быть null

}
//@BelongsTo(parent = Person.class, foreignKeyName = "person_id")
