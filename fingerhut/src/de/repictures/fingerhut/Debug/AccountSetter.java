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
        updatePrivateKey(req, resp);
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
            byte[] newPrivateKey = cryptor.encryptSymetricFromByte(privateKey, passwordKey);
            newPrivateKeyStr = cryptor.bytesToHex(newPrivateKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        accountSetter.setPrivateKeyStr(newPrivateKeyStr);
        accountSetter.saveAll();

        resp.getWriter().println(newPrivateKeyStr);
    }
}
