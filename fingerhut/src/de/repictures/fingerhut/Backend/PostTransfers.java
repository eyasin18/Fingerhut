package de.repictures.fingerhut.Backend;

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
                //Gehe durch jede Verlinkung einmal
                for (String transferKeyStr : transfersList){
                    //Lese die Entität aus der Verlinkung
                    Entity transfer = accountGetter.getEntity(transferKeyStr);
                    //Wenn es keine Überweisungsdaten zu der Verlinkung gibt, dann springe zur nächsten Verlinkung
                    if (transfer == null) continue;
                    Transfer transferGetter = new Transfer(resp.getLocale());
                    //Lese den Zeitpunkt der Überweisung
                    output.append(transferGetter.getDateTime(transfer));
                    //Lese den Auftraggeber aus der Überweisung
                    Entity sender = transferGetter.getSender(transfer);
                    //Lese den Empfänger aus der Überweisung
                    Entity receiver = transferGetter.getReceiver(transfer);
                    //Lese die Kontonummer des Auftraggebers
                    String senderAccountnumber = sender.getProperty("accountnumber").toString();
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
                        byte[] encryptedOwner = cryptor.encryptAsymetric(owner, publicKey);
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
                        byte[] encryptedOwner = cryptor.encryptAsymetric(owner, publicKey);
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
            } else {
                output.append("ĵ");
            }
            //Werte werden zurückgegeben
            resp.getWriter().println(URLEncoder.encode(output.toString(), "UTF-8"));
        } else {
            resp.getWriter().println(URLEncoder.encode("ĵ", "UTF-8"));
        }
    }
}
