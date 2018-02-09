package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

public class PostStats extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authAccountnumber = req.getParameter("authaccountnumber");
        String authString = req.getParameter("authstring");
        String companyNumber = req.getParameter("companynumber");

        JsonObject object = new JsonObject();
        JsonArray balanceDevelopmentArray = new JsonArray();
        JsonArray balanceDevelopmentTimesArray = new JsonArray();

        if (authAccountnumber == null || authString == null || companyNumber == null){
            object.addProperty("response", 0);
            resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
            return;
        }

        Account accountGetter = new Account(authAccountnumber);
        if (accountGetter.account == null){
            object.addProperty("response", 1);
            resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
            return;
        }

        Company companyGetter = new Company(companyNumber);
        boolean isInCompany = false;
        for (Entity companyEntity : accountGetter.getCompanies()){
            Company payingCompany = new Company(companyEntity);
            if (Objects.equals(payingCompany.getAccountnumber(), companyNumber)) isInCompany = true;
        }
        if (companyGetter.account == null || !isInCompany){
            object.addProperty("response", 2);
            resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
            return;
        }

        if (!Objects.equals(authString, accountGetter.getRandomWebString())){
            object.addProperty("response", 3);
            resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
            return;
        }

        List<Number> balanceDevelopment = companyGetter.getBalanceDevelopment();
        List<Number> balanceDevelopmentDates = companyGetter.getBalanceDevelopmentTimes();
        for (int i = 0; i < balanceDevelopment.size(); i++){
            balanceDevelopmentArray.add(balanceDevelopment.get(i));
            balanceDevelopmentTimesArray.add(balanceDevelopmentDates.get(i));
        }

        object.addProperty("response", 4);
        object.add("balance_development", balanceDevelopmentArray);
        object.add("balance_development_dates", balanceDevelopmentTimesArray);

        resp.getWriter().println(URLEncoder.encode(object.toString(), "UTF-8"));
    }
}
