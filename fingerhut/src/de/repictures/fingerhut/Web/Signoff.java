package de.repictures.fingerhut.Web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import de.repictures.fingerhut.Datastore.Account;

import java.io.IOException;
import java.util.Objects;

public class Signoff extends HttpServlet{

    protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException{
        String accountnumber = req.getParameter("accountnumber");
        String siteWebstring = req.getParameter("webstring");

        Account currentAccount = new Account(accountnumber);
        String currentWebstring = currentAccount.getRandomWebString();

        if(Objects.equals(siteWebstring, currentWebstring)){
            resp.getWriter().println("1");
            currentAccount.updateRandomWebString();
        }
        else{
            resp.getWriter().println("0");
        }
    }
}
