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

    public Product(String code){
        datastore = DatastoreServiceFactory.getDatastoreService();
        this.product = getProduct(code);
    }

    public Entity addProduct(String code, String name, Entity company, String priceStr){
        Key key = KeyFactory.createKey("code", code);
        Entity createdProduct = new Entity("Product", key);
        createdProduct.setProperty("code", code);
        createdProduct.setProperty("name", name);
        createdProduct.setProperty("selling_company", KeyFactory.keyToString(company.getKey()));
        double price = Double.parseDouble(priceStr);
        createdProduct.setProperty("price", price);
        return createdProduct;
    }

    public Entity getProduct(String code){
        Key loginKey = KeyFactory.createKey("code", code);
        Query loginQuery = new Query("Product", loginKey);
        List<Entity> accountList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        if (accountList.size() > 0){
            return accountList.get(0);
        } else {
            return null;
        }
    }

    public Entity getProduct(Key productKey){
        try {
            return datastore.get(productKey);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public List<Entity> getProducts(String code){
        Key productKey = KeyFactory.createKey("code", code);
        Query productQuery = new Query("Product", productKey);
        List<Entity> products = datastore.prepare(productQuery).asList(FetchOptions.Builder.withDefaults());
        if (products != null) return products;
        else return new ArrayList<>();
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

    public void setSellingCompany(String sellingCompanyName){
        product.setProperty("selling_company", sellingCompanyName);
    }

    public void setSellingCompany(Entity passedEntity, String sellingCompanyName){
        passedEntity.setProperty("selling_company", sellingCompanyName);
    }

    public String getSellingCompany(){
        return (String) product.getProperty("selling_company");
    }

    public String getSellingCompany(Entity passedEntity){
        return (String) passedEntity.getProperty("selling_company");
    }

    public void setImageUrl(String imageUrl){
        product.setProperty("image_url", imageUrl);
    }

    public void setImageUrl(Entity passedEntity, String imageUrl){
        passedEntity.setProperty("image_url", imageUrl);
    }

    public String getImageUrl(){
        return (String) product.getProperty("image_url");
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

    public void saveAll(){
        datastore.put(product);
    }

    public void saveAll(Entity passedEntity){
        datastore.put(passedEntity);
    }
}