package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
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
        try {
            DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            Key vatKey = KeyFactory.createKey("id", 1);
            Entity vatEntity = datastoreService.get(vatKey);
            List<Long> taxes = (List<Long>) vatEntity.getProperty("percentage");
            return Math.toIntExact(taxes.get(0));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
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
        try {
            DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            Key vatKey = KeyFactory.createKey("id", 2);
            Entity vatEntity = datastoreService.get(vatKey);
            List<Long> taxes = (List<Long>) vatEntity.getProperty("percentage");
            return taxes.get(0).intValue();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return 100;
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
        try {
            DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            Key vatKey = KeyFactory.createKey("id", 3);
            Entity vatEntity = datastoreService.get(vatKey);
            return (List<Long>) vatEntity.getProperty("percentage");
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
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
        try {
            DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            Key vatKey = KeyFactory.createKey("id", 4);
            Entity vatEntity = datastoreService.get(vatKey);
            List<Long> taxes = (List<Long>) vatEntity.getProperty("percentage");
            return taxes.get(0).intValue();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
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
        try {
            DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
            Key vatKey = KeyFactory.createKey("id", 5);
            Entity vatEntity = datastoreService.get(vatKey);
            List<Long> taxes = (List<Long>) vatEntity.getProperty("percentage");
            return taxes.get(0).intValue();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
