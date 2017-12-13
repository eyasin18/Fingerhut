package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Objects;

public class CompanyLogin extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String companynumber = req.getParameter("companynumber");
        String accountnumber = req.getParameter("accountnumber");
        String webstring = req.getParameter("webstring");
        String inputPassword = req.getParameter("password");
        if (inputPassword != null) {
            inputPassword = URLDecoder.decode(inputPassword, "UTF-8");
        }

        Account accountGetter = new Account(accountnumber);
        if (!Objects.equals(webstring, accountGetter.getRandomWebString())){
            resp.getWriter().println("2");
            return;
        }

        Company companyGetter = new Company();
        Entity company = companyGetter.getAccount(companynumber);
        if (company == null){
            resp.getWriter().println("0");
            return;
        }

        String savedPassword = companyGetter.getHashedPassword(company);

        if (Objects.equals(inputPassword, savedPassword)){
            resp.getWriter().println("1");
        } else {
            resp.getWriter().println("3");
        }
    }
}
