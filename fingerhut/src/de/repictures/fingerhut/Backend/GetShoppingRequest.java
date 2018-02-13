package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.repictures.fingerhut.Datastore.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetShoppingRequest extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String webstring = req.getParameter("code");
        if (webstring != null) webstring = URLDecoder.decode(webstring, "UTF-8");
        String authAccountnumber = req.getParameter("authaccountnumber");
        String accountnumber = req.getParameter("accountnumber");
        String companyNumber = req.getParameter("companynumber");
        String shoppingListRaw = req.getParameter("shoppinglist");
        if (shoppingListRaw != null) shoppingListRaw = URLDecoder.decode(shoppingListRaw, "UTF-8");
        boolean madeByUser = true;
        String madeByUserStr = req.getParameter("madebyuser");
        if (madeByUserStr != null) madeByUser = Boolean.parseBoolean(madeByUserStr);
        boolean completed = false;
        String completedStr = req.getParameter("completed");
        if (completedStr != null) completed = Boolean.parseBoolean(completedStr);

        Account accountGetter = new Account(accountnumber);
        Account authAccountGetter = new Account(authAccountnumber);
        Company companyGetter = new Company(companyNumber);

        if (accountGetter.account == null){
            resp.getWriter().println(3);
            return;
        }

        if (!Objects.equals(authAccountGetter.getRandomWebString(), webstring)){
            resp.getWriter().println(-1);
            return;
        }

        //Einkaufsauftragentität wird erstellt
        PurchaseOrder purchaseOrder;
        if (shoppingListRaw.length() > 0) {
            purchaseOrder = new PurchaseOrder(companyGetter.account, req.getLocale());
            purchaseOrder.updatePurchaseOrder(companyGetter.account, shoppingListRaw, accountnumber, completed);
            purchaseOrder.setMadeByUser(madeByUser);
            purchaseOrder.saveAll();
        } else {
            purchaseOrder = new PurchaseOrder(companyGetter.account, req.getLocale());
            purchaseOrder.purchaseOrder = new Entity("PurchaseOrder", companyGetter.account.getKey());

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", req.getLocale());
            purchaseOrder.setNumber();
            purchaseOrder.setDateTime(f.format(calendar.getTime()));
            purchaseOrder.setBuyerAccountnumber(accountnumber);
            purchaseOrder.setProductCodesList(new ArrayList<>());
            purchaseOrder.setPricesList(new ArrayList<>());
            purchaseOrder.setIsSelfBuyList(new ArrayList<>());
            purchaseOrder.setAmountsList(new ArrayList<>());
            purchaseOrder.setCompleted(completed);
            purchaseOrder.saveAll();
            int number = purchaseOrder.getNumber();
            purchaseOrder = new PurchaseOrder(companyGetter.account, number, req.getLocale());
        }

        if(madeByUser && shoppingListRaw.length() > 0 && purchaseOrder.getIsSelfBuyList().contains(true)) {

            JsonObject object = new JsonParser().parse(shoppingListRaw).getAsJsonObject();
            JsonArray productCodesArray = object.getAsJsonArray("product_codes");
            JsonArray pricesArray = object.getAsJsonArray("prices_array");
            JsonArray isSelfBuyArray = object.getAsJsonArray("is_self_buy");
            JsonArray amountsArray = object.getAsJsonArray("amounts");

            //Verwendungszweck wird generiert und Gesamtpreis kalkuliert
            StringBuilder purposeBuilder = new StringBuilder();
            purposeBuilder.append("Ihr Einkauf bei ")
                    .append(companyGetter.getOwner())
                    .append(":\n");
            double priceSum = 0.0;
            double taxes = 0.0;
            double vat = Tax.getVAT();
            vat = vat/100;

            for (int i = 0; i < productCodesArray.size(); i++) {
                if (!isSelfBuyArray.get(i).getAsBoolean()) {
                    continue;
                }
                Product product = new Product(productCodesArray.get(i).getAsString());
                int count = amountsArray.get(i).getAsInt();
                if (count > 1) purposeBuilder.append(count).append(" x ").append(product.getName()).append("\n");
                else purposeBuilder.append(product.getName()).append("\n");

                double itemPrice = pricesArray.get(i).getAsDouble();
                taxes += (count*(itemPrice*vat));
                itemPrice = (itemPrice + itemPrice*vat);
                priceSum += (count * itemPrice);
            }

            if (priceSum > Float.parseFloat(accountGetter.getBalance())) {
                resp.getWriter().println(2);
                return;
            }

            Transfer.buyItems(accountGetter, companyGetter, resp.getLocale(), purposeBuilder.toString(), priceSum);
            Company finanzministerium = new Company("0098");
            double finanzministeriumBalance = finanzministerium.getBalanceDouble();
            finanzministerium.setBalance(finanzministeriumBalance + taxes);
            finanzministerium.saveAll();
        }

        StringBuilder amountsBuilder = new StringBuilder();
        for (Long amount : purchaseOrder.getAmountsList()) {
            amountsBuilder.append(amount).append("ò");
        }

        StringBuilder isSelfBuysBuilder = new StringBuilder();
        for (Boolean isSelfBuy : purchaseOrder.getIsSelfBuyList()){
            isSelfBuysBuilder.append(isSelfBuy).append("ò");
        }

        StringBuilder pricesBuilder = new StringBuilder();
        for (Double price : purchaseOrder.getPricesList()){
            pricesBuilder.append(price).append("ò");
        }

        StringBuilder productCodesBuilder = new StringBuilder();
        for (String productCode : purchaseOrder.getProductCodesList()){
            productCodesBuilder.append(productCode).append("ò");
        }

        StringBuilder productNamesBuilder = new StringBuilder();
        for (String productCode : purchaseOrder.getProductCodesList()){
            Product product = new Product(productCode);
            productNamesBuilder.append(URLEncoder.encode(product.getName(), "UTF-8")).append("ò");
        }

        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("notificationId", "1");
        messageContent.put("updateKey", "1");
        messageContent.put("amounts", amountsBuilder.toString());
        messageContent.put("buyerAccountnumber", purchaseOrder.getBuyerAccountnumber());
        messageContent.put("dateTime", purchaseOrder.getDateTime());
        messageContent.put("isSelfBuys", isSelfBuysBuilder.toString());
        messageContent.put("number", String.valueOf(purchaseOrder.getNumber()));
        messageContent.put("prices", pricesBuilder.toString());
        messageContent.put("productNames", productNamesBuilder.toString());
        messageContent.put("productCodes", productCodesBuilder.toString());
        messageContent.put("completed", String.valueOf(purchaseOrder.getCompleted()));
        messageContent.put("madeByUser", String.valueOf(purchaseOrder.getMadeByUser()));
        new SendMessage().sendMessage(messageContent, "/topics/" + companyNumber + "-shoppingRequests");

        resp.getWriter().println(URLEncoder.encode("1ò" + purchaseOrder.getNumber(), "UTF-8"));
    }
}