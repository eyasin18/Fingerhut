package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Blob;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Account;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.IOException;

public class PostQR extends HttpServlet{

    private Cryptor cryptor = new Cryptor();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        Account accountBuilder = new Account(accountnumber);
        Blob qrBlob = accountBuilder.getQRBlob();
        byte[] qrData = qrBlob.getBytes();
        resp.getWriter().println(qrData.length);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String requestingAccountnumber = req.getParameter("reqaccountnumber");
        Account accountBuilder = new Account(accountnumber);
        String requestingAccountKey = accountBuilder.getHashedPassword(accountBuilder.getAccount(requestingAccountnumber));

        DataOutputStream response = new DataOutputStream(resp.getOutputStream());
        byte[] key = cryptor.hexToBytes(requestingAccountKey);

        Blob qrBlob = accountBuilder.getQRBlob();
        byte[] qrData = qrBlob.getBytes();

        qrData = cryptor.encryptSymmetricFromByte(qrData, key);
        response.write(qrData);
        response.flush();
        response.close();
    }
}