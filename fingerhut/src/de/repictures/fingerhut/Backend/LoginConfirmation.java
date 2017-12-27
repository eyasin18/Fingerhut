package de.repictures.fingerhut.Backend;

import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class LoginConfirmation extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String webString = req.getParameter("webstring");

        Account accountGetter = new Account(accountnumber);

        if (!Objects.equals(webString, accountGetter.getRandomWebString())){
            resp.getWriter().println();
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String serverTimeStamp = dateFormat.format(cal.getTime());
        serverTimeStamp = URLEncoder.encode(serverTimeStamp, "UTF-8");
        resp.getWriter().println(serverTimeStamp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        if (accountnumber != null) accountnumber = URLDecoder.decode(accountnumber, "UTF-8");
        String companynumber = req.getParameter("companynumber");
        if (companynumber != null) companynumber = URLDecoder.decode(companynumber, "UTF-8");
        String inputHashedSaltedPassword = req.getParameter("password");
        String serverTimeStamp = req.getParameter("servertimestamp");
        if (serverTimeStamp != null) serverTimeStamp = URLDecoder.decode(serverTimeStamp, "UTF-8");
        String webString = req.getParameter("webstring");

        Account accountGetter = new Account(accountnumber);
        Company companyGetter = null;
        if (companynumber != null) companyGetter = new Company(companynumber);

        if (!Objects.equals(webString, accountGetter.getRandomWebString())){
            resp.getWriter().println(2);
            return;
        }

        if (companynumber == null && !Objects.equals(inputHashedSaltedPassword, accountGetter.getSaltetPassword(serverTimeStamp))){
            resp.getWriter().println(3);
            return;
        }

        if (companynumber != null && !Objects.equals(inputHashedSaltedPassword, companyGetter.getSaltetPassword(serverTimeStamp))){
            resp.getWriter().println(3);
            return;
        }

        resp.getWriter().println(1);
    }
}