package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostFinancialStatus extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        String accountKey = req.getParameter("accountkey");

        try {
            Entity account = datastore.get(KeyFactory.stringToKey(accountKey));
            resp.getWriter().println(URLEncoder.encode(account.getProperty("accountnumber") + "ò" + account.getProperty("owner") + "ò" + account.getProperty("balance"), "UTF-8"));
        } catch (EntityNotFoundException e) {
            resp.getWriter().println("nf");
        }
    }
}