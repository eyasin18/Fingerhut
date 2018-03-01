package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Tax;
import de.repictures.fingerhut.Datastore.Transfer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AdminTransferWage extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String companyAccountnumber = req.getParameter("company");
        String employeeAccountnumber = req.getParameter("employee");
        String amountStr = req.getParameter("amount");
        String hoursStr = req.getParameter("hours");

        if (companyAccountnumber == null || employeeAccountnumber == null || amountStr == null || hoursStr == null){
            resp.getWriter().println(0);
            return;
        }

        double amount = Double.valueOf(amountStr);
        int hours = Integer.valueOf(hoursStr);

        Account account = new Account(employeeAccountnumber);
        if (account.account == null){
            resp.getWriter().println(1);
            return;
        }

        Company company = new Company(companyAccountnumber);
        if (company.account == null){
            resp.getWriter().println(2);
            return;
        }

        if (!account.getCompanies().contains(company.account)){
            resp.getWriter().println(4);
            return;
        }

        if (account.getWorkedHours().intValue() > 7 || account.getWorkedHours().intValue() + hours > 7){
            resp.getWriter().println(5);
            return;
        }
        account.setWorkedHours(account.getWorkedHours().intValue() + hours);

        Company fm = new Company("0098");

        double fractionalPart = amount % 1;
        double integralPart = (double) (amount - fractionalPart);
        List<Long> taxList = Tax.getWageTax();

        //Prozentsatz berechnen
        double integralPercentage = 0;
        for (int i = 0; i < integralPart; i++){
            if (i < taxList.size()) integralPercentage += taxList.get(i);
            else integralPercentage += 100;
        }
        integralPercentage = (integralPercentage/integralPart);
        double fractionPercentage = 0;
        if (integralPart < taxList.size()) fractionPercentage = taxList.get((int) integralPart);
        else fractionPercentage = 100;

        //Brutto in Netto und Abgabe spalten
        double tax = (((double) integralPart) * (double) (integralPercentage/100) + (fractionalPart * ((double) fractionPercentage/100)));
        double netWage = (amount - tax);

        for (int i = 0; i < hours; i++){
            company.setBalance(company.getBalanceDouble()-(amount));

            fm.setBalance(fm.getBalanceDouble() + (tax));

            account.setBalance(account.getBalanceDouble() + (netWage));

            Entity transferEntity = Transfer.transferWage(netWage, tax, false, company, account);
            account.addTransfer(transferEntity);
            fm.addTransfer(transferEntity);
            company.addTransfer(transferEntity);
            account.saveAll();
            fm.saveAll();
            company.saveAll();
        }
        resp.getWriter().println(3);

        //resp.getWriter().println("Nettolohn pro Stunde: " + netWage + "S\nLohnsteuer pro Stunde: " + tax + "S\nNettolohn gesamt: " + netWage*hours + "S\nLohnsteuer gesamt: " + tax*hours + "S");
    }
}
