package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class Login extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String passwordHash = req.getParameter("password");

        if (accountnumber == null || passwordHash == null){
            resp.getWriter().println("0");
        }

        Accounts accountGetter = new Accounts(accountnumber);
        String savedPasswordHash = accountGetter.getHashedPassword();

        if (Objects.equals(savedPasswordHash, passwordHash)){
            resp.getWriter().println("1");
        } else {
            resp.getWriter().println("2");
        }
    }

}
