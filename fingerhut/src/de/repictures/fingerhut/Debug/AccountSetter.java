package de.repictures.fingerhut.Debug;

import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//TODO: Klasse entfernen!!
public class AccountSetter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String owner = req.getParameter("owner");

        if (accountnumber == null){
            resp.getWriter().println("You have to pass a account number!");
            return;
        }
        Accounts accountSetter = new Accounts(accountnumber);

        if (owner != null){
            accountSetter.setOwner(owner);
        }
        accountSetter.saveAll();
        resp.getWriter().println("success");
    }
}
