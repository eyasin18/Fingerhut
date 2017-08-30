package de.repictures.fingerhut.Backend;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SendMessage extends HttpServlet{

    private final static String AUTHENTIFICATION_KEY = "AAAATqpEAqE:APA91bHDNQ6rnzBLpMgpuM_FZyrArDP5Fdu8nYtlEwIJ6PIAKxzaaoEcp4X0NYMok3A-BCjbRrLoCMZWZauGjkZ1wyx7NuQxliu08cZUPz1CvK5JFp3U72IrBWWNqGNxJMljc6e6vlQD";

    private int response = 0;
    private Logger log = Logger.getLogger(SendMessage.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message = req.getParameter("message");
        String registrationToken = req.getParameter("token");
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        sendMessage(map, registrationToken);
        resp.getWriter().println(getResonse());
    }

    public void sendMessage(Map<String, String> dataBody, String registrationToken){
        try{
            URL firebaseUrl = new URL("https://fcm.googleapis.com/fcm/send");

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("{ \"to\": \"")
                    .append(registrationToken)
                    .append("\", \"data\" : {");

            int counter = 0;
            for (Map.Entry<String, String> entry : dataBody.entrySet()){
                if (counter > 0){
                    messageBuilder.append(",");
                }
                messageBuilder
                        .append("\"")
                        .append(entry.getKey())
                        .append("\"")
                        .append(": ")
                        .append("\"")
                        .append(entry.getValue())
                        .append("\"");
                counter++;
            }

            messageBuilder.append("}}");

            HttpURLConnection urlConnection = (HttpURLConnection) firebaseUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "application/json");
            urlConnection.addRequestProperty("Authorization", "key=" + AUTHENTIFICATION_KEY);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(messageBuilder.toString());
            writer.close();

            InputStream postInputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader postBufferedReader = new BufferedReader(new InputStreamReader(postInputStream, "UTF-8"));
            StringBuilder postTotal = new StringBuilder();
            String postLine;
            while ((postLine = postBufferedReader.readLine()) != null) {
                postTotal.append(postLine);
            }
            log.info(postTotal.toString());
            response = urlConnection.getResponseCode();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private int getResonse() {
        return response;
    }
}
