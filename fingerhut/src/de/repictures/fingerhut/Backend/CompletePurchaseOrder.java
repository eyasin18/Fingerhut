package de.repictures.fingerhut.Backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.repictures.fingerhut.Datastore.*;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class CompletePurchaseOrder extends HttpServlet {

    private Logger log = Logger.getLogger(CompletePurchaseOrder.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonStr = req.getParameter("json");
        JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();

        if (jsonObject.get("companynumber") == null || jsonObject.get("selleraccountnumber") == null || jsonObject.get("buyeraccountnumber") == null || jsonObject.get("webstring") == null
                || jsonObject.get("productcodes") == null || jsonObject.get("amounts") == null || jsonObject.get("prices") == null || jsonObject.get("isselfbuy") == null){
            resp.getWriter().println(0);
            return;
        }

        Account sellerAccountGetter = new Account(jsonObject.get("selleraccountnumber").getAsString());

        if (!Objects.equals(sellerAccountGetter.getRandomWebString(), jsonObject.get("webstring").getAsString())){
            resp.getWriter().println(2);
            return;
        }

        Account buyerAccountGetter = new Account(jsonObject.get("buyeraccountnumber").getAsString());
        Company companyGetter = new Company(jsonObject.get("companynumber").getAsString());
        PurchaseOrder purchaseOrderSetter = new PurchaseOrder(companyGetter.account, jsonObject.get("purchaseOrderNumber").getAsInt(), req.getLocale());

        JsonArray isSelfBuyArray = jsonObject.getAsJsonArray("isselfbuy");
        JsonArray pricesArray = jsonObject.getAsJsonArray("prices");
        JsonArray amountsArray = jsonObject.getAsJsonArray("amounts");
        JsonArray productCodeArray = jsonObject.getAsJsonArray("productcodes");

        jsonObject.addProperty("false", false);
        if(isSelfBuyArray.contains(jsonObject.get("false"))) {
            //Verwendungszweck wird generiert und Gesamtpreis kalkuliert
            StringBuilder purposeBuilder = new StringBuilder();
            purposeBuilder.append("Ihr Einkauf bei ")
                    .append(companyGetter.getOwner())
                    .append(":\n");
            double priceSum = 0.0;
            double taxes = 0.0;
            double vat = Tax.getVAT();
            vat = vat/100;

            for (int i = 0; i < amountsArray.size(); i++) {
                if (isSelfBuyArray.get(i).getAsBoolean()) continue;
                Product product = new Product(productCodeArray.get(i).getAsString());
                long amount = amountsArray.get(i).getAsLong();
                if (amount > 1) purposeBuilder.append(amount).append(" x ").append(product.getName()).append("\n");
                else purposeBuilder.append(product.getName()).append("\n");

                double itemPrice = pricesArray.get(i).getAsDouble();
                taxes += (amount*(itemPrice*vat));
                itemPrice = (itemPrice + itemPrice*vat);
                priceSum += (amount * itemPrice);
            }

            if (priceSum > Float.parseFloat(buyerAccountGetter.getBalance())) {
                resp.getWriter().println(3);
                return;
            }

            Transfer.buyItems(buyerAccountGetter, companyGetter, req.getLocale(), purposeBuilder.toString(), priceSum);
            Company finanzministerium = new Company("0098");
            double finanzministeriumBalance = finanzministerium.getBalanceDouble();
            finanzministerium.setBalance(finanzministeriumBalance + taxes);
            finanzministerium.saveAll();
        }

        List<Double> pricesList = new ArrayList<>();
        List<Long> amountsList = new ArrayList<>();
        List<Boolean> isSelfBuyList = new ArrayList<>();
        List<String> productCodesList = new ArrayList<>();
        for (int i = 0; i < amountsArray.size(); i++){
            pricesList.add(pricesArray.get(i).getAsDouble());
            amountsList.add(amountsArray.get(i).getAsLong());
            isSelfBuyList.add(isSelfBuyArray.get(i).getAsBoolean());
            productCodesList.add(productCodeArray.get(i).getAsString());
        }

        purchaseOrderSetter.setPricesList(pricesList);
        purchaseOrderSetter.setAmountsList(amountsList);
        purchaseOrderSetter.setIsSelfBuyList(isSelfBuyList);
        purchaseOrderSetter.setProductCodesList(productCodesList);
        purchaseOrderSetter.setCompleted(true);
        purchaseOrderSetter.saveAll();

        Map<String, String> messageData = new HashMap<>();
        messageData.put("notificationId", "2");
        messageData.put("number", jsonObject.get("purchaseOrderNumber").getAsString());
        new SendMessage().sendMessage(messageData, "/topics/" + jsonObject.get("companynumber").getAsString() + "-shoppingRequests");

        resp.getWriter().println(1);
    }
}