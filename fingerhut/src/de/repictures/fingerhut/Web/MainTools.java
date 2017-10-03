package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Accounts;
import java.util.Objects;
import java.util.logging.Logger;

public class MainTools {
    public String getBalance(String accountnumber){
        Accounts accountsGetter = new Accounts(accountnumber);
        return accountsGetter.getBalance();
    }
    private Logger log = Logger.getLogger("main.jsp");
    private boolean isAuthenticated = false;

    public MainTools(String accountnumber, String code){
        Accounts accountsGetter = new Accounts();

        accountsGetter.account = accountsGetter.getAccount(accountnumber);
        String savedCode = accountsGetter.getRandomWebString();

        log.info("Passed Code: " + code + "\nSaved Code: " + savedCode);

        if (Objects.equals(code, savedCode)){
            isAuthenticated = true;
        }
    }
    public boolean isAuthentificated() { return isAuthenticated; }
}
