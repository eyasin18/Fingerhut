package de.repictures.fingerhut.Backend;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Transfer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Objects;

public class ChangeMoney extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isCompany = false;
        String jsonStr = req.getParameter("infos");

        if (jsonStr == null){
            resp.getWriter().println(0);
            return;
        }
        jsonStr = URLDecoder.decode(jsonStr, "UTF-8");
        JsonObject requestObject = new JsonParser().parse(jsonStr).getAsJsonObject();

        if (!requestObject.has("accountnumber") || !requestObject.has("webstring") || !requestObject.has("companynumber")
                || !requestObject.has("mode") || !requestObject.has("amount")){
            resp.getWriter().println(0);
            return;
        }


        String userAccountnumber = requestObject.get("user_accountnumber").getAsString();
        String accountnumber = requestObject.get("accountnumber").getAsString();
        String webstring = requestObject.get("webstring").getAsString();
        String companynumber = requestObject.get("companynumber").getAsString();
        int mode = requestObject.get("mode").getAsInt();
        double amount = requestObject.get("amount").getAsDouble();

        Account userAccount = new Account(userAccountnumber);

        if (!Objects.equals(userAccount.getRandomWebString(), webstring)){
            resp.getWriter().println(0);
            return;
        }

        if (!userAccount.getSpecificFeatures(companynumber).contains(5L)){
            resp.getWriter().println(4);
            return;
        }

        Account account = new Account(accountnumber);
        if (account.account == null){
            account = new Company(accountnumber);
            if (account.account == null){
                resp.getWriter().println(5);
                return;
            }
            isCompany = true;
        }

        //Es kann nur von Unternehmenskonten Geld ausgezahlt werden
        if (!isCompany && mode == 1){
            resp.getWriter().println(2);
            return;
        }

        //Es können in Prepaidkonten keine krummen Beträge eingezahlt werden
        if (account.getIsPrepaid() && amount % 5 != 0){
            resp.getWriter().println(3);
            return;
        }

        double accountBalance = account.getBalanceDouble();
        if (mode == 0){
            accountBalance += amount;
            Transfer.payInOff(amount, true, account);
            new Company("0002").setEuroValue(amount);
        } else {
            if ((isCompany && companyHasNotEnoughMoney(accountBalance, amount)) || (!isCompany && accountBalance < amount)){
                resp.getWriter().println(4);
                return;
            }
            accountBalance -= amount;
            Transfer.payInOff(amount, false, account);
            new Company("0002").setEuroValue(amount*(-1));
        }
        account.setBalance(accountBalance);
        account.saveAll();

        resp.getWriter().println(1);
    }

    private boolean companyHasNotEnoughMoney(double companyBalance, double amountToBeSubtracted) {
        return Account.getCurrentMinutes() < Account.getMinutesFromValues(4, 12, 0)
                && (companyBalance - amountToBeSubtracted) < -31.00

                || (companyBalance - amountToBeSubtracted) < 0.00;
    }
}