package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Tax;
import de.repictures.fingerhut.Datastore.Transfer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class PayBasicIncome extends HttpServlet {

    private Logger log = Logger.getLogger(PayBasicIncome.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Company fm = new Company("0098");
        Query query = new Query("Account");
        Query.Filter filter = new Query.FilterPredicate("got_basic_income", Query.FilterOperator.EQUAL, false);
        query.setFilter(filter);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> accountList = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        double basicIncome = Tax.getBasicIncome().doubleValue();
        for (Entity accountEntity : accountList){
            Account receiverAccount = new Account(accountEntity);
            double payingCompanyBalance = fm.getBalanceDouble();
            double userBalance = receiverAccount.getBalanceDouble();
            userBalance += basicIncome;
            payingCompanyBalance -= basicIncome;
            fm.setBalance(payingCompanyBalance);
            receiverAccount.setBalance(userBalance);
            Entity savedTransfer = Transfer.transferWage(basicIncome, 0, true, fm, receiverAccount);
            receiverAccount.addTransfer(savedTransfer);
            fm.addTransfer(savedTransfer);
            fm.saveAll();
            receiverAccount.setGotBasicIncome(true);
            receiverAccount.saveAll();
        }
    }
}