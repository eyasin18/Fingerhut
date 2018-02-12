package de.repictures.fingerhut.Backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetEmployee extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {String companynumber = req.getParameter("companynumber");
        String editorAccountnumber = req.getParameter("editoraccoutnumber");
        String authCode = req.getParameter("authstring");
        String jsonObjectStr = req.getParameter("body");
        if (jsonObjectStr != null) jsonObjectStr = URLDecoder.decode(jsonObjectStr, "UTF-8");

        if (companynumber == null || editorAccountnumber == null || authCode == null || jsonObjectStr == null){
            resp.getWriter().println(0);
            return;
        }

        Account authAccount = new Account(editorAccountnumber);
        if (!Objects.equals(authAccount.getRandomWebString(), authCode)){
            resp.getWriter().println(1);
            return;
        }

        if (!authAccount.getFeatures().contains(3L)){
            resp.getWriter().println(3);
            return;
        }

        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(jsonObjectStr).getAsJsonObject();

        String accountnumber = object.get("accountnumber").getAsString();
        double wage = object.get("wage").getAsDouble();
        JsonArray featuresArray = object.get("features").getAsJsonArray();
        JsonArray startTimesArray = object.getAsJsonArray("start_times");
        JsonArray endTimesArray = object.getAsJsonArray("end_times");
        List<Integer> startTimes = new ArrayList<>();
        List<Integer> endTimes = new ArrayList<>();
        for (int i = 0; i < startTimesArray.size(); i++) {
            startTimes.add(startTimesArray.get(i).getAsInt());
            endTimes.add(endTimesArray.get(i).getAsInt());
        }

        Account account = new Account(accountnumber);
        account.setWage(wage);
        ArrayList<Long> features = new ArrayList<>();
        for (int i = 0; i < featuresArray.size(); i++) {
            features.add(featuresArray.get(i).getAsLong());
        }
        account.setFeatures(features, companynumber);
        account.setWorkPeriods(startTimes, endTimes);
        account.saveAll();

        resp.getWriter().println(2);
    }
}