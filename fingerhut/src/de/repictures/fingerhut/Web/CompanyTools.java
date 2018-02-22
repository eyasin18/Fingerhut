package de.repictures.fingerhut.Web;

import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.repictures.fingerhut.Datastore.*;
import io.swagger.util.Json;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class CompanyTools {

    private Account accountGetter;
    private Logger log = Logger.getLogger("company.jsp");
    private boolean isAuthenticated = false;
    private String[] weekdays = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
    private DecimalFormat timePattern = new DecimalFormat("00");
    public List<Long> wageTaxes;

    public CompanyTools(String accountnumber){
        accountGetter = new Account(accountnumber);
        wageTaxes = Tax.getWageTax();
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

    public double getBalance(String accountnumber){
        Company companyGetter = new Company(accountnumber);
        return companyGetter.getBalanceDouble();
    }

    public List<Product> querySellingProducts(String companynumber){
        List<Entity> productEntities = Product.getProductsByCompany(companynumber, false);
        List<Product> products = new ArrayList<>();
        for (Entity productEntity : productEntities){
            Product product = new Product(productEntity);
            products.add(product);
        }
        return products;
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

    @SuppressWarnings("Duplicates")
    public String getEmployeesJsonStr(String companynumber){
        Company company = new Company(companynumber);
        Query accountQuery = new Query("Account");
        List<Entity> unfilteredAccountList = DatastoreServiceFactory.getDatastoreService().prepare(accountQuery).asList(FetchOptions.Builder.withDefaults());
        List<Account> accountList = new ArrayList<>();
        for (Entity accountEntity : unfilteredAccountList){
            Account account = new Account(accountEntity);
            if (account.containsCompany(company.account.getKey())){
                accountList.add(account);
            }
        }

        JsonArray accountnumberArray = new JsonArray();
        JsonArray wageArray = new JsonArray();
        JsonArray startTimesArray = new JsonArray();
        JsonArray endTimesArray = new JsonArray();
        JsonArray featuresArray = new JsonArray();

        for (Account account : accountList) {
            accountnumberArray.add(account.getAccountnumber());
            wageArray.add(account.getSpecificWage(companynumber));
            JsonArray oStartTimesMinutesArray = account.getSpecificWorkPeriod(false, companynumber);
            JsonArray oStartTimesArray = new JsonArray();
            JsonArray oEndTimesMinutesArray = account.getSpecificWorkPeriod(true, companynumber);
            JsonArray oEndTimesArray = new JsonArray();
            for (int i = 0; i < oEndTimesMinutesArray.size(); i++) {
                int startTime = oStartTimesMinutesArray.get(i).getAsInt();
                int endTime = oEndTimesMinutesArray.get(i).getAsInt();
                int startDays = Account.getDaysFromMinutes(startTime);
                int endDays = Account.getDaysFromMinutes(endTime);
                int startHours = Account.getHoursFromMinutes(startTime);
                int endHours = Account.getHoursFromMinutes(endTime);
                int startMinutes = Account.getMinutesOfHourFromMinutes(startTime);
                int endMinutes = Account.getMinutesOfHourFromMinutes(endTime);

                oStartTimesArray.add(weekdays[startDays%7] + " " + timePattern.format(startHours) + ":" + timePattern.format(startMinutes) + " Uhr");
                oEndTimesArray.add(weekdays[endDays%7] + " " + timePattern.format(endHours) + ":" + timePattern.format(endMinutes) + " Uhr");
            }
            startTimesArray.add(oStartTimesArray);
            endTimesArray.add(oEndTimesArray);
            ArrayList<Long> featuresList = account.getSpecificFeatures(companynumber);
            JsonArray oFeaturesArray = new JsonArray();
            if (featuresList.size() > 0){
                oFeaturesArray = new Gson().toJsonTree(featuresList).getAsJsonArray();
            }
            featuresArray.add(oFeaturesArray);
        }

        JsonObject object = new JsonObject();
        object.addProperty("responseCode", 1);
        object.add("accountnumbers", accountnumberArray);
        object.add("wages", wageArray);
        object.add("start_times", startTimesArray);
        object.add("end_times", endTimesArray);
        object.add("features", featuresArray);
        return object.toString();
    }
}