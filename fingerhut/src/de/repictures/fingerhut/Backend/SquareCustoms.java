package de.repictures.fingerhut.Backend;

import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Tax;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;

public class SquareCustoms extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String webstring = req.getParameter("webstring");
        String companynumber = req.getParameter("companynumber");
        String priceStr = req.getParameter("price");
        String meatStr = req.getParameter("meat");
        String bioMeatStr = req.getParameter("biomeat");
        String packageStr = req.getParameter("package");

        JsonObject responseObject = new JsonObject();

        if (accountnumber == null || webstring == null || companynumber == null || priceStr == null || meatStr == null || packageStr == null || bioMeatStr == null){
            responseObject.addProperty("response_code", 0);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        double price = Double.valueOf(priceStr);
        boolean hasMeat = Boolean.valueOf(meatStr);
        boolean hasPackage = Boolean.valueOf(packageStr);
        boolean hasBioMeat = Boolean.valueOf(bioMeatStr);

        Account account = new Account(accountnumber);
        if (account.account == null){
            responseObject.addProperty("response_code", 0);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        if (!Objects.equals(account.getRandomWebString(), webstring)){
            responseObject.addProperty("response_code", 0);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        Company company = new Company(companynumber);
        if (company.account == null){
            responseObject.addProperty("response_code", 1);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        double meatCustom = ((double)Tax.getMeatCustom())/100;
        double packageCustom = ((double)Tax.getPackageCustom())/100;
        double bioMeatCustom = ((double)Tax.getBioMeatCustom())/100;
        double customsSum = 0;

        if (hasMeat){
            customsSum += (price*meatCustom);
        }
        if (hasPackage){
            customsSum += (price*packageCustom);
        }
        if (hasBioMeat){
            customsSum += (price*bioMeatCustom);
        }
        double companyBalance = company.getBalanceDouble();
        /*if (companyHasNotEnoughMoney(companyBalance, customsSum)){
            responseObject.addProperty("response_code", 2);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }*/
        companyBalance -= customsSum;
        company.setBalance(companyBalance);
        company.saveAll();

        Company finanzministerium = new Company("0098");
        double finanzministeriumBalance = finanzministerium.getBalanceDouble();
        finanzministerium.setBalance(finanzministeriumBalance + customsSum);
        finanzministerium.saveAll();

        responseObject.addProperty("response_code", 3);
        responseObject.addProperty("amount", customsSum);
        resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
    }

    private boolean companyHasNotEnoughMoney(double companyBalance, double amountToBeSubtracted) {
        return Account.getCurrentMinutes() < Account.getMinutesFromValues(4, 12, 0)
                && (companyBalance - amountToBeSubtracted) < -31.00

                || (companyBalance - amountToBeSubtracted) < 0.00;
    }
}