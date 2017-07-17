package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Parameter die dem Server bei der Anfrage übergeben werden
        String accountnumber = req.getParameter("accountnumber");
        if (accountnumber != null) accountnumber = URLDecoder.decode(accountnumber, "UTF-8");
        String inputPassword = req.getParameter("password");
        if (inputPassword != null) {
            inputPassword = URLDecoder.decode(inputPassword, "UTF-8");
            Cryptor cryptor = new Cryptor();
            inputPassword = cryptor.bytesToHex(cryptor.hashToByte(inputPassword));
        }

        Accounts accounts = new Accounts();
        List<Entity> queriedAccounts = accounts.getAccounts(accountnumber);
        if (queriedAccounts.size() > 0){
            String savedPassword = accounts.getPassword(queriedAccounts.get(0));
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
