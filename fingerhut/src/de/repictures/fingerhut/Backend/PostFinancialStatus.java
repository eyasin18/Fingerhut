package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class PostFinancialStatus extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Parameter werden entgegen genommen
        String accountKey = req.getParameter("accountkey");

        Accounts accounts = new Accounts();

        Entity account = accounts.getAccount(KeyFactory.stringToKey(accountKey));
        if (account != null){
            String output = accounts.getAccountnumber(account) +
                    "ò" +
                    accounts.getOwner(account) +
                    "ò" +
                    accounts.getBalance(account);
            resp.getWriter().println(URLEncoder.encode(output, "UTF-8"));
        } else {
            resp.getWriter().println("nf");
        }
    }
}
