import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TransferMoney extends HttpServlet {

    private DatastoreService datastore;
    private ArrayList<String> tempArrayList = new ArrayList<>();
    private Logger log = Logger.getLogger(TransferMoney.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String receiverAccountnumber = URLDecoder.decode(req.getParameter("receiveraccountnumber"), "UTF-8");
        String senderAccountnumber = URLDecoder.decode(req.getParameter("senderaccountnumber"), "UTF-8");
        String intendedPurpose = URLDecoder.decode(req.getParameter("intendedpurpose"), "UTF-8");
        float amount = Float.parseFloat(URLDecoder.decode(req.getParameter("amount"), "UTF-8"));
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", req.getLocale());
        Calendar calendar = Calendar.getInstance();
        datastore = DatastoreServiceFactory.getDatastoreService();

        try {
            Key transferKey = KeyFactory.createKey("dateandtime", f.format(calendar.getTime()));
            Entity transfer = new Entity("Transfer", transferKey);
            Entity sender = getEntity("accountnumber", senderAccountnumber, "Account");
            Entity receiver = getEntity("accountnumber", receiverAccountnumber, "Account");

            double senderBalance = sender != null ? (double) sender.getProperty("balance") : 0;
            double receiverBalance = receiver != null ? (double) receiver.getProperty("balance") : 0;
            log.info("Sender is here? " + (sender != null ? "true" : "false" ));
            log.info("Receiver is here? " + (receiver != null ? "true" : "false"));
            log.info("Sender balance? " + senderBalance);
            log.info("Amount? " + amount);
            if(senderBalance >= amount && receiver != null){ //Überweisung klappt
                transfer.setProperty("sender", sender.getKey());
                transfer.setProperty("receiver", receiver.getKey());
                transfer.setProperty("amount", amount);
                transfer.setProperty("datetime", f.format(calendar.getTime()));
                transfer.setProperty("purpose", intendedPurpose);
                transfer.setProperty("type", "Überweisung");
                datastore.put(transfer);

                Entity savedTransfer = getEntity("dateandtime", f.format(calendar.getTime()), "Transfer");
                getTransferArrays(sender);
                tempArrayList.add(KeyFactory.keyToString(savedTransfer.getKey()));
                sender.setProperty("transferarray", tempArrayList);
                getTransferArrays(receiver);
                tempArrayList.add(KeyFactory.keyToString(savedTransfer.getKey()));
                receiver.setProperty("transferarray", tempArrayList);

                sender.setProperty("balance", senderBalance - amount);
                receiver.setProperty("balance", receiverBalance + amount);
                datastore.put(sender);
                datastore.put(receiver);
                resp.getWriter().println("3");
            } else if(receiver != null){ //Konto des Senders ist nicht ausreichend gedeckt
                resp.getWriter().println("1");
            } else { //Empfänger existiert nicht
                resp.getWriter().println("2");
            }

        } catch (NullPointerException e){
            e.printStackTrace();
            log.warning(e.toString());
            resp.getWriter().println("0");
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

    private void getTransferArrays(Entity entity){
        tempArrayList = (ArrayList<String>) entity.getProperty("transferarray");
        if (tempArrayList == null) tempArrayList = new ArrayList<>();
    }
}
