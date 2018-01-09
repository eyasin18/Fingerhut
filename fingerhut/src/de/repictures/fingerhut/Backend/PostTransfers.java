package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Transfer;
import de.repictures.fingerhut.MultipartResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class PostTransfers extends HttpServlet {

    Cryptor cryptor = new Cryptor();
    private Logger log = Logger.getLogger(PostTransfers.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String startStr = req.getParameter("start");
        int start = 0;
        if (startStr != null) start = Integer.parseInt(startStr);
        boolean itemsLeft = true;

        Company companyGetter = new Company();
        Account accountGetter = new Account();
        Entity account = accountGetter.getAccount(accountnumber);

        //Überprüfe ob wir eine Valide Kontonummer bekommen haben
        if (account != null){
            StringBuilder output = new StringBuilder();
            //Lese die Verlinkungen zu den Überweisungen aus den Kontodaten
            List<String> transfersList = accountGetter.getTransfers(account);

            //Wenn es mindestens eine Verlinkung gibt, dann...
            if (transfersList != null && transfersList.size() > 0){
                //Lege den Ablesebereich für den Output fest
                log.info("Anzahl Überweisungen: " + transfersList.size());
                int end = start + 20;
                int size = transfersList.size();
                if (end >= transfersList.size()){
                    end = transfersList.size();
                }
                //Gehe durch jede Verlinkung einmal
                for (int i = start; i < end; i++){
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
                    Transfer transferGetter = new Transfer(resp.getLocale());
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
                    output.append(transferGetter.getDateTime(transfer));

                    char plusminus = '+';
                    //Ist derjenige, der gerade die Daten abfragt der Auftraggeber oder der Empfänger dieser Überweisung?
                    if (Objects.equals(senderAccountnumber, accountnumber)){
                        plusminus = '-';
                        output.append("ò");

                        //Lese den entschlüsselten Kontobesitzernamen aus dem Speicher und verschlüssele diesen asymetrisch mit dem öff. Schlüssel des Anfragenstellers
                        String ownerStr;
                        String publicKeyStr;
                        try {
                            ownerStr = accountGetter.getOwner(receiver);
                            publicKeyStr = accountGetter.getPublicKeyStr(account);
                        } catch (StringIndexOutOfBoundsException e){
                            ownerStr = companyGetter.getOwner(receiver);
                            publicKeyStr = companyGetter.getPublicKeyStr(account);
                        }
                        PublicKey publicKey = cryptor.stringToPublicKey(publicKeyStr);
                        byte[] owner = ownerStr.getBytes("ISO-8859-1");
                        byte[] encryptedOwner = cryptor.encryptAsymmetric(owner, publicKey);
                        output.append(cryptor.bytesToHex(encryptedOwner));
                        output.append("ò");

                        //Lese die Kontonummer des Empfängers
                        output.append(accountGetter.getAccountnumber(receiver));
                    } else {
                        output.append("ò");

                        //Lese den entschlüsselten Kontobesitzernamen aus dem Speicher und verschlüssele diesen asymetrisch mit dem öff. Schlüssel des Anfragenstellers
                        String ownerStr;
                        String publicKeyStr;
                        try {
                            ownerStr = accountGetter.getOwner(sender);
                            publicKeyStr = accountGetter.getPublicKeyStr(account);
                        } catch (StringIndexOutOfBoundsException e){
                            ownerStr = companyGetter.getOwner(sender);
                            publicKeyStr = companyGetter.getPublicKeyStr(account);
                        }
                        PublicKey publicKey = cryptor.stringToPublicKey(publicKeyStr);
                        byte[] owner = ownerStr.getBytes("ISO-8859-1");
                        byte[] encryptedOwner = cryptor.encryptAsymmetric(owner, publicKey);
                        output.append(cryptor.bytesToHex(encryptedOwner));
                        output.append("ò");

                        //Lese die Kontonummer des Auftraggebers
                        output.append(accountGetter.getAccountnumber(sender));
                    }
                    output.append("ò");
                    //Lese den Typ der Überweisung
                    output.append(transferGetter.getType(transfer));
                    output.append("ò");
                    //Ist derjenige, der gerade die Daten abfragt der Auftraggeber oder der Empfänger dieser Überweisung?
                    if (Objects.equals(accountnumber, senderAccountnumber)){
                        //Lese den Verwendungszweck der mit einem zufälligem Schlüssel verschlüsselt ist, der asymetrisch verschlüsselt auf dem Server liegt
                        output.append(transferGetter.getSenderPurpose(transfer).getValue());
                        output.append("ò");

                        //Lese den Schlüssel mit dem der Verwendungszweck verschlüsselt wurde
                        output.append(transferGetter.getSenderAesKey(transfer));
                        output.append("ò");

                        //Gebe dem Backend zurück, dass der Nutzer der Auftraggeber war
                        output.append("true");
                    } else if(Objects.equals(accountGetter.getAccountnumber(receiver), accountnumber)){
                        //Lese den Verwendungszweck der mit einem zufälligem Schlüssel verschlüsselt ist, der asymetrisch verschlüsselt auf dem Server liegt
                        output.append(transferGetter.getReceiverPurpose(transfer).getValue());
                        output.append("ò");

                        //Lese den Schlüssel mit dem der Verwendungszweck verschlüsselt wurde
                        output.append(transferGetter.getReceiverAesKey(transfer));
                        output.append("ò");

                        //Gebe dem Backend zurück, dass der Nutzer der Empfänger war
                        output.append("false");
                    } else {
                        output.append("You were not involved in this transfer!");
                    }
                    output.append("ò");
                    //Vorzeichen für die Überweisung
                    output.append(plusminus);
                    //Betrag des Geldes das geflossen ist
                    output.append(transferGetter.getAmount(transfer));
                    output.append("ň");
                }
                if (end >= size) itemsLeft = false;
            } else {
                output.append("ĵ");
            }
            //Werte werden zurückgegeben
            MultipartResponse multi = new MultipartResponse(resp);
            multi.startResponse("text/plain");
            resp.getOutputStream().println(URLEncoder.encode(output.toString(), "UTF-8"));
            multi.endResponse();
            multi.startResponse("text/plain");
            resp.getOutputStream().println(String.valueOf(itemsLeft));
            multi.endResponse();
            multi.finish();
        } else {
            MultipartResponse multi = new MultipartResponse(resp);
            multi.startResponse("text/plain");
            resp.getOutputStream().println(URLEncoder.encode("ĵ", "UTF-8"));
            multi.endResponse();
            multi.startResponse("text/plain");
            resp.getOutputStream().println(String.valueOf(false));
            multi.endResponse();
            multi.finish();
        }
    }
}
