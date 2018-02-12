package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Account;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class MainTools {

    private Account accountGetter;
    private Logger log = Logger.getLogger("main.jsp");
    private boolean isAuthenticated = false;

    public MainTools(String accountnumber){
        accountGetter = new Account(accountnumber);
    }

    public boolean isAuthentificated(String code) {
        String savedCode = accountGetter.getRandomWebString();

        log.info("Passed Code: " + code + "\nSaved Code: " + savedCode);

        if (Objects.equals(code, savedCode)){
            isAuthenticated = true;
        }
        return isAuthenticated;
    }

    public boolean isCompanyAdmin(String companynumber){
        List<Long> featuresList = accountGetter.getSpecificFeatures(companynumber);
        log.info("Contains 0: " + featuresList.contains(0L) + "\nContains 5: " + featuresList.contains(5L) + "\nContains 7: " + featuresList.contains(7L));
        return featuresList.contains(0L) && featuresList.contains(5L) && featuresList.contains(7L);
    }

    public String getBalance(String accountnumber){
        Account accountGetter = new Account(accountnumber);
        return accountGetter.getBalance();
    }
}