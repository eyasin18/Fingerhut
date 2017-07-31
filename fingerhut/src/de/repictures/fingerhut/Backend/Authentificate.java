package de.repictures.fingerhut.Backend;

import de.repictures.fingerhut.Datastore.Accounts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class Authentificate extends HttpServlet{

    public String serverTimeStamp = "";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String authPart = req.getParameter("authPart");

        Accounts accountsBuilder = new Accounts(accountnumber);
        String authCode = accountsBuilder.getAuthString();
        int accountnumberlength = accountnumber.length();
        String[] authParts = {authCode.substring(accountnumberlength, accountnumberlength+8), authCode.substring(accountnumberlength+8, accountnumberlength+16)};
        if (Objects.equals(authParts[0], authPart)){
            resp.getWriter().println(authParts[1] + serverTimeStamp);
        } else {
            resp.getWriter().println("0");
        }
    }
}
