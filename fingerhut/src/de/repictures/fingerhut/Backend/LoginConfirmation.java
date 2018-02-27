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
import java.util.logging.Logger;

public class LoginConfirmation extends HttpServlet {

    private Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String sessionAccountnumber = req.getParameter("sessionaccountnumber");
        String webString = req.getParameter("webstring");

        Account sessionAccountGetter = new Account(sessionAccountnumber);
        Account accountGetter = new Account(accountnumber);

        log.info("Passed Webstring: " + webString + "\nSaved Webstring: " + sessionAccountGetter.getRandomWebString());
        /*if (!Objects.equals(webString, sessionAccountGetter.getRandomWebString())){
            log.info("hoi");
            resp.getWriter().println("hoi");
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            return;
        }*/
        log.info("hoi2");

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
        String sessionAccountnumber = req.getParameter("sessionaccountnumber");
        String companynumber = req.getParameter("companynumber");
        if (companynumber != null) companynumber = URLDecoder.decode(companynumber, "UTF-8");
        String inputHashedSaltedPassword = req.getParameter("password");
        String serverTimeStamp = req.getParameter("servertimestamp");
        if (serverTimeStamp != null) serverTimeStamp = URLDecoder.decode(serverTimeStamp, "UTF-8");
        String webString = req.getParameter("webstring");

        Account sessionAccountGetter = new Account(sessionAccountnumber);
        Account accountGetter = new Account(accountnumber);

        if(accountGetter.account == null){
            accountGetter = new Company(accountnumber);
            if (accountGetter.account == null) {
                resp.getWriter().println("4");
                return;
            }
        }

        Company companyGetter = null;
        resp.setStatus(HttpServletResponse.SC_OK);
        if (companynumber != null) companyGetter = new Company(companynumber);

        /*if (!Objects.equals(webString, sessionAccountGetter.getRandomWebString())){
            resp.getWriter().println("2");
            return;
        }*/

        String hashedSaltedPassword = accountGetter.getSaltedPassword(serverTimeStamp);
        log(serverTimeStamp);
        log(hashedSaltedPassword);
        log(inputHashedSaltedPassword);
        if (companynumber == null && !Objects.equals(inputHashedSaltedPassword, hashedSaltedPassword)){
            resp.getWriter().println("3");
            return;
        }

        resp.getWriter().println("1");
    }
}