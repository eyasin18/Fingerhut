package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Product;
import de.repictures.fingerhut.Datastore.PurchaseOrder;
import de.repictures.fingerhut.MultipartResponse;

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

        for (Entity purchaseOrderEntity : purchaseOrders){
            //Read Amounts
            JsonArray oAmountsArray = new JsonArray();
            for (Long amount : purchaseOrdersGetter.getAmountsList(purchaseOrderEntity)){
                oAmountsArray.add(Math.toIntExact(amount));
            }
            amountsArray.add(oAmountsArray);

            //Read BuyerAccountnumber
            buyerAccountnumbersArray.add(purchaseOrdersGetter.getBuyerAccountnumber(purchaseOrderEntity));

            //Read dateTimes
            dateTimesArray.add(purchaseOrdersGetter.getDateTime(purchaseOrderEntity));

            //Read isSelfBuys
            JsonArray oIsSelfBuysArray = new JsonArray();
            for (Boolean isSelfBuy : purchaseOrdersGetter.getIsSelfBuyList(purchaseOrderEntity)){
                oIsSelfBuysArray.add(isSelfBuy);
            }
            isSelfBuysArray.add(oIsSelfBuysArray);

            //Read numbers
            numbersArray.add(purchaseOrdersGetter.getNumber(purchaseOrderEntity));

            //Read prices
            JsonArray oPricesArray = new JsonArray();
            for (double price : purchaseOrdersGetter.getPricesList(purchaseOrderEntity)){
                oPricesArray.add(price);
            }
            pricesArray.add(oPricesArray);

            //Read productCodes
            JsonArray oProductNames = new JsonArray();
            for (String productCode : purchaseOrdersGetter.getProductCodesList(purchaseOrderEntity)){
                Product productGetter = new Product(productCode);
                oProductNames.add(productGetter.getName());
            }
            productNamesArray.add(oProductNames);

            //Read productCodes
            JsonArray oProductCodes = new JsonArray();
            for (String productCode : purchaseOrdersGetter.getProductCodesList(purchaseOrderEntity)){
                oProductCodes.add(productCode);
            }
            productCodesArray.add(oProductCodes);
        }

        object.add("amounts", amountsArray);
        object.add("buyerAccountnumbers", buyerAccountnumbersArray);
        object.add("dateTimes", dateTimesArray);
        object.add("isSelfBuys", isSelfBuysArray);
        object.add("numbers", numbersArray);
        object.add("prices", pricesArray);
        object.add("productNames", productNamesArray);
        object.add("productCodes", productCodesArray);

        multi.startResponse("text/plain");
        resp.getOutputStream().println(1);
        multi.endResponse();
        multi.startResponse("text/plain");
        resp.getOutputStream().println(object.toString());
        log.info(object.toString());
        multi.endResponse();
        multi.finish();
    }
}