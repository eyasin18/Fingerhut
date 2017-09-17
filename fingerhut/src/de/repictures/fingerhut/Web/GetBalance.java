package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Accounts;

public class GetBalance {
    public String getBalance(String accountnumber){
        Accounts accountsGetter = new Accounts(accountnumber);
        return accountsGetter.getBalance();
    }
}
