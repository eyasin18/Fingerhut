package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import de.repictures.fingerhut.Datastore.Accounts;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Transfers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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

        //Die entsprechenden Entitäts-Builder werden initialisiert
        Accounts receiverBuilder = new Accounts(receiverAccountnumber);
        Accounts senderBuilder = new Accounts(senderAccountnumber);

        String savedWebString = senderBuilder.getRandomWebString();
        if (!Objects.equals(webString, savedWebString)){
            resp.getWriter().println("2");
        }

        if (receiverBuilder.account == null){
            receiverBuilder = new Company(receiverAccountnumber);
        }
        if (senderBuilder.account == null){
            senderBuilder = new Company(senderAccountnumber);
        }

        //Öffentliche Schlüssel werden aus dem Datenspeicher gelesen
        String senderPublicKeyStr = senderBuilder.getPublicKeyStr();
        String receiverPublicKeyStr = receiverBuilder.getPublicKeyStr();
        //Antwort wird zurück gegeben
        String outputStr = URLEncoder.encode("1ò" + senderPublicKeyStr + "ò" + receiverPublicKeyStr, "UTF-8");
        resp.getWriter().println(outputStr);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Parameter werden entgegen genommen
        String senderPurposeStr = URLDecoder.decode(req.getParameter("senderpurpose"), "UTF-8");
        Text senderPurpose = new Text(senderPurposeStr);
        String receiverPurposeStr = URLDecoder.decode(req.getParameter("receiverpurpose"), "UTF-8");
        Text receiverPurpose = new Text(receiverPurposeStr);
        float amount = Float.parseFloat(URLDecoder.decode(req.getParameter("amount"), "UTF-8"));
        String receiverAccountnumber = URLDecoder.decode(req.getParameter("receiveraccountnumber"), "UTF-8");
        String senderAccountnumber = URLDecoder.decode(req.getParameter("senderaccountnumber"), "UTF-8");
        String senderAesKey = URLDecoder.decode(req.getParameter("senderkey"), "UTF-8");
        String receiverAesKey = URLDecoder.decode(req.getParameter("receiverkey"), "UTF-8");

        //Die entsprechenden Entitäts-Builder werden initialisiert
        Accounts receiverBuilder = new Accounts(receiverAccountnumber);
        Accounts senderBuilder = new Accounts(senderAccountnumber);
        if (receiverBuilder.account == null){
            receiverBuilder = new Company(receiverAccountnumber);
        }
        if (senderBuilder.account == null){
            senderBuilder = new Company(senderAccountnumber);
        }

        //Momentane Serverzeit wird abgefragt
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", resp.getLocale());
        Calendar calendar = Calendar.getInstance();

        //Kontostände des Auftaggebers und des Empfängers werden initialisiert
        float senderBalance = senderBuilder.account != null ? Float.parseFloat(senderBuilder.getBalance()) : 0;
        float receiverBalance = receiverBuilder.account != null ? Float.parseFloat(receiverBuilder.getBalance()) : 0;

        //Es wird überprüft ob der Auftraggeber sich selbst Geld überweisen möchte
        if (receiverBuilder.account != null && Objects.equals(senderAccountnumber, receiverAccountnumber)){
            resp.getWriter().println("4");
        } else if (receiverBuilder.account != null && senderBalance >= amount && receiverBuilder.account != null){ //Daten scheinen in Ordnung
            //Entitäts-Creator wird initialisiert
            Transfers transferCreator = new Transfers(resp.getLocale());
            Entity transfer = transferCreator.createTransaction(f.format(calendar.getTime()));

            //Entitäts-Builder wird initialisiert und Werte werden eingetragen
            Transfers transferBuilder = new Transfers(transfer, resp.getLocale());
            transferBuilder.setSender(senderBuilder.account);
            transferBuilder.setReceiver(receiverBuilder.account);
            transferBuilder.setAmount(amount);
            transferBuilder.setDateTime();
            transferBuilder.setSenderPurpose(senderPurpose);
            transferBuilder.setSenderAesKey(senderAesKey);
            transferBuilder.setReceiverPurpose(receiverPurpose);
            transferBuilder.setReceiverAesKey(receiverAesKey);
            transferBuilder.setType("Überweisung");
            transferBuilder.saveAll();

            //Die jeweiligen Accountentitäten werden aktualisiert
            Entity savedTransfer = transferBuilder.getTransfer(f.format(calendar.getTime()));
            senderBuilder.addTransfer(savedTransfer);
            senderBuilder.setBalance(senderBalance - amount);
            receiverBuilder.addTransfer(savedTransfer);
            receiverBuilder.setBalance(receiverBalance + amount);
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
