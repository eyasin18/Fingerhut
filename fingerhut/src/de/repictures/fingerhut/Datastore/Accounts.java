package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class Accounts {

    public Entity account;
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

    public Accounts(String accountnumber){
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
        this.account = getAccount(accountnumber);
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
        byte[] encryptedByteName = cryptor.encryptSymetricFromString(name, cryptor.hashToByte(password));
        name = cryptor.bytesToHex(encryptedByteName);

        KeyPair securityKeyPair = cryptor.generateKeyPair();
        String privateKeyStr = cryptor.privateKeyToString(securityKeyPair.getPrivate());
        String publicKeyStr = cryptor.publicKeyToString(securityKeyPair.getPublic());

        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Entity account = new Entity("Account", loginKey);

        setAccountnumber(account, accountnumber);
        setHashedPassword(account, encryptedPassword);
        setOwner(account, name);
        setBalance(account, 15.00f);
        account.setProperty("transferarray", new ArrayList<String>());
        setGroup(account, 7);
        setFeature(account,0, true);
        setPrivateKeyStr(account, privateKeyStr);
        setPublicKeyStr(account, publicKeyStr);

        saveAll(account);
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
        String encryptedPasswordHex = (String) account.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedPasswordHex);
        byte[] encryptedByteName = cryptor.encryptSymetricFromString(owner, encryptedPassword);
        String encryptedOwner = cryptor.bytesToHex(encryptedByteName);
        account.setProperty("owner", encryptedOwner);
    }

    public void setOwner(Entity accountEntity, String owner){
        String encryptedPasswordHex = (String) accountEntity.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedPasswordHex);
        byte[] encryptedByteName = cryptor.encryptSymetricFromString(owner, encryptedPassword);
        String encryptedOwner = cryptor.bytesToHex(encryptedByteName);
        accountEntity.setProperty("owner", encryptedOwner);
    }

    public void setOwner(String owner, String encryptedPasswordHex){
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedPasswordHex);
        byte[] encryptedByteName = cryptor.encryptSymetricFromString(owner, encryptedPassword);
        String encryptedOwner = cryptor.bytesToHex(encryptedByteName);
        account.setProperty("owner", encryptedOwner);
    }

    public String getOwner(){
        String encryptedNameStr = (String) account.getProperty("owner");
        byte[] encryptedName = cryptor.hexToBytes(encryptedNameStr);
        String encryptedHexPasswordStr = (String) account.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedHexPasswordStr);
        return cryptor.decryptSymetricToString(encryptedName, encryptedPassword);
    }

    public String getOwner(Entity passedEntity){
        String encryptedNameStr = (String) passedEntity.getProperty("owner");
        byte[] encryptedName = cryptor.hexToBytes(encryptedNameStr);
        String encryptedHexPasswordStr = (String) passedEntity.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedHexPasswordStr);
        return cryptor.decryptSymetricToString(encryptedName, encryptedPassword);
    }

    public String getEncryptedOwner(){
        return (String) account.getProperty("owner");
    }

    public String getEncryptedOwner(Entity passedEntity){
        return (String) passedEntity.getProperty("owner");
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

    public String getHashedPassword(){
        return (String) account.getProperty("password");
    }

    public String getHashedPassword(Entity passedEntity){
        return (String) passedEntity.getProperty("password");
    }

    public void setHashedPassword(Entity passedEntity, String hashedPassword){
        if (hashedPassword == null){
            Random rand = new Random();
            hashedPassword = String.format("%04d", rand.nextInt(10000));
        }
        passedEntity.setProperty("password", hashedPassword);
    }

    public String getSaltetPassword(String salt){
        String password = (String) account.getProperty("password");
        return cryptor.hashToString(password + salt);
    }

    public String getSaltedPassword(Entity passedEntity, String salt){
        String password = (String) passedEntity.getProperty("password");
        return cryptor.hashToString(password + salt);
    }

    public void addTransfer(Entity transfer){
        ArrayList<String> transfersList = new ArrayList<>();
        if (account.getProperty("transferarray") != null)
            transfersList = (ArrayList<String>) account.getProperty("transferarray");
        transfersList.add(0, KeyFactory.keyToString(transfer.getKey()));
        account.setProperty("transferarray", transfersList);
    }

    public void addTransfer(Entity passedEntity, Entity transfer){
        ArrayList<String> transfersList = new ArrayList<>();
        if (passedEntity.getProperty("transferarray") != null)
            transfersList = (ArrayList<String>) passedEntity.getProperty("transferarray");
        transfersList.add(0, KeyFactory.keyToString(transfer.getKey()));
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


    /**
     * Liste der Features:
     * 0 = Produkt hinzufügen
     * 1 = Authentifizierungs QR-Codes lesen und schreiben
     */

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


    /**
     * Liste der Groups:
     * 0 = Beamter der Zentralbank
     * 1 = Beamter des Finanzministeriums
     * 2 = Beamter des Wirtschaftsministeriums
     * 3 = Beamter des Ministeriums für Kultus und Soziales
     * 4 = Beamter des Umweltministeriums
     * 5 = Beamter des Innen-/Justizministeriums
     * 6 = Angestellter eines Unternehmens
     * 7 = Arbeitslos
     */

    public void setGroup(Entity passedEntity, int groupNumber){
        passedEntity.setProperty("group", groupNumber);
    }

    public void setGroup(int groupNumber){
        account.setProperty("group", groupNumber);
    }

    public int getGroup(){
        return (int) account.getProperty("group");
    }

    public int getGroup(Entity passedEntity){
        return (int) passedEntity.getProperty("group");
    }

    public String createAuthString(){
        SecureRandom sr = new SecureRandom();
        return new BigInteger(130, sr).toString(32);
    }

    public void setAuthString(String authString){
        account.setProperty("authString", authString);
    }

    public void setAuthString(){
        account.setProperty("authString", createAuthString());
    }

    public void setAuthString(String authString, Entity passedEntity){
        passedEntity.setProperty("authString", authString);
    }

    public void setAuthString(Entity passedEntity){
        passedEntity.setProperty("authString", createAuthString());
    }

    public String getAuthString(){
        return (String) account.getProperty("authString");
    }

    public String getAuthString(Entity passedEntity){
        return (String) passedEntity.getProperty("authString");
    }

    public void setGeneric(String propertyName, Object value){
        account.setProperty(propertyName, value);
    }

    public void setGeneric(Entity passedEntity, String propertyName, Object value){
        passedEntity.setProperty(propertyName, value);
    }

    public void setQRBlob(Entity passedEntity, Blob image){
        passedEntity.setProperty("qrBlob", image);
    }

    public void setQRBlob(Blob image){
        account.setProperty("qrBlob", image);
    }

    public Blob getQRBlob(){
        return (Blob) account.getProperty("qrBlob");
    }

    public Blob getQRBlob(Entity passedEntity){
        return (Blob) passedEntity.getProperty("qrBlob");
    }

    public String getPrivateKeyStr(){
        return (String) account.getProperty("privateKey");
    }

    public String getPrivateKeyStr(Entity passedEntity){
        return (String) passedEntity.getProperty("privateKey");
    }

    public void setPrivateKeyStr(String privateKeyStr){
        account.setProperty("privateKey", privateKeyStr);
    }

    public void setPrivateKeyStr(Entity passedEntity, String privateKeyStr){
        passedEntity.setProperty("privateKey", privateKeyStr);
    }

    public String getPublicKeyStr(){
        return (String) account.getProperty("publicKey");
    }

    public String getPublicKeyStr(Entity passedEntity){
        return (String) passedEntity.getProperty("publicKey");
    }

    public void setPublicKeyStr(String publicKeyStr){
        account.setProperty("publicKey", publicKeyStr);
    }

    public void setPublicKeyStr(Entity passedEntity, String publicKeyStr){
        passedEntity.setProperty("publicKey", publicKeyStr);
    }

    public String getFirebaseDeviceToken(){
        return (String) account.getProperty("firebaseDeviceToken");
    }

    public String getFirebaseDeviceToken(Entity passedEntity){
        return (String) passedEntity.getProperty("firebaseDeviceToken");
    }

    public void setFirebaseDeviceToken(String deviceToken){
        account.setProperty("firebaseDeviceToken", deviceToken);
    }

    public void setFirebaseDeviceToken(Entity passedEntity, String deviceToken){
        passedEntity.setProperty("firebaseDeviceToken", deviceToken);
    }

    public void deleteDeviceTokenFromAllAccounts(String deviceToken){
        Query.Filter propertyFiler = new Query.FilterPredicate("firebaseDeviceToken", Query.FilterOperator.EQUAL, deviceToken);
        Query deviceTokensAccountQuery = new Query("Account").setFilter(propertyFiler);
        List<Entity> deviceTokenAccounts = datastore.prepare(deviceTokensAccountQuery).asList(FetchOptions.Builder.withDefaults());
        for (Entity account : deviceTokenAccounts){
            setFirebaseDeviceToken(account, null);
            saveAll(account);
        }
    }

    public void saveAll(){
        datastore.put(account);
    }

    public void saveAll(Entity passedEntity){
        datastore.put(passedEntity);
    }
}
