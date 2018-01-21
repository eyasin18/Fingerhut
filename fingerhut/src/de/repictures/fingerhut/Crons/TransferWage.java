package de.repictures.fingerhut.Crons;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Tax;
import de.repictures.fingerhut.Datastore.Transfer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransferWage extends HttpServlet {

    private Calendar currentTime;
    private Company finanzministerium;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        finanzministerium = new Company("0098");

        Date monwedBegin, monwedEnd, thursdayBegin, thursdayEnd, fridayBegin, fridayEnd, saturdayBegin, saturdayEnd;
        try {
            DateFormat f = new SimpleDateFormat("HH:mm z");
            monwedBegin = f.parse("08:00 UTC+01:00");
            monwedEnd = f.parse("15:30 UTC+01:00");
            thursdayBegin = f.parse("08:00 UTC+01:00");
            thursdayEnd = f.parse("13:00 UTC+01:00");
            fridayBegin = f.parse("09:00 UTC+01:00");
            fridayEnd = f.parse("16:30 UTC+01:00");
            saturdayBegin = f.parse("09:30 UTC+01:00");
            saturdayEnd = f.parse("14:30 UTC+01:00");
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        currentTime = Calendar.getInstance();
        int day = currentTime.get(Calendar.DAY_OF_WEEK);
        switch (day){
            /*case 2:
                if (currentTime.after(monwedBegin) && currentTime.before(monwedEnd)){
                    checkTimes(currentTime);
                }
                break;
            case 3:
                if (currentTime.after(monwedBegin) && currentTime.before(monwedEnd)){
                    checkTimes(currentTime);
                }
                break;
            case 4:
                if (currentTime.after(monwedBegin) && currentTime.before(monwedEnd)){
                    checkTimes(currentTime);
                }
                break;
            case 5:
                if (currentTime.after(thursdayBegin) && currentTime.before(thursdayEnd)){
                    checkTimes(currentTime);
                }
                break;
            case 6:
                if (currentTime.after(fridayBegin) && currentTime.before(fridayEnd)){
                    checkTimes(currentTime);
                }
                break;
            case 7:
                if (currentTime.after(saturdayBegin) && currentTime.before(saturdayEnd)){
                    checkTimes(currentTime);
                }
                break;*/
            default:
                checkTimes();
                break;
        }
    }

    private void checkTimes(){
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Query purchaseOrderQuery = new Query("Account");
        List<Entity> accounts = datastoreService.prepare(purchaseOrderQuery).asList(FetchOptions.Builder.withDefaults());

        for(Entity account : accounts){
            Account accountGetter = new Account(account);
            List<Calendar> startTimes = accountGetter.getWorkingPeriods(false);
            List<Calendar> endTimes = accountGetter.getWorkingPeriods(true);
            boolean isCurrentTimeWholeHour = currentTime.get(Calendar.MINUTE) < 28 || currentTime.get(Calendar.MINUTE) > 57;
            for (int i = 0; i < startTimes.size(); i++){
                boolean isStartTimeWholeHour = startTimes.get(i).get(Calendar.MINUTE) < 28 || startTimes.get(i).get(Calendar.MINUTE) > 57;
                if (currentTime.get(Calendar.DAY_OF_WEEK) == startTimes.get(i).get(Calendar.DAY_OF_WEEK)
                        && endTimes.get(i).after(currentTime)
                        && startTimes.get(i).before(currentTime)
                        && (isStartTimeWholeHour && isCurrentTimeWholeHour || !isStartTimeWholeHour && !isCurrentTimeWholeHour)
                        && currentTime.get(Calendar.HOUR) > startTimes.get(i).get(Calendar.HOUR)){
                    payWage(accountGetter);
                }
            }
        }
    }

    private void payWage(Account accountGetter) {
        Company payingCompany = new Company(accountGetter.getCompany());

        double companyBalance = payingCompany.getBalanceDouble();
        double wage = accountGetter.getWage();

        if (companyHasNotEnoughMoney(companyBalance, wage)){
            //TODO: Unternehmen insolvent
        }

        double fractionalPart = wage % 1;
        int integralPart = Math.toIntExact((long) (wage - fractionalPart));
        List<Long> taxList = Tax.getWageTax();

        //Prozentsatz berechnen
        long integralPercentage = 0;
        for (int i = 0; i < integralPart; i++){
            if (i < taxList.size()) integralPercentage += taxList.get(i);
            else integralPercentage += 100;
        }
        integralPercentage = (integralPercentage/integralPart);
        long fractionPercentage = 0;
        if (integralPart < taxList.size()) fractionPercentage = taxList.get(integralPart);
        else fractionPercentage = 100;

        //Brutto in Netto und Abgabe spalten
        double tax = (((double) integralPart) * (double) (integralPercentage/100) + (fractionalPart * ((double) fractionPercentage/100)));
        double netWage = (wage - tax);

        //Geld transferieren
        Transfer.transferWage(netWage, tax, currentTime, payingCompany, accountGetter);
        companyBalance = (companyBalance-wage);
        double receiverBalance = accountGetter.getBalanceDouble() + netWage;
        double fmBalance = finanzministerium.getBalanceDouble() + tax;
        payingCompany.setBalance(companyBalance);
        accountGetter.setBalance(receiverBalance);
        finanzministerium.setBalance(fmBalance);
        payingCompany.saveAll();
        accountGetter.saveAll();
        finanzministerium.saveAll();
    }

    private boolean companyHasNotEnoughMoney(double companyBalance, double wage) {
        return (currentTime.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                || currentTime.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY
                || currentTime.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                || currentTime.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY
                || (currentTime.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && currentTime.get(Calendar.HOUR) < 12))
                && (companyBalance - wage) < -31.00

                || (companyBalance - wage) < 0.00;
    }
}