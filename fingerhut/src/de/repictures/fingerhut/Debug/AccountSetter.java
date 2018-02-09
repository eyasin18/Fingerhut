package de.repictures.fingerhut.Debug;

import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

//TODO: Klasse entfernen!!
public class AccountSetter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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
        String[] accountnumbers = new String[]{"0024", "0593", "0620", "0022"};
        for (int i = 0; i < accountnumbers.length; i++){
            Account account = new Account(accountnumbers[i]);
            account.addCompany("0155");
            account.saveAll();
        }
    }
}
