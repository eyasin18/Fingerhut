package de.repictures.fingerhut.Web;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Transfer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShowTransferTools {

    private Account account;
    private HttpServletResponse response;
    private String[] weekdays = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
    private DecimalFormat timePattern = new DecimalFormat("00");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.GERMANY);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("EE HH:mm", Locale.GERMANY);
    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public ShowTransferTools(String accountnumber, HttpServletResponse response){
        this.account = new Account(accountnumber);
        this.response = response;
    }

    public List<String[]> getTransfers(){
        List<String[]> transferData = new ArrayList<>();
        List<String> transferKeyStrs = account.getTransfers();
        for (String transferKeyStr : transferKeyStrs){
            //Lese die Entität aus der Verlinkung
            Entity transferEntity = account.getEntity(transferKeyStr);
            //Wenn es keine Überweisungsdaten zu der Verlinkung gibt, dann springe zur nächsten Verlinkung
            if (transferEntity == null){
                continue;
            }
            Transfer transferGetter = new Transfer(transferEntity, response.getLocale());
            //Lese den Auftraggeber aus der Überweisung
            Entity sender = transferGetter.getSender(transferEntity);
            //Lese den Empfänger aus der Überweisung
            Entity receiver = transferGetter.getReceiver(transferEntity);
            //Wenn Sender oder Empfänger nicht (mehr) existieren: springe zur nächsten Verlinkung
            if (sender == null || receiver == null){
                continue;
            }
            String[] transferStr = new String[3];
            Calendar dateTime = Calendar.getInstance();
            try {
                dateTime.setTime(sdf.parse(transferGetter.getDateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            transferStr[0] = timeFormat.format(dateTime.getTime()) + " Uhr";
            if (Objects.equals(account.getAccountnumber(), account.getAccountnumber(sender))){
                transferStr[1] = "-" + decimalFormat.format(transferGetter.getAmount());
                transferStr[2] = account.getAccountnumber(receiver);
            } else {
                transferStr[1] = "+" + decimalFormat.format(transferGetter.getAmount());
                transferStr[2] = account.getAccountnumber(sender);
            }
            transferData.add(transferStr);
        }
        return transferData;
    }
}
