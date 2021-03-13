package com.example.soa1_1;

import com.github.underscore.lodash.U;
import org.javalite.activejdbc.LazyList;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProductManager {
    static final ArrayList<String> REAL_PRODUCT_FIELDS = new ArrayList<>(Arrays.asList("name", "price", "manufactureCost", "unitOfMeasure",
            "owner_id", "coordinate_id"));
    static final ArrayList<String> REAL_COORDINATE_FIELDS = new ArrayList<>(Arrays.asList("x", "y"));
    static final ArrayList<String> REAL_OWNER_FIELDS = new ArrayList<>(Arrays.asList("eyeColor", "hairColor", "nationality", "location_id"));
    static final Map<String, String> CLIENT_OWNER_FIELDS = new HashMap<String, String>() {{
        put("owner_x", "location_x");
        put("owner_y", "location_y");
        put("location_name", "location_name");
        put("owner_name", "owner_name");
    }};

    public static Product makeProductFromParams(Map<String, String[]> parameters)
            throws ParseException, NumberFormatException {
        String name = parameters.get("name")[0];
        LocalDate creationDate = (new Date(System.currentTimeMillis())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Double x = Double.parseDouble(parameters.get("x")[0]);
        double y = Double.parseDouble(parameters.get("y")[0]);
        Coordinate cord = new Coordinate(x, y);
        Product p1 = new Product(name, creationDate);
        Person ow = new Person();
        p1.saveIt();

//        final boolean[] makeParent = {false};
//        for (String field: REAL_OWNER_FIELDS) {
//            if(parameters.get(field) != null)
//                makeParent[0] = true;
//        }

//        CLIENT_OWNER_FIELDS.forEach((k, v) -> {
//            if(parameters.get(k) != null)
//                makeParent[0] = true;
//        });
//
//        if (makeParent[0]) {
            ow.saveIt();
            ow.add(p1);
//        }


        cord.saveIt();
        cord.add(p1);
        Product np = (Product) Product.where("id = ?", p1.getId()).include(Person.class, Coordinate.class).orderBy("id").get(0);
        updateProduct(np, parameters);
        return np;
    }

    public static LazyList<Product> getAllWorkers(Map<String, String[]> parameters) {
        int limit = parameters.get("pageSize") == null ? 100 : Integer.parseInt(parameters.get("pageSize")[0]); // TODO TRY
        int offset = parameters.get("pageNumber") == null ? 0 : Integer.parseInt(parameters.get("pageNumber")[0])*limit;
        String orderBy = parameters.get("sortFields") == null ? "id" : parameters.get("sortFields")[0];
        List<String> orderList = Arrays.asList(orderBy.split(","));
//        String filterStr = parameters.get("filters") == null ? "" : parameters.get("filters")[0];
//        List<String> whereList = Arrays.asList(filterStr.split(","));
//        TODO filter -> where
//        name=A,prise=3

        if (limit <= 0) {
            return null;
        }

        if (offset < 0) {
            return null;
        }

        for (String field : orderList) {
            if (field.equals("id") || field.equals("creationDate"))
                continue;
            if (!REAL_PRODUCT_FIELDS.contains(field))
                return null;
        }

        StringBuilder whereStr = new StringBuilder();
        boolean addAnd = false;
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            if (REAL_PRODUCT_FIELDS.contains(entry.getKey()) || entry.getKey().equals("creationDate")) { //TODO check
                if (addAnd) {
                    whereStr.append(" and ");
                }
                addAnd = true;
                whereStr.append(entry.getKey()).append(" = '").append(entry.getValue()[0]).append("'");
                if(entry.getValue()[0].contains("'")) {
                    return null;
                }
            }
        }
        LazyList<Product> p;

        if (whereStr.length() != 0){
            p = Product.where(String.valueOf(whereStr)).include(Person.class, Coordinate.class).limit(limit).offset(offset);
        } else {
            p = Product.findAll().include(Person.class, Coordinate.class).limit(limit).offset(offset);
        }
        return p.orderBy(orderBy);
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
    public static Map<String, String[]> paramsFromXml(String s) {
        Map<String, Object> m = U.fromXmlMap(s);
        Map<String, String[]> p = new HashMap<>();
        for (String key : m.keySet()) {
//            System.out.println(key + " = " + m.get(key));
//            System.out.println(m.get(key).getClass().getName());
            if (m.get(key).getClass().getName().equals("java.util.LinkedHashMap")) {
                p.put(key, new String[] {""});
            }
            else {
                p.put(key, new String[]{(String) m.get(key)});
            }
        }
        return p;
    }

    public static void updateProduct(Product product, Map<String, String[]> parameters) throws ParseException, NumberFormatException {
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
        if (product.get("owner") != null) {
            product.parent(Person.class).saveIt();
        }
    }

    public static LazyList<Product> getWorkersWithPrefix(String prefix) {
        LazyList<Product> products = Product.where("name LIKE ?", prefix + "%").include(Person.class, Coordinate.class).orderBy("id");
        return products;
    }
}
