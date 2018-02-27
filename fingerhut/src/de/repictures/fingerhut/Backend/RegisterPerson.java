package de.repictures.fingerhut.Backend;

import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;

public class RegisterPerson extends HttpServlet {

    private long[] minPresences = {0, 5*60, 5*60, 4*60, 5*60, 4*60, 0};

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userAccountnumber = req.getParameter("user_accountnumber");
        String webString = req.getParameter("webstring");
        String accountnumber = req.getParameter("accountnumber");

        JsonObject responseObject = new JsonObject();
        if (userAccountnumber == null || webString == null || accountnumber == null){
            responseObject.addProperty("response_code", 0);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        Account userAccount = new Account(userAccountnumber);
        /*if (!Objects.equals(webString, userAccount.getRandomWebString())){
            responseObject.addProperty("response_code", 0);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }*/

        Account account = new Account(accountnumber);
        if (account.account == null){
            responseObject.addProperty("response_code", 2);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        if (account.getIsPrepaid()){
            responseObject.addProperty("response_code", 3);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        responseObject.addProperty("response_code", 1);
        if (account.isPresent()){
            long presenceTime = account.exitState();
            account.saveAll();
            int currentDay = (int) Account.getDaysFromMinutes(Account.getCurrentRoundedMinutes());
            long minPresence = minPresences[currentDay];
            long minutesToGo = 0;
            if (minPresence > presenceTime){
                minutesToGo = minPresence - presenceTime;
            }
            responseObject.addProperty("entered", false);
            responseObject.addProperty("presence_time", presenceTime);
            responseObject.addProperty("minutes_to_go", minutesToGo);
        } else {
            account.enterState();
            account.saveAll();
            responseObject.addProperty("entered", true);
        }

        resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
    }
}