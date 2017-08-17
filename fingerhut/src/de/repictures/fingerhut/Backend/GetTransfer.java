package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
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
import java.util.Objects;

public class GetTransfer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String receiverAccountnumber = URLDecoder.decode(req.getParameter("receiveraccountnumber"), "UTF-8");
        String senderAccountnumber = URLDecoder.decode(req.getParameter("senderaccountnumber"), "UTF-8");

        Accounts receiverBuilder = new Accounts(receiverAccountnumber);
         Accounts senderBuilder = new Accounts(senderAccountnumber);
        if (receiverBuilder.account == null){
            receiverBuilder = new Company(receiverAccountnumber);
        }
        if (senderBuilder.account == null){
            senderBuilder = new Company(senderAccountnumber);
        }

        String senderPublicKeyStr = senderBuilder.getPublicKeyStr();
        String receiverPublicKeyStr = receiverBuilder.getPublicKeyStr();
        String outputStr = URLEncoder.encode(senderPublicKeyStr + "ò" + receiverPublicKeyStr, "UTF-8");
        resp.getWriter().println(outputStr);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String senderPurpose = URLDecoder.decode(req.getParameter("senderpurpose"), "UTF-8");
        String receiverPurpose = URLDecoder.decode(req.getParameter("receiverpurpose"), "UTF-8");
        float amount = Float.parseFloat(URLDecoder.decode(req.getParameter("amount"), "UTF-8"));
        String receiverAccountnumber = URLDecoder.decode(req.getParameter("receiveraccountnumber"), "UTF-8");
        String senderAccountnumber = URLDecoder.decode(req.getParameter("senderaccountnumber"), "UTF-8");

        Accounts receiverBuilder = new Accounts(receiverAccountnumber);
        Accounts senderBuilder = new Accounts(senderAccountnumber);
        if (receiverBuilder.account == null){
            receiverBuilder = new Company(receiverAccountnumber);
        }
        if (senderBuilder.account == null){
            senderBuilder = new Company(senderAccountnumber);
        }

        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", resp.getLocale());
        Calendar calendar = Calendar.getInstance();

        float senderBalance = senderBuilder.account != null ? Float.parseFloat(senderBuilder.getBalance()) : 0;
        float receiverBalance = receiverBuilder.account != null ? Float.parseFloat(receiverBuilder.getBalance()) : 0;

        if (receiverBuilder.account != null && Objects.equals(senderAccountnumber, receiverAccountnumber)){
            resp.getWriter().println("4");
        } else if (receiverBuilder.account != null && senderBalance >= amount && receiverBuilder.account != null){

            Transfers transferCreator = new Transfers(resp.getLocale());
            Entity transfer = transferCreator.createTransaction(f.format(calendar.getTime()));

            Transfers transferBuilder = new Transfers(transfer, resp.getLocale());
            transferBuilder.setSender(senderBuilder.account);
            transferBuilder.setReceiver(receiverBuilder.account);
            transferBuilder.setAmount(amount);
            transferBuilder.setDateTime();
            transferBuilder.setSenderPurpose(senderBuilder.account, senderPurpose);
            transferBuilder.setReceiverPurpose(receiverBuilder.account, receiverPurpose);
            transferBuilder.setType("Überweisung");
            transferBuilder.saveAll();

            Entity savedTransfer = transferBuilder.getTransfer(f.format(calendar.getTime()));
            senderBuilder.addTransfer(savedTransfer);
            senderBuilder.setBalance(senderBalance - amount);
            receiverBuilder.addTransfer(savedTransfer);
            receiverBuilder.setBalance(receiverBalance + amount);
            senderBuilder.saveAll();
            receiverBuilder.saveAll();

            resp.getWriter().println("3");
        } else if (receiverBuilder.account != null){ //Konto des Senders ist nicht ausreichend gedeckt
            resp.getWriter().println("1");
        } else { //Empfänger existiert nicht
            resp.getWriter().println("2");
        }
    }
}
