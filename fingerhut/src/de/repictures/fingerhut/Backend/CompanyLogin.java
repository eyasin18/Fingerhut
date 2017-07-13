package de.repictures.fingerhut.Backend;

import com.google.appengine.api.datastore.Entity;
import de.repictures.fingerhut.Cryptor;
import de.repictures.fingerhut.Datastore.Company;
import de.repictures.fingerhut.Datastore.Transfers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

public class CompanyLogin extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountnumber = req.getParameter("accountnumber");
        String inputPassword = req.getParameter("password");
        if (inputPassword != null) {
            inputPassword = URLDecoder.decode(inputPassword, "UTF-8");
            Cryptor cryptor = new Cryptor();
            inputPassword = cryptor.bytesToHex(cryptor.hashToByte(inputPassword));
        }

        Company companyGetter = new Company();
        Entity company = companyGetter.getAccount(accountnumber);

        String savedPassword = companyGetter.getPassword(company);

        if (Objects.equals(inputPassword, savedPassword)){
            resp.getWriter().println("1ò" + getCompanyInfo(company, companyGetter));
        } else {
            resp.getWriter().println("0");
        }
    }

    private String getCompanyInfo(Entity company, Company companyGetter) throws UnsupportedEncodingException {
        StringBuilder output = new StringBuilder();
        output.append(companyGetter.getOwner(company));
        output.append("ò");
        output.append(companyGetter.getBalance());
        return URLEncoder.encode(output.toString(), "UTF-8");
    }
}
