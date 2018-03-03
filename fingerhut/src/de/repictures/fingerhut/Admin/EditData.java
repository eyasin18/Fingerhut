package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Tax;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class EditData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*Query accountQuery = new Query("Account");
        String bestCompany = "";
        String bestPayedAccount = "";
        double bestWage = 0;
        List<Entity> accountList = DatastoreServiceFactory.getDatastoreService().prepare(accountQuery).asList(FetchOptions.Builder.withDefaults());
        for (Entity accountEntity : accountList){
            Account account = new Account(accountEntity);
            List<Entity> companies = account.getCompanies();
            for (Entity companyEntity : companies){
                Company company = new Company(companyEntity);
                double wage = account.getSpecificWage(company.getAccountnumber());
                if (wage > bestWage){
                    bestPayedAccount = account.getAccountnumber();
                    bestCompany = company.getAccountnumber();
                    bestWage = wage;
                }
            }
        }
        resp.getWriter().println("Best payed Account: " + bestPayedAccount + "; Best paying Company: " + bestCompany + "; Lohn: " + bestWage);*/
    }

    private void updateEntities(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Query accountQuery = new Query("Account");
        List<Entity> accountList = DatastoreServiceFactory.getDatastoreService().prepare(accountQuery).asList(FetchOptions.Builder.withDefaults());
        for (Entity accountEntity : accountList){
            Account account = new Account(accountEntity);
            if (account.getCompanies().size() > 0){
                double wage = 1.0f;
                if (accountEntity.getProperty("wage") != null) wage = ((Number) accountEntity.getProperty("wage")).doubleValue();
                ArrayList<Long> featuresList = new ArrayList<>();
                if (accountEntity.getProperty("feature_list") != null) featuresList = (ArrayList<Long>) accountEntity.getProperty("feature_list");
                List<Integer> startList = new ArrayList<>();
                if (accountEntity.getProperty("work_start_times") != null) startList = (List<Integer>) accountEntity.getProperty("work_start_times");
                List<Integer> endList = new ArrayList<>();
                if (accountEntity.getProperty("work_end_times") != null) endList = (List<Integer>) accountEntity.getProperty("work_end_times");

                Company mainCompany = new Company(account.getCompanies().get(0));
                account.setSpecificWage(wage, mainCompany.getAccountnumber());
                account.setFeatures(featuresList, mainCompany.getAccountnumber());
                account.setSpecificWorkPeriods(new Gson().toJsonTree(startList).getAsJsonArray(), new Gson().toJsonTree(endList).getAsJsonArray(), mainCompany.getAccountnumber());
            }
            account.account.removeProperty("wage");
            account.account.removeProperty("feature_list");
            account.account.removeProperty("work_start_times");
            account.account.removeProperty("work_end_times");
            account.saveAll();
        }
        resp.getWriter().println("success");
    }

    private void setCompanies(){
        /*Company company = new Company("0002");
        Query accountQuery = new Query("Account");
        Query.Filter buyerAccountnumberFilter = new Query.FilterPredicate("company", Query.FilterOperator.EQUAL, company.account.getKey());
        accountQuery.setFilter(buyerAccountnumberFilter);
        List<Entity> accountList = DatastoreServiceFactory.getDatastoreService().prepare(accountQuery).asList(FetchOptions.Builder.withDefaults());
        for (Entity accountEntity : accountList){
            Account account = new Account(accountEntity);
            account.deleteCompany();
            account.saveAll();
        }*/
        String[] accountnumbers = new String[]{"0633", "0622", "0614"};
        for (int i = 0; i < accountnumbers.length; i++){
            Account account = new Account(accountnumbers[i]);
            account.addCompany("0002");
            account.saveAll();
        }
    }
}
