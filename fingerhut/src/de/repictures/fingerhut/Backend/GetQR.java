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

public class GetQR extends HttpServlet{

    private byte[] qrData;
    private Accounts accountsBuilder;
    private Cryptor cryptor = new Cryptor();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        accountsBuilder = new Accounts(accountnumber);
        Blob qrBlob = accountsBuilder.getQRBlob();
        qrData = qrBlob.getBytes();
        resp.getWriter().println(qrData.length);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataOutputStream response = new DataOutputStream(resp.getOutputStream());
        byte[] key = cryptor.hexToBytes(accountsBuilder.getPassword());
        qrData = cryptor.encryptSymetricFromByte(qrData, key);
        response.write(qrData);
        response.flush();
        response.close();
    }
}
