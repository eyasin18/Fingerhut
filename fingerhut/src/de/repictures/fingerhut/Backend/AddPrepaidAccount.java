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

public class AddPrepaidAccount extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String companynumber = req.getParameter("companynumber");
        String webstring = req.getParameter("webstring");

        JsonObject responseObject = new JsonObject();
        Account account = new Account(accountnumber);

        if (!Objects.equals(account.getRandomWebString(), webstring)){
            responseObject.addProperty("response_code", 0);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        if (!account.getSpecificFeatures(companynumber).contains(7L)){
            responseObject.addProperty("response_code", 2);
            resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
            return;
        }

        responseObject = account.postPrepaidAccount();
        responseObject.addProperty("response_code", 1);
        resp.getWriter().println(URLEncoder.encode(responseObject.toString(), "UTF-8"));
    }
}