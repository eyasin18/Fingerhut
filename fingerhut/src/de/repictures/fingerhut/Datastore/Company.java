package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
public class Company extends Account {

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
        this.company = this.account;
    }

    public void postAccount(String companynumber, String ownername, String password){
        if (password == null){
            Random rand = new Random();
            password = String.format("%04d", rand.nextInt(10000));
        }
        String encryptedPassword = cryptor.hashToString(password);

        KeyPair securityKeyPair = cryptor.generateKeyPair();
        String privateKeyStr = cryptor.privateKeyToString(securityKeyPair.getPrivate());
        byte[] privateKey = cryptor.hexToBytes(privateKeyStr);
        String publicKeyStr = cryptor.publicKeyToString(securityKeyPair.getPublic());

        String encryptedPrivateKeyStr = "";
        try {
            byte[] passwordBytes = password.getBytes("ISO-8859-1");
            byte[] passwordKey = new byte[32];
            for (int i = 0; i < passwordKey.length; i++){
                passwordKey[i] = passwordBytes[i % passwordBytes.length];
            }
            byte[] encryptedPrivateKey = cryptor.encryptSymetricFromByte(privateKey, passwordKey);
            encryptedPrivateKeyStr = cryptor.bytesToHex(encryptedPrivateKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Key loginKey = KeyFactory.createKey("accountnumber", companynumber);
        Entity company = new Entity("Company", loginKey);

        setAccountnumber(company, companynumber);
        setHashedPassword(company, encryptedPassword);
        setOwner(company, ownername);
        setBalance(company, 0.0f);
        company.setProperty("transferarray", new ArrayList<String>());
        setPrivateKeyStr(company, encryptedPrivateKeyStr);
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

    @Deprecated
    public void setShoppingRequests(Map<String, List<String[]>> shoppingMap){
        List<String> shoppingMapAccountnumbers = new ArrayList<>(shoppingMap.keySet());
        List<String> shoppingLists = new ArrayList<>();

        for (List<String[]> items : shoppingMap.values()){
            StringBuilder itemStrBuilder = new StringBuilder();
            for (String[] item : items){
                for (String property : item){
                    itemStrBuilder.append(property);
                    itemStrBuilder.append("ò");
                }
                itemStrBuilder.append("ň");
            }
            shoppingLists.add(itemStrBuilder.toString());
        }

        account.setProperty("shoppingMapKeys", shoppingMapAccountnumbers);
        account.setProperty("shoppingLists", shoppingLists);
    }

    @Deprecated
    public void setShoppingRequests(Entity passedEntity, Map<String, List<String[]>> shoppingMap){
        List<String> shoppingMapAccountnumbers = new ArrayList<>(shoppingMap.keySet());
        List<String> shoppingLists = new ArrayList<>();

        for (List<String[]> items : shoppingMap.values()){
            StringBuilder itemStrBuilder = new StringBuilder();
            for (String[] item : items){
                for (String property : item){
                    itemStrBuilder.append(property);
                    itemStrBuilder.append("ò");
                }
                itemStrBuilder.append("ň");
            }
            shoppingLists.add(itemStrBuilder.toString());
        }

        passedEntity.setProperty("shoppingMapKeys", shoppingMapAccountnumbers);
        passedEntity.setProperty("shoppingLists", shoppingLists);
    }

    @Deprecated
    public Map<String, List<String[]>> getShoppingRequests(){
        try {
            List<String> shoppingMapAccountnumbers = (List<String>) account.getProperty("shoppingMapKeys");
            List<String> shoppingListsRaw = (List<String>) account.getProperty("shoppingLists");

            List<List<String[]>> shoppingLists = new ArrayList<>();
            for (String listItem : shoppingListsRaw){
                List<String> itemsRaw = Arrays.asList(listItem.split("ň"));
                List<String[]> items = new ArrayList<>();
                for (String propertiesRaw : itemsRaw){
                    items.add(propertiesRaw.split("ò"));
                }
                shoppingLists.add(items);
            }

            Iterator<String> shoppingMapAccountnumbersIter = shoppingMapAccountnumbers.iterator();
            Iterator<List<String[]>> shoppingListsIter = shoppingLists.iterator();
            return IntStream.range(0, shoppingMapAccountnumbers.size()).boxed()
                    .collect(Collectors.toMap(_i -> shoppingMapAccountnumbersIter.next(), _i -> shoppingListsIter.next()));
        } catch (NullPointerException e){
            return new HashMap<>();
        }
    }

    @Deprecated
    public Map<String, List<String[]>> getShoppingRequests(Entity passedEntity){
        try {
            List<String> shoppingMapAccountnumbers = (List<String>) passedEntity.getProperty("shoppingMapKeys");
            List<String> shoppingListsRaw = (List<String>) passedEntity.getProperty("shoppingLists");

            List<List<String[]>> shoppingLists = new ArrayList<>();
            for (String listItem : shoppingListsRaw){
                List<String> itemsRaw = Arrays.asList(listItem.split("ň"));
                List<String[]> items = new ArrayList<>();
                for (String propertiesRaw : itemsRaw){
                    items.add(propertiesRaw.split("ò"));
                }
                shoppingLists.add(items);
            }

            Iterator<String> shoppingMapAccountnumbersIter = shoppingMapAccountnumbers.iterator();
            Iterator<List<String[]>> shoppingListsIter = shoppingLists.iterator();
            return IntStream.range(0, shoppingMapAccountnumbers.size()).boxed()
                    .collect(Collectors.toMap(_i -> shoppingMapAccountnumbersIter.next(), _i -> shoppingListsIter.next()));
        } catch (NullPointerException e){
            return new HashMap<>();
        }
    }
}
