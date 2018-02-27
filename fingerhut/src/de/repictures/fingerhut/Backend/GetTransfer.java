package de.repictures.fingerhut.Backend;

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
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetTransfer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Kontnummern werden entgegen genommen
        String receiverAccountnumber = URLDecoder.decode(req.getParameter("receiveraccountnumber"), "UTF-8");
        String senderAccountnumber = URLDecoder.decode(req.getParameter("senderaccountnumber"), "UTF-8");
        String webString = req.getParameter("webstring");
        String keyAsNumbersStr = req.getParameter("keyasnumbers");
        boolean keyAsNumbers = false;
        if (keyAsNumbersStr != null) keyAsNumbers = Boolean.parseBoolean(keyAsNumbersStr);

        //Die entsprechenden Entitäts-Builder werden initialisiert
        Account receiverBuilder = new Account(receiverAccountnumber);
        Account senderBuilder = new Account(senderAccountnumber);

        if (receiverBuilder.account == null || senderBuilder.account == null){
            resp.getWriter().println("5");
            return;
        }

        String savedWebString = senderBuilder.getRandomWebString();
        if (!Objects.equals(webString, savedWebString)){
            resp.getWriter().println("6");
            return;
        }

        //Öffentliche Schlüssel werden aus dem Datenspeicher gelesen
        String senderPublicKeyStr = senderBuilder.getPublicKeyStr();
        String receiverPublicKeyStr = receiverBuilder.getPublicKeyStr();
        String[] senderValues = senderBuilder.getPublicKeyValues();
        String[] receiverValues = receiverBuilder.getPublicKeyValues();

        //Antwort wird zurück gegeben
        String outputStr;
        if (!keyAsNumbers) outputStr = URLEncoder.encode("7ò" + senderPublicKeyStr + "ò" + receiverPublicKeyStr, "UTF-8");
        else outputStr = "7ò" + senderValues[0] + "ò" + senderValues[1] + "ò" + receiverValues[0] + "ò" + receiverValues[1];
        resp.getWriter().println(outputStr);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Parameter werden entgegen genommen
        String senderPurposeStr = req.getParameter("senderpurpose");
        if (senderPurposeStr != null)senderPurposeStr = URLDecoder.decode(senderPurposeStr, "UTF-8");
        String receiverPurposeStr = req.getParameter("receiverpurpose");
        if (receiverPurposeStr != null)receiverPurposeStr = URLDecoder.decode(receiverPurposeStr, "UTF-8");
        float amount = Float.parseFloat(URLDecoder.decode(req.getParameter("amount"), "UTF-8"));
        String receiverAccountnumber = URLDecoder.decode(req.getParameter("receiveraccountnumber"), "UTF-8");
        String senderAccountnumber = URLDecoder.decode(req.getParameter("senderaccountnumber"), "UTF-8");
        String senderAesKey = req.getParameter("senderkey");
        if (senderAesKey != null)senderAesKey = URLDecoder.decode(senderAesKey, "UTF-8");
        String receiverAesKey = req.getParameter("receiverkey");
        if (receiverAesKey != null)receiverAesKey = URLDecoder.decode(receiverAesKey, "UTF-8");
        String senderName = req.getParameter("sendername");
        if(senderName != null) senderName = URLDecoder.decode(senderName, "UTF-8");
        String receiverName = req.getParameter("receivername");
        if(receiverName != null) receiverName = URLDecoder.decode(receiverName, "UTF-8");
        String webString = req.getParameter("code");

        //Die entsprechenden Entitäts-Builder werden initialisiert
        Account receiverBuilder = new Account(receiverAccountnumber);
        Account senderBuilder = new Account(senderAccountnumber);

        if (receiverBuilder.account == null || senderBuilder.account == null){
            resp.getWriter().println("6");
            return;
        }

        String savedWebString = senderBuilder.getRandomWebString();
        /*if (!Objects.equals(webString, savedWebString)){
            resp.getWriter().println("5");
            return;
        }*/

        if (senderPurposeStr == null || receiverPurposeStr == null || receiverName == null || senderName == null){
            Cryptor cryptor = new Cryptor();
            String senderPublicKeyStr = senderBuilder.getPublicKeyStr();
            String receiverPublicKeyStr = receiverBuilder.getPublicKeyStr();

            PublicKey senderPublicKey = cryptor.stringToPublicKey(senderPublicKeyStr);
            PublicKey receiverPublicKey = cryptor.stringToPublicKey(receiverPublicKeyStr);

            byte[] randomSenderAesKey = cryptor.generateRandomAesKey();
            byte[] randomReceiverAesKey = cryptor.generateRandomAesKey();

            String rawPurpose = "Web-Überweisung";

            byte[] encryptedSenderPurpose = cryptor.encryptSymmetricFromString(rawPurpose, randomSenderAesKey);
            byte[] encryptedReceiverPurpose = cryptor.encryptSymmetricFromString(rawPurpose, randomReceiverAesKey);
            byte[] encryptedSenderName = cryptor.encryptSymmetricFromString(senderAccountnumber, randomReceiverAesKey);
            byte[] encryptedReceiverName = cryptor.encryptSymmetricFromString(receiverAccountnumber, randomSenderAesKey);

            senderPurposeStr = cryptor.bytesToHex(encryptedSenderPurpose);
            receiverPurposeStr = cryptor.bytesToHex(encryptedReceiverPurpose);
            senderName = cryptor.bytesToHex(encryptedSenderName);
            receiverName = cryptor.bytesToHex(encryptedReceiverName);

            byte[] encryptedSenderAesKeyByte = cryptor.encryptAsymmetric(randomSenderAesKey, senderPublicKey);
            byte[] encryptedReceiverAesKeyByte = cryptor.encryptAsymmetric(randomReceiverAesKey, receiverPublicKey);

            senderAesKey = cryptor.bytesToHex(encryptedSenderAesKeyByte);
            receiverAesKey = cryptor.bytesToHex(encryptedReceiverAesKeyByte);
        }

        Text senderPurpose = new Text(senderPurposeStr);
        Text receiverPurpose = new Text(receiverPurposeStr);

        //Momentane Serverzeit wird abgefragt
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", resp.getLocale());
        Calendar calendar = Calendar.getInstance();

        //Kontostände des Auftaggebers und des Empfängers werden initialisiert
        float senderBalanceFloat = senderBuilder.account != null ? Float.parseFloat(senderBuilder.getBalance()) : 0;
        float receiverBalanceFloat = receiverBuilder.account != null ? Float.parseFloat(receiverBuilder.getBalance()) : 0;
        BigDecimal senderBalance = new BigDecimal(senderBalanceFloat);
        BigDecimal receiverBalance = new BigDecimal(receiverBalanceFloat);

        //Es wird überprüft ob der Auftraggeber sich selbst Geld überweisen möchte
        if (receiverBuilder.account != null && Objects.equals(senderAccountnumber, receiverAccountnumber)){
            resp.getWriter().println("4");
        } else if (receiverBuilder.account != null && senderBalance.compareTo(new BigDecimal(amount)) >= 0 && receiverBuilder.account != null){ //Daten scheinen in Ordnung
            //Entitäts-Creator wird initialisiert
            Transfer transferCreator = new Transfer(resp.getLocale());
            Entity transfer = transferCreator.createTransaction(f.format(calendar.getTime()));

            //Entitäts-Builder wird initialisiert und Werte werden eingetragen
            Transfer transferBuilder = new Transfer(transfer, resp.getLocale());
            transferBuilder.setSender(senderBuilder.account);
            transferBuilder.setReceiver(receiverBuilder.account);
            transferBuilder.setAmount(amount);
            transferBuilder.setDateTime();
            transferBuilder.setSenderPurpose(senderPurpose);
            transferBuilder.setSenderAesKey(senderAesKey);
            transferBuilder.setReceiverPurpose(receiverPurpose);
            transferBuilder.setReceiverAesKey(receiverAesKey);
            transferBuilder.setType("Überweisung");
            transferBuilder.setSenderNameForReceiver(senderName);
            transferBuilder.setReceiverNameForSender(receiverName);
            transferBuilder.saveAll();

            //Die jeweiligen Accountentitäten werden aktualisiert
            Entity savedTransfer = transferBuilder.getTransfer(f.format(calendar.getTime()));
            senderBuilder.addTransfer(savedTransfer);
            senderBalance = senderBalance.subtract(new BigDecimal(amount));
            senderBuilder.setBalance(senderBalance.floatValue());
            receiverBuilder.addTransfer(savedTransfer);
            receiverBalance = receiverBalance.add(new BigDecimal(amount));
            receiverBuilder.setBalance(receiverBalance.floatValue());
            senderBuilder.saveAll();
            receiverBuilder.saveAll();

            //Benachrichtigung wird an Empfänger gesendet
            String receiverDeviceToken = receiverBuilder.getFirebaseDeviceToken();
            if (receiverDeviceToken != null){
                SendMessage sendMessageBuilder = new SendMessage();
                Map<String, String> messageData = new HashMap<>();
                messageData.put("notificationId", "0");
                messageData.put("accountnumber", receiverAccountnumber);
                messageData.put("sender", "Olaf");
                //TODO: Sender den richtigen Namen geben!
                sendMessageBuilder.sendMessage(messageData, receiverDeviceToken);
            }

            //Antwort wird zurück gegeben
            resp.getWriter().println("3");
        } else if (receiverBuilder.account != null){ //Konto des Senders ist nicht ausreichend gedeckt
            resp.getWriter().println("1");
        } else { //Empfänger existiert nicht
            resp.getWriter().println("2");
        }
    }
}
