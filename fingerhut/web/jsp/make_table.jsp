<%@ page import="de.repictures.fingerhut.Datastore.Company" %>
<%@ page import="java.util.List" %>
<%@ page import="de.repictures.fingerhut.Web.MainTools" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Unternehmensdaten</title>
</head>
<body>
<table>
    <%
        String value = "";
        int myNumber = Integer.valueOf(request.getParameter("n"));
        int sector = Integer.valueOf(request.getParameter("s"));
        List<Company> companies = MainTools.getCompaniesBySector(sector);
        double theNumber = 0;
        for (Company company : companies){
            List<Number> dates = company.getBalanceDevelopmentTimes();
            List<Number> values = company.getBalanceDevelopment();
            int distance = Math.abs(dates.get(0).intValue() - myNumber);
            int idx = 0;
            for(int c = 1; c < dates.size(); c++){
                int cdistance = Math.abs(dates.get(c).intValue() - myNumber);
                if(cdistance < distance){
                    idx = c;
                    distance = cdistance;
                }
            }
            theNumber += values.get(idx).doubleValue();
            value = String.valueOf(theNumber).replace(".", ",");
        }
    %>
    <tr>
        <td><%=value%></td>
    </tr>
</table>
</body>
</html>