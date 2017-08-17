package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Blob;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class SaveQR extends HttpServlet {

    private Logger log = Logger.getLogger(Accounts.class.getName());
    private Cryptor cryptor = new Cryptor();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String byteSizeStr = req.getParameter("bytelength");
        String encryptedAuthCodeHex = req.getParameter("authcode");

        Accounts accountsBuilder = new Accounts(accountnumber);
        byte[] password = cryptor.hexToBytes(accountsBuilder.getHashedPassword());

        byte[] encryptedAuthCode = cryptor.hexToBytes(encryptedAuthCodeHex);
        String authCode = cryptor.decryptSymetricToString(encryptedAuthCode, password);
        log.info(authCode);
        int byteLength = Integer.parseInt(byteSizeStr);
        resp.getWriter().println("ready");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String byteSizeStr = req.getParameter("bytelength");
        String encryptedAuthCodeHex = req.getParameter("authcode");

        Accounts accountsBuilder = new Accounts(accountnumber);
        byte[] password = cryptor.hexToBytes(accountsBuilder.getHashedPassword());

        byte[] encryptedAuthCode = cryptor.hexToBytes(encryptedAuthCodeHex);
        String authCode = cryptor.decryptSymetricToString(encryptedAuthCode, password);
        log.info(authCode);
        int byteLength = Integer.parseInt(byteSizeStr);

        DataInputStream dis = new DataInputStream(req.getInputStream());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[byteLength];
        while ((nRead = dis.read(data, 0, data.length)) != -1){
            buffer.write(data, 0, nRead);
        }
        buffer.flush();

        byte[] encryptedImage = buffer.toByteArray();

        byte[] image = cryptor.decryptSymetricToByte(encryptedImage, password);

        Blob imageBlob = new Blob(image);
        accountsBuilder.setQRBlob(imageBlob);
        accountsBuilder.setAuthString(authCode);
        accountsBuilder.saveAll();

        resp.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = resp.getWriter();
        out.println("success");
        out.flush();
        out.close();
    }
}
