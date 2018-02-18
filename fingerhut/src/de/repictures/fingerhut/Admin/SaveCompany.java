package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;

public class SaveCompany extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //URL-Parameter werden entgegen genommen
        String accountnumber = req.getParameter("accountnumber");
        if (accountnumber == null) accountnumber = Account.getUnusedAccountnumber();
        String name = req.getParameter("name");
        if (name != null) name = URLDecoder.decode(name, "UTF-8");
        String sectorStr = req.getParameter("sector");
        int sector = 0;
        if (sectorStr != null) sector = Integer.valueOf(sectorStr);
        String password = req.getParameter("password");
        if (password != null){
            password = URLDecoder.decode(password, "UTF-8");
        } else {
            Random rand = new Random();
            password = String.format("%04d", rand.nextInt(10000));
        }

        //Company wird erstellt
        Company company = new Company();
        company.postAccount(accountnumber, name, password, sector);
        Entity createdCompany = company.getAccount(accountnumber);

        resp.getWriter().println("Kontonummer: " + company.getAccountnumber(createdCompany)
            + " Klartextpasswort: " + password);
    }
}