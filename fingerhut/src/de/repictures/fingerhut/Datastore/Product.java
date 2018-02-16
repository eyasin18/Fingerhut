package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({"unchecked", "Duplicates"})
public class Product {

    private DatastoreService datastore;
    private Logger log = Logger.getLogger(Account.class.getName());
    public Entity product;

    public Product(){
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public Product(Entity productEntity){
        datastore = DatastoreServiceFactory.getDatastoreService();
        this.product = productEntity;
    }

    public Product(String code, String companynumber){
        datastore = DatastoreServiceFactory.getDatastoreService();
        this.product = getSpecificProduct(code, companynumber);
    }

    public Entity addProduct(String code, String name, Entity companyEntity, String priceStr){
        Company company = new Company(companyEntity);
        Key key = KeyFactory.createKey("code_companynumber", code + company.getAccountnumber());
        Entity createdProduct = new Entity("Product", key);
        createdProduct.setProperty("code", code);
        createdProduct.setProperty("name", name);
        createdProduct.setProperty("selling_company", company.getAccountnumber());
        double price = Double.parseDouble(priceStr);
        createdProduct.setProperty("price", price);
        return createdProduct;
    }

    public static List<Entity> getProductsByCode(String code, boolean mustBeBuyable){
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query productQuery = new Query("Product");
        Query.Filter codeFilter = new Query.FilterPredicate("code", Query.FilterOperator.EQUAL, code);
        productQuery.setFilter(codeFilter);
        if (mustBeBuyable){
            Query.Filter buyableFilter = new Query.FilterPredicate("buyable", Query.FilterOperator.EQUAL, true);
            productQuery.setFilter(buyableFilter);
        }
        productQuery.addSort("name", Query.SortDirection.ASCENDING);
        List<Entity> productsList = datastore.prepare(productQuery).asList(FetchOptions.Builder.withDefaults());
        if (productsList.size() > 0){
            return productsList;
        } else {
            return new ArrayList<>();
        }
    }

    public static List<Entity> getProductsByCompany(String companynumber, boolean mustBeBuyable){
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query productQuery = new Query("Product");
        Query.Filter companyFilter = new Query.FilterPredicate("selling_company", Query.FilterOperator.EQUAL, companynumber);
        if (mustBeBuyable){
            Query.Filter buyableFilter = new Query.FilterPredicate("buyable", Query.FilterOperator.EQUAL, true);
            productQuery.setFilter(buyableFilter);
            Query.CompositeFilter compositeFilter = Query.CompositeFilterOperator.and(companyFilter, buyableFilter);
            productQuery.setFilter(compositeFilter);
        } else {
            productQuery.setFilter(companyFilter);
        }
        productQuery.addSort("name", Query.SortDirection.ASCENDING);
        List<Entity> productsList = datastore.prepare(productQuery).asList(FetchOptions.Builder.withDefaults());
        if (productsList.size() > 0){
            return productsList;
        } else {
            return new ArrayList<>();
        }
    }

    public static Entity getSpecificProduct(String code, String companyNumber){
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query productQuery = new Query("Product");
        Query.Filter companyFilter = new Query.FilterPredicate("selling_company", Query.FilterOperator.EQUAL, companyNumber);
        Query.Filter codeFilter = new Query.FilterPredicate("code", Query.FilterOperator.EQUAL, code);
        Query.CompositeFilter compositeFilter = Query.CompositeFilterOperator.and(companyFilter, codeFilter);
        productQuery.setFilter(compositeFilter);
        List<Entity> productsList = datastore.prepare(productQuery).asList(FetchOptions.Builder.withDefaults());
        if (productsList.size() > 0){
            return productsList.get(0);
        } else {
            return null;
        }
    }

    public static Entity getSpecificProduct(Key productKey){
        try {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            return datastore.get(productKey);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public void setCode(String code){
        product.setProperty("code", code);
    }

    public void setCode(Entity passedEntity, String code){
        passedEntity.setProperty("code", code);
    }

    public String getCode(){
        return (String) product.getProperty("code");
    }

    public String getCode(Entity passedEntity){
        return (String) passedEntity.getProperty("code");
    }

    public void setName(String name){
        product.setProperty("name", name);
    }

    public void setName(Entity passedEntity, String name){
        passedEntity.setProperty("name", name);
    }

    public String getName(){
        return (String) product.getProperty("name");
    }

    public String getName(Entity passedEntity){
        return (String) passedEntity.getProperty("name");
    }

    public void setSellingCompany(String sellingCompanyAccountnumber){
        product.setProperty("selling_company", sellingCompanyAccountnumber);
    }

    public void setSellingCompany(Entity passedEntity, String sellingCompanyAccountnumber){
        passedEntity.setProperty("selling_company", sellingCompanyAccountnumber);
    }

    public String getSellingCompany(){
        return (String) product.getProperty("selling_company");
    }

    public String getSellingCompany(Entity passedEntity){
        return (String) passedEntity.getProperty("selling_company");
    }

    public String getImageUrl(Entity passedEntity){
        return (String) passedEntity.getProperty("image_url");
    }

    public void setPrice(double price){
        product.setProperty("price", price);
    }

    public void setPrice(Entity passedEntity, double price){
        passedEntity.setProperty("price", price);
    }

    public double getPrice(){
        return (double) product.getProperty("price");
    }

    public double getPrice(Entity passedEntity){
        return (double) passedEntity.getProperty("price");
    }

    public void setSelfBuy(boolean selfBuy){
        product.setProperty("selfBuy", selfBuy);
    }

    public void setSelfBuy(Entity passedEntity, boolean selfBuy){
        passedEntity.setProperty("selfBuy", selfBuy);
    }

    public boolean getSelfBuy(){
        return (boolean) product.getProperty("selfBuy");
    }

    public boolean getSelfBuy(Entity passedEntity){
        return (boolean) passedEntity.getProperty("selfBuy");
    }

    public void setBuyable(boolean buyable){
        product.setProperty("buyable", buyable);
    }

    public void setBuyable(Entity passedEntity, boolean buyable){
        passedEntity.setProperty("buyable", buyable);
    }

    public boolean getBuyable(){
        return (boolean) product.getProperty("buyable");
    }

    public boolean getBuyable(Entity passedEntity){
        return (boolean) passedEntity.getProperty("buyable");
    }

    public void saveAll(){
        datastore.put(product);
    }

    public void saveAll(Entity passedEntity){
        datastore.put(passedEntity);
    }
}