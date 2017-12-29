package de.repictures.fingerhut.Web;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class CompanyTools {

    private Account accountGetter;
    private Logger log = Logger.getLogger("company.jsp");
    private boolean isAuthenticated = false;

    public CompanyTools(String accountnumber){
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

    public String getOwner(String accountnumber){
        Company companyGetter = new Company(accountnumber);
        return companyGetter.getOwner();
    }

    public Product[] querySellingProducts(String companynumber){
        Company companyGetter = new Company(companynumber);
        List<Entity> productEntities = companyGetter.getProducts();
        Product[] products = new Product[productEntities.size()];
        for (int i = 0; i < productEntities.size(); i++){
            products[i] = new Product(productEntities.get(i));
        }
        return products;
    }

}