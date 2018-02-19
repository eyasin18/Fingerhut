package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class CheckWebstring extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String webstring = req.getParameter("webstring");

        Account account = new Account(accountnumber);
        log("\ngw: " + webstring + "\nsw: " + account.getRandomWebString());
        if (!Objects.equals(webstring, account.getRandomWebString())){
            resp.getWriter().println(0);
        } else {
            resp.getWriter().println(1);
        }
    }
}