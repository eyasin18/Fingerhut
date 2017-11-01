package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;

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
        List<Integer> amountsList = new ArrayList<>();

        purchaseOrder = getPurchaseOrder(parentCompany.getKey(), buyerAccountnumber);

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
            amountsList.add(amount);
        }

        setDateTime(f.format(calendar.getTime()));
        setBuyerAccountnumber(buyerAccountnumber);
        setProductCodesList(produtCodesList);
        setPricesList(pricesList);
        setIsSelfBuyList(isSelfBuyList);
        setAmountsList(amountsList);
        saveAll();
        purchaseOrder = getPurchaseOrder(parentCompany.getKey(), buyerAccountnumber);
    }

    public List<Entity> getPurchaseOrders(Key parentCompanyKey){
        Query purchaseOrderQuery = new Query("PurchaseOrder", parentCompanyKey);
        return datastore.prepare(purchaseOrderQuery).asList(FetchOptions.Builder.withDefaults());
    }

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
            return Integer.compare((int) b.getProperty("number"), (int) a.getProperty("number"));
        }
    }

    public int getNumber(){
        long number = (long) purchaseOrder.getProperty("number");
        return Math.toIntExact(number);
    }

    public void setBuyerAccountnumber(String buyerAccountnumber){
        purchaseOrder.setProperty("buyer_accountnumber", buyerAccountnumber);
    }

    public String getBuyerAccountnumber(){
        return (String) purchaseOrder.getProperty("buyer_accountnumber");
    }

    public void setDateTime(String dateTime){
        purchaseOrder.setProperty("date_time", dateTime);
    }

    public String getDateTime(){
        return (String) purchaseOrder.getProperty("date_time");
    }

    public void setProductCodesList(List<String> productCodesList){
        purchaseOrder.setProperty("product_codes_list", productCodesList);
    }

    public List<String> getProductCodesList(){
        return (List<String>) purchaseOrder.getProperty("product_codes_list");
    }

    public void setPricesList(List<Double> pricesList){
        purchaseOrder.setProperty("prices_list", pricesList);
    }

    public List<Double> getPricesList(){
        return (List<Double>) purchaseOrder.getProperty("prices_list");
    }

    public void setIsSelfBuyList(List<Boolean> isSelfBuyList){
        purchaseOrder.setProperty("is_self_buy_list", isSelfBuyList);
    }

    public List<Boolean> getIsSelfBuyList(){
        return (List<Boolean>) purchaseOrder.getProperty("is_self_buy_list");
    }

    public void setAmountsList(List<Integer> amountsList){
        purchaseOrder.setProperty("amounts_list", amountsList);
    }

    public List<Integer> getAmountsList(){
        return (List<Integer>) purchaseOrder.getProperty("amounts_list");
    }

    public void saveAll(){
        datastore.put(purchaseOrder);
    }
}