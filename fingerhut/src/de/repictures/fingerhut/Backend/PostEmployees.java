package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import io.swagger.util.Json;
import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostEmployees extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String authAccountnumber = req.getParameter("accountnumber");
        String companyNumber = req.getParameter("companynumber");

        Account authAccount = new Account(authAccountnumber);
        JsonObject object = new JsonObject();

        if (!authAccount.getSpecificFeatures(companyNumber).contains(3L)){
            object.addProperty("responseCode", 2);
            resp.getWriter().println(object.toString());
            return;
        }

        if (!Objects.equals(code, authAccount.getRandomWebString())){
            object.addProperty("responseCode", 3);
            resp.getWriter().println(object.toString());
            return;
        }

        Company company = new Company(companyNumber);
        Query accountQuery = new Query("Account");
        List<Entity> unfilteredAccountList = DatastoreServiceFactory.getDatastoreService().prepare(accountQuery).asList(FetchOptions.Builder.withDefaults());
        List<Account> accountList = new ArrayList<>();
        for (Entity accountEntity : unfilteredAccountList){
            Account account = new Account(accountEntity);
            if (account.containsCompany(company.account.getKey())){
                accountList.add(account);
            }
        }

        JsonArray accountnumberArray = new JsonArray();
        JsonArray wageArray = new JsonArray();
        JsonArray startTimesArray = new JsonArray();
        JsonArray endTimesArray = new JsonArray();
        JsonArray featuresArray = new JsonArray();

        for (Account account : accountList) {
            accountnumberArray.add(account.getAccountnumber());
            wageArray.add(account.getSpecificWage(companyNumber));
            JsonArray oStartTimesArray = account.getSpecificWorkPeriod(false, companyNumber);
            startTimesArray.add(oStartTimesArray);
            JsonArray oEndTimesArray = account.getSpecificWorkPeriod(true, companyNumber);
            endTimesArray.add(oEndTimesArray);
            ArrayList<Long> featuresList = account.getSpecificFeatures(companyNumber);
            JsonArray oFeaturesArray = new JsonArray();
            if (featuresList.size() > 0){
                oFeaturesArray = new Gson().toJsonTree(featuresList).getAsJsonArray();
            }
            featuresArray.add(oFeaturesArray);
        }

        object.addProperty("responseCode", 1);
        object.add("accountnumbers", accountnumberArray);
        object.add("wages", wageArray);
        object.add("start_times", startTimesArray);
        object.add("end_times", endTimesArray);
        object.add("features", featuresArray);
        resp.getWriter().println(object.toString());
    }
}
