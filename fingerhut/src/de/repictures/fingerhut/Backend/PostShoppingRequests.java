package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Product;
import de.repictures.fingerhut.Datastore.PurchaseOrder;
import de.repictures.fingerhut.MultipartResponse;
import io.swagger.util.Json;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class PostShoppingRequests extends HttpServlet{

    private Logger log = Logger.getLogger(PostShoppingRequests.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String companyNumber = req.getParameter("companynumber");
        String webString = req.getParameter("webstring");
        String accountnumber = req.getParameter("accountnumber");

        MultipartResponse multi = new MultipartResponse(resp);

        Account accountGetter = new Account(accountnumber);

        if (accountGetter.account == null){
            multi.startResponse("text/plain");
            resp.getOutputStream().println(3);
            multi.endResponse();
            return;
        }

        if (!Objects.equals(webString, accountGetter.getRandomWebString())){
            multi.startResponse("text/plain");
            resp.getOutputStream().println(2);
            multi.endResponse();
            return;
        }

        Company companyGetter = new Company(companyNumber);

        if (companyGetter.account == null){
            multi.startResponse("text/plain");
            resp.getOutputStream().println(4);
            multi.endResponse();
            return;
        }

        PurchaseOrder purchaseOrdersGetter = new PurchaseOrder(companyGetter.account, req.getLocale());

        List<Entity> purchaseOrders = purchaseOrdersGetter.getPurchaseOrders(companyGetter.account.getKey());

        JsonObject object = new JsonObject();

        JsonArray amountsArray = new JsonArray();
        JsonArray buyerAccountnumbersArray = new JsonArray();
        JsonArray dateTimesArray = new JsonArray();
        JsonArray isSelfBuysArray = new JsonArray();
        JsonArray numbersArray = new JsonArray();
        JsonArray pricesArray = new JsonArray();
        JsonArray productNamesArray = new JsonArray();
        JsonArray productCodesArray = new JsonArray();
        JsonArray completedArray = new JsonArray();
        JsonArray madeByUserArray = new JsonArray();

        for (Entity purchaseOrderEntity : purchaseOrders){
            //Read Amounts
            amountsArray.add(listToJsonArray(purchaseOrdersGetter.getAmountsList(purchaseOrderEntity)));

            //Read BuyerAccountnumber
            buyerAccountnumbersArray.add(purchaseOrdersGetter.getBuyerAccountnumber(purchaseOrderEntity));

            //Read dateTimes
            dateTimesArray.add(purchaseOrdersGetter.getDateTime(purchaseOrderEntity));

            //Read isSelfBuys
            isSelfBuysArray.add(listToJsonArray(purchaseOrdersGetter.getIsSelfBuyList(purchaseOrderEntity)));

            //Read numbers
            numbersArray.add(purchaseOrdersGetter.getNumber(purchaseOrderEntity));

            //Read prices
            pricesArray.add(listToJsonArray(purchaseOrdersGetter.getPricesList(purchaseOrderEntity)));

            //Read productNames
            JsonArray oProductNames = new JsonArray();
            for (String productCode : purchaseOrdersGetter.getProductCodesList(purchaseOrderEntity)){
                Product productGetter = new Product(productCode, companyNumber);
                oProductNames.add(productGetter.getName());
            }
            productNamesArray.add(oProductNames);

            //Read productCodes
            productCodesArray.add(listToJsonArray(purchaseOrdersGetter.getProductCodesList(purchaseOrderEntity)));

            //Read completed
            completedArray.add(purchaseOrdersGetter.getCompleted(purchaseOrderEntity));

            //Read madeByUser
            madeByUserArray.add(purchaseOrdersGetter.getMadeByUser(purchaseOrderEntity));
        }

        List<Entity> productEntities = Product.getProductsByCompany(companyNumber, false);
        JsonArray sellingProductsArray = new JsonArray();
        for (Entity productEntity : productEntities){
            Product product = new Product(productEntity);
            JsonObject productObject = new JsonObject();
            productObject.addProperty("code", product.getCode());
            productObject.addProperty("name", product.getName());
            productObject.addProperty("price", product.getPrice());
            productObject.addProperty("is_self_buy", product.getSelfBuy());
            sellingProductsArray.add(productObject);
        }

        object.add("amounts", amountsArray);
        object.add("buyerAccountnumbers", buyerAccountnumbersArray);
        object.add("dateTimes", dateTimesArray);
        object.add("isSelfBuys", isSelfBuysArray);
        object.add("numbers", numbersArray);
        object.add("prices", pricesArray);
        object.add("productNames", productNamesArray);
        object.add("productCodes", productCodesArray);
        object.add("completed", completedArray);
        object.add("madeByUser", madeByUserArray);
        object.add("selling_products", sellingProductsArray);

        multi.startResponse("text/plain");
        resp.getOutputStream().println(1);
        multi.endResponse();
        multi.startResponse("text/plain");
        resp.getOutputStream().println(object.toString());
        log.info(object.toString());
        multi.endResponse();
        multi.finish();
    }

    private JsonArray listToJsonArray(List list){
        if (list == null){
            return new JsonArray();
        } else {
            JsonArray array = new JsonArray();
            for (Object o : list) {
                array.add(String.valueOf(o));
            }
            return array;
        }
    }
}