package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Product;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetProduct extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String barcode = req.getParameter("code");
        String productName = req.getParameter("name");
        String priceStr = req.getParameter("price");
        String companyAccountnumber = req.getParameter("accountnumber");

        Company companyBuilder = new Company();
        Entity company = companyBuilder.getAccount(companyAccountnumber);

        Product productBuilder = new Product();
        Entity generatedProduct = productBuilder.addProduct(barcode, productName, company, priceStr);
        companyBuilder.saveAll(generatedProduct);

        Entity savedProduct = productBuilder.getProduct(barcode);
        companyBuilder.addProduct(company, savedProduct);
        companyBuilder.saveAll(company);

        resp.getWriter().println("success");
    }
}
