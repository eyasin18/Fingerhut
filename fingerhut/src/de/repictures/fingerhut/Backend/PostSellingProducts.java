package de.repictures.fingerhut.Backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Product;
import de.repictures.fingerhut.MultipartResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PostSellingProducts extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String companynumber = req.getParameter("companynumber");

        MultipartResponse multi = new MultipartResponse(resp);

        if (companynumber == null){
            multi.startResponse("text/plain");
            resp.getOutputStream().println(0);
            multi.endResponse();
            multi.finish();
            return;
        }

        Company companyGetter = new Company(companynumber);
        Product[] products = companyGetter.getSellingProducts();

        JsonObject jsonObject = new JsonObject();
        JsonArray productsArray = new JsonArray();

        for (Product product : products){
            JsonArray productJson = new JsonArray();
            productJson.add(product.getCode());
            productJson.add(product.getName());
            productJson.add(product.getPrice());
            productJson.add(product.getSelfBuy());
            productsArray.add(productJson);
        }
        jsonObject.add("products", productsArray);

        multi.startResponse("text/plain");
        resp.getOutputStream().println(1);
        multi.endResponse();
        multi.startResponse("text/plain");
        resp.getOutputStream().println(jsonObject.toString());
        multi.endResponse();
        multi.finish();
    }
}
