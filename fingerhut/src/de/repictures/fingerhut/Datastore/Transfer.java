package de.repictures.fingerhut.Datastore;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class Transfer {

    private Entity transfer;
    private DatastoreService datastore;
    private Cryptor cryptor;
    private SimpleDateFormat f;
    private Calendar calendar = Calendar.getInstance();
    private Logger log = Logger.getLogger(Transfer.class.getName());

    public Transfer(Locale locale){
        f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", locale);
        datastore = DatastoreServiceFactory.getDatastoreService();
        cryptor = new Cryptor();
    }

    public Transfer(Entity transfer, Locale locale){
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

    public void setSenderPurpose(Text purpose){
        transfer.setProperty("senderPurpose", purpose);
    }

    public void setSenderPurpose(Entity passedEntity, Text purpose){
        passedEntity.setProperty("senderPurpose", purpose);
    }

    public Text getSenderPurpose(){
        return (Text) transfer.getProperty("senderPurpose");
    }

    public Text getSenderPurpose(Entity passedEntity){
        return (Text) passedEntity.getProperty("senderPurpose");
    }

    public void setReceiverPurpose(Text purpose){
        transfer.setProperty("receiverPurpose", purpose);
    }

    public void setReceiverPurpose(Entity passedEntity, Text purpose){
        passedEntity.setProperty("receiverPurpose", purpose);
    }

    public Text getReceiverPurpose(){
        return (Text) transfer.getProperty("receiverPurpose");
    }

    public Text getReceiverPurpose(Entity passedEntity){
        return (Text) passedEntity.getProperty("receiverPurpose");
    }

    public void setSenderAesKey(String keyStr){
        transfer.setProperty("senderAesKey", keyStr);
    }

    public void setSenderAesKey(Entity passedEntity, String keyStr){
        passedEntity.setProperty("senderAesKey", keyStr);
    }

    public String getSenderAesKey(){
        return (String) transfer.getProperty("senderAesKey");
    }

    public String getSenderAesKey(Entity passedEntity){
        return (String) passedEntity.getProperty("senderAesKey");
    }

    public void setReceiverAesKey(String keyStr){
        transfer.setProperty("receiverAesKey", keyStr);
    }

    public void setReceiverAesKey(Entity passedEntity, String keyStr){
        passedEntity.setProperty("receiverAesKey", keyStr);
    }

    public String getReceiverAesKey(){
        return (String) transfer.getProperty("receiverAesKey");
    }

    public String getReceiverAesKey(Entity passedEntity){
        return (String) passedEntity.getProperty("receiverAesKey");
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