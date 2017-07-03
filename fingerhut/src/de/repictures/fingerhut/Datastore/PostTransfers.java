package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class PostTransfers extends HttpServlet {

    private DatastoreService datastore;
    private Logger log = Logger.getLogger(PostTransfers.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        datastore = DatastoreServiceFactory.getDatastoreService();

        String accountnumber = req.getParameter("accountnumber");

        try {
            Entity account = getEntity("accountnumber", accountnumber, "Account");
            ArrayList<String> transfersList = (ArrayList<String>) account.getProperty("transferarray");
            String output = "";
            if (transfersList != null && transfersList.size() > 0)
            for (int i = transfersList.size() -1 ; i >= 0; i--) {
                String transferKeyStr = transfersList.get(i);
                Entity transfer = datastore.get(KeyFactory.stringToKey(transferKeyStr));

                output += transfer.getProperty("datetime").toString() + "ò";
                Key receiverKey = (Key) transfer.getProperty("receiver");
                Entity receiver = datastore.get(receiverKey);
                Key senderKey = (Key) transfer.getProperty("sender");
                Entity sender = datastore.get(senderKey);
                String senderAccountnumber = sender.getProperty("accountnumber").toString();
                char plusminus = '+';
                if (Objects.equals(senderAccountnumber, accountnumber)) plusminus = '-';
                output += receiver.getProperty("owner").toString() + "ò";
                output += receiver.getProperty("accountnumber").toString() + "ò";
                output += transfer.getProperty("type").toString() + "ò";
                output += transfer.getProperty("purpose").toString() + "ò";
                output += plusminus;
                output += transfer.getProperty("amount").toString() + "ň";
            }
            resp.getWriter().println(URLEncoder.encode(output, "UTF-8"));
        } catch (EntityNotFoundException e){
            log.warning(e.toString());
            resp.getWriter().println(URLEncoder.encode("ĵ", "UTF-8"));
        }
    }

    private Entity getEntity(String kind, String name, String entityTitle){
        Key senderKey = KeyFactory.createKey(kind, name);
        Query loginQuery = new Query(entityTitle, senderKey);
        List<Entity> entityList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        if(entityList.size() > 0){
            return entityList.get(0);
        } else {
            log.warning("Entity " + kind + " " + name + " " + entityTitle + " was not found!");
            return null;
        }
    }
}
