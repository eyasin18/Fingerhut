package de.repictures.fingerhut.Datastore;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import de.repictures.fingerhut.Cryptor;

public class LoginServlet extends HttpServlet{

    //Logs können in der CloudConsole unter "Logs" eingesehen werden
    private Logger log = Logger.getLogger(PostTransfers.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        //Verschlüsselungsklasse wird initialisiert
        Cryptor cryptor = new Cryptor();

        //Parameter die dem Server bei der Anfrage übergeben werden
        String accountnumber = req.getParameter("accountnumber");
        if (accountnumber != null) accountnumber = URLDecoder.decode(accountnumber, "UTF-8");
        String inputPassword = req.getParameter("password");
        if (inputPassword != null) {
            inputPassword = URLDecoder.decode(inputPassword, "UTF-8");
            log.info("Ankommendes Passwort: " + inputPassword);
            inputPassword = cryptor.hashToString(inputPassword);
        }
        //Accountinformationen werden geladen
        respondAccountInformation(resp, datastore, cryptor, accountnumber, inputPassword);
    }

    private void respondAccountInformation(HttpServletResponse resp, DatastoreService datastore, Cryptor cryptor, String accountnumber, String inputPassword) throws IOException {
        //Es wird ein Key aus der accountnumber erzeugt. Der Name "accountnumber" ist dem Wert übergeordnet
        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        //Der Server wird nun nach einem Eintrag durchsucht, der "Account" heißt und dem der obige Key übergeordnet ist
        Query loginQuery = new Query("Account", loginKey);
        //Gefundene Einträge werden in die Liste gespeichert
        List<Entity> accountList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        //Die Query kann entweder genau einen oder keinen Eintrag finden, da wir nach einem für jeden Eintrag individuellen Wert gesucht haben (Die Kontonummer)
        //Wenn die Query genau einen Wert findet, dann...
        if(accountList.size() > 0){
            Entity account = accountList.get(0);
            //Passwort wird aus dem Eintrag ausgelesen
            String accountPassword = account.getProperty("password").toString();
            log.info("Accountpasswort: " + accountPassword + " Angekommenes Passwort: " + inputPassword);
            //Wenn Passwort das den Server vorliegt das gleiche ist, wie vom Nutzer gesendet wurde dann
            if(accountPassword.equals(inputPassword)){
                String response = "2ò" + KeyFactory.keyToString(account.getKey()) + "ò" + getAccounts(datastore, cryptor);
                resp.getWriter().println(URLEncoder.encode(response, "UTF-8"));
            } else {
                resp.getWriter().println("1");
            }
        } else {
            resp.getWriter().println("0");
        }
    }

    //Methode die alle Accounts auf dem Server ausliest
    private String getAccounts(DatastoreService datastore, Cryptor cryptor){
        StringBuilder output = new StringBuilder();
        Query query = new Query("Account");
        for (Entity entity : datastore.prepare(query).asIterable()) {
            output.append(entity.getProperty("accountnumber"));
            output.append("ĵ");

            //Name wird entschlüsselt
            String encryptedNameStr = (String) entity.getProperty("owner");
            byte[] encryptedName = cryptor.hexToBytes(encryptedNameStr);
            String encryptedHexPasswordStr = (String) entity.getProperty("password");
            byte[] encryptedPassword = cryptor.hexToBytes(encryptedHexPasswordStr);
            output.append(cryptor.decrypt(encryptedName, encryptedPassword));

            output.append("ň");
        }
        return output.toString();
    }
}
