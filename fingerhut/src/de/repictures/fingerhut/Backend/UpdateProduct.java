package de.repictures.fingerhut.Backend;

import de.repictures.fingerhut.Datastore.Product;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

public class UpdateProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String barcode = req.getParameter("code");
        String companynumber = req.getParameter("companynumber");
        String productName = req.getParameter("name");
        String priceStr = req.getParameter("price");
        String selfBuyStr = req.getParameter("selfbuy");
        String buyableStr = req.getParameter("buyable");

        productName = URLDecoder.decode(productName, "UTF-8");
        boolean selfBuy = selfBuyStr != null && Boolean.parseBoolean(selfBuyStr);
        boolean buyable = true;
        if(buyableStr != null) buyable = Boolean.parseBoolean(buyableStr);

        Product product = new Product(barcode, companynumber);
        if (product.product == null){
            resp.getWriter().println(0);
            return;
        }

        if (productName != null){
            product.setName(productName);
        }
        if (priceStr != null){
            product.setPrice(Double.valueOf(priceStr));
        }
        if (selfBuyStr != null){
            product.setSelfBuy(selfBuy);
        }
        if (buyableStr != null){
            product.setBuyable(buyable);
        }
        product.saveAll();

        resp.getWriter().println(1);
    }
}