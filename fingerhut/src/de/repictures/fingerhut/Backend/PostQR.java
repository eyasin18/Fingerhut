package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Blob;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Accounts;

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
        Accounts accountsBuilder = new Accounts(accountnumber);
        Blob qrBlob = accountsBuilder.getQRBlob();
        byte[] qrData = qrBlob.getBytes();
        resp.getWriter().println(qrData.length);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String requestingAccountnumber = req.getParameter("reqaccountnumber");
        Accounts accountsBuilder = new Accounts(accountnumber);
        String requestingAccountKey = accountsBuilder.getHashedPassword(accountsBuilder.getAccount(requestingAccountnumber));

        DataOutputStream response = new DataOutputStream(resp.getOutputStream());
        byte[] key = cryptor.hexToBytes(requestingAccountKey);

        Blob qrBlob = accountsBuilder.getQRBlob();
        byte[] qrData = qrBlob.getBytes();

        qrData = cryptor.encryptSymetricFromByte(qrData, key);
        response.write(qrData);
        response.flush();
        response.close();
    }
}