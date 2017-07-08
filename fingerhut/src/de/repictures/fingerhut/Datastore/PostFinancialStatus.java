package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import de.repictures.fingerhut.Cryptor;

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

        //Parameter werden entgegen genommen
        String accountKey = req.getParameter("accountkey");

        try {
            Cryptor cryptor = new Cryptor();
            Entity account = datastore.get(KeyFactory.stringToKey(accountKey));

            //Name wird entschlüsselt
            String encryptedNameStr = (String) account.getProperty("owner");
            byte[] encryptedName = cryptor.hexToBytes(encryptedNameStr);
            String encryptedHexPasswordStr = (String) account.getProperty("password");
            byte[] encryptedPassword = cryptor.hexToBytes(encryptedHexPasswordStr);
            String name = cryptor.decrypt(encryptedName, encryptedPassword);

            //Daten werden ausgegeben
            resp.getWriter().println(URLEncoder.encode(account.getProperty("accountnumber") + "ò" + name + "ò" + account.getProperty("balance"), "UTF-8"));
        } catch (EntityNotFoundException e) {
            resp.getWriter().println("nf");
        }
    }
}