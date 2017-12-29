package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Company;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class CompanyTools {

    private Company accountGetter;
    private Logger log = Logger.getLogger("company.jsp");
    private boolean isAuthenticated = false;

    public CompanyTools(String accountnumber){
        accountGetter = new Company(accountnumber);
    }

    public boolean isAuthentificated(String code) {
        String savedCode = accountGetter.getRandomWebString();

        log.info("Passed Code: " + code + "\nSaved Code: " + savedCode);

        if (Objects.equals(code, savedCode)){
            isAuthenticated = true;
        }
        return isAuthenticated;
    }

    public String getOwner(String accountnumber){
        Company accountGetter = new Company(accountnumber);
        return accountGetter.getOwner();
    }

}
