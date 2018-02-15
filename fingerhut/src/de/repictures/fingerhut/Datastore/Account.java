package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.repictures.fingerhut.Cryptor;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static com.sun.activation.registries.LogSupport.log;

@SuppressWarnings({"unchecked", "Duplicates"})
public class Account {

    public Entity account;
    private DatastoreService datastore;
    private Cryptor cryptor;
    private Logger log = Logger.getLogger(Account.class.getName());

    public Account(){
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
    }

    public Account(Entity account){
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
        this.account = account;
    }

    public Account(String accountnumber){
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
        this.account = getAccount(accountnumber);
    }

    public Entity createAccount(String accountnumber){
        Key key = KeyFactory.createKey("accountnumber", accountnumber);
        return new Entity("Account", key);
    }

    public void postAccount(String accountnumber, String password){
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
            byte[] encryptedPrivateKey = cryptor.encryptSymmetricFromByte(privateKey, passwordKey);
            encryptedPrivateKeyStr = cryptor.bytesToHex(encryptedPrivateKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Entity account = new Entity("Account", loginKey);

        setAccountnumber(account, accountnumber);
        setHashedPassword(account, encryptedPassword);
        setBalance(account, 1500.00f);
        setIsPrepaid(account, false);
        account.setProperty("transferarray", new ArrayList<String>());
        setPrivateKeyStr(account, encryptedPrivateKeyStr);
        setPublicKeyStr(account, publicKeyStr);
        setAuthString(account, accountnumber);
        saveAll(account);
    }

    public JsonObject postPrepaidAccount(){
        Random rand = new Random();
        String password = String.format("%04d", rand.nextInt(10000));
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
            byte[] encryptedPrivateKey = cryptor.encryptSymmetricFromByte(privateKey, passwordKey);
            encryptedPrivateKeyStr = cryptor.bytesToHex(encryptedPrivateKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String accountnumber;
        List<Entity> expiredPrepaidAccounts = getExpiredPrepaidAccounts();
        Entity account;
        if (expiredPrepaidAccounts.size() > 0){
            account = expiredPrepaidAccounts.get(0);
            accountnumber = getAccountnumber(account);
        } else {
            accountnumber = getUnusedAccountnumber();
            if (accountnumber == null){
                return null;
            }
            Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
            account = new Entity("Account", loginKey);
        }

        setAccountnumber(account, accountnumber);
        setHashedPassword(account, encryptedPassword);
        setBalance(account, 0f);
        setIsPrepaid(account, true);
        account.setProperty("transferarray", new ArrayList<String>());
        setPrivateKeyStr(account, encryptedPrivateKeyStr);
        setPublicKeyStr(account, publicKeyStr);
        setAuthString(account, accountnumber);
        saveAll(account);

        JsonObject accountInformation = new JsonObject();
        accountInformation.addProperty("accountnumber", accountnumber);
        accountInformation.addProperty("pin", password);
        return accountInformation;
    }

    public static String getUnusedAccountnumber(){
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        DecimalFormat decimalFormat = new DecimalFormat("0000");

        Query loginQuery = new Query("Account");
        Query companyQuery = new Query("Company");
        List<Entity> accountsList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        accountsList.addAll(datastore.prepare(companyQuery).asList(FetchOptions.Builder.withDefaults()));

        List<String> accountnumbers = new ArrayList<>();
        for (Entity account : accountsList){
            accountnumbers.add((String) account.getProperty("accountnumber"));
        }

        for (int i = 0; i < 9999; i++) {
            String accountnumber = decimalFormat.format(i);
            if (!accountnumbers.contains(accountnumber)){
                return accountnumber;
            }
        }
        return null;
    }

    public static List<Entity> getExpiredPrepaidAccounts(){
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query accountQuery = new Query("Account");
        Query.Filter isPrepaidFilter = new Query.FilterPredicate("is_prepaid", Query.FilterOperator.EQUAL, true);
        Query.Filter isExpiredFilter = new Query.FilterPredicate("expire_date", Query.FilterOperator.LESS_THAN_OR_EQUAL, getCurrentMinutes());
        Query.CompositeFilter compositeFilter = Query.CompositeFilterOperator.and(isPrepaidFilter, isExpiredFilter);
        accountQuery.setFilter(compositeFilter);
        return datastore.prepare(accountQuery).asList(FetchOptions.Builder.withDefaults());
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

    public void setBalance(double balance){
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

    public double getBalanceDouble(){
        return (double) account.getProperty("balance");
    }

    public double getBalanceDouble(Entity passedEntity){
        return (double) passedEntity.getProperty("balance");
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

    public String getSaltedPassword(String salt){
        String password = (String) account.getProperty("password");
        String combinedString = password + salt;
        log.info(combinedString);
        return cryptor.hashToString(combinedString);
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

    public void setTransfers(List<String> transfersList){
        account.setProperty("transferarray", transfersList);
    }

    public void setTransfers(Entity passedEntity, List<String> transfersList){
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
     * 0 = Produkte verwalten
     * 1 = Authentifizierungs QR-Codes lesen und schreiben
     * 2 = Kaufaufträge
     * 3 = Mitarbeiter verwalten
     * 4 = Statistiken
     * 5 = Geld wechseln
     * 6 = Mitarbeiter hinzufügen
     * 7 = Prepaidkonto hinzufügen
     */

    public void setFeatures(ArrayList<Long> features, String companynumber){
        JsonObject jsonObject;
        if (account.getProperty("feature_json") != null) {
            String jsonString = (String) account.getProperty("feature_json");
            jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        } else {
            jsonObject = new JsonObject();
        }
        JsonArray jsonArray = new Gson().toJsonTree(features).getAsJsonArray();
        jsonObject.add(companynumber, jsonArray);
        account.setProperty("feature_json", jsonObject.toString());
    }

    public void setFeatures(Entity passedEntity, ArrayList<Long> features, String companynumber){
        JsonObject jsonObject;
        if (passedEntity.getProperty("feature_json") != null) {
            String jsonString = (String) passedEntity.getProperty("feature_json");
            jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        } else {
            jsonObject = new JsonObject();
        }
        JsonArray jsonArray = new Gson().toJsonTree(features).getAsJsonArray();
        jsonObject.add(companynumber, jsonArray);
        passedEntity.setProperty("feature_json", jsonObject.toString());
    }

    public ArrayList<Long> getSpecificFeatures(String companynumber){
        if (account.getProperty("feature_json") != null){
            String jsonString = (String) account.getProperty("feature_json");
            JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
            ArrayList<Long> features = new ArrayList<>();
            JsonArray jsonArray = jsonObject.getAsJsonArray(companynumber);
            for (int i = 0; i < jsonArray.size(); i++) {
                features.add(jsonArray.get(i).getAsLong());
            }
            return features;
        } else return new ArrayList<>();
    }

    public ArrayList<Long> getSpecificFeatures(Entity passedEntity, String companynumber){
        if (passedEntity.getProperty("feature_json") != null){
            String jsonString = (String) passedEntity.getProperty("feature_json");
            JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
            ArrayList<Long> features = new ArrayList<>();
            JsonArray jsonArray = jsonObject.getAsJsonArray(companynumber);
            for (int i = 0; i < jsonArray.size(); i++) {
                features.add(jsonArray.get(i).getAsLong());
            }
            return features;
        } else return new ArrayList<>();
    }

    public String getFeaturesString(){
        return (String) account.getProperty("feature_json");
    }

    public String getFeaturesString(Entity passedEntity){
        return (String) passedEntity.getProperty("feature_json");
    }

    public void addCompany(Entity passedEntity, String companyNumber){
        Company company = new Company(companyNumber);
        List<Key> companyKeys = new ArrayList<>();
        if (passedEntity.getProperty("companies") != null){
            companyKeys = (List<Key>) passedEntity.getProperty("companies");
        }
        if (companyKeys.contains(company.account.getKey())) return;
        companyKeys.add(company.account.getKey());
        passedEntity.setProperty("companies", companyKeys);
    }

    public void addCompany(String companyNumber){
        Company company = new Company(companyNumber);
        List<Key> companyKeys = new ArrayList<>();
        if (account.getProperty("companies") != null){
            companyKeys = (List<Key>) account.getProperty("companies");
        }
        if (companyKeys.contains(company.account.getKey())) return;
        companyKeys.add(company.account.getKey());
        account.setProperty("companies", companyKeys);
    }

    public void removeCompany(Entity passedEntity, String companyNumber){
        Company company = new Company(companyNumber);
        List<Key> companyKeys = new ArrayList<>();
        if (passedEntity.getProperty("companies") != null){
            companyKeys = (List<Key>) passedEntity.getProperty("companies");
        }
        if (!companyKeys.contains(company.account.getKey())) return;
        companyKeys.remove(company.account.getKey());
        passedEntity.setProperty("companies", companyKeys);
    }

    public void removeCompany(String companyNumber){
        Company company = new Company(companyNumber);
        List<Key> companyKeys = new ArrayList<>();
        if (account.getProperty("companies") != null){
            companyKeys = (List<Key>) account.getProperty("companies");
        }
        if (!companyKeys.contains(company.account.getKey())) return;
        companyKeys.remove(company.account.getKey());
        account.setProperty("companies", companyKeys);
    }

    public List<Entity> getCompanies(){
            if (account.getProperty("companies") == null) return new ArrayList<>();
            else {
                List<Key> companyKeys = (List<Key>) account.getProperty("companies");
                List<Entity> companies = new ArrayList<>();
                for (Key companyKey : companyKeys){
                    try{
                        companies.add(datastore.get(companyKey));
                    } catch (EntityNotFoundException e) {
                        log.warning(e.getMessage());
                    }
                }
                return companies;
            }
    }

    public List<Entity> getCompanies(Entity passedEntity){
        if (passedEntity.getProperty("companies") == null) return new ArrayList<>();
        else {
            List<Key> companyKeys = (List<Key>) passedEntity.getProperty("companies");
            List<Entity> companies = new ArrayList<>();
            for (Key companyKey : companyKeys){
                try{
                    companies.add(datastore.get(companyKey));
                } catch (EntityNotFoundException e) {
                    log.warning(e.getMessage());
                }
            }
            return companies;
        }
    }

    public boolean containsCompany(Key key) {
        if (account.getProperty("companies") == null) return false;
        else {
            List<Key> companyKeys = (List<Key>) account.getProperty("companies");
            return companyKeys.contains(key);
        }
    }

    public String createAuthString(){
        SecureRandom sr = new SecureRandom();
        return new BigInteger(130, sr).toString(32);
    }

    public void setAuthString(){
        String authString = cryptor.generateRandomString(16);
        account.setProperty("authString", authString);
    }

    public void setAuthString(String authString){
        account.setProperty("authString", authString);
    }

    public void setAuthString(String authString, Entity passedEntity){
        passedEntity.setProperty("authString", authString);
    }

    public void setAuthString(Entity passedEntity){
        String authString = cryptor.generateRandomString(16);
        passedEntity.setProperty("authString", authString);
    }

    public void setAuthString(Entity passedEntity, String accountnumber){
        String authString = cryptor.generateRandomString(16);
        passedEntity.setProperty("authString", accountnumber + authString);
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
        return ((Text) account.getProperty("privateKey")).getValue();
    }

    public String getPrivateKeyStr(Entity passedEntity){
        return ((Text) passedEntity.getProperty("privateKey")).getValue();
    }

    public void setPrivateKeyStr(String privateKeyStr){
        account.setProperty("privateKey", new Text(privateKeyStr));
    }

    public void setPrivateKeyStr(Entity passedEntity, String privateKeyStr){
        passedEntity.setProperty("privateKey", new Text(privateKeyStr));
    }

    public String getPublicKeyStr(){
        return ((Text) account.getProperty("publicKey")).getValue();
    }

    public String getPublicKeyStr(Entity passedEntity){
        return ((Text) passedEntity.getProperty("publicKey")).getValue();
    }

    public void setPublicKeyStr(String publicKeyStr){
        account.setProperty("publicKey", new Text(publicKeyStr));
    }

    public void setPublicKeyStr(Entity passedEntity, String publicKeyStr){
        passedEntity.setProperty("publicKey", new Text(publicKeyStr));
    }

    public String[] getPublicKeyValues(){
        String keyStr = ((Text) account.getProperty("publicKey")).getValue();
        PublicKey publicKey = cryptor.stringToPublicKey(keyStr);
        return cryptor.publicKeyToModulusExponentHex(publicKey);
    }

    public String[] getPublicKeyValues(Entity passedEntity){
        String keyStr = ((Text) passedEntity.getProperty("publicKey")).getValue();
        PublicKey publicKey = cryptor.stringToPublicKey(keyStr);
        return cryptor.publicKeyToModulusExponentHex(publicKey);
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

    public void setRandomWebString(String randomWebString){
        account.setProperty("randomWebString", randomWebString);
    }

    public void setRandomWebString(Entity passedEntity, String randomWebString){
        account.setProperty("randomWebString", randomWebString);
    }

    public void updateRandomWebString(){
        String randomWebString = cryptor.generateRandomAlphaNummericString(32);
        account.setProperty("randomWebString", randomWebString);
    }

    public void updateRandomWebString(Entity passedEntity){
        String randomWebString = cryptor.generateRandomAlphaNummericString(32);
        passedEntity.setProperty("randomWebString", randomWebString);
    }

    public String getRandomWebString(){
        return (String) account.getProperty("randomWebString");
    }

    public String getRandomWebString(Entity passedEntity){
        return (String) passedEntity.getProperty("randomWebString");
    }

    public void setLoginAttempts(long attempts){
        account.setProperty("login_attempts", attempts);
    }

    public void setLoginAttempts(Entity passedEntity, long attempts){
        passedEntity.setProperty("login_attempts", attempts);
    }

    public long countUpLoginAttempts(){
        Number attemptsNr = (Number) account.getProperty("login_attempts");
        long attempts = 0;
        if (attemptsNr != null) attempts = attemptsNr.longValue();
        attempts++;
        account.setProperty("login_attempts", attempts);
        saveAll();
        return attempts;
    }

    public long countUpLoginAttempts(Entity passedEntity){
        Number attemptsNr = (Number) passedEntity.getProperty("login_attempts");
        long attempts = 0;
        if (attemptsNr != null) attempts = attemptsNr.longValue();
        attempts++;
        passedEntity.setProperty("login_attempts", attempts);
        saveAll(passedEntity);
        return attempts;
    }

    public long getLoginAttempts(){
        Number attemptsNr = (Number) account.getProperty("login_attempts");
        if (attemptsNr == null) return 0;
        else return attemptsNr.longValue();
    }

    public long getLoginAttempts(Entity passedEntity){
        Number attemptsNr = (Number) passedEntity.getProperty("login_attempts");
        if (attemptsNr == null) return 0;
        else return attemptsNr.longValue();
    }

    public void setCooldownTime(Date cooldownTime){
        account.setProperty("login_cooldown", cooldownTime);
    }

    public void setCooldownTime(Entity passedEntity, Date cooldownTime){
        passedEntity.setProperty("login_cooldown", cooldownTime);
    }

    public Calendar getCooldownTime(){
        Date date = (Date) account.getProperty("login_cooldown");
        if (date == null) return null;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    public Calendar getCooldownTime(Entity passedEntity){
        Date date = (Date) passedEntity.getProperty("login_cooldown");
        if (date == null) return null;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }

    public void setSpecificWage(double wage, String companynumber){
        JsonObject companyObject;
        if (account.getProperty("wages_json") != null){
            JsonParser parser = new JsonParser();
            companyObject = parser.parse((String) account.getProperty("wages_json")).getAsJsonObject();
        } else {
            companyObject = new JsonObject();
        }
        companyObject.addProperty(companynumber, wage);
        account.setProperty("wages_json", companyObject.toString());
    }

    public void setSpecificWage(Entity passedEntity, double wage, String companynumber){
        JsonObject companyObject;
        if (passedEntity.getProperty("wages_json") != null){
            JsonParser parser = new JsonParser();
            companyObject = parser.parse((String) passedEntity.getProperty("wages_json")).getAsJsonObject();
        } else {
            companyObject = new JsonObject();
        }
        companyObject.addProperty(companynumber, wage);
        passedEntity.setProperty("wages_json", companyObject.toString());
    }

    public double getSpecificWage(String companynumber){
        if (account.getProperty("wages_json") != null){
            JsonParser parser = new JsonParser();
            JsonObject companyObject = parser.parse((String) account.getProperty("wages_json")).getAsJsonObject();
            if (companyObject.get(companynumber) != null)
                return companyObject.get(companynumber).getAsDouble();
            else
                return 1.0;
        } else {
            return 1.0;
        }
    }

    public double getSpecificWage(Entity passedEntity, String companynumber){
        if (passedEntity.getProperty("wages_json") != null){
            JsonParser parser = new JsonParser();
            JsonObject companyObject = parser.parse((String) passedEntity.getProperty("wages_json")).getAsJsonObject();
            return companyObject.get(companynumber).getAsDouble();
        } else {
            return 1.0;
        }
    }

    public JsonObject getWages(){
        if (account.getProperty("wages_json") != null)
            return new JsonParser().parse((String) account.getProperty("wages_json")).getAsJsonObject();
        else return new JsonObject();
    }

    public JsonObject getWages(Entity passedEntity){
        if (passedEntity.getProperty("wages_json") != null)
            return new JsonParser().parse((String) passedEntity.getProperty("wages_json")).getAsJsonObject();
        else return new JsonObject();
    }

    public void setSpecificWorkPeriods(JsonArray startTimes, JsonArray endTimes, String companynumber){
        JsonObject companyJson;
        if (account.getProperty("work_period_json") != null) {
            JsonParser parser = new JsonParser();
            companyJson = parser.parse((String) account.getProperty("work_period_json")).getAsJsonObject();
        } else {
            companyJson = new JsonObject();
        }
        JsonObject workPeriodJson = new JsonObject();
        workPeriodJson.add("start", startTimes);
        workPeriodJson.add("end", endTimes);
        companyJson.add(companynumber, workPeriodJson);
        account.setProperty("work_period_json", companyJson.toString());
    }

    public void setSpecificWorkPeriods(Entity passedEntity, JsonArray startTimes, JsonArray endTimes, String companynumber){
        JsonObject companyJson;
        if (passedEntity.getProperty("work_period_json") != null) {
            JsonParser parser = new JsonParser();
            companyJson = parser.parse((String) passedEntity.getProperty("work_period_json")).getAsJsonObject();
        } else {
            companyJson = new JsonObject();
        }
        JsonObject workPeriodJson = new JsonObject();
        workPeriodJson.add("start", startTimes);
        workPeriodJson.add("end", endTimes);
        companyJson.add(companynumber, workPeriodJson);
        passedEntity.setProperty("work_period_json", companyJson.toString());
    }

    public JsonArray getSpecificWorkPeriod(boolean isEndTime, String companynumber){
        JsonObject companyJson;
        if (account.getProperty("work_period_json") != null) {
            JsonParser parser = new JsonParser();
            companyJson = parser.parse((String) account.getProperty("work_period_json")).getAsJsonObject();
        } else {
            companyJson = new JsonObject();
        }
        JsonObject workPeriodJson = companyJson.getAsJsonObject(companynumber);
        if (workPeriodJson == null) return new JsonArray();
        if (isEndTime) return workPeriodJson.getAsJsonArray("end");
        else return workPeriodJson.getAsJsonArray("start");
    }

    public JsonArray getSpecificWorkPeriod(Entity passedEntity, boolean isEndTime, String companynumber){
        JsonObject companyJson;
        if (passedEntity.getProperty("work_period_json") != null) {
            JsonParser parser = new JsonParser();
            companyJson = parser.parse((String) passedEntity.getProperty("work_period_json")).getAsJsonObject();
        } else {
            companyJson = new JsonObject();
        }
        JsonObject workPeriodJson = companyJson.getAsJsonObject(companynumber);
        if (workPeriodJson == null) return new JsonArray();
        if (isEndTime) return workPeriodJson.getAsJsonArray("end");
        else return workPeriodJson.getAsJsonArray("start");
    }

    public JsonObject getWorkPeriods(){
        if (account.getProperty("work_period_json") != null) {
            JsonParser parser = new JsonParser();
            return parser.parse((String) account.getProperty("work_period_json")).getAsJsonObject();
        } else {
            return new JsonObject();
        }
    }

    public JsonObject getWorkPeriods(Entity passedEntity){
        if (passedEntity.getProperty("work_period_json") != null) {
            JsonParser parser = new JsonParser();
            return parser.parse((String) passedEntity.getProperty("work_period_json")).getAsJsonObject();
        } else {
            return new JsonObject();
        }
    }

    public void setIsPrepaid(boolean isPrepaid){
        account.setProperty("is_prepaid", isPrepaid);
    }

    public void setIsPrepaid(Entity passedEntity, boolean isPrepaid){
        passedEntity.setProperty("is_prepaid", isPrepaid);
    }

    public boolean getIsPrepaid() {
        return account.getProperty("is_prepaid") != null && (boolean) account.getProperty("is_prepaid");
    }

    public boolean getIsPrepaid(Entity passedEntity){
        return passedEntity.getProperty("is_prepaid") != null && (boolean) passedEntity.getProperty("is_prepaid");
    }

    public void setExpireDate(Number expireDate){
        account.setProperty("expire_date", expireDate);
    }

    public void setExpireDate(Entity passedEntity, Number expireDate){
        passedEntity.setProperty("expire_date", expireDate);
    }

    public Number getExpireDate(){
        if (account.getProperty("expire_date") != null){
            return (Number) account.getProperty("expire_date");
        } else {
            return getMinutesFromValues(6, 23, 59);
        }
    }

    public Number getExpireDate(Entity passedEntity){
        if (passedEntity.getProperty("expire_date") != null){
            return (Number) passedEntity.getProperty("expire_date");
        } else {
            return getMinutesFromValues(6, 23, 59);
        }
    }

    public void saveAll(){
        datastore.put(account);
    }

    public void saveAll(Entity passedEntity){
        datastore.put(passedEntity);
    }

    public static int getDaysFromMinutes(int minutes){
        return minutes/1440;
    }

    public static int getHoursFromMinutes(int minutes){
        int minutesOfDay = minutes%1440;
        return minutesOfDay/60;
    }

    public static int getMinutesOfHourFromMinutes(int minutes){
        int minutesOfDay = minutes%1440;
        return minutesOfDay%60;
    }

    public static int getMinutesFromValues(int days, int hours, int minutes){
        return days*1440 + hours*60 + minutes;
    }

    public static long getDaysFromMinutes(long minutes){
        return minutes/1440;
    }

    public static long getHoursFromMinutes(long minutes){
        long minutesOfDay = minutes%1440;
        return minutesOfDay/60;
    }

    public static long getMinutesOfHourFromMinutes(long minutes){
        long minutesOfDay = minutes%1440;
        return minutesOfDay%60;
    }

    public static long getMinutesFromValues(long days, long hours, long minutes){
        return days*1440 + hours*60 + minutes;
    }

    public static long getCurrentMinutes(){
        Calendar currentTime = Calendar.getInstance();
        int days = currentTime.get(Calendar.DAY_OF_WEEK) - 2;
        int hours = currentTime.get(Calendar.HOUR_OF_DAY) + 1;
        int minutes = ((int) currentTime.get(Calendar.MINUTE)/30)*30;
        return days*1440 + hours*60 + minutes;
    }
}