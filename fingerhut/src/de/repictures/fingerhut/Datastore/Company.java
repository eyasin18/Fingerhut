package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"unchecked", "Duplicates"})
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
            byte[] encryptedPrivateKey = cryptor.encryptSymmetricFromByte(privateKey, passwordKey);
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

    public void setBalance(String balanceStr){
        float balance = Float.parseFloat(balanceStr);
        account.setProperty("balance", balance);

        List<Number> balanceRecordList = new ArrayList<>();
        if (account.getProperty("balance_records") != null){
            balanceRecordList = (List<Number>) account.getProperty("balance_records");
        }
        balanceRecordList.add(balance);
        account.setProperty("balance_records", balanceRecordList);

        Calendar currentTimeCal = Calendar.getInstance();
        int day = currentTimeCal.get(Calendar.DAY_OF_WEEK) - 2;
        int hours = currentTimeCal.get(Calendar.HOUR_OF_DAY) + 1;
        int minutes = currentTimeCal.get(Calendar.MINUTE);
        List<Number> datesList = new ArrayList<>();
        if (account.getProperty("balance_record_dates") != null){
            datesList = (List<Number>) account.getProperty("balance_record_dates");
        }
        datesList.add(Account.getMinutesFromValues(day, hours, minutes));
        account.setProperty("balance_record_dates", datesList);
    }

    public void setBalance(float balance){
        account.setProperty("balance", balance);

        List<Number> balanceRecordList = new ArrayList<>();
        if (account.getProperty("balance_records") != null){
            balanceRecordList = (List<Number>) account.getProperty("balance_records");
        }
        balanceRecordList.add(balance);
        account.setProperty("balance_records", balanceRecordList);

        Calendar currentTimeCal = Calendar.getInstance();
        int day = currentTimeCal.get(Calendar.DAY_OF_WEEK) - 2;
        int hours = currentTimeCal.get(Calendar.HOUR_OF_DAY) + 1;
        int minutes = currentTimeCal.get(Calendar.MINUTE);
        List<Number> datesList = new ArrayList<>();
        if (account.getProperty("balance_record_dates") != null){
            datesList = (List<Number>) account.getProperty("balance_record_dates");
        }
        datesList.add(Account.getMinutesFromValues(day, hours, minutes));
        account.setProperty("balance_record_dates", datesList);
    }

    public void setBalance(double balance){
        account.setProperty("balance", balance);

        List<Number> balanceRecordList = new ArrayList<>();
        if (account.getProperty("balance_records") != null){
            balanceRecordList = (List<Number>) account.getProperty("balance_records");
        }
        balanceRecordList.add(balance);
        account.setProperty("balance_records", balanceRecordList);

        Calendar currentTimeCal = Calendar.getInstance();
        int day = currentTimeCal.get(Calendar.DAY_OF_WEEK) - 2;
        int hours = currentTimeCal.get(Calendar.HOUR_OF_DAY) + 1;
        int minutes = currentTimeCal.get(Calendar.MINUTE);
        List<Number> datesList = new ArrayList<>();
        if (account.getProperty("balance_record_dates") != null){
            datesList = (List<Number>) account.getProperty("balance_record_dates");
        }
        datesList.add(Account.getMinutesFromValues(day, hours, minutes));
        account.setProperty("balance_record_dates", datesList);
    }

    public void setBalance(Entity passedEntity, String balanceStr){
        float balance = Float.parseFloat(balanceStr);

        passedEntity.setProperty("balance", balance);
        List<Number> balanceRecordList = new ArrayList<>();
        if (passedEntity.getProperty("balance_records") != null){
            balanceRecordList = (List<Number>) passedEntity.getProperty("balance_records");
        }
        balanceRecordList.add(balance);
        passedEntity.setProperty("balance_records", balanceRecordList);

        Calendar currentTimeCal = Calendar.getInstance();
        int day = currentTimeCal.get(Calendar.DAY_OF_WEEK) - 2;
        int hours = currentTimeCal.get(Calendar.HOUR_OF_DAY) + 1;
        int minutes = currentTimeCal.get(Calendar.MINUTE);
        List<Number> datesList = new ArrayList<>();
        if (passedEntity.getProperty("balance_record_dates") != null){
            datesList = (List<Number>) passedEntity.getProperty("balance_record_dates");
        }
        datesList.add(Account.getMinutesFromValues(day, hours, minutes));
        passedEntity.setProperty("balance_record_dates", datesList);
    }

    public void setBalance(Entity passedEntity, float balance){
        passedEntity.setProperty("balance", balance);

        List<Number> balanceRecordList = new ArrayList<>();
        if (passedEntity.getProperty("balance_records") != null){
            balanceRecordList = (List<Number>) passedEntity.getProperty("balance_records");
        }
        balanceRecordList.add(balance);
        passedEntity.setProperty("balance_records", balanceRecordList);

        Calendar currentTimeCal = Calendar.getInstance();
        int day = currentTimeCal.get(Calendar.DAY_OF_WEEK) - 2;
        int hours = currentTimeCal.get(Calendar.HOUR_OF_DAY) + 1;
        int minutes = currentTimeCal.get(Calendar.MINUTE);
        List<Number> datesList = new ArrayList<>();
        if (passedEntity.getProperty("balance_record_dates") != null){
            datesList = (List<Number>) passedEntity.getProperty("balance_record_dates");
        }
        datesList.add(Account.getMinutesFromValues(day, hours, minutes));
        passedEntity.setProperty("balance_record_dates", datesList);
    }

    public List<Number> getBalanceDevelopment(){
        List<Number> balancesList = new ArrayList<>();
        if (account.getProperty("balance_records") != null){
            balancesList = (List<Number>) account.getProperty("balance_records");
        }
        return balancesList;
    }

    public List<Number> getBalanceDevelopment(Entity passedEntity){
        List<Number> balancesList = new ArrayList<>();
        if (passedEntity.getProperty("balance_records") != null){
            balancesList = (List<Number>) passedEntity.getProperty("balance_records");
        }
        return balancesList;
    }

    public List<Number> getBalanceDevelopmentTimes(){
        List<Number> balancesDTimesList = new ArrayList<>();
        if (account.getProperty("balance_record_dates") != null){
            balancesDTimesList = (List<Number>) account.getProperty("balance_record_dates");
        }
        return balancesDTimesList;
    }

    public List<Number> getBalanceDevelopmentTimes(Entity passedEntity){
        List<Number> balancesDTimesList = new ArrayList<>();
        if (passedEntity.getProperty("balance_record_dates") != null){
            balancesDTimesList = (List<Number>) passedEntity.getProperty("balance_record_dates");
        }
        return balancesDTimesList;
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

    public List<Entity> getSellingProductEntities(Entity passedEntity){
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

    public List<Entity> getSellingProductEntities(){
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

    public Product[] getSellingProducts(){
        try {
            if (company.getProperty("products") != null) {
                ArrayList<String> productsStr = (ArrayList<String>) company.getProperty("products");
                Product[] products = new Product[productsStr.size()];
                for (int i = 0; i < productsStr.size(); i++) {
                    Entity productEntity = datastore.get(KeyFactory.stringToKey(productsStr.get(i)));
                    products[i] = new Product(productEntity);
                }
                return products;
            }
            return new Product[0];
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return new Product[0];
        }
    }

    public Product[] getSellingProducts(Entity passedEntity){
        try {
            if (passedEntity.getProperty("products") != null) {
                ArrayList<String> productsStr = (ArrayList<String>) passedEntity.getProperty("products");
                Product[] products = new Product[productsStr.size()];
                for (int i = 0; i < productsStr.size(); i++) {
                    Entity productEntity = datastore.get(KeyFactory.stringToKey(productsStr.get(i)));
                    products[i] = new Product(productEntity);
                }
                return products;
            }
            return new Product[0];
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return new Product[0];
        }
    }

    public void setEmployeeList(List<Entity> employeeList){
        account.setProperty("employees", employeeList);
    }

    public void setEmployeeList(Entity passedEntity, List<Entity> employeeList){
        passedEntity.setProperty("employees", employeeList);
    }

    public List<Entity> getEmployeeList(){
        List<Entity> employeeList = (List<Entity>) account.getProperty("employees");
        if (employeeList == null) return new ArrayList<>();
        else return employeeList;
    }

    public List<Entity> getEmployeeList(Entity passedEntity){
        List<Entity> employeeList = (List<Entity>) passedEntity.getProperty("employees");
        if (employeeList == null) return new ArrayList<>();
        else return employeeList;
    }

    /**
        0 = Privatunternehmen
        1 = Zentralbank
        2 = Ministerium
        3 = Parlament
        4 = Polizei
        5 = Agrarwirtschaft
        6 = Bau
        7 = Chemie & Rohstoffe
        8 = Dienstleistungen & Handwerk
        9 = E-Commerce & Versandhandel
        10 = Energie & Umwelt
        11 = Finanzen, Versicherungen & Immobilien
        12 = Freizeit
        13 = Gesellschaft
        14 = Handel
        15 = Internet
        16 = Konsum & Fast Moving Consumer Goods
        17 = Länder
        18 = Leben
        19 = Medien & Marketing
        20 = Metall & Elektronik
        21 = Pharma & Gesundheit
        22 = Technik & Telekommunikation
        23 = Tourismus & Gastronomie
        24 = Verkehr & Logistik
        25 = Verwaltung und Verteidigung
        26 = Wirtschaft & Politik
     */

    public void setSector(long sector){
        if (sector < 0 || sector > 26) return;
        account.setProperty("sector", sector);
    }

    public void setSector(Entity passedEntity, long sector){
        if (sector < 0 || sector > 26) return;
        passedEntity.setProperty("sector", sector);
    }

    public long getSector(){
        Number sectorNr = (Number) account.getProperty("sector");
        if (sectorNr != null) return sectorNr.longValue();
        else return 0;
    }

    public long getSector(Entity passedEntity){
        Number sectorNr = (Number) passedEntity.getProperty("sector");
        if (sectorNr != null) return sectorNr.longValue();
        else return 0;
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
