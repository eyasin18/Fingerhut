package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Random;

public class AddAccount extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Cryptor cryptor = new Cryptor();

        //URL-Parameter werden angenommen
        String accountnumber = req.getParameter("accountnumber");
        String password = req.getParameter("password");
        if(password == null){
            //Wenn kein Passwort übergeben wurde, dann soll es zufällig generiert werden
            Random rand = new Random();
            password = String.format("%04d", rand.nextInt(10000));
        } else {
            URLDecoder.decode(password, "UTF-8");
        }
        String encryptedPassword = cryptor.hashToString(password);

        //Name wird verschlüsselt
        String plainName = req.getParameter("name");
        byte[] encryptedByteName = cryptor.encrypt(plainName, cryptor.hashToByte(password));
        plainName = cryptor.bytesToHex(encryptedByteName);

        //Es wird ein Key aus der accountnumber erzeugt. Der Name "accountnumber" ist dem Wert übergeordnet
        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        //Es wird ein Servereintrag "Account" erstellt. Der Key ist diesem übergeordnet
        Entity account = new Entity("Account", loginKey);
        //Werte werden Servereintrag zugeordnet
        account.setProperty("accountnumber", accountnumber);
        account.setProperty("password", encryptedPassword);
        account.setProperty("owner", plainName);
        account.setProperty("balance", 100.00);
        account.setProperty("transferarray", new ArrayList<String>());
        //Eintrag wird gespeichert
        datastore.put(account);
        //Ergebnis wird an Anfragensteller zurückgegeben
        resp.getWriter().println("Klartextpasswort: " + password + "\nVerschluesseltes Passwort: " + encryptedPassword);
    }
}