package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Company extends Accounts {

    private Entity company;
    private Cryptor cryptor;
    private DatastoreService datastore;

    public Company(){
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
    }

    public Company(Entity company){
        this.company = company;
        super.account = company;
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
    }

    public Company(String accountnumber){
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
        this.account = getAccount(accountnumber);
    }

    public void postAccount(String accountnumber, String name, String password){
        if (password == null){
            Random rand = new Random();
            password = String.format("%04d", rand.nextInt(10000));
        }
        String encryptedPassword = cryptor.hashToString(password);

        KeyPair securityKeyPair = cryptor.generateKeyPair();
        String privateKeyStr = cryptor.privateKeyToString(securityKeyPair.getPrivate());
        String publicKeyStr = cryptor.publicKeyToString(securityKeyPair.getPublic());

        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Entity company = new Entity("Company", loginKey);

        setAccountnumber(company, accountnumber);
        setPassword(company, encryptedPassword);
        setOwner(company, name);
        setBalance(company, 0.0f);
        company.setProperty("transferarray", new ArrayList<String>());
        setPrivateKeyStr(company, privateKeyStr);
        setPublicKeyStr(company, publicKeyStr);

        saveAll(company);
    }

    public Entity createAccount(String accountnumber){
        Key key = KeyFactory.createKey("accountnumber", accountnumber);
        return new Entity("Company", key);
    }

    public Entity getAccount(String accountnumber){
        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Query loginQuery = new Query("Company", loginKey);
        List<Entity> accountList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        if (accountList.size() > 0){
            return accountList.get(0);
        } else {
            return null;
        }
    }

    public List<Entity> getAccounts(String accountnumber){
        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Query loginQuery = new Query("Company", loginKey);
        return datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> getAllAccounts(){
        Query query = new Query("Company");
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    public void addProduct(Entity passedEntity, Entity product){
        ArrayList<String> products = new ArrayList<>();
        if (passedEntity.getProperty("products") != null)
            products = (ArrayList<String>) passedEntity.getProperty("products");
        products.add(KeyFactory.keyToString(product.getKey()));
        passedEntity.setProperty("products", products);
    }

    public void setOwner(String owner){
        account.setProperty("owner", owner);
    }

    public void setOwner(Entity accountEntity, String owner){
        accountEntity.setProperty("owner", owner);
    }

    public String getOwner(){
        return (String) account.getProperty("owner");
    }

    public String getOwner(Entity passedEntity){
        return (String) passedEntity.getProperty("owner");
    }

    public List<Entity> getProducts(Entity passedEntity){
        try {
            ArrayList<Entity> products = new ArrayList<>();
            if (passedEntity.getProperty("products") != null) {
                ArrayList<String> productsStr = (ArrayList<String>) passedEntity.getProperty("products");
                for (String productStr : productsStr) {
                    products.add(datastore.get(KeyFactory.stringToKey(productStr)));
                }
            }
            return products;
        } catch (EntityNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Entity> getProducts(){
        try {
            ArrayList<Entity> products = new ArrayList<>();
            if (company.getProperty("products") != null) {
                ArrayList<String> productsStr = (ArrayList<String>) company.getProperty("products");
                for (String productStr : productsStr) {
                    products.add(datastore.get(KeyFactory.stringToKey(productStr)));
                }
            }
            return products;
        } catch (EntityNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }
}
