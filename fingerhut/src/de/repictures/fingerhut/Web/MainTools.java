package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Account;

import java.util.Objects;
import java.util.logging.Logger;

public class MainTools {
    public String getBalance(String accountnumber){
        Account accountGetter = new Account(accountnumber);
        return accountGetter.getBalance();
    }
    private Logger log = Logger.getLogger("main.jsp");
    private boolean isAuthenticated = false;

    public MainTools(String accountnumber, String code){
        Account accountGetter = new Account();

        accountGetter.account = accountGetter.getAccount(accountnumber);
        String savedCode = accountGetter.getRandomWebString();

        log.info("Passed Code: " + code + "\nSaved Code: " + savedCode);

        if (Objects.equals(code, savedCode)){
            isAuthenticated = true;
        }
    }
    public boolean isAuthentificated() { return isAuthenticated; }
}
