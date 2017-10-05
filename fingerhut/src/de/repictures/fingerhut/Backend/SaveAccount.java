package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

public class SaveAccount extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //URL-Parameter werden angenommen
        String accountnumber = req.getParameter("accountnumber");
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        if(password != null){
            password = URLDecoder.decode(password, "UTF-8");
        } else {

        }

        //Account wird erstellt
        Accounts accounts = new Accounts();
        accounts.postAccount(accountnumber, password, name);
        Entity createdAccount = accounts.getAccount(accountnumber);

        //Ergebnis wird ausgegeben
        resp.getWriter().println("Kontonummer: " + accounts.getAccountnumber(createdAccount)
                + " Klartextpasswort: " + password);
    }
}
