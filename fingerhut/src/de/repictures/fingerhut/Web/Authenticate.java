package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Accounts;

import java.util.Objects;
import java.util.logging.Logger;

public class Authenticate {


    private Logger log = Logger.getLogger("main.jsp");
    private boolean isAuthentificated = false;

    public Authenticate(String accountnumber, String code){
        Accounts accountsGetter = new Accounts();

        accountsGetter.account = accountsGetter.getAccount(accountnumber);
        String savedCode = accountsGetter.getRandomWebString();

        log.info("Passed Code: " + code + "\nSaved Code: " + savedCode);

        if (Objects.equals(code, savedCode)){
            isAuthentificated = true;
        }
    }


    public boolean isAuthentificated() {
        return isAuthentificated;
    }
}
