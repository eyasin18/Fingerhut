package de.repictures.fingerhut.Backend;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class CompletePurchaseOrder extends HttpServlet {

    private Logger log = Logger.getLogger(CompletePurchaseOrder.class.getName());

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
    }

    private String getStringBodyName(BodyPart bodyPart) throws MessagingException {
        String[] header = bodyPart.getHeader("Content-Disposition");
        String[] contents = header[0].split("=");
        return contents[1].substring(1, contents[1].length()-1);
    }
}
