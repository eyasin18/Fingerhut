package de.repictures.fingerhut.Backend;

import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.logging.Logger;

public class PostFinancialStatus extends HttpServlet {

    private Logger log = Logger.getLogger(PostFinancialStatus.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Parameter werden entgegen genommen
        String accountnumber = req.getParameter("accountnumber");
        String webstring = req.getParameter("webstring");

        Accounts accounts = new Accounts(accountnumber);

        if (accounts.account != null){

            String savedWebString = accounts.getRandomWebString();
            log.info("Saved Web String: " + savedWebString + "\nPassed Web String: " + webstring);
            if (!Objects.equals(webstring, savedWebString)){
                resp.getWriter().println("2");
                return;
            }

            String output = "1ò" +
                    accounts.getAccountnumber() +
                    "ò" +
                    accounts.getEncryptedOwner() +
                    "ò" +
                    accounts.getBalance();
            resp.getWriter().println(URLEncoder.encode(output, "UTF-8"));
        } else {
            resp.getWriter().println("0");
        }
    }
}