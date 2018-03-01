package de.repictures.fingerhut.Admin;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.DeferredTask;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Transfer;

import static com.sun.activation.registries.LogSupport.log;

public class DefferedPayment implements DeferredTask {

    private final String senderAccountnumber;
    private final String receiverAccountnumber;
    private final double amount;
    private boolean isBasicIncome;

    public DefferedPayment(String senderAccountnumber, String receiverAccountnumber, double amount, boolean isBasicIncome){
        this.senderAccountnumber = senderAccountnumber;
        this.receiverAccountnumber = receiverAccountnumber;
        this.amount = amount;
        this.isBasicIncome = isBasicIncome;
    }

    @Override
    public void run() {
        Account receiverAccount = new Account(receiverAccountnumber);
        Company payingCompany = new Company(senderAccountnumber);
        double payingCompanyBalance = payingCompany.getBalanceDouble();
        double userBalance = receiverAccount.getBalanceDouble();
        userBalance += amount;
        payingCompanyBalance -= amount;
        payingCompany.setBalance(payingCompanyBalance);
        receiverAccount.setBalance(userBalance);
        Entity savedTransfer = Transfer.transferWage(amount, 0, isBasicIncome, payingCompany, receiverAccount);
        receiverAccount.addTransfer(savedTransfer);
        payingCompany.addTransfer(savedTransfer);
        if (isBasicIncome) receiverAccount.setGotBasicIncome(true);
        payingCompany.saveAll();
        receiverAccount.saveAll();
    }
}