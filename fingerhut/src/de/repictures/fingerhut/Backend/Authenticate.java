package de.repictures.fingerhut.Backend;

import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.logging.Logger;

public class Authenticate extends HttpServlet{

    String serverTimeStamp = "";
    private Logger log = Logger.getLogger(Account.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");

        Account accountBuilder = new Account(accountnumber);
        String authCode = accountBuilder.getAuthString();
        int accountnumberlength = accountnumber.length();
        String[] authParts = {authCode.substring(accountnumberlength, accountnumberlength+8),
                authCode.substring(accountnumberlength+8, accountnumberlength+16)};
        String output = URLEncoder.encode(authParts[1] + serverTimeStamp, "UTF-8");
        resp.getWriter().println(output);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String authPart = req.getParameter("authPart");

        Account accountBuilder = new Account(accountnumber);

        String authCode = accountBuilder.getAuthString();
        int accountnumberlength = accountnumber.length();
        String[] authParts = {authCode.substring(accountnumberlength, accountnumberlength+8),
                authCode.substring(accountnumberlength+8, accountnumberlength+16)};

        if (accountBuilder.account != null && Objects.equals(authParts[0], authPart)){
            String privateKey = accountBuilder.getPrivateKeyStr();
            resp.getWriter().println(privateKey);
        } else {
            log.warning("Falsche Authentifizierung!");
            resp.getWriter().println("0");
        }
    }
}