package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.JsonArray;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Tax;
import de.repictures.fingerhut.Datastore.Transfer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class TransferWage extends HttpServlet {

    private int currentTime;
    private Company finanzministerium;
    private int z = 0;
    private int y = 0;
    private Queue queue = QueueFactory.getDefaultQueue();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        finanzministerium = new Company("0098");

        int mondayBegin, mondayEnd, tuesdayBegin, tuesdayEnd, wednesdayBegin, wednesdayEnd, thursdayBegin, thursdayEnd, fridayBegin, fridayEnd, saturdayBegin, saturdayEnd;
        mondayBegin = Account.getMinutesFromValues(0, 8, 0); //8:00 Uhr
        mondayEnd = Account.getMinutesFromValues(0, 15, 30); //15:30 Uhr
        tuesdayBegin = Account.getMinutesFromValues(1, 8, 0); //8:00 Uhr
        tuesdayEnd = Account.getMinutesFromValues(1, 15, 30); //15:30
        wednesdayBegin = Account.getMinutesFromValues(2, 8, 0); //08:00
        wednesdayEnd = Account.getMinutesFromValues(2, 15, 30); //15:30
        thursdayBegin = Account.getMinutesFromValues(3, 8, 0); //08:00
        thursdayEnd = Account.getMinutesFromValues(3, 13, 0); //13:00
        fridayBegin = Account.getMinutesFromValues(4, 9, 0); //09:00
        fridayEnd = Account.getMinutesFromValues(4, 16, 30); //16:30
        saturdayBegin = Account.getMinutesFromValues(5, 9, 30); //09:30
        saturdayEnd = Account.getMinutesFromValues(5, 14, 30); //14:30

        Calendar currentTime = Calendar.getInstance();
        int day = currentTime.get(Calendar.DAY_OF_WEEK) - 2;
        int hours = currentTime.get(Calendar.HOUR_OF_DAY) + 1;
        int minutes = ((int) currentTime.get(Calendar.MINUTE)/30)*30;
        this.currentTime = Account.getMinutesFromValues(day, hours, minutes);
        log(this.currentTime + "\n" + day + " " + hours + " " + minutes);
        switch (day){
            case 0:
                if (this.currentTime > mondayBegin && this.currentTime < mondayEnd+1){
                    checkTimes();
                }
                break;
            case 1:
                if (this.currentTime > tuesdayBegin && this.currentTime < tuesdayEnd+1){
                    checkTimes();
                }
                break;
            case 2:
                if (this.currentTime > wednesdayBegin && this.currentTime < wednesdayEnd+1){
                    checkTimes();
                }
                break;
            case 3:
                if (this.currentTime > thursdayEnd && this.currentTime < thursdayBegin+1){
                    checkTimes();
                }
                break;
            case 4:
                if (this.currentTime > fridayBegin && this.currentTime < fridayEnd+1){
                    checkTimes();
                }
                break;
            case 5:
                if (this.currentTime > saturdayBegin && this.currentTime < saturdayEnd+1){
                    checkTimes();
                }
                break;
            default:
                break;
        }
    }

    private void checkTimes() throws IOException {
        z = Tax.getWageStart().intValue();
        loopPart();
    }

    private void loopPart() throws IOException {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Query purchaseOrderQuery = new Query("Account");
        List<Entity> accounts = datastoreService.prepare(purchaseOrderQuery).asList(FetchOptions.Builder.withDefaults());
        while(z < accounts.size()) {
            Account accountGetter = new Account(accounts.get(z));
            if (accountGetter.account == null) continue;
            if (accountGetter.getCompanies().size() < 1) continue;
            List<Entity> companyEntities = accountGetter.getCompanies();
            for (Entity companyEntity : companyEntities) {
                Company company = new Company(companyEntity);
                JsonArray startTimes = accountGetter.getSpecificWorkPeriod(false, company.getAccountnumber());
                JsonArray endTimes = accountGetter.getSpecificWorkPeriod(true, company.getAccountnumber());
                boolean isCurrentTimeWholeHour = Account.getMinutesOfHourFromMinutes(currentTime) == 0;
                for (int i = 0; i < startTimes.size(); i++) {
                    boolean isStartTimeWholeHour = Account.getMinutesOfHourFromMinutes(startTimes.get(i).getAsInt()) == 0;
                    if ((isStartTimeWholeHour && isCurrentTimeWholeHour || !isStartTimeWholeHour && !isCurrentTimeWholeHour)
                            && Account.getDaysFromMinutes(currentTime) == Account.getDaysFromMinutes(startTimes.get(i).getAsInt())
                            && endTimes.get(i).getAsInt() > currentTime
                            && startTimes.get(i).getAsInt() < currentTime
                            && startTimes.get(i).getAsInt() != endTimes.get(i).getAsInt()
                            || endTimes.get(i).getAsInt() == currentTime && startTimes.get(i).getAsInt() != endTimes.get(i).getAsInt()) {
                        payWage(accountGetter, company);
                        break;
                    }
                }
            }
            z++;
        }
        Tax.setWageStart(0);
    }

    private void payWage(Account accountGetter, Company payingCompany) throws IOException {

        double companyBalance = payingCompany.getBalanceDouble();
        double wage = accountGetter.getSpecificWage(payingCompany.getAccountnumber());

        /*if (companyHasNotEnoughMoney(companyBalance, wage)){
            log("Company " + payingCompany.getAccountnumber() + " has no money");
            payingCompany.setInsolvent(true);
            return;
        }*/

        double fractionalPart = wage % 1;
        double integralPart = (double) (wage - fractionalPart);
        List<Long> taxList = Tax.getWageTax();

        //Prozentsatz berechnen
        double integralPercentage = 0;
        for (int i = 0; i < integralPart; i++){
            if (i < taxList.size()) integralPercentage += taxList.get(i);
            else integralPercentage += 100;
        }
        integralPercentage = (integralPercentage/integralPart);
        double fractionPercentage = 0;
        if (integralPart < taxList.size()) fractionPercentage = taxList.get((int) integralPart);
        else fractionPercentage = 100;

        //Brutto in Netto und Abgabe spalten
        double tax = (((double) integralPart) * (double) (integralPercentage/100) + (fractionalPart * ((double) fractionPercentage/100)));
        double netWage = (wage - tax);

        queue.add(TaskOptions.Builder.withPayload(new DefferedPayment(payingCompany.getAccountnumber(), accountGetter.getAccountnumber(), netWage, false)));
        queue.add(TaskOptions.Builder.withPayload(new DefferedPayment(payingCompany.getAccountnumber(), finanzministerium.getAccountnumber(), tax, false)));

        /*//Geld transferieren
        Transfer.transferWage(netWage, tax, false, payingCompany, accountGetter);
        companyBalance = (companyBalance-wage);
        double receiverBalance = accountGetter.getBalanceDouble() + netWage;
        double fmBalance = finanzministerium.getBalanceDouble() + tax;
        payingCompany.setBalance(companyBalance);
        accountGetter.setBalance(receiverBalance);
        finanzministerium.setBalance(fmBalance);
        payingCompany.saveAllAsync();
        accountGetter.saveAllAsync();
        finanzministerium.saveAllAsync();
        log("\nTax: " + tax + "\nNet wage: " + netWage + "\nFinanzministerium: " + finanzministerium.getBalanceDouble() + "\nKonto: " + accountGetter.getBalanceDouble());*/
    }

    private boolean companyHasNotEnoughMoney(double companyBalance, double wage) {
        return currentTime < Account.getMinutesFromValues(4, 12, 0)
                && (companyBalance - wage) < -31.00

                || (companyBalance - wage) < 0.00;
    }
}