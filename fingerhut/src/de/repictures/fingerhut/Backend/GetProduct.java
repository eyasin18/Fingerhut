package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Product;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

public class GetProduct extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String barcode = req.getParameter("code");
        String productName = req.getParameter("name");
        productName = URLDecoder.decode(productName, "UTF-8");
        String priceStr = req.getParameter("price");
        String companyAccountnumber = req.getParameter("accountnumber");
        String selfBuyStr = req.getParameter("selfbuy");
        boolean selfBuy;
        selfBuy = selfBuyStr != null && Boolean.parseBoolean(selfBuyStr);

        if (new Product(barcode).product != null){
            resp.getWriter().println(2);
            return;
        }

        Company companyBuilder = new Company();
        Entity company = companyBuilder.getAccount(companyAccountnumber);

        Product productBuilder = new Product();
        productBuilder.product = productBuilder.addProduct(barcode, productName, company, priceStr);
        productBuilder.setImageUrl("https://c1.staticflickr.com/5/4123/4793188726_5d34ab7120_z.jpg");
        productBuilder.setSelfBuy(selfBuy);
        productBuilder.saveAll();

        Entity savedProduct = productBuilder.getProduct(barcode);
        companyBuilder.addProduct(company, savedProduct);
        companyBuilder.saveAll(company);

        resp.getWriter().println(1);
    }
}
