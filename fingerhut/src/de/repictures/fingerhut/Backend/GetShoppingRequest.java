package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;

public class GetShoppingRequest extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String webstring = req.getParameter("code");
        if (webstring != null) webstring = URLDecoder.decode(webstring, "UTF-8");
        String accountnumber = req.getParameter("accountnumber");
        String companyNumber = req.getParameter("companynumber");
        String shoppingListRaw = req.getParameter("shoppinglist");
        if (shoppingListRaw != null) shoppingListRaw = URLDecoder.decode(shoppingListRaw, "UTF-8");

        Account accountGetter = new Account(accountnumber);
        Company companyGetter = new Company(companyNumber);

        if (!Objects.equals(accountGetter.getRandomWebString(), webstring)){
            resp.getWriter().println(-1);
            return;
        }

        //Einkaufsauftragentität wird erstellt
        List<String[]> newItems = new ArrayList<>();
        String[] shoppingListSplittedItems = shoppingListRaw.split("ň");
        for (String shoppingListSpittedItem : shoppingListSplittedItems) {
            newItems.add(shoppingListSpittedItem.split("ò"));
        }
        PurchaseOrder purchaseOrder = new PurchaseOrder(companyGetter.account, req.getLocale());
        purchaseOrder.updatePurchaseOrder(companyGetter.account, newItems, accountnumber);

        if(purchaseOrder.getIsSelfBuyList().contains(true)) {
            //Verwendungszweck wird generiert und Gesamtpreis kalkuliert
            StringBuilder purposeBuilder = new StringBuilder();
            purposeBuilder.append("Ihr Einkauf bei ")
                    .append(companyGetter.getOwner())
                    .append("\n");
            double priceSum = 0.0;

            for (String[] item : newItems) {
                if (!Boolean.parseBoolean(item[2])) {
                    continue;
                }
                Product product = new Product(item[0]);
                int count = Integer.parseInt(item[3]);
                if (count > 1) purposeBuilder.append(count).append(" x ").append(product.getName()).append("\n");
                else purposeBuilder.append(product.getName()).append("\n");

                double itemPrice = Double.parseDouble(item[1]);
                priceSum += (count * itemPrice);
            }

            if (priceSum > Float.parseFloat(accountGetter.getBalance())) {
                resp.getWriter().println(2);
                return;
            }

            Transfer.buyItems(accountGetter, companyGetter, resp.getLocale(), purposeBuilder.toString(), priceSum);
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
        new SendMessage().sendMessage(messageContent, "/topics/" + companyNumber + "-shoppingRequests");

        resp.getWriter().println(1);
    }
}