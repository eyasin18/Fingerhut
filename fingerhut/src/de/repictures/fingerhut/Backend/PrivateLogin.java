package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
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
import java.util.*;
import java.util.logging.Logger;

public class PrivateLogin extends HttpServlet {

    private Logger log = Logger.getLogger(Account.class.getName());
    public static final int appVersion = 9;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Authenticate authenticate = new Authenticate();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String serverTimeStamp = dateFormat.format(cal.getTime());
        serverTimeStamp = URLEncoder.encode(serverTimeStamp, "UTF-8");
        authenticate.serverTimeStamp = serverTimeStamp;
        authenticate.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject object = new JsonObject();

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
        if (accountnumber == null || inputPassword == null || serverTimeStamp == null || deviceToken == null || appVersion == 0){
            object.addProperty("response_code", -1);
            resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
            return;
        }

        //Überprüfe ob die App-Version aktuell ist
        if (appVersion < PrivateLogin.appVersion){
            object.addProperty("response_code", -3);
            resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
            return;
        }

        //Finde Account mit dieser Accountnummber
        Account account = new Account();
        List<Entity> queriedAccounts = account.getAccounts(accountnumber);
        //Wenn du einen gefunden hast, dann...
        if (queriedAccounts.size() > 0){

            long failedAttempts = account.countUpLoginAttempts(queriedAccounts.get(0));
            if (!account.getIsPrepaid(queriedAccounts.get(0))) {
                //Vergleiche den empfangenen Authentifizierungspart mit dem auf der Datenbank
                String authCode = account.getAuthString(queriedAccounts.get(0));
                int accountnumberlength = accountnumber.length();
                String[] authParts = {authCode.substring(accountnumberlength, accountnumberlength + 8), authCode.substring(accountnumberlength + 8, accountnumberlength + 16)};
                if (!authParts[0].equals(authPart)) {
                    object.addProperty("response_code", 3);
                    resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
                    return;
                }

                Calendar cooldownTimeCalendar = account.getCooldownTime(queriedAccounts.get(0));
                if (cooldownTimeCalendar != null && cooldownTimeCalendar.after(Calendar.getInstance(Locale.getDefault()))){
                    SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.getDefault());
                    String cooldownTimeStr = f.format(cooldownTimeCalendar.getTime());
                    object.addProperty("response_code", 4);
                    object.addProperty("cooldown_time", cooldownTimeStr);
                    resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
                    return;
                }

                if (failedAttempts > 9){
                    object.addProperty("response_code", 5);
                    resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
                    return;
                }
            }

            //Vergleiche salte das gespeicherte Passwort und vergleiche es mit dem empfangenem Passwort
            String savedPassword = account.getSaltedPassword(queriedAccounts.get(0), serverTimeStamp);
            log.info("\nInputPassword: " + inputPassword + "\nSavedSaltedPassword: " + savedPassword + "\nSavedHashedPassword: " + account.getHashedPassword(queriedAccounts.get(0))
            + "\nServer Timestamp: " + serverTimeStamp);
            if (Objects.equals(savedPassword, inputPassword)){
                account.setLoginAttempts(queriedAccounts.get(0), 0);
                account.saveAll(queriedAccounts.get(0));

                //Speichere das DeviceToken
                account.deleteDeviceTokenFromAllAccounts(deviceToken);
                account.setFirebaseDeviceToken(queriedAccounts.get(0), deviceToken);
                account.saveAll(queriedAccounts.get(0));

                //Gebe alle Kontonummern und die verschlüsselten Namen hinter den Kontonummern zurück
                account.updateRandomWebString(queriedAccounts.get(0));
                account.saveAll(queriedAccounts.get(0));
                object.addProperty("random_web_string", account.getRandomWebString(queriedAccounts.get(0)));
                ArrayList<Long> featuresList = account.getFeatures(queriedAccounts.get(0));
                JsonArray featuresJsonArray = (JsonArray) gson.toJsonTree(featuresList, new TypeToken<List<Long>>() {}.getType());
                object.add("features", featuresJsonArray);
                JsonArray companyAccountnumbersJsonArray = new JsonArray();
                JsonArray companySectorsJsonArray = new JsonArray();
                JsonArray companyNamesJsonArray = new JsonArray();
                for (Entity companyEntity : account.getCompanies(queriedAccounts.get(0))){
                    Company companyGetter = new Company(companyEntity);
                    companyAccountnumbersJsonArray.add(companyGetter.getAccountnumber());
                    companySectorsJsonArray.add(companyGetter.getSector());
                    companyNamesJsonArray.add(companyGetter.getOwner());
                }
                object.add("company_accountnumbers", companyAccountnumbersJsonArray);
                object.add("company_sectors", companySectorsJsonArray);
                object.add("company_names", companyNamesJsonArray);
                object.addProperty("account_key", account.getKey(queriedAccounts.get(0)));
                object.addProperty("response_code", 2);
                object.addProperty("accountnumber", account.getAccountnumber(queriedAccounts.get(0)));
                object.addProperty("is_prepaid", account.getIsPrepaid(queriedAccounts.get(0)));
                resp.setStatus(200);
                resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
            } else {
                if (failedAttempts > 3){
                    double cooldownSeconds = Math.pow(2, failedAttempts);
                    Calendar cooldownTime = Calendar.getInstance(Locale.getDefault());
                    cooldownTime.add(Calendar.SECOND, (int) cooldownSeconds);
                    account.setCooldownTime(queriedAccounts.get(0), cooldownTime.getTime());
                    account.saveAll(queriedAccounts.get(0));
                    object.addProperty("response_code", 1);
                    object.addProperty("failed_attempts", failedAttempts);
                    resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
                } else {
                    object.addProperty("response_code", 1);
                    object.addProperty("failed_attempts", failedAttempts);
                    resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
                }
            }
        } else {
            object.addProperty("response_code", 0);
            resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
        }
    }
}
