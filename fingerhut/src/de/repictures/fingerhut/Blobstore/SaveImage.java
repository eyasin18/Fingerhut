package de.repictures.fingerhut.Blobstore;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

public class SaveImage extends HttpServlet {

    private Logger log = Logger.getLogger(SaveImage.class.getName());
    BlobstoreService bServ = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("doGet is called");
        String temporaryURL = bServ.createUploadUrl("/saveimage");
        log.info("doGet: temporaryURL= " + temporaryURL);
        resp.getWriter().println(temporaryURL);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("doPost is called");
        List<BlobKey> blobs = bServ.getUploads(req).get("image");
        BlobKey blobKey = blobs.get(0);

        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);

        String servingUrl = imagesService.getServingUrl(servingOptions);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");

        PrintWriter out = resp.getWriter();
        out.println(servingUrl + "~" + blobKey);
        out.flush();
        out.close();
        log.info("doPost has results:\nServing URL: " + servingUrl + "\nBlobKey: " + blobKey);
    }
}