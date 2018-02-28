package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
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
import java.util.Locale;

public class AdminTransfer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isSenderCompany = false;
        String senderAccountnumber = req.getParameter("sender");
        String receiverAccountnumber = req.getParameter("receiver");
        String amountStr = req.getParameter("amount");

        if (senderAccountnumber == null || receiverAccountnumber == null || amountStr == null){
            resp.getWriter().println("Du hasch da was ned mitgeßendet");
            return;
        }
        if (senderAccountnumber.length() < 1 || receiverAccountnumber.length() < 1 || amountStr.length() < 1){
            resp.getWriter().println("Du hasch da was ned mitgeßendet");
            return;
        }

        Account senderAccount = new Account(senderAccountnumber);
        if (senderAccount.account == null){
            senderAccount = new Company(senderAccountnumber);
            isSenderCompany = true;
        }
        if (senderAccount.account == null){
            resp.getWriter().println("Des Senderkondo gibts ed");
            return;
        }

        Account receiverAccount = new Account(receiverAccountnumber);
        if (receiverAccount.account == null){
            receiverAccount = new Company(receiverAccountnumber);
        }
        if (receiverAccount.account == null){
            resp.getWriter().println("Des Embfängerkondo gibts ed");
            return;
        }

        double amount = Double.valueOf(amountStr);
        if (amount < 0){
            resp.getWriter().println("Du kannsch nur positive Beträge als Betrag veschdlägen");
            return;
        }

        String purpose = "Nachträgliche Überweisung";

        double senderBalance = senderAccount.getBalanceDouble();
        double receiverBalance = receiverAccount.getBalanceDouble();

        senderAccount.setBalance(senderBalance - amount);
        receiverAccount.setBalance(receiverBalance + amount);

        Cryptor cryptor = new Cryptor();
        Calendar currentTime = Calendar.getInstance();

        PublicKey senderPublicKey = cryptor.stringToPublicKey(senderAccount.getPublicKeyStr());
        PublicKey receiverPublicKey = cryptor.stringToPublicKey(receiverAccount.getPublicKeyStr());
        byte[] senderAesKey = cryptor.generateRandomAesKey();
        byte[] receiverAesKey = cryptor.generateRandomAesKey();

        byte[] encryptedSenderPurpose = cryptor.encryptSymmetricFromString(purpose, senderAesKey);
        String encryptedSenderPurposeHex = cryptor.bytesToHex(encryptedSenderPurpose);
        byte[] encryptedReceiverPurpose = cryptor.encryptSymmetricFromString(purpose, receiverAesKey);
        String encryptedReceiverPurposeHex = cryptor.bytesToHex(encryptedReceiverPurpose);

        byte[] encryptedSenderNameForReceiver = cryptor.encryptSymmetricFromString(senderAccount.getAccountnumber(), receiverAesKey);
        byte[] encryptedReceiverNameForSender = cryptor.encryptSymmetricFromString(receiverAccount.getAccountnumber(), senderAesKey);

        byte[] encryptedSenderAesKey = cryptor.encryptAsymmetric(senderAesKey, senderPublicKey);
        String encryptedSenderAesKeyHex = cryptor.bytesToHex(encryptedSenderAesKey);
        byte[] encryptedReceiverAesKey = cryptor.encryptAsymmetric(receiverAesKey, receiverPublicKey);
        String encryptedReceiverAesKeyHex = cryptor.bytesToHex(encryptedReceiverAesKey);

        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.GERMANY);
        String datetime = f.format(currentTime.getTime());

        Transfer transferBuilder = new Transfer(new Transfer(Locale.GERMANY).createTransaction(datetime), Locale.GERMANY);
        transferBuilder.setSender(senderAccount.account);
        transferBuilder.setReceiver(receiverAccount.account);
        transferBuilder.setAmount((float) amount);
        transferBuilder.setDateTime();
        transferBuilder.setSenderPurpose(new Text(encryptedSenderPurposeHex));
        transferBuilder.setSenderNameForReceiver(cryptor.bytesToHex(encryptedSenderNameForReceiver));
        transferBuilder.setSenderAesKey(encryptedSenderAesKeyHex);
        transferBuilder.setReceiverPurpose(new Text(encryptedReceiverPurposeHex));
        transferBuilder.setReceiverNameForSender(cryptor.bytesToHex(encryptedReceiverNameForSender));
        transferBuilder.setReceiverAesKey(encryptedReceiverAesKeyHex);
        transferBuilder.setType("Nachträgliche Überweisung");
        transferBuilder.saveAll();

        Entity savedTransfer = transferBuilder.getTransfer(datetime);
        receiverAccount.addTransfer(savedTransfer);
        senderAccount.addTransfer(savedTransfer);

        senderAccount.saveAll();
        receiverAccount.saveAll();

        resp.getWriter().println("Isch alles ogee");
    }

    private boolean accountHasNotEnoughMoney(double balance, double amountToBeSubtracted) {
        return Account.getCurrentMinutes() < Account.getMinutesFromValues(4, 12, 0)
                && (balance - amountToBeSubtracted) < -31.00

                || (balance - amountToBeSubtracted) < 0.00;
    }
}