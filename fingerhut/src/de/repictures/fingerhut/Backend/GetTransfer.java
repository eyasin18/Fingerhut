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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class GetTransfer extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", resp.getLocale());;
        Calendar calendar = Calendar.getInstance();

        //Parameter die dem Server bei der Anfrage übergeben werden
        String receiverAccountnumber = URLDecoder.decode(req.getParameter("receiveraccountnumber"), "UTF-8");
        String senderAccountnumber = URLDecoder.decode(req.getParameter("senderaccountnumber"), "UTF-8");
        String intendedPurpose = URLDecoder.decode(req.getParameter("intendedpurpose"), "UTF-8");
        float amount = Float.parseFloat(URLDecoder.decode(req.getParameter("amount"), "UTF-8"));

        Accounts receiverBuilder = new Accounts();
        Accounts senderBuilder = new Accounts();
        Entity receiver = receiverBuilder.getAccount(receiverAccountnumber);
        Entity sender = senderBuilder.getAccount(senderAccountnumber);

        if (receiver == null){
            receiverBuilder = new Company();
            receiver = receiverBuilder.getAccount(receiverAccountnumber);
        }
        if (sender == null){
            senderBuilder = new Company();
            sender = senderBuilder.getAccount(senderAccountnumber);
        }

        float senderBalance = sender != null ? Float.parseFloat(senderBuilder.getBalance(sender)) : 0;
        float receiverBalance = receiver != null ? Float.parseFloat(receiverBuilder.getBalance(receiver)) : 0;

        if (Objects.equals(senderAccountnumber, receiverAccountnumber)){
            resp.getWriter().println("4");
        } else if (senderBalance >= amount && receiver != null){

            Transfers transferCreator = new Transfers(resp.getLocale());
            Entity transfer = transferCreator.createTransaction(f.format(calendar.getTime()));

            Transfers transferBuilder = new Transfers(transfer, resp.getLocale());
            transferBuilder.setSender(sender);
            transferBuilder.setReceiver(receiver);
            transferBuilder.setAmount(amount);
            transferBuilder.setDateTime();
            transferBuilder.setPurpose(sender, intendedPurpose);
            transferBuilder.setType("Überweisung");
            transferBuilder.saveAll();

            Entity savedTransfer = transferBuilder.getTransfer(f.format(calendar.getTime()));
            senderBuilder.addTransfer(sender, savedTransfer);
            senderBuilder.setBalance(sender, senderBalance - amount);
            receiverBuilder.addTransfer(receiver, savedTransfer);
            receiverBuilder.setBalance(receiver, receiverBalance + amount);
            senderBuilder.saveAll(sender);
            receiverBuilder.saveAll(receiver);

            resp.getWriter().println("3");
        } else if (receiver != null){ //Konto des Senders ist nicht ausreichend gedeckt
            resp.getWriter().println("1");
        } else { //Empfänger existiert nicht
            resp.getWriter().println("2");
        }
    }
}
