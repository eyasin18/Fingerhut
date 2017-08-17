package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Blob;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Accounts;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
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

    private Logger log = Logger.getLogger(SaveQR.class.getName());
    private Cryptor cryptor = new Cryptor();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String accountnumber = null;
        String userAccountnumber = null;
        String byteSizeStr = null;
        String encryptedAuthCodeHex = null;
        DataInputStream dataInputStream = null;
        byte[] encryptedImage = null;
        try {
            ByteArrayDataSource dataSource = new ByteArrayDataSource(req.getInputStream(), "multipart/form-data");
            MimeMultipart multipart = new MimeMultipart(dataSource);
            int count = multipart.getCount();
            log.info("Multipart content count: " + count);
            for (int i = 0; i < count; i++){
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")){
                    log.info("bodypart no. " + i + ": " + String.valueOf(bodyPart.getContent()));
                    log.info(getStringBodyName(bodyPart));
                    switch (getStringBodyName(bodyPart)){
                        case "accountnumber":
                            accountnumber = String.valueOf(bodyPart.getContent());
                            break;
                        case "userAccountnumber":
                            userAccountnumber = String.valueOf(bodyPart.getContent());
                            break;
                        case "bytelength":
                            byteSizeStr = String.valueOf(bodyPart.getContent());
                            break;
                        case "authcode":
                            encryptedAuthCodeHex = String.valueOf(bodyPart.getContent());
                            break;
                    }
                } else if (bodyPart.isMimeType("application/octet-stream")){
                    log.info("application/octet-stream " + bodyPart.getDisposition());
                    dataInputStream = new DataInputStream(bodyPart.getInputStream());
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        if (accountnumber == null || byteSizeStr == null || encryptedAuthCodeHex == null || dataInputStream == null || userAccountnumber == null){
            log.warning("Infos are not parsed correctly");
            resp.getWriter().println("failed");
            return;
        }

        Accounts accountsBuilder = new Accounts(accountnumber);
        Accounts userAccountsBuilder = new Accounts(userAccountnumber);
        log.info("Account hashedPassword: " + userAccountsBuilder.getHashedPassword());
        byte[] password = cryptor.hexToBytes(userAccountsBuilder.getHashedPassword());
        log.info("Password Length: " + password.length);
        byte[] encryptedAuthCode = cryptor.hexToBytes(encryptedAuthCodeHex);
        log.info("EncryptedAuthCode Length: " + encryptedAuthCode.length);
        String authCode = cryptor.decryptSymetricToString(encryptedAuthCode, password);
        log.info("Decrypted Authcode: " + authCode);


        int byteLength = Integer.parseInt(byteSizeStr);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[byteLength];
        while ((nRead = dataInputStream.read(data, 0, data.length)) != -1){
            buffer.write(data, 0, nRead);
        }
        buffer.flush();

        encryptedImage = data;
        log.info("Encrypted Image length: " + encryptedImage.length);
        byte[] image = cryptor.decryptSymetricToByte(encryptedImage, password);

        log.info("Image size: " + image.length + " bytes");
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

    private String getStringBodyName(BodyPart bodyPart) throws MessagingException {
        String[] header = bodyPart.getHeader("Content-Disposition");
        String[] contents = header[0].split("=");
        return contents[1].substring(1, contents[1].length()-1);
    }
}
