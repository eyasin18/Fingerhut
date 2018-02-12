package de.repictures.fingerhut.Backend;

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

        String companynumber = null;
        String sellerAccountnumber = null;
        String buyerAccountnumber = null;
        String webstring = null;
        int purchaseOrderNumber = 0;
        String[] productCodesArray = null;
        Long[] amountsArray = null;
        Double[] pricesArray = null;
        Boolean[] isSelfBuyArray = null;

        try {
            ByteArrayDataSource dataSource = new ByteArrayDataSource(req.getInputStream(), "multipart/form-data");
            MimeMultipart multipart = new MimeMultipart(dataSource);
            int count = multipart.getCount();
            log.info("Multipart content count: " + count);
            for (int i = 0; i < count; i++){
                log("Loop point: " + i);
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")){
                    log.info("bodypart no. " + i + "\n" + getStringBodyName(bodyPart) + "\n" + String.valueOf(bodyPart.getContent()));
                    switch (getStringBodyName(bodyPart)){
                        case "webstring":
                            webstring = String.valueOf(bodyPart.getContent());
                            break;
                        case "selleraccountnumber":
                            sellerAccountnumber = String.valueOf(bodyPart.getContent());
                            break;
                        case "buyeraccountnumber":
                            buyerAccountnumber = String.valueOf(bodyPart.getContent());
                            break;
                        case "companynumber":
                            companynumber = String.valueOf(bodyPart.getContent());
                            break;
                        case "purchaseOrderNumber":
                            purchaseOrderNumber = Integer.parseInt(String.valueOf(bodyPart.getContent()));
                            break;
                        case "productcodes":
                            String productCodesStr = String.valueOf(bodyPart.getContent());
                            productCodesArray = productCodesStr.split("ò");
                            break;
                        case "amounts":
                            String amountsStr = String.valueOf(bodyPart.getContent());
                            amountsArray = Arrays.stream(amountsStr.split("ò")).map(Long::parseLong).toArray(Long[]::new);
                            break;
                        case "prices":
                            String pricesStr = String.valueOf(bodyPart.getContent());
                            pricesArray = Arrays.stream(pricesStr.split("ò")).map(Double::parseDouble).toArray(Double[]::new);
                            break;
                        case "isselfbuy":
                            String isSelfBuyStr = String.valueOf(bodyPart.getContent());
                            isSelfBuyArray = Arrays.stream(isSelfBuyStr.split("ò")).map(Boolean::parseBoolean).toArray(Boolean[]::new);
                            break;
                    }
                } else {
                    log.info("Bodypart " + i + " is not type text");
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        if (companynumber == null || sellerAccountnumber == null || buyerAccountnumber == null || webstring == null
                || productCodesArray == null || amountsArray == null || pricesArray == null || isSelfBuyArray == null){
            resp.getWriter().println(0);
            return;
        }

        Account sellerAccountGetter = new Account(sellerAccountnumber);

        if (!Objects.equals(sellerAccountGetter.getRandomWebString(), webstring)){
            resp.getWriter().println(2);
            return;
        }

        Account buyerAccountGetter = new Account(buyerAccountnumber);
        Company companyGetter = new Company(companynumber);
        PurchaseOrder purchaseOrderSetter = new PurchaseOrder(companyGetter.account, purchaseOrderNumber, req.getLocale());

        if(Arrays.asList(isSelfBuyArray).contains(false)) {
            //Verwendungszweck wird generiert und Gesamtpreis kalkuliert
            StringBuilder purposeBuilder = new StringBuilder();
            purposeBuilder.append("Ihr Einkauf bei ")
                    .append(companyGetter.getOwner())
                    .append(":\n");
            double priceSum = 0.0;
            double taxes = 0.0;
            double vat = Tax.getVAT();
            vat = vat/100;

            for (int i = 0; i < amountsArray.length; i++) {
                if (isSelfBuyArray[i]) continue;
                Product product = new Product(productCodesArray[i]);
                long amount = amountsArray[i];
                if (amount > 1) purposeBuilder.append(amount).append(" x ").append(product.getName()).append("\n");
                else purposeBuilder.append(product.getName()).append("\n");

                double itemPrice = pricesArray[i];
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

        purchaseOrderSetter.setPricesList(Arrays.asList(pricesArray));
        purchaseOrderSetter.setAmountsList(Arrays.asList(amountsArray));
        purchaseOrderSetter.setIsSelfBuyList(Arrays.asList(isSelfBuyArray));
        purchaseOrderSetter.setProductCodesList(Arrays.asList(productCodesArray));
        purchaseOrderSetter.setCompleted(true);
        purchaseOrderSetter.saveAll();

        Map<String, String> messageData = new HashMap<>();
        messageData.put("notificationId", "2");
        messageData.put("number", String.valueOf(purchaseOrderNumber));
        new SendMessage().sendMessage(messageData, "/topics/" + companynumber + "-shoppingRequests");

        resp.getWriter().println(1);
    }

    private String getStringBodyName(BodyPart bodyPart) throws MessagingException {
        String[] header = bodyPart.getHeader("Content-Disposition");
        String[] contents = header[0].split("=");
        return contents[1].substring(1, contents[1].length()-1);
    }
}
