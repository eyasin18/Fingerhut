package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unchecked", "Duplicates"})
public class Tax {

    public static void setVAT(int percentage){
        List<Long> taxes = new ArrayList<>();
        taxes.add((long) percentage);
        Key vatKey = KeyFactory.createKey("id", 1);
        Entity vatEntity = new Entity("Tax", vatKey);
        vatEntity.setProperty("id", 1);
        vatEntity.setProperty("percentage", taxes);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.put(vatEntity);
    }

    public static int getVAT(){
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key vatKey = KeyFactory.createKey("id", 1);
        Query taxQuery = new Query("Tax", vatKey);
        List<Entity> vatList = datastoreService.prepare(taxQuery).asList(FetchOptions.Builder.withDefaults());
        if (vatList.size() > 0){
            List<Long> taxes = (List<Long>) vatList.get(0).getProperty("percentage");
            return taxes.get(0).intValue();
        } else {
            return 0;
        }
    }

    public static void setProfitTax(int percentage){
        List<Long> taxes = new ArrayList<>();
        taxes.add((long) percentage);
        Key vatKey = KeyFactory.createKey("id", 2);
        Entity vatEntity = new Entity("Tax", vatKey);
        vatEntity.setProperty("id", 2);
        vatEntity.setProperty("percentage", taxes);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.put(vatEntity);
    }

    public static int getProfitTax(){
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key vatKey = KeyFactory.createKey("id", 2);
        Query taxQuery = new Query("Tax", vatKey);
        List<Entity> vatList = datastoreService.prepare(taxQuery).asList(FetchOptions.Builder.withDefaults());
        if (vatList.size() > 0){
            List<Long> taxes = (List<Long>) vatList.get(0).getProperty("percentage");
            return taxes.get(0).intValue();
        } else {
            return 0;
        }
    }

    public static void setWageTax(int[] percentages){
        List<Long> taxes = new ArrayList<>();
        for (int percentage : percentages) {
            taxes.add((long) percentage);
        }
        Key vatKey = KeyFactory.createKey("id", 3);
        Entity vatEntity = new Entity("Tax", vatKey);
        vatEntity.setProperty("id", 3);
        vatEntity.setProperty("percentage", taxes);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.put(vatEntity);
    }

    public static List<Long> getWageTax(){
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key vatKey = KeyFactory.createKey("id", 3);
        Query taxQuery = new Query("Tax", vatKey);
        List<Entity> vatList = datastoreService.prepare(taxQuery).asList(FetchOptions.Builder.withDefaults());
        if (vatList.size() > 0){
            return  (List<Long>) vatList.get(0).getProperty("percentage");
        } else {
            return new ArrayList<>();
        }
    }

    public static void setPackageCustom(int percentage){
        List<Long> taxes = new ArrayList<>();
        taxes.add((long) percentage);
        Key vatKey = KeyFactory.createKey("id", 4);
        Entity vatEntity = new Entity("Tax", vatKey);
        vatEntity.setProperty("id", 4);
        vatEntity.setProperty("percentage", taxes);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.put(vatEntity);
    }

    public static int getPackageCustom(){
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key vatKey = KeyFactory.createKey("id", 4);
        Query taxQuery = new Query("Tax", vatKey);
        List<Entity> vatList = datastoreService.prepare(taxQuery).asList(FetchOptions.Builder.withDefaults());
        if (vatList.size() > 0){
            List<Long> taxes = (List<Long>) vatList.get(0).getProperty("percentage");
            return taxes.get(0).intValue();
        } else {
            return 0;
        }
    }

    public static void setMeatCustom(int percentage){
        List<Long> taxes = new ArrayList<>();
        taxes.add((long) percentage);
        Key vatKey = KeyFactory.createKey("id", 5);
        Entity vatEntity = new Entity("Tax", vatKey);
        vatEntity.setProperty("id", 5);
        vatEntity.setProperty("percentage", taxes);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.put(vatEntity);
    }

    public static int getMeatCustom(){
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key vatKey = KeyFactory.createKey("id", 5);
        Query taxQuery = new Query("Tax", vatKey);
        List<Entity> vatList = datastoreService.prepare(taxQuery).asList(FetchOptions.Builder.withDefaults());
        if (vatList.size() > 0){
            List<Long> taxes = (List<Long>) vatList.get(0).getProperty("percentage");
            return taxes.get(0).intValue();
        } else {
            return 0;
        }
    }

    public static void setBioMeatCustom(int percentage){
        List<Long> taxes = new ArrayList<>();
        taxes.add((long) percentage);
        Key vatKey = KeyFactory.createKey("id", 7);
        Entity vatEntity = new Entity("Tax", vatKey);
        vatEntity.setProperty("id", 7);
        vatEntity.setProperty("percentage", taxes);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.put(vatEntity);
    }

    public static int getBioMeatCustom(){
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key vatKey = KeyFactory.createKey("id", 7);
        Query taxQuery = new Query("Tax", vatKey);
        List<Entity> vatList = datastoreService.prepare(taxQuery).asList(FetchOptions.Builder.withDefaults());
        if (vatList.size() > 0){
            List<Long> taxes = (List<Long>) vatList.get(0).getProperty("percentage");
            return taxes.get(0).intValue();
        } else {
            return 0;
        }
    }

    public static void setBasicIncome(double basicIncome){
        Key incomeKey = KeyFactory.createKey("id", 6);
        Entity incomeEntity = new Entity("Tax", incomeKey);
        incomeEntity.setProperty("id", 6);
        incomeEntity.setProperty("amount", basicIncome);
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.put(incomeEntity);
    }

    public static Number getBasicIncome(){
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key incomeKey = KeyFactory.createKey("id", 6);
        Query taxQuery = new Query("Tax", incomeKey);
        List<Entity> vatList = datastoreService.prepare(taxQuery).asList(FetchOptions.Builder.withDefaults());
        if (vatList.size() > 0){
            Number number = (Number) vatList.get(0).getProperty("amount");
            if (number.doubleValue() >= 2){
                return number;
            } else {
                return 2;
            }
        } else {
            return 2;
        }
    }
}
