package de.repictures.fingerhut.Web;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Product;
import de.repictures.fingerhut.Datastore.PurchaseOrder;

import javax.servlet.http.HttpServletRequest;
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
        return companyGetter.getSellingProducts();
    }

    public PurchaseOrder[] queryPurchasOrders(String companynumber, HttpServletRequest req){
        Company companyGetter = new Company(companynumber);
        List<Entity> purchaseOrderEntities = new PurchaseOrder(companyGetter.account, req.getLocale()).getPurchaseOrders(companyGetter.account.getKey());
        PurchaseOrder[] purchaseOrders = new PurchaseOrder[purchaseOrderEntities.size()];
        for (int i = 0; i < purchaseOrderEntities.size(); i++){
            purchaseOrders[i] = new PurchaseOrder(companyGetter.account, purchaseOrderEntities.get(i), req.getLocale());
        }
        return purchaseOrders;
    }
}
