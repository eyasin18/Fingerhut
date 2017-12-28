package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unchecked")
public class PurchaseOrder{

    private DatastoreService datastore;
    public Entity purchaseOrder;
    private Entity parentCompany;
    private int number = 0;
    private SimpleDateFormat f;
    private Calendar calendar;

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

    @Deprecated
    public PurchaseOrder(Entity parentCompany, String buyerAccountnumber, Locale locale){
        datastore = DatastoreServiceFactory.getDatastoreService();
        this.parentCompany = parentCompany;
        calendar = Calendar.getInstance();
        f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", locale);
        purchaseOrder = getPurchaseOrder(parentCompany.getKey(), buyerAccountnumber);
    }

    public void updatePurchaseOrder(Entity parentCompany, List<String[]> items, String buyerAccountnumber){
        List<String> produtCodesList = new ArrayList<>();
        List<Double> pricesList = new ArrayList<>();
        List<Boolean> isSelfBuyList = new ArrayList<>();
        List<Long> amountsList = new ArrayList<>();

        //purchaseOrder = getPurchaseOrder(parentCompany.getKey(), buyerAccountnumber);

        if (purchaseOrder == null){
            purchaseOrder = new Entity("PurchaseOrder", parentCompany.getKey());
            setNumber();
        } else {
            produtCodesList = getProductCodesList();
            pricesList = getPricesList();
            isSelfBuyList = getIsSelfBuyList();
            amountsList = getAmountsList();
        }

        for (String[] item : items){
            produtCodesList.add(item[0]);
            double price = Double.parseDouble(item[1]);
            pricesList.add(price);
            Boolean isSelfBuy = Boolean.parseBoolean(item[2]);
            isSelfBuyList.add(isSelfBuy);
            int amount = Integer.parseInt(item[3]);
            amountsList.add((long) amount);
        }

        setDateTime(f.format(calendar.getTime()));
        setBuyerAccountnumber(buyerAccountnumber);
        setProductCodesList(produtCodesList);
        setPricesList(pricesList);
        setIsSelfBuyList(isSelfBuyList);
        setAmountsList(amountsList);
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
            long maxNumber = ((long) maxNumberEntiy.getProperty("number"));
            number = Math.toIntExact(maxNumber) + 1;
        }
        purchaseOrder.setProperty("number", number);
    }

    public void setNumber(int number){
        purchaseOrder.setProperty("number", number);
        this.number = number;
    }

    private class MaxNumberComparator implements Comparator<Entity>{

        @Override
        public int compare(Entity a, Entity b) {
            return Long.compare((long) a.getProperty("number"), (long) b.getProperty("number"));
        }
    }

    public int getNumber(){
        long number = (long) purchaseOrder.getProperty("number");
        return Math.toIntExact(number);
    }

    public int getNumber(Entity passedEntity){
        long number = (long) passedEntity.getProperty("number");
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
        return (List<String>) purchaseOrder.getProperty("product_codes_list");
    }

    public List<String> getProductCodesList(Entity passedEntity){
        return (List<String>) passedEntity.getProperty("product_codes_list");
    }

    public void setPricesList(List<Double> pricesList){
        purchaseOrder.setProperty("prices_list", pricesList);
    }

    public List<Double> getPricesList(){
        return (List<Double>) purchaseOrder.getProperty("prices_list");
    }

    public List<Double> getPricesList(Entity passedEntity){
        return (List<Double>) passedEntity.getProperty("prices_list");
    }

    public void setIsSelfBuyList(List<Boolean> isSelfBuyList){
        purchaseOrder.setProperty("is_self_buy_list", isSelfBuyList);
    }

    public List<Boolean> getIsSelfBuyList(){
        return (List<Boolean>) purchaseOrder.getProperty("is_self_buy_list");
    }

    public List<Boolean> getIsSelfBuyList(Entity passedEntity){
        return (List<Boolean>) passedEntity.getProperty("is_self_buy_list");
    }

    public void setAmountsList(List<Long> amountsList){
        purchaseOrder.setProperty("amounts_list", amountsList);
    }

    public List<Long> getAmountsList(){
        return (List<Long>) purchaseOrder.getProperty("amounts_list");
    }

    public List<Long> getAmountsList(Entity passedEntity){
        return (List<Long>) passedEntity.getProperty("amounts_list");
    }

    public void setCompleted(boolean completed){
        purchaseOrder.setProperty("completed", completed);
    }

    public void setCompleted(boolean completed, Entity passedEntity){
        passedEntity.setProperty("completed", completed);
    }

    public boolean getCompleted(){
        return (boolean) purchaseOrder.getProperty("completed");
    }

    public boolean getCompleted(Entity passedEntity){
        return (boolean) passedEntity.getProperty("completed");
    }

    public void saveAll(){
        datastore.put(purchaseOrder);
    }
}