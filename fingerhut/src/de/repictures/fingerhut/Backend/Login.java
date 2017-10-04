package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class Login extends HttpServlet {

    private Logger log = Logger.getLogger(Accounts.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Authenticate authenticate = new Authenticate();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String serverTimeStamp = dateFormat.format(cal.getTime());
        serverTimeStamp = URLEncoder.encode(serverTimeStamp, "UTF-8");
        authenticate.serverTimeStamp = "ò" + serverTimeStamp;
        authenticate.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Parameter die dem Server bei der Anfrage übergeben werden
        String accountnumber = req.getParameter("accountnumber");
        if (accountnumber != null) accountnumber = URLDecoder.decode(accountnumber, "UTF-8");
        String inputPassword = req.getParameter("password");
        String serverTimeStamp = req.getParameter("servertimestamp");
        if (serverTimeStamp != null) serverTimeStamp = URLDecoder.decode(serverTimeStamp, "UTF-8");
        String authPart = req.getParameter("authPart");
        String deviceToken = req.getParameter("token");
        if (deviceToken != null) deviceToken = URLDecoder.decode(deviceToken, "UTF-8");

        //Überprüfe ob alle Parameter empfangen wurden
        if (accountnumber == null || inputPassword == null || serverTimeStamp == null || authPart == null || deviceToken == null){
            resp.getWriter().println("-1");
            return;
        }

        //Finde Account mit dieser Accountnummber
        Accounts accounts = new Accounts();
        List<Entity> queriedAccounts = accounts.getAccounts(accountnumber);
        //Wenn du einen gefunden hast, dann...
        if (queriedAccounts.size() > 0){

            //Vergleiche den empfangenen Authentifizierungspart mit dem auf der Datenbank
            String authCode = accounts.getAuthString(queriedAccounts.get(0));
            int accountnumberlength = accountnumber.length();
            String[] authParts = {authCode.substring(accountnumberlength, accountnumberlength+8), authCode.substring(accountnumberlength+8, accountnumberlength+16)};
            if (!authParts[0].equals(authPart)){
                resp.getWriter().println("3");
                return;
            }

            //Vergleiche salte das gespeicherte Passwort und vergleiche es mit dem empfangenem Passwort
            String savedPassword = accounts.getSaltedPassword(queriedAccounts.get(0), serverTimeStamp);
            log.info("\nInputPassword: " + inputPassword + "\nSavedSaltedPassword: " + savedPassword + "\nSavedHashedPassword: " + accounts.getHashedPassword(queriedAccounts.get(0))
            + "\nServer Timestamp: " + serverTimeStamp);
            if (Objects.equals(savedPassword, inputPassword)){

                //Speichere das DeviceToken
                accounts.deleteDeviceTokenFromAllAccounts(deviceToken);
                accounts.setFirebaseDeviceToken(queriedAccounts.get(0), deviceToken);
                accounts.saveAll(queriedAccounts.get(0));

                //Gebe alle Kontonummern und die verschlüsselten Namen hinter den Kontonummern zurück
                StringBuilder output = new StringBuilder();
                accounts.updateRandomWebString(queriedAccounts.get(0));
                accounts.saveAll(queriedAccounts.get(0));
                output.append(accounts.getRandomWebString(queriedAccounts.get(0)));
                output.append("ò");
                //Gebe zurück, welche Berechtigungen der Nutzer hat
                ArrayList<Long> featuresList = accounts.getFeatures(queriedAccounts.get(0));
                for (Long feature : featuresList){
                    output.append(feature);
                    output.append("ň");
                }
                output.append("ò");
                accounts.setGroup(queriedAccounts.get(0), 7);
                output.append(accounts.getGroup(queriedAccounts.get(0)));
                String response = "2ò" + accounts.getKey(queriedAccounts.get(0)) + "ò" + output.toString();
                resp.setStatus(200);
                resp.getWriter().println(URLEncoder.encode(response, "UTF-8"));
            } else {
                resp.getWriter().println("1");
            }
        } else {
            resp.getWriter().println("0");
        }
    }
}
