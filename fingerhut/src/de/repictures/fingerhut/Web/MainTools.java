package de.repictures.fingerhut.Web;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class MainTools {

    private Account accountGetter;
    private Logger log = Logger.getLogger("main.jsp");
    private boolean isAuthenticated = false;
    private List<String> companyNumbers;

    public MainTools(String accountnumber){
        accountGetter = new Account(accountnumber);
        List<Entity> companies = accountGetter.getCompanies();
        companyNumbers = new ArrayList<>();
        for (Entity companyEntity :
                companies) {
            Company company = new Company(companyEntity);
            companyNumbers.add(company.getAccountnumber());
        }
    }

    public boolean isAuthentificated(String code) {
        String savedCode = accountGetter.getRandomWebString();

        log.info("Passed Code: " + code + "\nSaved Code: " + savedCode);

        if (Objects.equals(code, savedCode)){
            isAuthenticated = true;
        }
        return isAuthenticated;
    }

    public boolean[] getIsAdmins(){
        boolean[] isAdmins = new boolean[companyNumbers.size()];
        for (int i = 0; i < companyNumbers.size(); i++) {
            String companynumber = companyNumbers.get(i);
            List<Long> featuresList = accountGetter.getSpecificFeatures(companynumber);
            log.info("Contains 0: " + featuresList.contains(0L) + "\nContains 2: " + featuresList.contains(2L) + "\nContains 3: " + featuresList.contains(3L) + "\nContains 4: " + featuresList.contains(4L));
            isAdmins[i] = featuresList.contains(0L) && featuresList.contains(2L) && featuresList.contains(3L) && featuresList.contains(4L);
        }
        return isAdmins;
    }

    public String getBalance(String accountnumber){
        Account accountGetter = new Account(accountnumber);
        return accountGetter.getBalance();
    }

    public List<String> getCompanyNumbers(String accountnumber){
        return companyNumbers;
    }

    public boolean isPrepaid(){
        return accountGetter.getIsPrepaid();
    }

    public static List<Account> getPrepaidAccounts(){
        Query query = new Query("Account");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> accountList = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        List<Account> prepaidAccounts = new ArrayList<>();
        for (Entity accountEntity : accountList){
            Account account = new Account(accountEntity);
            if (account.getIsPrepaid() && account.getBalanceDouble() > 0){
                prepaidAccounts.add(account);
            }
        }
        return prepaidAccounts;
    }

    public static List<Company> getCompaniesBySector(int sector){
        Query query = new Query("Company");
        Query.Filter sectorFilter = new Query.FilterPredicate("sector", Query.FilterOperator.EQUAL, sector);
        query.setFilter(sectorFilter);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        List<Company> companies = new ArrayList<>();
        for (Entity entity : entities){
            companies.add(new Company(entity));
        }
        return companies;
    }
}