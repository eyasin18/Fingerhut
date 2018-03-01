package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SetAccountTimes extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Query companyQuery = new Query("Account");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> accountList = datastore.prepare(companyQuery).asList(FetchOptions.Builder.withDefaults());
        for (Entity accountEntity : accountList){
            Account account = new Account(accountEntity);
            account.account.setProperty("state_entry_time", 0);
            account.account.setProperty("was_present", false);
            account.account.setProperty("is_present", false);
            account.account.setProperty("presence_time", 0);
            account.setGotBasicIncome(false);
            account.setWorkedHours(0);
            account.saveAll();
        }
    }
}