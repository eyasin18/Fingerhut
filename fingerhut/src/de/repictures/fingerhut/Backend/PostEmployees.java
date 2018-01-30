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

        if (!authAccount.getFeatures().contains(3L)){
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
        Query.Filter buyerAccountnumberFilter = new Query.FilterPredicate("company", Query.FilterOperator.EQUAL, company.account.getKey());
        accountQuery.setFilter(buyerAccountnumberFilter);
        List<Entity> accountList = DatastoreServiceFactory.getDatastoreService().prepare(accountQuery).asList(FetchOptions.Builder.withDefaults());

        JsonArray accountnumberArray = new JsonArray();
        JsonArray wageArray = new JsonArray();
        JsonArray startTimesArray = new JsonArray();
        JsonArray endTimesArray = new JsonArray();
        JsonArray featuresArray = new JsonArray();

        for (Entity anAccountList : accountList) {
            Account account = new Account(anAccountList);
            accountnumberArray.add(account.getAccountnumber());
            wageArray.add(account.getWage());
            Gson gson = new Gson();
            List<Number> startTimesInt = account.getWorkPeriod(false);
            JsonArray oStartTimesArray = new JsonArray();
            if (startTimesInt.size() > 0) {
                oStartTimesArray = gson.toJsonTree(startTimesInt).getAsJsonArray();
            }
            startTimesArray.add(oStartTimesArray);
            List<Number> endTimesInt = account.getWorkPeriod(true);
            JsonArray oEndTimesArray = new JsonArray();
            if (endTimesInt.size() > 0) {
                oEndTimesArray = gson.toJsonTree(endTimesInt).getAsJsonArray();
            }
            endTimesArray.add(oEndTimesArray);
            ArrayList<Long> featuresList = account.getFeatures();
            JsonArray oFeaturesArray = new JsonArray();
            if (featuresList.size() > 0){
                oFeaturesArray = gson.toJsonTree(featuresList).getAsJsonArray();
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
