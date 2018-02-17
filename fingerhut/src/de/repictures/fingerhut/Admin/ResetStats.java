package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResetStats extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Query companyQuery = new Query("Company");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> companyList = datastore.prepare(companyQuery).asList(FetchOptions.Builder.withDefaults());
        for (Entity companyEntity : companyList){
            Company company = new Company(companyEntity);

            List<Number> balanceRecordList = new ArrayList<>();
            balanceRecordList.add(company.getBalanceDouble());
            company.account.setProperty("balance_records", balanceRecordList);

            List<Number> datesList = new ArrayList<>();
            datesList.add(Account.getCurrentMinutes());
            company.account.setProperty("balance_record_dates", datesList);
            company.saveAll();
        }
    }
}