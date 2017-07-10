package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

public class SaveCompany extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //URL-Parameter werden entgegen genommen
        String accountnumber = req.getParameter("accountnumber");
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        if (password != null){
            password = URLDecoder.decode(password, "UTF-8");
        }

        //Company wird erstellt
        Company company = new Company();
        company.postAccount(accountnumber, password, name);
        Entity createdCompany = company.getAccount(accountnumber);

        resp.getWriter().println("Kontonummer: " + company.getAccountnumber(createdCompany)
            + " Klartextpasswort: " + password);
    }
}
