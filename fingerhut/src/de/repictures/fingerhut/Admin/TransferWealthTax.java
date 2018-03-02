package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Transfer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TransferWealthTax extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Company fm = new Company("0098");
        Query query = new Query("Account");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> accountList = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity accountEntity : accountList){
            Account account = new Account(accountEntity);
            if (account.getBalanceDouble() >= 12 && !account.payedWealthTax() && !account.getIsPrepaid() && !Objects.equals(account.getAccountnumber(), "0006")){
                double taxRate = 0.15;
                if (account.getBalanceDouble() >= 25){
                    taxRate = 0.40;
                } else if (account.getBalanceDouble() >= 17){
                    taxRate = 0.30;
                }
                double tax = account.getBalanceDouble()*taxRate;
                double newAccountBalance = account.getBalanceDouble()-tax;
                account.setBalance(newAccountBalance);
                account.setPayedWealthTax(true);
                fm.setBalance(fm.getBalanceDouble()+tax);

                Calendar currentTime = Calendar.getInstance();
                Cryptor cryptor = new Cryptor();

                String purpose = "Die Vermögenssteuer für Freitag";

                PublicKey senderPublicKey = cryptor.stringToPublicKey(account.getPublicKeyStr());
                byte[] senderAesKey = cryptor.generateRandomAesKey();

                byte[] encryptedSenderPurpose = cryptor.encryptSymmetricFromString(purpose, senderAesKey);
                String encryptedSenderPurposeHex = cryptor.bytesToHex(encryptedSenderPurpose);

                byte[] encryptedReceiverNameForSender = cryptor.encryptSymmetricFromString(fm.getAccountnumber(), senderAesKey);

                byte[] encryptedSenderAesKey = cryptor.encryptAsymmetric(senderAesKey, senderPublicKey);
                String encryptedSenderAesKeyHex = cryptor.bytesToHex(encryptedSenderAesKey);

                SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.GERMANY);
                String datetime = f.format(currentTime.getTime());

                Transfer transferBuilder = new Transfer(new Transfer(Locale.GERMANY).createTransaction(datetime), Locale.GERMANY);
                transferBuilder.setSender(account.account);
                transferBuilder.setReceiver(fm.account);
                transferBuilder.setAmount((float) tax);
                transferBuilder.setDateTime();
                transferBuilder.setSenderPurpose(new Text(encryptedSenderPurposeHex));
                transferBuilder.setSenderAesKey(encryptedSenderAesKeyHex);
                transferBuilder.setReceiverNameForSender(cryptor.bytesToHex(encryptedReceiverNameForSender));
                transferBuilder.setType("Vermögenssteuer");
                transferBuilder.saveAll();

                Entity transferEntity = transferBuilder.getTransfer(datetime);

                account.addTransfer(transferEntity);
                resp.getWriter().println(account.getAccountnumber() + " hat noch " + account.getBalanceDouble() + "S");

                account.saveAll();
                fm.saveAll();
            }
        }
    }
}
