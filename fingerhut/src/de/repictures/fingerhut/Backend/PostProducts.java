package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Product;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PostProducts extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String barcode = req.getParameter("code");
        String companyNumber = req.getParameter("companynumber");
        boolean mustBeBuyable = false;
        String mustBeBuyableStr = req.getParameter("mbb");
        if (mustBeBuyableStr != null) mustBeBuyable = Boolean.valueOf(mustBeBuyableStr);

        JsonObject responseObject = new JsonObject();

        List<Entity> products;
        if (barcode != null)
            products = Product.getProductsByCode(barcode, mustBeBuyable);
        else if (companyNumber != null){
            products = Product.getProductsByCompany(companyNumber, mustBeBuyable);
        } else {
            responseObject.addProperty("response_code", 2);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        if (products.size() < 1){
            responseObject.addProperty("response_code", 0);
        } else {
            JsonArray productArray = new JsonArray();
            for(Entity productEntity : products) {
                Product product = new Product(productEntity);
                Company companyGetter = new Company(product.getSellingCompany());

                JsonObject productObject = new JsonObject();

                productObject.addProperty("name", product.getName());
                productObject.addProperty("company_name", companyGetter.getOwner());
                productObject.addProperty("company_accountnumber", companyGetter.getAccountnumber());
                productObject.addProperty("price", product.getPrice());
                productObject.addProperty("is_self_buy", product.getSelfBuy());
                productObject.addProperty("code", product.getCode());

                productArray.add(productObject);
            }
            responseObject.add("products", productArray);
            responseObject.addProperty("response_code", 1);
        }
        resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
    }
}
