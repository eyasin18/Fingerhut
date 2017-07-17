package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class Accounts {

    private static int featureCount = 25;

    Entity account;
    private DatastoreService datastore;
    private Cryptor cryptor;
    private Logger log = Logger.getLogger(Accounts.class.getName());

    public Accounts(){
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
    }

    public Accounts(Entity account){
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
        this.account = account;
    }

    public Entity createAccount(String accountnumber){
        Key key = KeyFactory.createKey("accountnumber", accountnumber);
        return new Entity("Account", key);
    }

    public void postAccount(String accountnumber, String password, String name){
        if (password == null){
            Random rand = new Random();
            password = String.format("%04d", rand.nextInt(10000));
        }
        String encryptedPassword = cryptor.hashToString(password);
        byte[] encryptedByteName = cryptor.encrypt(name, cryptor.hashToByte(password));
        name = cryptor.bytesToHex(encryptedByteName);

        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Entity account = new Entity("Account", loginKey);

        account.setProperty("accountnumber", accountnumber);
        account.setProperty("password", encryptedPassword);
        account.setProperty("owner", name);
        account.setProperty("balance", 15.00);
        account.setProperty("transferarray", new ArrayList<String>());
        setFeature(account,0, true);

        datastore.put(account);
    }

    public Entity getAccount(String accountnumber){
        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Query loginQuery = new Query("Account", loginKey);
        List<Entity> accountList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        if (accountList.size() > 0){
            return accountList.get(0);
        } else {
            return null;
        }
    }

    public Entity getAccount(Key accountKey){
        try {
            return datastore.get(accountKey);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public List<Entity> getAccounts(String accountnumber){
        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Query loginQuery = new Query("Account", loginKey);
        return datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
    }

    public List<Entity> getAllAccounts(){
        Query query = new Query("Account");
        return datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    }

    public String getKey(){
        return KeyFactory.keyToString(account.getKey());
    }

    public String getKey(Entity passedEntity){
        return KeyFactory.keyToString(passedEntity.getKey());
    }

    public void setAccountnumber(String accountnumber){
        account.setProperty("accountnumber", accountnumber);
    }

    public void setAccountnumber(Entity passedEntity, String accountnumber){
        passedEntity.setProperty("accountnumber", accountnumber);
    }

    public String getAccountnumber(){
        return (String) account.getProperty("accountnumber");
    }

    public String getAccountnumber(Entity passedEntity){
        return (String) passedEntity.getProperty("accountnumber");
    }

    public void setBalance(String balanceStr){
        float balance = Float.parseFloat(balanceStr);
        account.setProperty("balance", balance);
    }

    public void setBalance(float balance){
        account.setProperty("balance", balance);
    }

    public void setBalance(Entity passedEntity, String balanceStr){
        float balance = Float.parseFloat(balanceStr);
        passedEntity.setProperty("balance", balance);
    }

    public void setBalance(Entity passedEntity, float balance){
        passedEntity.setProperty("balance", balance);
    }

    public String getBalance(){
        return String.valueOf((double) account.getProperty("balance"));
    }

    public String getBalance(Entity passedEntity){
        return String.valueOf((double) passedEntity.getProperty("balance"));
    }

    public void setOwner(String owner){
        String password = (String) account.getProperty("password");
        byte[] encryptedByteName = cryptor.encrypt(owner, cryptor.hashToByte(password));
        String encryptedOwner = cryptor.bytesToHex(encryptedByteName);
        account.setProperty("owner", encryptedOwner);
    }

    public void setOwner(String owner, Entity accountEntity){
        String password = (String) accountEntity.getProperty("password");
        byte[] encryptedByteName = cryptor.encrypt(owner, cryptor.hashToByte(password));
        String encryptedOwner = cryptor.bytesToHex(encryptedByteName);
        accountEntity.setProperty("owner", encryptedOwner);
    }

    public void setOwner(String owner, String password){
        byte[] encryptedByteName = cryptor.encrypt(owner, cryptor.hashToByte(password));
        String encryptedOwner = cryptor.bytesToHex(encryptedByteName);
        account.setProperty("owner", encryptedOwner);
    }

    public String getOwner(){
        String encryptedNameStr = (String) account.getProperty("owner");
        byte[] encryptedName = cryptor.hexToBytes(encryptedNameStr);
        String encryptedHexPasswordStr = (String) account.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedHexPasswordStr);
        return cryptor.decrypt(encryptedName, encryptedPassword);
    }

    public String getOwner(Entity passedEntity){
        String encryptedNameStr = (String) passedEntity.getProperty("owner");
        byte[] encryptedName = cryptor.hexToBytes(encryptedNameStr);
        String encryptedHexPasswordStr = (String) passedEntity.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedHexPasswordStr);
        return cryptor.decrypt(encryptedName, encryptedPassword);
    }

    public void setPassword(String password){
        if (password == null){
            Random rand = new Random();
            password = String.format("%04d", rand.nextInt(10000));
        }
        String encryptedPassword = cryptor.hashToString(password);
        account.setProperty("password", encryptedPassword);
    }

    public void setPassword(Entity passedEntity, String password){
        if (password == null){
            Random rand = new Random();
            password = String.format("%04d", rand.nextInt(10000));
        }
        String encryptedPassword = cryptor.hashToString(password);
        passedEntity.setProperty("password", encryptedPassword);
    }

    public String getPassword(){
        return (String) account.getProperty("password");
    }

    public String getPassword(Entity passedEntity){
        return (String) passedEntity.getProperty("password");
    }

    public void addTransfer(Entity transfer){
        ArrayList<String> transfersList = new ArrayList<>();
        if (account.getProperty("transferarray") != null)
            transfersList = (ArrayList<String>) account.getProperty("transferarray");
        transfersList.add(KeyFactory.keyToString(transfer.getKey()));
        account.setProperty("transferarray", transfersList);
    }

    public void addTransfer(Entity passedEntity, Entity transfer){
        ArrayList<String> transfersList = new ArrayList<>();
        if (passedEntity.getProperty("transferarray") != null)
            transfersList = (ArrayList<String>) passedEntity.getProperty("transferarray");
        transfersList.add(KeyFactory.keyToString(transfer.getKey()));
        passedEntity.setProperty("transferarray", transfersList);
    }

    public ArrayList<String> getTransfers(){
        ArrayList<String> transfersList = new ArrayList<>();
        if (account.getProperty("transferarray") != null)
            transfersList = (ArrayList<String>) account.getProperty("transferarray");
        return transfersList;
    }

    public ArrayList<String> getTransfers(Entity passedEntity){
        ArrayList<String> transfersList = new ArrayList<>();
        if (passedEntity.getProperty("transferarray") != null)
            transfersList = (ArrayList<String>) passedEntity.getProperty("transferarray");
        return transfersList;
    }

    public Entity getEntity(String keyStr){
        try {
            return datastore.get(KeyFactory.stringToKey(keyStr));
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public void setFeature(Entity passedEntity, long featureNumber, boolean add){
        ArrayList<Long> featureList = new ArrayList<>();
        if (passedEntity.getProperty("feature_list") != null)
            featureList = (ArrayList<Long>) passedEntity.getProperty("feature_list");
        if (add) featureList.add(featureNumber);
        else featureList.remove(featureNumber);
        passedEntity.setProperty("feature_list", featureList);
    }

    public void setFeature(long featureNumber, boolean add){
        ArrayList<Long> featureList = new ArrayList<>();
        if (account.getProperty("feature_list") != null)
            featureList = (ArrayList<Long>) account.getProperty("feature_list");
        if (add) featureList.add(featureNumber);
        else featureList.remove(featureNumber);
        account.setProperty("feature_list", featureList);
    }

    public ArrayList<Long> getFeatures(){
        ArrayList<Long> featureList = new ArrayList<>();
        if (account.getProperty("feature_list") != null)
            featureList = (ArrayList<Long>) account.getProperty("feature_list");
        return featureList;
    }

    public ArrayList<Long> getFeatures(Entity passedEntity){
        ArrayList<Long> featureList = new ArrayList<>();
        if (passedEntity.getProperty("feature_list") != null)
            featureList = (ArrayList<Long>) passedEntity.getProperty("feature_list");
        return featureList;
    }

    public void saveAll(){
        datastore.put(account);
    }

    public void saveAll(Entity passedEntity){
        datastore.put(passedEntity);
    }
}
