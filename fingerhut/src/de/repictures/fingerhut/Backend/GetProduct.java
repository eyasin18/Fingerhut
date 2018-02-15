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
        boolean selfBuy = selfBuyStr != null && Boolean.parseBoolean(selfBuyStr);
        String buyableStr = req.getParameter("buyable");
        boolean buyable = true;
        if(buyableStr != null) buyable = Boolean.parseBoolean(buyableStr);

        Product product = new Product(barcode, companyAccountnumber);
        if (product.product != null){
            resp.getWriter().println(2);
            return;
        }

        Company companyBuilder = new Company(companyAccountnumber);

        Product productBuilder = new Product();
        productBuilder.product = productBuilder.addProduct(barcode, productName, companyBuilder.account, priceStr);
        productBuilder.setSelfBuy(selfBuy);
        productBuilder.setBuyable(buyable);
        productBuilder.saveAll();

        Entity savedProduct = Product.getSpecificProduct(barcode, companyAccountnumber);
        companyBuilder.addProduct(savedProduct);
        companyBuilder.saveAll();

        resp.getWriter().println(1);
    }
}