package de.repictures.fingerhut.Admin;

import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminTransfer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isSenderCompany = false;
        String senderAccountnumber = req.getParameter("sender");
        String receiverAccountnumber = req.getParameter("receiver");
        String amountStr = req.getParameter("amount");

        if (senderAccountnumber == null || receiverAccountnumber == null || amountStr == null){
            resp.getWriter().println("Du hasch da was ned mitgeßendet");
            return;
        }
        if (senderAccountnumber.length() < 1 || receiverAccountnumber.length() < 1 || amountStr.length() < 1){
            resp.getWriter().println("Du hasch da was ned mitgeßendet");
            return;
        }

        Account senderAccount = new Account(senderAccountnumber);
        if (senderAccount.account == null){
            senderAccount = new Company(senderAccountnumber);
            isSenderCompany = true;
        }
        if (senderAccount.account == null){
            resp.getWriter().println("Des Senderkondo gibts ed");
            return;
        }

        Account receiverAccount = new Account(receiverAccountnumber);
        if (receiverAccount.account == null){
            receiverAccount = new Company(receiverAccountnumber);
        }
        if (receiverAccount.account == null){
            resp.getWriter().println("Des Embfängerkondo gibts ed");
            return;
        }

        double amount = Double.valueOf(amountStr);
        if (amount < 0){
            resp.getWriter().println("Du kannsch nur positive Beträge als Betrag veschdlägen");
            return;
        }

        double senderBalance = senderAccount.getBalanceDouble();
        double receiverBalance = receiverAccount.getBalanceDouble();

        /*if ((isSenderCompany && accountHasNotEnoughMoney(senderBalance, amount)) || (!isSenderCompany && senderBalance < amount)){
            resp.getWriter().println("Der Sender hat ned gnug Gäld");
            return;
        }*/

        senderAccount.setBalance(senderBalance - amount);
        receiverAccount.setBalance(receiverBalance + amount);
        senderAccount.saveAll();
        receiverAccount.saveAll();
        resp.getWriter().println("Isch alles ogee");
    }

    private boolean accountHasNotEnoughMoney(double balance, double amountToBeSubtracted) {
        return Account.getCurrentMinutes() < Account.getMinutesFromValues(4, 12, 0)
                && (balance - amountToBeSubtracted) < -31.00

                || (balance - amountToBeSubtracted) < 0.00;
    }
}