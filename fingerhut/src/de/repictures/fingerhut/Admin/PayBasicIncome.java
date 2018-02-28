package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class PayBasicIncome extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Query query = new Query("Account");
        Query.Filter filter = new Query.FilterPredicate("got_basic_income", Query.FilterOperator.EQUAL, false);
        query.setFilter(filter);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> accountList = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity accountEntity : accountList){
            Account account = new Account(accountEntity);
            account.transferBasicIncome();
            account.setGotBasicIncome(true);
            account.saveAll();
        }
    }
}