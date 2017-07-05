package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AddAccount extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        String accountnumber = req.getParameter("accountnumber");
        String plainName = req.getParameter("name");

        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Entity account = new Entity("Account", loginKey);
        account.setProperty("accountnumber", accountnumber);
        Random rand = new Random();
        String password = String.format("%04d%n", rand.nextInt(10000));
        account.setProperty("password", password);
        account.setProperty("owner", plainName);
        account.setProperty("balance", 100.00);
        account.setProperty("transferarray", new ArrayList<String>());
        datastore.put(account);
        resp.getWriter().println("success");
    }
}