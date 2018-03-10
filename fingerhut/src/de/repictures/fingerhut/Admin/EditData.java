package de.repictures.fingerhut.Admin;

import javax.servlet.ServletException;
import com.google.appengine.api.datastore.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class EditData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query("PurchaseOrder");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        resp.getWriter().println(entities.size());
    }
}
