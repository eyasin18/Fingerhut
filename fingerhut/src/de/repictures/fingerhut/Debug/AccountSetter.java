package de.repictures.fingerhut.Debug;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Company;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

//TODO: Klasse entfernen!!
public class AccountSetter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCompanies();
    }

    private void changeOwnerName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String accountnumber = req.getParameter("accountnumber");
        String owner = req.getParameter("owner");

        if (accountnumber == null){
            resp.getWriter().println("You have to pass a account number!");
            return;
        }
        Account accountSetter = new Account(accountnumber);

        if (owner != null){
            accountSetter.setOwner(owner);
        }
        accountSetter.saveAll();
        resp.getWriter().println("success");
    }

    private void updatePrivateKey(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String accountnumber = req.getParameter("accountnumber");
        String password = req.getParameter("password");

        if (accountnumber == null){
            resp.getWriter().println("You have to pass a account number!");
            return;
        }
        if (password == null){
            resp.getWriter().println("You have to pass a password!");
            return;
        }
        Account accountSetter = new Account(accountnumber);
        Cryptor cryptor = new Cryptor();
        String newPrivateKeyStr = null;
        try {
            String privateKeyStr = accountSetter.getPrivateKeyStr();
            byte[] privateKey = cryptor.hexToBytes(privateKeyStr);
            byte[] passwordBytes = password.getBytes("ISO-8859-1");
            byte[] passwordKey = new byte[32];
            for (int i = 0; i < passwordKey.length; i++){
                passwordKey[i] = passwordBytes[i % passwordBytes.length];
            }
            byte[] newPrivateKey = cryptor.encryptSymmetricFromByte(privateKey, passwordKey);
            newPrivateKeyStr = cryptor.bytesToHex(newPrivateKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        accountSetter.setPrivateKeyStr(newPrivateKeyStr);
        accountSetter.saveAll();
        resp.getWriter().println(newPrivateKeyStr);
    }

    private void setCompanies(){
        /*Company company = new Company("0002");
        Query accountQuery = new Query("Account");
        Query.Filter buyerAccountnumberFilter = new Query.FilterPredicate("company", Query.FilterOperator.EQUAL, company.account.getKey());
        accountQuery.setFilter(buyerAccountnumberFilter);
        List<Entity> accountList = DatastoreServiceFactory.getDatastoreService().prepare(accountQuery).asList(FetchOptions.Builder.withDefaults());
        for (Entity accountEntity : accountList){
            Account account = new Account(accountEntity);
            account.deleteCompany();
            account.saveAll();
        }*/
        String[] accountnumbers = new String[]{"0580", "0001", "0004", "0003"};
        for (int i = 0; i < accountnumbers.length; i++){
            Account account = new Account(accountnumbers[i]);
            account.setCompany("0002");
            account.saveAll();
        }
    }
}
