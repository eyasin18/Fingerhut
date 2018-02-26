package de.repictures.fingerhut.Admin;

import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResetLockedAccount extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");

        if (accountnumber == null || accountnumber.length() < 1){
            resp.getWriter().println("Kontonummerfeld muss ausgefüllt sein");
            return;
        }

        Account account = new Account(accountnumber);
        if (account.account == null){
            resp.getWriter().println("Konto existiert nicht");
            return;
        }

        account.setLoginAttempts(0);
        account.saveAll();
        resp.getWriter().println("Erfolgreich");
    }
}
