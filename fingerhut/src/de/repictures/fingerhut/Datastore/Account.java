package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

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
        account.setProperty("transferarray", new ArrayList<String>());
        setCompany(account, "0002");
        setFeature(account,0, true);
        setPrivateKeyStr(account, encryptedPrivateKeyStr);
        setPublicKeyStr(account, publicKeyStr);
        setAuthString(account, accountnumber);
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

    @Deprecated
    public void setOwner(String owner){
        String encryptedPasswordHex = (String) account.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedPasswordHex);
        byte[] encryptedByteName = cryptor.encryptSymmetricFromString(owner, encryptedPassword);
        String encryptedOwner = cryptor.bytesToHex(encryptedByteName);
        account.setProperty("owner", encryptedOwner);
    }

    @Deprecated
    public void setOwner(Entity accountEntity, String owner){
        String encryptedPasswordHex = (String) accountEntity.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedPasswordHex);
        byte[] encryptedByteName = cryptor.encryptSymmetricFromString(owner, encryptedPassword);
        String encryptedOwner = cryptor.bytesToHex(encryptedByteName);
        accountEntity.setProperty("owner", encryptedOwner);
    }

    @Deprecated
    public void setOwner(String owner, String encryptedPasswordHex){
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedPasswordHex);
        byte[] encryptedByteName = cryptor.encryptSymmetricFromString(owner, encryptedPassword);
        String encryptedOwner = cryptor.bytesToHex(encryptedByteName);
        account.setProperty("owner", encryptedOwner);
    }

    @Deprecated
    public String getOwner(){
        String encryptedNameStr = (String) account.getProperty("owner");
        byte[] encryptedName = cryptor.hexToBytes(encryptedNameStr);
        String encryptedHexPasswordStr = (String) account.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedHexPasswordStr);
        return cryptor.decryptSymmetricToString(encryptedName, encryptedPassword);
    }

    @Deprecated
    public String getOwner(Entity passedEntity){
        String encryptedNameStr = (String) passedEntity.getProperty("owner");
        byte[] encryptedName = cryptor.hexToBytes(encryptedNameStr);
        String encryptedHexPasswordStr = (String) passedEntity.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encryptedHexPasswordStr);
        return cryptor.decryptSymmetricToString(encryptedName, encryptedPassword);
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

    public String getSaltedPassword(String salt){
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
     * 0 = Produkt hinzufügen
     * 1 = Authentifizierungs QR-Codes lesen und schreiben
     * 2 = Kaufaufträge
     * 3 = Mitarbeiter verwalten
     * 4 = Statistiken
     */

    public void setFeature(Entity passedEntity, long featureNumber, boolean add){
        ArrayList<Long> featureList = new ArrayList<>();
        if (passedEntity.getProperty("feature_list") != null)
            featureList = (ArrayList<Long>) passedEntity.getProperty("feature_list");
        if (!featureList.contains(featureNumber)) {
            if (add) featureList.add(featureNumber);
            else featureList.remove(featureNumber);
            passedEntity.setProperty("feature_list", featureList);
        }
    }

    public void setFeature(long featureNumber, boolean add){
        ArrayList<Long> featureList = new ArrayList<>();
        if (account.getProperty("feature_list") != null)
            featureList = (ArrayList<Long>) account.getProperty("feature_list");
        if (!featureList.contains(featureNumber)) {
            if (add) featureList.add(featureNumber);
            else featureList.remove(featureNumber);
            account.setProperty("feature_list", featureList);
        }
    }

    public void setFeatures(ArrayList<Long> features){
        account.setProperty("feature_list", features);
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

    public void deleteCompany(){
        account.setProperty("company", null);
    }

    public void setCompany(Entity passedEntity, String companyNumber){
        Company company = new Company(companyNumber);
        passedEntity.setProperty("company", company.account.getKey());
    }

    public void setCompany(String companyNumber){
        Company company = new Company(companyNumber);
        account.setProperty("company", company.account.getKey());
    }

    public Entity getCompany(){
        try {
            if (account.getProperty("company") == null) return null;
            else return datastore.get((Key) account.getProperty("company"));
        } catch (EntityNotFoundException e) {
            log.warning(e.toString());
            return null;
        }
    }

    public Entity getCompany(Entity passedEntity){
        try {
            if (passedEntity.getProperty("company") == null) return null;
            else return datastore.get((Key) passedEntity.getProperty("company"));
        } catch (EntityNotFoundException e) {
            log.warning(e.toString());
            return null;
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

    public void setWage(double wage){
        account.setProperty("wage", wage);
    }

    public void setWage(Entity passedEntity, double wage){
        passedEntity.setProperty("wage", wage);
    }

    public double getWage(){
        Number wageNr = (Number) account.getProperty("wage");
        if (wageNr != null) return wageNr.doubleValue();
        else return 1.0;
    }

    public double getWage(Entity passedEntity){
        Number wageNr = (Number) passedEntity.getProperty("wage");
        if (wageNr != null) return wageNr.doubleValue();
        else return 1.0;
    }

    public void setWorkPeriods(List<Integer> startTimes, List<Integer> endTimes){
        account.setProperty("work_start_times", startTimes);
        account.setProperty("work_end_times", endTimes);
    }

    public void setWorkPeriods(Entity passedEntity, List<Integer> startTimes, List<Integer> endTimes){
        passedEntity.setProperty("work_start_times", startTimes);
        passedEntity.setProperty("work_end_times", endTimes);
    }

    public List<Number> getWorkPeriod(boolean isEndTime){
        List<Number> times = new ArrayList<>();
        if (isEndTime && account.getProperty("work_end_times") != null){
            times = (List<Number>) account.getProperty("work_end_times");
        } else if (account.getProperty("work_start_times") != null){
            times = (List<Number>) account.getProperty("work_start_times");
        }
        return times;
    }

    public List<Number> getWorkPeriod(Entity passedEntity, boolean isEndTime){
        List<Number> times = new ArrayList<>();
        if (isEndTime && passedEntity.getProperty("work_end_times") != null){
            times = (List<Number>) passedEntity.getProperty("work_end_times");
        } else if (passedEntity.getProperty("work_start_times") != null){
            times = (List<Number>) passedEntity.getProperty("work_start_times");
        }
        return times;
    }

    @Deprecated
    public void setWorkingPeriods(List<Calendar> startTimes, List<Calendar> endTimes){
        List<String> startTimesStr = new ArrayList<>();
        List<String> endTimesStr = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("EEE HH:mm Z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        for (int i = 0; i < startTimes.size(); i++){
            startTimesStr.add(format.format(startTimes.get(i).getTime()));
            endTimesStr.add(format.format(endTimes.get(i).getTime()));
        }
        account.setProperty("working_start_times", startTimesStr);
        account.setProperty("working_end_times", endTimesStr);
    }

    @Deprecated
    public void setWorkingPeriods(Entity passedEntity, List<Calendar> startTimes, List<Calendar> endTimes){
        List<String> startTimesStr = new ArrayList<>();
        List<String> endTimesStr = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("EEE HH:mm Z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        for (int i = 0; i < startTimes.size(); i++){
            startTimesStr.add(format.format(startTimes.get(i).getTime()));
            endTimesStr.add(format.format(endTimes.get(i).getTime()));
        }
        passedEntity.setProperty("working_start_times", startTimesStr);
        passedEntity.setProperty("working_end_times", endTimesStr);
    }

    @Deprecated
    public List<Calendar> getWorkingPeriods(boolean isEndPeriod){
        try {
            DateFormat format = new SimpleDateFormat("EEE HH:mm Z", Locale.US);
            List<String> workingPeriodsStr = new ArrayList<>();
            List<Calendar> workingPeriods = new ArrayList<>();
            if (isEndPeriod && account.getProperty("working_end_times") != null)
                workingPeriodsStr = (List<String>) account.getProperty("working_end_times");
            else if (!isEndPeriod && account.getProperty("working_start_times") != null)
                workingPeriodsStr = (List<String>) account.getProperty("working_start_times");

            for (int i = 0; i < workingPeriodsStr.size(); i++) {
                Calendar calendar = new GregorianCalendar(Locale.GERMANY);
                calendar.setTime(format.parse(workingPeriodsStr.get(i)));
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                log.info("Time: " + calendar.getTime().toString());
                workingPeriods.set(i, calendar);
            }
            return workingPeriods;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public List<Calendar> getWorkingPeriods(Entity passedEntity, boolean isEndPeriod){
        try {
            DateFormat format = new SimpleDateFormat("EEE HH:mm Z", Locale.US);
            List<String> workingPeriodsStr = new ArrayList<>();
            List<Calendar> workingPeriods = new ArrayList<>();
            if (isEndPeriod && passedEntity.getProperty("working_end_times") != null)
                workingPeriodsStr = (List<String>) passedEntity.getProperty("working_end_times");
            else if (!isEndPeriod && passedEntity.getProperty("working_start_times") != null)
                workingPeriodsStr = (List<String>) passedEntity.getProperty("working_start_times");

            for (int i = 0; i < workingPeriodsStr.size(); i++) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(format.parse(workingPeriodsStr.get(i)));
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                log.info("Time: " + calendar.getTime().toString());
                workingPeriods.set(i, calendar);
            }
            return workingPeriods;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public List<String> getWorkingPeriodsStr(boolean isEndPeriod){
        List<String> workingPeriodsStr = new ArrayList<>();
        if (isEndPeriod && account.getProperty("working_end_times") != null)
            workingPeriodsStr = (List<String>) account.getProperty("working_end_times");
        else if (!isEndPeriod && account.getProperty("working_start_times") != null)
            workingPeriodsStr = (List<String>) account.getProperty("working_start_times");

        return workingPeriodsStr;
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
}
