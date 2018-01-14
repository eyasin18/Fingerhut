package de.repictures.fingerhut.Debug;

import de.repictures.fingerhut.Datastore.Tax;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class TaxSetter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        int id = -1;
        if (idStr != null) id = Integer.valueOf(idStr);
        String taxesStr = req.getParameter("tax");
        int[] taxes = Arrays.stream(taxesStr.split("~")).mapToInt(Integer::parseInt).toArray();

        switch (id){
            case 1:
                Tax.setVAT(taxes[0]);
                resp.getWriter().println("Jo");
                break;
            case 2:
                Tax.setProfitTax(taxes[0]);
                resp.getWriter().println("Jo");
                break;
            case 3:
                Tax.setWageTax(taxes);
                resp.getWriter().println("Jo");
                break;
            case 4:
                Tax.setPackageCustom(taxes[0]);
                resp.getWriter().println("Jo");
                break;
            case 5:
                Tax.setMeatCustom(taxes[0]);
                resp.getWriter().println("Jo");
                break;
            default:
                resp.getWriter().println("Upsi");
        }
    }
}
