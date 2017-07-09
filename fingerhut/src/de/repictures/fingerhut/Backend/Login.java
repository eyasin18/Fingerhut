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
                StringBuilder accountsListStrB = new StringBuilder();
                for (Entity entity : accounts.getAllAccounts()){
                    accountsListStrB.append(accounts.getAccountnumber(entity));
                    accountsListStrB.append("ĵ");
                    accountsListStrB.append(accounts.getOwner(entity));
                    accountsListStrB.append("ň");
                }
                String response = "2ò" + accounts.getKey(queriedAccounts.get(0)) + "ò" + accountsListStrB.toString();
                resp.getWriter().println(URLEncoder.encode(response, "UTF-8"));
            } else {
                resp.getWriter().println("1");
            }
        } else {
            resp.getWriter().println("0");
        }
    }
}
