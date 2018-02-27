package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Transfer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class PostTransfers extends HttpServlet {

    private Logger log = Logger.getLogger(PostTransfers.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String startStr = req.getParameter("start");
        String webstring = req.getParameter("code");
        int start = 0;
        if (startStr != null) start = Integer.parseInt(startStr);
        boolean itemsLeft = true;

        JsonObject responseObject = new JsonObject();
        Account accountGetter = new Account();
        Entity account = accountGetter.getAccount(accountnumber);

        /*if (!Objects.equals(accountGetter.getRandomWebString(account), webstring)){
            responseObject.addProperty("response_code", 0);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
        }*/

        //Überprüfe ob wir eine Valide Kontonummer bekommen haben
        if (account != null){
            //Lese die Verlinkungen zu den Überweisungen aus den Kontodaten
            List<String> transfersList = accountGetter.getTransfers(account);

            //Wenn es mindestens eine Verlinkung gibt, dann...
            if (transfersList != null && transfersList.size() > 0){
                //Lege den Ablesebereich für den Output fest
                JsonArray transfersArray = new JsonArray();
                log.info("Anzahl Überweisungen: " + transfersList.size());
                int end = start + 20;
                int size = transfersList.size();
                if (end >= transfersList.size()){
                    end = transfersList.size();
                }
                //Gehe durch jede Verlinkung einmal
                for (int i = start; i < end; i++){
                    JsonObject transferObject = new JsonObject();
                    String transferKeyStr = transfersList.get(i);
                    //Lese die Entität aus der Verlinkung
                    Entity transfer = accountGetter.getEntity(transferKeyStr);
                    //Wenn es keine Überweisungsdaten zu der Verlinkung gibt, dann springe zur nächsten Verlinkung
                    if (transfer == null){
                        transfersList.remove(i);
                        accountGetter.setTransfers(account, transfersList);
                        accountGetter.saveAll(account);
                        i--;
                        end--;
                        size--;
                        continue;
                    }
                    Transfer transferGetter = new Transfer(transfer, resp.getLocale());
                    //Lese den Auftraggeber aus der Überweisung
                    Entity sender = transferGetter.getSender(transfer);
                    //Lese den Empfänger aus der Überweisung
                    Entity receiver = transferGetter.getReceiver(transfer);
                    //Wenn Sender oder Empfänger nicht (mehr) existieren: springe zur nächsten Verlinkung
                    if (sender == null || receiver == null){
                        transfersList.remove(i);
                        accountGetter.setTransfers(account, transfersList);
                        accountGetter.saveAll(account);
                        i--;
                        end--;
                        size--;
                        continue;
                    }

                    //Lese die Kontonummer des Auftraggebers
                    String senderAccountnumber = sender.getProperty("accountnumber").toString();
                    //Lese den Zeitpunkt der Überweisung
                    transferObject.addProperty("date_time", transferGetter.getDateTime());

                    char plusminus = '+';
                    //Ist derjenige, der gerade die Daten abfragt der Auftraggeber oder der Empfänger dieser Überweisung?
                    if (Objects.equals(senderAccountnumber, accountnumber)){
                        plusminus = '-';

                        //Lese den verschlüsselten Kontobesitzernamen aus dem Speicher
                        transferObject.addProperty("name", transferGetter.getReceiverNameForSender());

                        //Lese die Kontonummer des Empfängers
                        transferObject.addProperty("accountnumber", accountGetter.getAccountnumber(receiver));
                    } else {
                        //Lese den verschlüsselten Kontobesitzernamen aus dem Speicher
                        transferObject.addProperty("name", transferGetter.getSenderNameForReceiver());

                        //Lese die Kontonummer des Auftraggebers
                        transferObject.addProperty("accountnumber", accountGetter.getAccountnumber(sender));
                    }
                    //Lese den Typ der Überweisung
                    transferObject.addProperty("type", transferGetter.getType());
                    //Ist derjenige, der gerade die Daten abfragt der Auftraggeber oder der Empfänger dieser Überweisung?
                    if (Objects.equals(accountnumber, senderAccountnumber)){
                        //Lese den Verwendungszweck der mit einem zufälligem Schlüssel verschlüsselt ist, der asymetrisch verschlüsselt auf dem Server liegt
                        transferObject.addProperty("purpose", transferGetter.getSenderPurpose().getValue());

                        //Lese den Schlüssel mit dem der Verwendungszweck verschlüsselt wurde
                        transferObject.addProperty("aes", transferGetter.getSenderAesKey());

                        //Gebe dem Backend zurück, dass der Nutzer der Auftraggeber war
                        transferObject.addProperty("is_sender", true);
                    } else if(Objects.equals(accountGetter.getAccountnumber(receiver), accountnumber)){
                        //Lese den Verwendungszweck der mit einem zufälligem Schlüssel verschlüsselt ist, der asymetrisch verschlüsselt auf dem Server liegt
                        transferObject.addProperty("purpose", transferGetter.getReceiverPurpose().getValue());

                        //Lese den Schlüssel mit dem der Verwendungszweck verschlüsselt wurde
                        transferObject.addProperty("aes", transferGetter.getReceiverAesKey());

                        //Gebe dem Backend zurück, dass der Nutzer der Empfänger war
                        transferObject.addProperty("is_sender", false);
                    }
                    transferObject.addProperty("amount", Double.valueOf(plusminus + String.valueOf(transferGetter.getAmount())));
                    transfersArray.add(transferObject);
                }
                responseObject.add("transfers", transfersArray);
                if (end >= size) itemsLeft = false;
                responseObject.addProperty("response_code", 1);
                responseObject.addProperty("items_left", itemsLeft);
                resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            } else {
                responseObject.addProperty("response_code", 3);
                resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            }
        } else {
            responseObject.addProperty("response_code", 2);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
        }
    }
}