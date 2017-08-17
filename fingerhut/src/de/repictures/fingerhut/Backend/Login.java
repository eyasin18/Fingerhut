package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
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

        Accounts accounts = new Accounts();
        List<Entity> queriedAccounts = accounts.getAccounts(accountnumber);
        if (queriedAccounts.size() > 0){
            String savedPassword = accounts.getSaltedPassword(queriedAccounts.get(0), serverTimeStamp);
            log.info("Input Password: " + inputPassword + "\nSavedPassword: " + savedPassword + "\nKlarpasswort: " + accounts.getHashedPassword(queriedAccounts.get(0)));
            if (Objects.equals(savedPassword, inputPassword)){
                StringBuilder output = new StringBuilder();
                for (Entity entity : accounts.getAllAccounts()){
                    output.append(accounts.getAccountnumber(entity));
                    output.append("ĵ");
                    output.append(accounts.getOwner(entity));
                    output.append("ň");
                }
                output.append("ò");
                ArrayList<Long> featuresList = accounts.getFeatures(queriedAccounts.get(0));
                for (Long feature : featuresList){
                    output.append(feature);
                    output.append("ň");
                }
                String response = "2ò" + accounts.getKey(queriedAccounts.get(0)) + "ò" + output.toString();
                resp.getWriter().println(URLEncoder.encode(response, "UTF-8"));
            } else {
                resp.getWriter().println("1");
            }
        } else {
            resp.getWriter().println("0");
        }
    }
}
