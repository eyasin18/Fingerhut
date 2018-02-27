package de.repictures.fingerhut.Web;

import de.repictures.fingerhut.Datastore.Account;
import de.repictures.fingerhut.Datastore.Tax;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public class Login extends HttpServlet{

    private Logger log = Logger.getLogger(Account.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String accountnumber = req.getParameter("accountnumber");
        String passwordHash = req.getParameter("password");

        if (accountnumber == null || passwordHash == null){
            resp.getWriter().println("0");
            return;
        } else if (accountnumber.length() < 1 || passwordHash.length() < 1) {
            resp.getWriter().println("4");
            return;
        } else {
            passwordHash = passwordHash.toUpperCase(Locale.getDefault());
        }

        if (Tax.getIsServerLocked().intValue() > 0 && !(Objects.equals(accountnumber, "0000") || Objects.equals(accountnumber, "0001") || Objects.equals(accountnumber, "0004"))){
            resp.getWriter().println(URLEncoder.encode("-2", "UTF-8"));
            return;
        }

        Account accountGetter = new Account(accountnumber);

        if (accountGetter.account == null) {
            resp.getWriter().println("3");
            return;
        }

        if (!accountGetter.getIsPrepaid()) {
            Calendar cooldownTimeCalendar = accountGetter.getCooldownTime();
            if (cooldownTimeCalendar != null && cooldownTimeCalendar.after(Calendar.getInstance(Locale.getDefault()))) {
                SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String cooldownTimeStr = f.format(cooldownTimeCalendar.getTime());
                resp.getWriter().println(URLEncoder.encode("5ò" + cooldownTimeStr, "UTF-8"));
                return;
            }

            long failedAttempts = accountGetter.getLoginAttempts();
            /*if (failedAttempts > 9) {
                resp.getWriter().println(URLEncoder.encode("6", "UTF-8"));
                return;
            }*/
        }

        String savedPasswordHash = accountGetter.getHashedPassword();

        if (Objects.equals(savedPasswordHash, passwordHash)){
            if (!accountGetter.getIsPrepaid() && !accountGetter.gotBasicIncome() && Account.getDaysFromMinutes(Account.getCurrentMinutes()) != 6){
                accountGetter.transferBasicIncome();
                accountGetter.setGotBasicIncome(true);
            }

            accountGetter.updateRandomWebString();
            if (!accountGetter.getIsPrepaid() && !accountGetter.gotBasicIncome()){
                accountGetter.transferBasicIncome();
                accountGetter.setGotBasicIncome(true);
            }
            accountGetter.setLoginAttempts(0);
            accountGetter.saveAll();
            resp.getWriter().println(URLEncoder.encode("1ò" + accountGetter.getRandomWebString(), "UTF-8"));
        } else {
            //accountGetter.countUpLoginAttempts();
            accountGetter.saveAll();
            resp.getWriter().println("2");
        }
    }

}
