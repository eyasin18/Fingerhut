package de.repictures.fingerhut.Website;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Datastore.Accounts;

public class Login {
    public Login(){
        Accounts accountsBuilder = new Accounts();
        Entity account = accountsBuilder.getAccount("0001");
        double balance = Double.parseDouble(accountsBuilder.getBalance(account));
        accountsBuilder.setBalance(account, String.valueOf(balance + 3));
        accountsBuilder.saveAll(account);
    }
}
