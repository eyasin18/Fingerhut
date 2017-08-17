package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class Transfers {

    private Entity transfer;
    private DatastoreService datastore;
    private Cryptor cryptor;
    private SimpleDateFormat f;
    private Calendar calendar = Calendar.getInstance();
    private Logger log = Logger.getLogger(Transfers.class.getName());

    public Transfers(Locale locale){
        f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", locale);
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
    }

    public Transfers(Entity transfer, Locale locale){
        f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", locale);
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
        this.transfer = transfer;
    }

    public Entity getTransfer(){
        Key transferKey = KeyFactory.createKey("dateandtime", f.format(calendar.getTime()));
        Query loginQuery = new Query("Transfer", transferKey);
        List<Entity> entityList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        if(entityList.size() > 0){
            return entityList.get(0);
        } else {
            return null;
        }
    }

    public Entity getTransfer(String datetime){
        Key transferKey = KeyFactory.createKey("dateandtime", datetime);
        Query loginQuery = new Query("Transfer", transferKey);
        List<Entity> entityList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        if(entityList.size() > 0){
            return entityList.get(0);
        } else {
            return null;
        }
    }

    public Entity createTransaction(){
        Key transferKey = KeyFactory.createKey("dateandtime", f.format(calendar.getTime()));
        return new Entity("Transfer", transferKey);
    }

    public Entity createTransaction(String datetime){
        Key transferKey = KeyFactory.createKey("dateandtime", datetime);
        return new Entity("Transfer", transferKey);
    }

    public void setAmount(float amount){
        transfer.setProperty("amount", amount);
    }

    public void setAmount(Entity passedEntity, float amount){
        passedEntity.setProperty("amount", amount);
    }

    public double getAmount(){
        return (double) transfer.getProperty("amount");
    }

    public double getAmount(Entity passedEntity){
        return (double) passedEntity.getProperty("amount");
    }

    public void setDateTime(){
        transfer.setProperty("datetime", f.format(calendar.getTime()));
    }

    public void setDateTime(String dateTime){
        transfer.setProperty("datetime", dateTime);
    }

    public void setDateTime(Entity passedEntity, String dateTime){
        passedEntity.setProperty("datetime", dateTime);
    }

    public String getDateTime(){
        return (String) transfer.getProperty("datetime");
    }

    public String getDateTime(Entity passedEntity){
        return (String) passedEntity.getProperty("datetime");
    }

    public void setSenderPurpose(Entity sender, String purpose){
        String password = (String) sender.getProperty("password");
        byte[] encryptedBytePurpose = cryptor.encryptSymetricFromString(purpose, cryptor.hexToBytes(password));
        transfer.setProperty("purpose", cryptor.bytesToHex(encryptedBytePurpose));
    }

    public void setSenderPurpose(Entity passedEntity, Entity sender ,String purpose){
        String password = (String) sender.getProperty("password");
        byte[] encryptedBytePurpose = cryptor.encryptSymetricFromString(purpose, cryptor.hexToBytes(password));
        passedEntity.setProperty("purpose", cryptor.bytesToHex(encryptedBytePurpose));
    }

    public String getSenderPurpose(Entity sender){
        String encyptedPasswordStr = (String) sender.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encyptedPasswordStr);
        String encryptedPurposeStr = (String) transfer.getProperty("purpose");
        byte[] encryptedPurpose = cryptor.hexToBytes(encryptedPurposeStr);
        return cryptor.decryptSymetricToString(encryptedPurpose, encryptedPassword);
    }

    public String getSenderPurpose(Entity passedEntity, Entity sender){
        String encyptedPasswordStr = (String) sender.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encyptedPasswordStr);
        String encryptedPurposeStr = (String) passedEntity.getProperty("purpose");
        byte[] encryptedPurpose = cryptor.hexToBytes(encryptedPurposeStr);
        return cryptor.decryptSymetricToString(encryptedPurpose, encryptedPassword);
    }

    public void setReceiverPurpose(Entity sender, String purpose){
        String password = (String) sender.getProperty("password");
        byte[] encryptedBytePurpose = cryptor.encryptSymetricFromString(purpose, cryptor.hexToBytes(password));
        transfer.setProperty("purpose", cryptor.bytesToHex(encryptedBytePurpose));
    }

    public void setReceiverPurpose(Entity passedEntity, Entity sender ,String purpose){
        String password = (String) sender.getProperty("password");
        byte[] encryptedBytePurpose = cryptor.encryptSymetricFromString(purpose, cryptor.hexToBytes(password));
        passedEntity.setProperty("purpose", cryptor.bytesToHex(encryptedBytePurpose));
    }

    public String getReceiverPurpose(Entity sender){
        String encyptedPasswordStr = (String) sender.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encyptedPasswordStr);
        String encryptedPurposeStr = (String) transfer.getProperty("purpose");
        byte[] encryptedPurpose = cryptor.hexToBytes(encryptedPurposeStr);
        return cryptor.decryptSymetricToString(encryptedPurpose, encryptedPassword);
    }

    public String getReceiverPurpose(Entity passedEntity, Entity sender){
        String encyptedPasswordStr = (String) sender.getProperty("password");
        byte[] encryptedPassword = cryptor.hexToBytes(encyptedPasswordStr);
        String encryptedPurposeStr = (String) passedEntity.getProperty("purpose");
        byte[] encryptedPurpose = cryptor.hexToBytes(encryptedPurposeStr);
        return cryptor.decryptSymetricToString(encryptedPurpose, encryptedPassword);
    }

    public void setReceiver(Entity receiver){
        transfer.setProperty("receiver", receiver.getKey());
    }

    public void setReceiver(Entity passedEntity, Entity receiver){
        passedEntity.setProperty("receiver", receiver.getKey());
    }

    public Entity getReceiver(){
        try {
            return datastore.get((Key) transfer.getProperty("receiver"));
        } catch (EntityNotFoundException e) {
            log.warning(e.toString());
            return null;
        }
    }

    public Entity getReceiver(Entity passedEntity){
        try {
            return datastore.get((Key) passedEntity.getProperty("receiver"));
        } catch (EntityNotFoundException e) {
            log.warning(e.toString());
            return null;
        }
    }

    public void setSender(Entity sender){
        transfer.setProperty("sender", sender.getKey());
    }

    public void setSender(Entity passedEntity, Entity sender){
        passedEntity.setProperty("sender", sender.getKey());
    }

    public Entity getSender(){
        try {
            return datastore.get((Key) transfer.getProperty("sender"));
        } catch (EntityNotFoundException e) {
            log.warning(e.toString());
            return null;
        }
    }

    public Entity getSender(Entity passedEntity){
        try {
            return datastore.get((Key) passedEntity.getProperty("sender"));
        } catch (EntityNotFoundException e) {
            log.warning(e.toString());
            return null;
        }
    }

    public void setType(String type){
        transfer.setProperty("type", type);
    }

    public void setType(Entity passedEntity, String type){
        passedEntity.setProperty("type", type);
    }

    public String getType(){
        return (String) transfer.getProperty("type");
    }

    public String getType(Entity passedEntity){
        return (String) passedEntity.getProperty("type");
    }

    public void saveAll(){
        datastore.put(transfer);
    }

    public void saveAll(Entity passedEntity){
        datastore.put(passedEntity);
    }
}
