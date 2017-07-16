package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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

        Product productGetter = new Product();
        List<Entity> products = productGetter.getProducts(barcode);
        Company companyGetter = new Company();

        if (products.size() == 0){
            resp.getWriter().println("0");
        } else {
            StringBuilder output = new StringBuilder();
            for (Entity product : products){
                output.append(product.getProperty("name"));
                output.append("ò");
                String companyKeyStr = (String) product.getProperty("selling_company");
                Key companyKey = KeyFactory.stringToKey(companyKeyStr);
                Entity company = companyGetter.getAccount(companyKey);
                output.append(companyGetter.getOwner(company));
                output.append("ò");
                output.append(companyGetter.getAccountnumber(company));
                output.append("ò");
                output.append(product.getProperty("price"));
                output.append("ň");
            }
            resp.getWriter().println(URLEncoder.encode(output.toString(), "UTF-8"));
        }
    }
}
