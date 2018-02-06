package de.repictures.fingerhut.Admin;

import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EditBalance extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String amountStr = req.getParameter("amount");
        if (amountStr == null || accountnumber == null || accountnumber.length() < 4 || amountStr.length() == 0){
            resp.getWriter().println("Da hasch du was ned ausgefüllt");
            return;
        }

        Account accountGetter = new Account(accountnumber);
        if (accountGetter.account == null){
            accountGetter = new Company(accountnumber);
        }
        if (accountGetter.account == null){
            resp.getWriter().println("Diesen Account/Unternähmen gibts ed");
            return;
        }

        double amount = Double.valueOf(amountStr);
        accountGetter.setBalance(amount);
        accountGetter.saveAll();
        resp.getWriter().println("Isch alles ogee");
    }
}