package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings({"unchecked", "Duplicates"})
public class PurchaseOrder{


    private DatastoreService datastore;
    public Entity purchaseOrder;
    private Entity parentCompany;
    private int number = 0;
    private SimpleDateFormat f;
    private Calendar calendar;
    private Logger log = Logger.getLogger(PurchaseOrder.class.getName());

    public PurchaseOrder(Entity parentCompany, Locale locale){
        datastore = DatastoreServiceFactory.getDatastoreService();
        this.parentCompany = parentCompany;
        calendar = Calendar.getInstance();
        f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", locale);
    }

    public PurchaseOrder(Entity parentCompany, int number, Locale locale){
        datastore = DatastoreServiceFactory.getDatastoreService();
        this.parentCompany = parentCompany;
        calendar = Calendar.getInstance();
        f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", locale);
        purchaseOrder = getPurchaseOrder(parentCompany.getKey(), number);
    }

    public PurchaseOrder(Entity parentCompany, Entity purchaseOrder, Locale locale){
        datastore = DatastoreServiceFactory.getDatastoreService();
        this.parentCompany = parentCompany;
        calendar = Calendar.getInstance();
        f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", locale);
        this.purchaseOrder = purchaseOrder;
    }

    @Deprecated
    public PurchaseOrder(Entity parentCompany, String buyerAccountnumber, Locale locale){
        datastore = DatastoreServiceFactory.getDatastoreService();
        this.parentCompany = parentCompany;
        calendar = Calendar.getInstance();
        f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", locale);
        purchaseOrder = getPurchaseOrder(parentCompany.getKey(), buyerAccountnumber);
    }

    public void updatePurchaseOrder(Entity parentCompany, String shoppingListJson, String buyerAccountnumber, boolean completed){
        List<String> produtCodesList = new ArrayList<>();
        List<Double> pricesList = new ArrayList<>();
        List<Boolean> isSelfBuyList = new ArrayList<>();
        List<Long> amountsList = new ArrayList<>();

        if (purchaseOrder == null){
            purchaseOrder = new Entity("PurchaseOrder", parentCompany.getKey());
            setNumber();
        } else {
            produtCodesList = getProductCodesList();
            pricesList = getPricesList();
            isSelfBuyList = getIsSelfBuyList();
            amountsList = getAmountsList();
        }

        JsonObject object = new JsonParser().parse(shoppingListJson).getAsJsonObject();
        JsonArray productCodesArray = object.getAsJsonArray("product_codes");
        JsonArray pricesArray = object.getAsJsonArray("prices_array");
        JsonArray isSelfBuyArray = object.getAsJsonArray("is_self_buy");
        JsonArray amountsArray = object.getAsJsonArray("amounts");

        for (int i = 0; i < productCodesArray.size(); i++){
            produtCodesList.add(productCodesArray.get(i).getAsString());
            pricesList.add(pricesArray.get(i).getAsDouble());
            isSelfBuyList.add(isSelfBuyArray.get(i).getAsBoolean());
            amountsList.add(amountsArray.get(i).getAsLong());
        }

        if (!isSelfBuyList.contains(false)){
            completed = true;
        }

        setDateTime(f.format(calendar.getTime()));
        setBuyerAccountnumber(buyerAccountnumber);
        setProductCodesList(produtCodesList);
        setPricesList(pricesList);
        setIsSelfBuyList(isSelfBuyList);
        setAmountsList(amountsList);
        setCompleted(completed);
        saveAll();
        purchaseOrder = getPurchaseOrder(parentCompany.getKey(), number);
    }

    public List<Entity> getPurchaseOrders(Key parentCompanyKey){
        Query purchaseOrderQuery = new Query("PurchaseOrder", parentCompanyKey);
        purchaseOrderQuery.addSort("date_time", Query.SortDirection.DESCENDING);
        return datastore.prepare(purchaseOrderQuery).asList(FetchOptions.Builder.withDefaults());
    }

    @Deprecated
    public Entity getPurchaseOrder(Key parentCompanyKey, String buyerAccountnumber){
        Query purchaseOrderQuery = new Query("PurchaseOrder", parentCompanyKey);
        Query.Filter buyerAccountnumberFilter = new Query.FilterPredicate("buyer_accountnumber", Query.FilterOperator.EQUAL, buyerAccountnumber);
        purchaseOrderQuery.setFilter(buyerAccountnumberFilter);
        List<Entity> purchaseOrders = datastore.prepare(purchaseOrderQuery).asList(FetchOptions.Builder.withDefaults());
        if (purchaseOrders.size() > 0){
            return purchaseOrders.get(0);
        } else {
            return null;
        }
    }

    public Entity getPurchaseOrder(Key parentCompanyKey, int number){
        Query purchaseOrderQuery = new Query("PurchaseOrder", parentCompanyKey);
        Query.Filter numberFilter = new Query.FilterPredicate("number", Query.FilterOperator.EQUAL, number);
        purchaseOrderQuery.setFilter(numberFilter);
        List<Entity> purchaseOrders = datastore.prepare(purchaseOrderQuery).asList(FetchOptions.Builder.withDefaults());
        if (purchaseOrders.size() > 0){
            return purchaseOrders.get(0);
        } else {
            return null;
        }
    }

    public void setNumber(){
        List<Entity> purchaseOrders = getPurchaseOrders(parentCompany.getKey());
        if (purchaseOrders.size() > 0){
            Entity maxNumberEntiy = Collections.max(purchaseOrders, new MaxNumberComparator());
            long maxNumber = ((Number) maxNumberEntiy.getProperty("number")).longValue();
            number = Math.toIntExact(maxNumber) + 1;
        }
        log.info("Number: " + number);
        purchaseOrder.setProperty("number", number);
    }

    public void setNumber(int number){
        purchaseOrder.setProperty("number", number);
        this.number = number;
    }

    public void setMadeByUser(boolean madeByUser) {
        purchaseOrder.setProperty("made_by_user", madeByUser);
    }

    public boolean getMadeByUser() {
        Object madeByUser = purchaseOrder.getProperty("made_by_user");
        return madeByUser == null || (boolean) madeByUser;
    }

    public boolean getMadeByUser(Entity passedEntity) {
        Object madeByUser = passedEntity.getProperty("made_by_user");
        return madeByUser == null || (boolean) madeByUser;
    }

    private class MaxNumberComparator implements Comparator<Entity>{

        @Override
        public int compare(Entity a, Entity b) {
            return Long.compare((long) a.getProperty("number"), (long) b.getProperty("number"));
        }
    }

    public int getNumber(){
        long number = ((Number) purchaseOrder.getProperty("number")).intValue();
        return Math.toIntExact(number);
    }

    public int getNumber(Entity passedEntity){
        long number = ((Number) passedEntity.getProperty("number")).intValue();
        return Math.toIntExact(number);
    }

    public void setBuyerAccountnumber(String buyerAccountnumber){
        purchaseOrder.setProperty("buyer_accountnumber", buyerAccountnumber);
    }

    public String getBuyerAccountnumber(){
        return (String) purchaseOrder.getProperty("buyer_accountnumber");
    }

    public String getBuyerAccountnumber(Entity passedEntity){
        return (String) passedEntity.getProperty("buyer_accountnumber");
    }

    public void setDateTime(String dateTime){
        Date date = null;
        try {
            date = f.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        purchaseOrder.setProperty("date_time", date);
    }

    public String getDateTime(){
        Date dateTime = (Date) purchaseOrder.getProperty("date_time");
        return f.format(dateTime);
    }

    public String getDateTime(Entity passedEntity){
        Date dateTime = (Date) passedEntity.getProperty("date_time");
        return f.format(dateTime);
    }

    public void setProductCodesList(List<String> productCodesList){
        purchaseOrder.setProperty("product_codes_list", productCodesList);
    }

    public List<String> getProductCodesList(){
        List<String> productCodes = (List<String>) purchaseOrder.getProperty("product_codes_list");
        if (productCodes == null){
            return new ArrayList<String>();
        } else {
            return productCodes;
        }
    }

    public List<String> getProductCodesList(Entity passedEntity){
        List<String> productCodes = (List<String>) passedEntity.getProperty("product_codes_list");
        if (productCodes == null){
            return new ArrayList<String>();
        } else {
            return productCodes;
        }
    }

    public void setPricesList(List<Double> pricesList){
        purchaseOrder.setProperty("prices_list", pricesList);
    }

    public List<Double> getPricesList(){
        List<Double> pricesList = (List<Double>) purchaseOrder.getProperty("prices_list");
        if (pricesList == null){
            return new ArrayList<>();
        } else {
            return pricesList;
        }
    }

    public List<Double> getPricesList(Entity passedEntity){
        List<Double> pricesList = (List<Double>) passedEntity.getProperty("prices_list");
        if (pricesList == null){
            return new ArrayList<>();
        } else {
            return pricesList;
        }
    }

    public void setIsSelfBuyList(List<Boolean> isSelfBuyList){
        purchaseOrder.setProperty("is_self_buy_list", isSelfBuyList);
    }

    public List<Boolean> getIsSelfBuyList(){
        List<Boolean> isSelfBuyList = (List<Boolean>) purchaseOrder.getProperty("is_self_buy_list");
        if (isSelfBuyList == null){
            return new ArrayList<>();
        } else {
            return isSelfBuyList;
        }
    }

    public List<Boolean> getIsSelfBuyList(Entity passedEntity){
        List<Boolean> isSelfBuyList = (List<Boolean>) passedEntity.getProperty("is_self_buy_list");
        if (isSelfBuyList == null){
            return new ArrayList<>();
        } else {
            return isSelfBuyList;
        }
    }

    public void setAmountsList(List<Long> amountsList){
        purchaseOrder.setProperty("amounts_list", amountsList);
    }

    public List<Long> getAmountsList(){
        List<Long> amountsList = (List<Long>) purchaseOrder.getProperty("amounts_list");
        if (amountsList == null){
            return new ArrayList<>();
        } else {
            return amountsList;
        }
    }

    public List<Long> getAmountsList(Entity passedEntity){
        List<Long> amountsList = (List<Long>) passedEntity.getProperty("amounts_list");
        if (amountsList == null){
            return new ArrayList<>();
        } else {
            return amountsList;
        }
    }

    public void setCompleted(boolean completed){
        purchaseOrder.setProperty("completed", completed);
    }

    public void setCompleted(boolean completed, Entity passedEntity){
        passedEntity.setProperty("completed", completed);
    }

    public boolean getCompleted(){
        Object completed = purchaseOrder.getProperty("completed");
        return completed != null && (boolean) completed;
    }

    public boolean getCompleted(Entity passedEntity){
        Object completed = passedEntity.getProperty("completed");
        return completed != null && (boolean) completed;
    }

    public void saveAll(){
        datastore.put(purchaseOrder);
    }

    public void saveAllAsync(){
        AsyncDatastoreService datastore = DatastoreServiceFactory.getAsyncDatastoreService();
        datastore.put(purchaseOrder);
    }
}