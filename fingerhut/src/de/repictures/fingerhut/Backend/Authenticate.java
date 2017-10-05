package de.repictures.fingerhut.Backend;

import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.logging.Logger;

//Test
public class Authenticate extends HttpServlet{

    String serverTimeStamp = "";
    private Cryptor cryptor = new Cryptor();
    private Logger log = Logger.getLogger(Accounts.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");

        Accounts accountsBuilder = new Accounts(accountnumber);
        String authCode = accountsBuilder.getAuthString();
        int accountnumberlength = accountnumber.length();
        String[] authParts = {authCode.substring(accountnumberlength, accountnumberlength+8), authCode.substring(accountnumberlength+8, accountnumberlength+16)};
        String output = URLEncoder.encode(authParts[1] + serverTimeStamp, "UTF-8");
        resp.getWriter().println(output);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String authPart = req.getParameter("authPart");

        Accounts accountsBuilder = new Accounts(accountnumber);

        String authCode = accountsBuilder.getAuthString();
        int accountnumberlength = accountnumber.length();
        String[] authParts = {authCode.substring(accountnumberlength, accountnumberlength+8), authCode.substring(accountnumberlength+8, accountnumberlength+16)};

        if (accountsBuilder.account != null && Objects.equals(authParts[0], authPart)){
            byte[] password = cryptor.hexToBytes(accountsBuilder.getHashedPassword());
            String privateKey = accountsBuilder.getPrivateKeyStr();
            byte[] encryptedPrivateKey = cryptor.encryptSymetricFromString(privateKey, password);
            String encryptedPrivateKeyHex = cryptor.bytesToHex(encryptedPrivateKey);
            resp.getWriter().println(encryptedPrivateKeyHex);
        } else {
            log.warning("Falsche Authentifizierung!");
            resp.getWriter().println("0");
        }
    }
}