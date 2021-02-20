package com.example.soa1_1;

import com.github.underscore.lodash.U;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.connection_config.DBConfiguration;
import org.javalite.common.Convert;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/product/*")
public class ProductServlet extends MyServlet {
    private static final String SERVLET_PATH_PRODUCT = "/";
//    private static final String SERVLET_PATH_LESS_MANUFACTURE_COST = "/less_manufacture_cost";
//    private static final String SERVLET_PATH_GREATER_MANUFACTURE_COST = "/greater_manufacture_cost";
//    private static final String SERVLET_PATH_GREATER_PRICE = "/greater_price";

    private static final String SERVLET_PATH_SUM_PRICE = "/sum_price";
    private static final String SERVLET_PATH_AVG_MANUFACTURE_COST = "/avg_manufacture_cost";
    private static final String SERVLET_PATH_NAME_STARTS = "/name_starts";

    static final ArrayList<String> PRODUCT_FIELDS = new ArrayList<>(Arrays.asList("name", "x", "y", "price", "manufactureCost", "unitOfMeasure",
            "owner_x", "owner_y", "owner_name", "eyeColor", "hairColor", "nationality"));
    static final ArrayList<String> PRODUCT_FIELDS_REQUIRED = new ArrayList<>(Arrays.asList("name", "x", "y", "price"));
    static final ArrayList<String> PRODUCT_FIELDS_WITH_ID_AND_CREATION_DATE =
            new ArrayList<>(Arrays.asList("id", "name", "x", "y", "creationDate"));

    private String getPath(HttpServletRequest request) {
        String path = request.getPathInfo();
        return (path == null) ? "/" : path;
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String path = getPath(request);
        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();
        if (checkUrlOnNumber(path)) {
            try {
                long id = Long.parseLong(path.substring(1));
                Product product = ProductManager.getWorkerById(id);
                if (product == null) {
                    response.sendError(404); // no content
                } else {
                    if (hasRedundantParameters(request.getParameterMap().keySet()) ||
                            !validatePostPutFields(request.getParameterMap())) {
                        response.sendError(422);
                    } else {
                        ProductManager.updateProduct(product, request.getParameterMap());
                        out.println(ProductManager.toXml(product));
                    }
            }
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                response.sendError(422, e.getMessage());
            }
        } else {
            response.sendError(400); // bad request
        }
        Base.close();
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = getPath(request);
        PrintWriter out = response.getWriter();
        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();
        if (path.equals(SERVLET_PATH_PRODUCT)) {
            response.setContentType("text/xml;charset=UTF-8");
            try {
                if (hasRedundantParameters(request.getParameterMap().keySet()) ||
                        !hasAllRequiredParameters(request.getParameterMap().keySet()) ||
                        !validatePostPutFields(request.getParameterMap())) {
                    response.sendError(422);
                } else {
                    Product product = ProductManager.makeProductFromParams(request.getParameterMap());
                    out.println(ProductManager.toXml(product));
                }
            } catch (NumberFormatException | ParseException e) {
                System.out.println(e.getMessage());
                response.sendError(422, e.getMessage());
            }
//            catch (Exception e) {
//                System.out.println(e.getMessage());
//                response.sendError(500, e.getMessage());
//            }
        } else {
            response.sendError(400);
        }
        Base.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String path = getPath(request);
        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();
        if (path.equals(SERVLET_PATH_SUM_PRICE)) {
            LazyList<Product> products = ProductManager.getAllWorkers(request.getParameterMap());
            int sum = 0;
            for (Product i : products)
                if (i.get("price") != null)
                    sum = sum + Convert.toInteger(i.get("price"));

            out.println(ProductManager.toXml(Collections.singleton(sum)));
        } else if (path.equals(SERVLET_PATH_AVG_MANUFACTURE_COST)) {
            LazyList<Product> products = ProductManager.getAllWorkers(request.getParameterMap());
            double sum = 0;
            for (Product i : products)
                if(i.get("manufactureCost") != null)
                    sum = sum + Convert.toDouble(i.get("manufactureCost"));

            out.println(ProductManager.toXml(Collections.singleton(sum/products.size())));
        } else if (checkUrlOnPrefixForNameStarts(path)) {
            String prefix = path.substring(1 + SERVLET_PATH_NAME_STARTS.length());
//            String prefix = request.getParameterMap().get("prefix") == null ? "" : request.getParameterMap().get("prefix")[0];
            LazyList<Product> products = ProductManager.getWorkersWithPrefix(prefix);
//            System.out.println(products.get(0));
            if (products == null) {
                response.sendError(404); // no content
            } else {
                out.println(ProductManager.toXml(products.toMaps()));
            }
        } else {
            response.sendError(400); // bad request
        }
        Base.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String path = getPath(request);
        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();
        if (path.equals(SERVLET_PATH_PRODUCT)) {
            LazyList<Product> products = ProductManager.getAllWorkers(request.getParameterMap());
            if (products == null) {
                response.sendError(400); // bad request
            } else if (products.isEmpty()) {
                response.sendError(404);
            } else {
                out.println(ProductManager.toXml(products.toMaps()));
            }
        } else if (checkUrlOnNumber(path)) {
            long id = Long.parseLong(path.substring(1));
            Product product = ProductManager.getWorkerById(id);
            if (product == null) {
                response.sendError(404); // no content
            } else {
                out.println(ProductManager.toXml(product));
            }
        } else {
            response.sendError(400); // bad request
        }
        Base.close();
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String path = getPath(request);
        DBConfiguration.loadConfiguration("/database.properties");
        Base.open();
        if (checkUrlOnNumber(path)) {
            long id = Long.parseLong(path.substring(1));
            Product product = ProductManager.getWorkerById(id);
            if (product == null) {
                response.sendError(404); // no content
            } else {
                product.deleteCascade();
//                out.println();
            }
        } else {
            response.sendError(400); // bad request
        }
        Base.close();
    }

    private static boolean hasRedundantParameters(Set<String> params) {
        return params.stream().anyMatch(x -> PRODUCT_FIELDS.stream()
                .noneMatch(x::equals));
    }

    private static boolean hasAllRequiredParameters(Set<String> params) {
        return PRODUCT_FIELDS_REQUIRED.stream().filter(params::contains).count() == PRODUCT_FIELDS_REQUIRED.size();
    }

    private static boolean hasRedundantFields(String fields) {
        return Arrays.stream(fields.split(","))
                .anyMatch(x -> PRODUCT_FIELDS_WITH_ID_AND_CREATION_DATE.stream()
                        .noneMatch(x::equals)) && !fields.isEmpty();
    }

    private static boolean checkUrlOnNumber(String url){
        Pattern p = Pattern.compile("^" + SERVLET_PATH_PRODUCT + "\\d+$");
        Matcher m = p.matcher(url);
        return m.matches();
    }

    private static boolean checkUrlOnPrefixForNameStarts(String url){
        Pattern p = Pattern.compile("^" + SERVLET_PATH_NAME_STARTS + "/.*$");
        Matcher m = p.matcher(url);
        return m.matches();
    }

    private static boolean validatePostPutFields(Map<String, String[]> params){
//        return true;
        try {
            String name = params.get("name")[0];
            Double x = (params.get("x") == null) ? null : Double.parseDouble(params.get("x")[0]);
            Double y = (params.get("y") == null) ? null : Double.parseDouble(params.get("y")[0]);

            Long price = (params.get("price") == null) ? null : Long.parseLong(params.get("price")[0]);
            UnitOfMeasure unitOfMeasure = (params.get("unitOfMeasure") == null) ? null : UnitOfMeasure.valueOf(params.get("unitOfMeasure")[0]);

            boolean res = name != null;

            res = res && !name.equals("") && (price == null || price > 0);
            String owner_name = params.get("owner_name") == null ? "" : params.get("owner_name")[0];
            String eyeColorString = (params.get("eyeColor") == null) ? null : params.get("eyeColor")[0];
            Color eyeColor = (eyeColorString == null) ? null : Color.valueOf(eyeColorString);
            Color hairColor = params.get("hairColor") == null ? null : Color.valueOf(params.get("hairColor")[0]);
            Country nationality = params.get("nationality") == null ? null : Country.valueOf(params.get("nationality")[0]);
            res = res && !owner_name.equals("");

            Long loc_x = (params.get("owner_x") == null) ? null : Long.parseLong(params.get("owner_x")[0]);
            Double loc_y = (params.get("owner_y") == null) ? null : Double.parseDouble(params.get("owner_y")[0]);

            return res;
        } catch (Exception e) {
            return false;
        }
    }
}
