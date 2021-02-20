package com.example.soa1_1;

import com.github.underscore.lodash.U;
import org.javalite.activejdbc.LazyList;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

public class ProductManager {
    static final ArrayList<String> REAL_PRODUCT_FIELDS = new ArrayList<>(Arrays.asList("name", "price", "manufactureCost", "unitOfMeasure",
            "owner_id", "coordinate_id"));
    static final ArrayList<String> REAL_COORDINATE_FIELDS = new ArrayList<>(Arrays.asList("x", "y"));
    static final ArrayList<String> REAL_OWNER_FIELDS = new ArrayList<>(Arrays.asList("eyeColor", "hairColor", "nationality", "location_id"));
    static final Map<String, String> CLIENT_OWNER_FIELDS = new HashMap<String, String>() {{
        put("owner_x", "location_x");
        put("owner_y", "location_y");
        put("location_name", "location_name");
        put("owner_name", "name");
    }};
//    static final ArrayList<String> REAL_LOCATION_FIELDS = new ArrayList<>(Arrays.asList("x", "y", "name"));

    static final Map<String, String> CLIENT_LOCATION_FIELDS = new HashMap<String, String>() {{
        put("location_x", "x");
        put("location_y", "y");
        put("location_name", "name");
    }};

    public static Product makeProductFromParams(Map<String, String[]> parameters)
            throws ParseException, NumberFormatException {
        String name = parameters.get("name")[0];
        Date creationDate = new Date(System.currentTimeMillis());
        Double x = Double.parseDouble(parameters.get("x")[0]);
        double y = Double.parseDouble(parameters.get("y")[0]);
        Coordinate cord = new Coordinate(x, y);
        Product p1 = new Product(name, creationDate);
        Person ow = new Person();
        p1.saveIt();

        final boolean[] makeParent = {false};
        for (String field: REAL_OWNER_FIELDS) {
            if(parameters.get(field) != null)
                makeParent[0] = true;
        }

        CLIENT_OWNER_FIELDS.forEach((k, v) -> {
            if(parameters.get(k) != null)
                makeParent[0] = true;
        });

        if (makeParent[0]) {
//            ow = ;
            ow.saveIt();
            ow.add(p1);
        }


        cord.saveIt();
        cord.add(p1);
        Product np = (Product) Product.where("id = ?", p1.getId()).include(Person.class, Coordinate.class).orderBy("id").get(0);
        updateProduct(np, parameters);
        return np;
    }
    /*
    public static Product makeProductFromParams(Map<String, String[]> parameters)
            throws ParseException, NumberFormatException {
        String name = parameters.get("name")[0];
        Double x = Double.parseDouble(parameters.get("x")[0]);
        double y = Double.parseDouble(parameters.get("y")[0]);
        Date creationDate = new Date(System.currentTimeMillis());
        Long price = (parameters.get("price") == null) ? null
                : Long.parseLong(parameters.get("price")[0]);
        Integer manufactureCost = (parameters.get("manufactureCost") == null) ? null
                : Integer.parseInt(parameters.get("manufactureCost")[0]);
        String unitOfMeasure = parameters.get("unitOfMeasure") == null ? null : parameters.get("unitOfMeasure")[0];

        Coordinate cord = new Coordinate(x, y);
        Product p1 = new Product(name, creationDate);
        p1.set("price", price);
        p1.set("manufactureCost", manufactureCost);
        p1.set("unitOfMeasure", unitOfMeasure);
        Person ow;
        if (parameters.get("owner_name") != null && parameters.get("hairColor") != null
                && parameters.get("nationality") != null && parameters.get("location") != null) {
            String owner_name = parameters.get("owner_name") == null ? null : parameters.get("owner_name")[0];
            String passportID = parameters.get("passportID") == null ? null : parameters.get("passportID")[0];
            String eyeColor = (parameters.get("eyeColor") == null) ? null : parameters.get("eyeColor")[0];
            String hairColor = parameters.get("hairColor") == null ? null : parameters.get("hairColor")[0];
            String nationality = parameters.get("nationality") == null ? null : parameters.get("nationality")[0];
            ow = new Person(owner_name, (eyeColor == null) ? null : Color.valueOf(eyeColor));
            ow.set("passportID", passportID);
            ow.set("hairColor", hairColor);
            ow.set("nationality", nationality);
            ow.saveIt();
            ow.add(p1);
        }

        p1.saveIt();
        cord.saveIt();
        cord.add(p1);
//        List<Person> persons = Person.where("id = ?", p1.getId()).include(Coordinate.class).include(Product.class).orderBy("id");
        return p1;
    }
    */

    public static LazyList<Product> getAllWorkers(Map<String, String[]> parameters) {
        int limit = parameters.get("pageSize") == null ? 100 : Integer.parseInt(parameters.get("pageSize")[0]);
        int offset = parameters.get("pageNumber") == null ? 0 : Integer.parseInt(parameters.get("pageNumber")[0])*limit;
        String orderBy = parameters.get("sortFields") == null ? "id" : parameters.get("sortFields")[0];
        String str[] = orderBy.split(",");
//        TODO check orderBy, make x -> owner.x
//        return null
//        ? location name
        return Product.findAll().include(Person.class, Coordinate.class).limit(limit).offset(offset).orderBy(orderBy);
    }

    public static Product getWorkerById(long id) {
        LazyList<Product> products = Product.where("id = ?", id).include(Person.class, Coordinate.class).orderBy("id");
        return products.size() == 0? null : products.get(0);
    }

    public static String toXml(Collection collection) {
        return U.toXml(collection);
    }
    public static String toXml(Product p) {
        return U.toXml(p.toMap());
    }

    public static void updateProduct(Product product, Map<String, String[]> parameters) throws ParseException, NumberFormatException {
//        String name = parameters.get("name")[0];
//        Double x = Double.parseDouble(parameters.get("x")[0]);
//        double y = Double.parseDouble(parameters.get("y")[0]);
//        Date creationDate = new Date(System.currentTimeMillis());
//        Long price = (parameters.get("price") == null) ? null
//                : Long.parseLong(parameters.get("price")[0]);
//        Integer manufactureCost = (parameters.get("manufactureCost") == null) ? null
//                : Integer.parseInt(parameters.get("manufactureCost")[0]);
//        String unitOfMeasure = parameters.get("unitOfMeasure") == null ? null : parameters.get("unitOfMeasure")[0];

        for (String field: REAL_PRODUCT_FIELDS) {
            if(parameters.get(field) != null)
                product.set(field, parameters.get(field)[0]);
        }

        for (String field: REAL_COORDINATE_FIELDS) {
            if(parameters.get(field) != null)
                product.parent(Coordinate.class).set(field, parameters.get(field)[0]);
        }

        for (String field: REAL_OWNER_FIELDS) {
            if(parameters.get(field) != null)
                product.parent(Person.class).set(field, parameters.get(field)[0]);
        }

        CLIENT_OWNER_FIELDS.forEach((k, v) -> {
            if(parameters.get(k) != null)
                product.parent(Person.class).set(v, parameters.get(k)[0]);
        });

        product.saveIt();
        product.parent(Coordinate.class).saveIt();
        product.parent(Person.class).saveIt();

//        product.parent(Person.class).saveIt();

//        if(parameters.get("x") != null)
//            product.set("x", parameters.get("x")[0]);
//
//        if(parameters.get("x") != null)
//            product.set("x", parameters.get("x")[0]);

//        Coordinate cord = new Coordinate(x, y);
//        Product p1 = new Product(name, creationDate);
//        p1.set("price", price);
//        p1.set("manufactureCost", manufactureCost);
//        p1.set("unitOfMeasure", unitOfMeasure);
//        Person ow;
//        if (parameters.get("owner_name") != null && parameters.get("hairColor") != null
//                && parameters.get("nationality") != null && parameters.get("location") != null) {
//            String owner_name = parameters.get("owner_name") == null ? null : parameters.get("owner_name")[0];
//            String passportID = parameters.get("passportID") == null ? null : parameters.get("passportID")[0];
//            String eyeColor = (parameters.get("eyeColor") == null) ? null : parameters.get("eyeColor")[0];
//            String hairColor = parameters.get("hairColor") == null ? null : parameters.get("hairColor")[0];
//            String nationality = parameters.get("nationality") == null ? null : parameters.get("nationality")[0];
//            ow = new Person(owner_name, (eyeColor == null) ? null : Color.valueOf(eyeColor));
//            ow.set("passportID", passportID);
//            ow.set("hairColor", hairColor);
//            ow.set("nationality", nationality);
//            ow.saveIt();
//            ow.add(p1);
//        }
//
//        p1.saveIt();
//        cord.saveIt();
//        cord.add(p1);
//        List<Person> persons = Person.where("id = ?", p1.getId()).include(Coordinate.class).include(Product.class).orderBy("id");
//        return p1;
    }

    public static LazyList<Product> getWorkersWithPrefix(String prefix) {
        LazyList<Product> products = Product.where("name LIKE ?", prefix + "%").include(Person.class, Coordinate.class).orderBy("id");
        return products.size() == 0? null : products;
    }
}
