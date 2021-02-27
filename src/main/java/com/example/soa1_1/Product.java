package com.example.soa1_1;


import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.BelongsToParents;
import org.javalite.activejdbc.annotations.Table;

import java.time.LocalDate;

@Table("products")
@BelongsToParents({
        @BelongsTo(foreignKeyName="owner",parent=Person.class)
})
public class Product extends Model {
    public Product(String name, LocalDate creationDate){
        set("name", name, "creationDate", creationDate);
    }

    public Product() {
    }
}