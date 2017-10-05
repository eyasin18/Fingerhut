package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Accounts;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Product;
import de.repictures.fingerhut.Datastore.Transfers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
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

        Accounts accountGetter = new Accounts(accountnumber);
        Company companyGetter = new Company(companyNumber);

        if (!Objects.equals(accountGetter.getRandomWebString(), webstring)){
            resp.getWriter().println(-1);
            return;
        }

        Map<String, List<String[]>> shoppingRequests = companyGetter.getShoppingRequests();
        List<String[]> newItems = new ArrayList<>();
        /*if (shoppingRequests.get(companyNumber) != null) newItems = shoppingRequests.get(companyNumber);
        else newItems = new ArrayList<>();*/

        String[] shoppingListSplittedItems = shoppingListRaw.split("ň");
        for (String shoppingListSpittedItem : shoppingListSplittedItems) {
            newItems.add(shoppingListSpittedItem.split("ò"));
        }

        StringBuilder purposeBuilder = new StringBuilder();
        double priceSum = 0.0;

        for (String[] item : newItems){
            if (!Boolean.parseBoolean(item[2])){
                continue;
            }
            Product product = new Product(item[0]);
            int count = Integer.parseInt(item[3]);
            if (count > 1) purposeBuilder.append(count).append(" x ").append(product.getName()).append("\n");
            else purposeBuilder.append(product.getName()).append("\n");

            double itemPrice = Double.parseDouble(item[1]);
            priceSum += (count * itemPrice);
        }

        if (priceSum > Float.parseFloat(accountGetter.getBalance())){
            resp.getWriter().println(2);
            return;
        }

        buyItems(accountGetter, companyGetter, resp.getLocale(), purposeBuilder.toString(), priceSum);

        shoppingRequests.put(accountnumber, newItems);
        companyGetter.setShoppingRequests(shoppingRequests);
        companyGetter.saveAll();

        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("notificationId", "1");
        messageContent.put("updateKey", "1");
        new SendMessage().sendMessage(messageContent, "/topics/" + companyNumber + "-shoppingRequests");

        resp.getWriter().println(1);
    }

    private void buyItems(Accounts accountGetter, Company companyGetter, Locale locale, String purpose, double priceSum) {

        Cryptor cryptor = new Cryptor();

        PublicKey senderPublicKey = cryptor.stringToPublicKey(accountGetter.getPublicKeyStr());
        PublicKey receiverPublicKey = cryptor.stringToPublicKey(companyGetter.getPublicKeyStr());
        byte[] senderAesKey = cryptor.generateRandomAesKey();
        byte[] receiverAesKey = cryptor.generateRandomAesKey();

        byte[] encryptedSenderPurpose = cryptor.encryptSymetricFromString(purpose, senderAesKey);
        String encryptedSenderPurposeHex = cryptor.bytesToHex(encryptedSenderPurpose);
        byte[] encryptedReceiverPurpose = cryptor.encryptSymetricFromString(purpose, receiverAesKey);
        String encryptedReceiverPurposeHex = cryptor.bytesToHex(encryptedReceiverPurpose);

        byte[] encryptedSenderAesKey = cryptor.encryptAsymetric(senderAesKey, senderPublicKey);
        String encryptedSenderAesKeyHex = cryptor.bytesToHex(encryptedSenderAesKey);
        byte[] encryptedReceiverAesKey = cryptor.encryptAsymetric(receiverAesKey, receiverPublicKey);
        String encryptedReceiverAesKeyHex = cryptor.bytesToHex(encryptedReceiverAesKey);


        Calendar calendar = Calendar.getInstance(locale);
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", locale);
        String datetime = f.format(calendar.getTime());

        Transfers transferBuilder = new Transfers(new Transfers(locale).createTransaction(datetime), locale);
        transferBuilder.setSender(accountGetter.account);
        transferBuilder.setReceiver(companyGetter.account);
        transferBuilder.setAmount((float) priceSum);
        transferBuilder.setDateTime();
        transferBuilder.setSenderPurpose(new Text(encryptedSenderPurposeHex));
        transferBuilder.setSenderAesKey(encryptedSenderAesKeyHex);
        transferBuilder.setReceiverPurpose(new Text(encryptedReceiverPurposeHex));
        transferBuilder.setReceiverAesKey(encryptedReceiverAesKeyHex);
        transferBuilder.setType("Einkauf");
        transferBuilder.saveAll();

        float accountBalance = Float.parseFloat(accountGetter.getBalance());
        float companyBalance = Float.parseFloat(companyGetter.getBalance());
        Entity savedTransfer = transferBuilder.getTransfer(datetime);
        accountGetter.addTransfer(savedTransfer);
        companyGetter.addTransfer(savedTransfer);
        accountGetter.setBalance((float) (accountBalance - priceSum));
        companyGetter.setBalance((float) (companyBalance + priceSum));
        accountGetter.saveAll();
        companyGetter.saveAll();
    }
}