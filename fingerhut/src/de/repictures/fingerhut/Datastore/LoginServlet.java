package de.repictures.fingerhut.Datastore;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class LoginServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        String accountnumber = URLDecoder.decode(req.getParameter("accountnumber"), "UTF-8");
        String inputPassword = URLDecoder.decode(req.getParameter("password"), "UTF-8");

        Key loginKey = KeyFactory.createKey("accountnumber", accountnumber);
        Query loginQuery = new Query("Account", loginKey);
        List<Entity> accountList = datastore.prepare(loginQuery).asList(FetchOptions.Builder.withDefaults());
        if(accountList.size() > 0){
            Entity account = accountList.get(0);
            String accountPassword = account.getProperty("password").toString();
            if(accountPassword.equals(inputPassword)){
                resp.getWriter().println(URLEncoder.encode("2ò" + KeyFactory.keyToString(account.getKey()), "UTF-8"));
            } else {
                resp.getWriter().println("1");
            }
        } else {
            resp.getWriter().println("0");
        }
    }
}
