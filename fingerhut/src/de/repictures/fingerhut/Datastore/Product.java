package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Product {

    private DatastoreService datastore;
    private Logger log = Logger.getLogger(Accounts.class.getName());

    public Product(){
        datastore = DatastoreServiceFactory.getDatastoreService();
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
}