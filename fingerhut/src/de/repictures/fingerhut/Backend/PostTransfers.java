package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.*;
import de.repictures.fingerhut.Datastore.Accounts;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Transfers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class PostTransfers extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");

        Company companyGetter = new Company();
        Accounts accountGetter = new Accounts();
        Entity account = accountGetter.getAccount(accountnumber);

        if (account != null){
            StringBuilder output = new StringBuilder();
            List<String> transferList = accountGetter.getTransfers(account);

            if (transferList != null && transferList.size() > 0){
                for (String transferKeyStr : transferList){
                    Entity transfer = accountGetter.getEntity(transferKeyStr);
                    if (transfer == null) continue;
                    Transfers transfersGetter = new Transfers(resp.getLocale());
                    output.append(transfersGetter.getDateTime(transfer));
                    Entity sender = transfersGetter.getSender(transfer);
                    Entity receiver = transfersGetter.getReceiver(transfer);
                    String senderAccountnumber = sender.getProperty("accountnumber").toString();
                    char plusminus = '+';
                    if (Objects.equals(senderAccountnumber, accountnumber)){
                        plusminus = '-';
                        output.append("ò");
                        try {
                            output.append(accountGetter.getOwner(receiver));
                        } catch (StringIndexOutOfBoundsException e){
                            output.append(companyGetter.getOwner(receiver));
                        }
                        output.append("ò");
                        output.append(accountGetter.getAccountnumber(receiver));
                    } else {
                        output.append("ò");
                        try {
                            output.append(accountGetter.getOwner(sender));
                        } catch (StringIndexOutOfBoundsException e){
                            output.append(companyGetter.getOwner(sender));
                        }
                        output.append("ò");
                        output.append(accountGetter.getAccountnumber(sender));
                    }
                    output.append("ò");
                    output.append(transfersGetter.getType(transfer));
                    output.append("ò");
                    if (Objects.equals(accountnumber, senderAccountnumber)){
                        output.append(transfersGetter.getSenderPurpose(transfer, sender));
                    } else if(Objects.equals(accountGetter.getAccountnumber(receiver), accountnumber)){
                        output.append(transfersGetter.getReceiverPurpose(transfer, receiver));
                    } else {
                        output.append("You were nor involved in this transfer!");
                    }
                    output.append("ò");
                    output.append(plusminus);
                    output.append(transfersGetter.getAmount(transfer));
                    output.append("ň");
                }
            } else {
                output.append("ĵ");
            }

            resp.getWriter().println(URLEncoder.encode(output.toString(), "UTF-8"));
        } else {
            resp.getWriter().println(URLEncoder.encode("ĵ", "UTF-8"));
        }
    }
}
