package de.repictures.fingerhut.Admin;

import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AddEmployee extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String companynumber = req.getParameter("companynumber");
        String isCeoStr = req.getParameter("isceo");
        String removeStr = req.getParameter("remove");

        boolean isCeo = false;
        if (isCeoStr != null) isCeo = Boolean.valueOf(isCeoStr);
        boolean remove = false;
        if (removeStr != null) remove = Boolean.valueOf(removeStr);

        Company company = new Company(companynumber);
        if (company.account == null){
            resp.getWriter().println("Dieses Unternehmen existiert nicht!");
            return;
        }

        Account account = new Account(accountnumber);
        if (account.account == null){
            resp.getWriter().println("Ein Konto mit dieser Kontonummer existiert nicht!");
            return;
        }

        if (account.getIsPrepaid()){
            resp.getWriter().println("Dieses Konto ist ein Prepaidkonto und kann deshalb keinem Unternehmen zugewiesen werden");
            return;
        }

        if (!remove){
            account.addCompany(company.getAccountnumber());
            if (isCeo){
                ArrayList<Long> features = account.getSpecificFeatures(company.getAccountnumber());
                features.add(0L);
                features.add(2L);
                features.add(3L);
                features.add(4L);
                account.setFeatures(features, company.getAccountnumber());
            }
        } else {
            account.removeCompany(company.getAccountnumber());
            account.setFeatures(new ArrayList<>(), company.getAccountnumber());
        }
        account.saveAll();
        resp.getWriter().println("Erfolgreich");
    }
}