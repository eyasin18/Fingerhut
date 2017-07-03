package de.repictures.fingerhut.Datastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class AddAccount extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        String accountnumber = req.getParameter("accountnumber");

        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Entity account = new Entity("Account", loginKey);
        account.setProperty("accountnumber", accountnumber);
        account.setProperty("password", "0000");
        account.setProperty("owner", "Max Mustermann");
        account.setProperty("balance", 100.00);
        account.setProperty("transferarray", new ArrayList<String>());
        datastore.put(account);
        resp.getWriter().println("success");
    }
}
