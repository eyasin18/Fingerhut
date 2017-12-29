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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
                            amountsArray = Arrays.stream(amountsStr.split("ò")).mapToLong(Long::parseLong).boxed().toArray(Long[]::new);
                            break;
                        case "prices":
                            String pricesStr = String.valueOf(bodyPart.getContent());
                            String[] pricesStrArray = pricesStr.split("ò");
                            pricesArray = new Double[pricesStrArray.length];
                            for (int o = 0; i < pricesStrArray.length; i++){
                                pricesArray[o] = Double.parseDouble(pricesStrArray[o]);
                            }
                            break;
                        case "isselfbuy":
                            String isSelfBuyStr = String.valueOf(bodyPart.getContent());
                            String[] isSelfBuyStrArray = isSelfBuyStr.split("ò");
                            isSelfBuyArray = new Boolean[isSelfBuyStrArray.length];
                            for (int o = 0; i < isSelfBuyStrArray.length; i++){
                                isSelfBuyArray[o] = Boolean.parseBoolean(isSelfBuyStrArray[o]);
                            }
                            break;
                    }
                } else {
                    log.info("Bodypart " + i + " is not type text");
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        /*log("Company Number: " + companynumber + "\nsellerAccountnumber: "+ sellerAccountnumber + "\nbuyerAccountnumber: " + buyerAccountnumber
            + "\nwebstring: " + webstring + "\npurchaseOrderNumber: " + purchaseOrderNumber + "\nproductCodesArray: " + productCodesArray.toString() + "\namountsArray: " + amountsArray.toString()
            + "\npricesArray: " + pricesArray.toString() + "\nisSelfBuyArray: " + isSelfBuyArray.toString());*/
        if (companynumber == null || sellerAccountnumber == null || buyerAccountnumber == null || webstring == null || purchaseOrderNumber == 0
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

        //Verwendungszweck wird generiert und Gesamtpreis kalkuliert
        StringBuilder purposeBuilder = new StringBuilder();
        double priceSum = 0.0;

        for (int i = 0; i < amountsArray.length; i++){
            if (isSelfBuyArray[i]) continue;
            Product product = new Product(productCodesArray[i]);
            long amount = amountsArray[i];
            if (amount > 1) purposeBuilder.append(amount).append(" x ").append(product.getName()).append("\n");
            else purposeBuilder.append(product.getName()).append("\n");

            double itemPrice = pricesArray[i];
            priceSum += (amount * itemPrice);
        }

        Transfer.buyItems(buyerAccountGetter, companyGetter, req.getLocale(), purposeBuilder.toString(), priceSum);

        purchaseOrderSetter.setPricesList(Arrays.asList(pricesArray));
        purchaseOrderSetter.setAmountsList(Arrays.asList(amountsArray));
        purchaseOrderSetter.setIsSelfBuyList(Arrays.asList(isSelfBuyArray));
        purchaseOrderSetter.setProductCodesList(Arrays.asList(productCodesArray));
        purchaseOrderSetter.setCompleted(true);
        purchaseOrderSetter.saveAll();

        resp.getWriter().println(1);
    }

    private String getStringBodyName(BodyPart bodyPart) throws MessagingException {
        String[] header = bodyPart.getHeader("Content-Disposition");
        String[] contents = header[0].split("=");
        return contents[1].substring(1, contents[1].length()-1);
    }
}
