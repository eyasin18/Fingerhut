package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

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

public class PrivateLogin extends HttpServlet {

    private Logger log = Logger.getLogger(Account.class.getName());
    public static final int appVersion = 3;

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
        String appVersionStr = req.getParameter("appversion");
        int appVersion = 0;
        if (appVersionStr != null) appVersion = Integer.valueOf(appVersionStr);

        //Überprüfe ob alle Parameter empfangen wurden
        if (accountnumber == null || inputPassword == null || serverTimeStamp == null || authPart == null || deviceToken == null || appVersion == 0){
            resp.getWriter().println("-1");
            return;
        }

        //Überprüfe ob die App-Version aktuell ist
        if (appVersion < PrivateLogin.appVersion){
            resp.getWriter().println("-3");
            return;
        }

        //Finde Account mit dieser Accountnummber
        Account account = new Account();
        List<Entity> queriedAccounts = account.getAccounts(accountnumber);
        //Wenn du einen gefunden hast, dann...
        if (queriedAccounts.size() > 0){

            //Vergleiche den empfangenen Authentifizierungspart mit dem auf der Datenbank
            String authCode = account.getAuthString(queriedAccounts.get(0));
            int accountnumberlength = accountnumber.length();
            String[] authParts = {authCode.substring(accountnumberlength, accountnumberlength+8), authCode.substring(accountnumberlength+8, accountnumberlength+16)};
            if (!authParts[0].equals(authPart)){
                resp.getWriter().println("3");
                return;
            }

            //Vergleiche salte das gespeicherte Passwort und vergleiche es mit dem empfangenem Passwort
            String savedPassword = account.getSaltedPassword(queriedAccounts.get(0), serverTimeStamp);
            log.info("\nInputPassword: " + inputPassword + "\nSavedSaltedPassword: " + savedPassword + "\nSavedHashedPassword: " + account.getHashedPassword(queriedAccounts.get(0))
            + "\nServer Timestamp: " + serverTimeStamp);
            if (Objects.equals(savedPassword, inputPassword)){

                //Speichere das DeviceToken
                account.deleteDeviceTokenFromAllAccounts(deviceToken);
                account.setFirebaseDeviceToken(queriedAccounts.get(0), deviceToken);
                account.saveAll(queriedAccounts.get(0));

                //Gebe alle Kontonummern und die verschlüsselten Namen hinter den Kontonummern zurück
                StringBuilder output = new StringBuilder();
                account.updateRandomWebString(queriedAccounts.get(0));
                account.saveAll(queriedAccounts.get(0));
                output.append(account.getRandomWebString(queriedAccounts.get(0)));
                output.append("ò");
                //Gebe zurück, welche Berechtigungen der Nutzer hat
                ArrayList<Long> featuresList = account.getFeatures(queriedAccounts.get(0));
                for (Long feature : featuresList){
                    output.append(feature);
                    output.append("ň");
                }
                output.append("ò");
                account.setCompany(queriedAccounts.get(0), "0002");
                Company companyGetter = new Company(account.getCompany(queriedAccounts.get(0)));
                output.append(companyGetter.getAccountnumber());
                String response = "2ò" + account.getKey(queriedAccounts.get(0)) + "ò" + output.toString();
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
