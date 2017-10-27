package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public class Login extends HttpServlet{

    private Logger log = Logger.getLogger(Account.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String passwordHash = req.getParameter("password");

        if (accountnumber == null || passwordHash == null){
            resp.getWriter().println("0");
            return;
        } else if (accountnumber.length() < 1 || passwordHash.length() < 1) {
            resp.getWriter().println("4");
            return;
        } else {
            passwordHash = passwordHash.toUpperCase(Locale.getDefault());
        }

        Account accountGetter = new Account(accountnumber);

        if (accountGetter.account == null) {
            resp.getWriter().println("3");
            return;
        }

        String savedPasswordHash = accountGetter.getHashedPassword();

        if (Objects.equals(savedPasswordHash, passwordHash)){
            accountGetter.updateRandomWebString();
            accountGetter.saveAll();
            resp.getWriter().println("1~" + accountGetter.getRandomWebString());
        } else {
            resp.getWriter().println("2");
        }
    }

}
