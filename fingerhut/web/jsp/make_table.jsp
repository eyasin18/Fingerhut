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
        int[] times = new int[]{0, 720, 1440, 2160, 2880, 3600, 4320, 5040, 5760, 6480, 7200, 7920, 9360};
        String value;
        int sector = Integer.valueOf(request.getParameter("s"));
        List<Company> companies = MainTools.getCompaniesBySector(sector);
        for (int time : times) {
            double theNumber = 0;
            for (Company company : companies) {
                List<Number> dates = company.getBalanceDevelopmentTimes();
                List<Number> values = company.getBalanceDevelopment();
                int distance = Math.abs(dates.get(0).intValue() - time);
                int idx = 0;
                for (int c = 1; c < dates.size(); c++) {
                    int cdistance = Math.abs(dates.get(c).intValue() - time);
                    if (cdistance < distance) {
                        idx = c;
                        distance = cdistance;
                    }
                }
                theNumber += values.get(idx).doubleValue();
            }
            value = String.valueOf(theNumber).replace(".", ",");
            %>
            <tr>
                <td><%=value%></td>
            </tr>
            <%
        }

        double balanceDouble = 0;
        for (Company company : companies){
            balanceDouble += company.getBalanceDouble();
        }
        %>
        <tr>
            <td><%=String.valueOf(balanceDouble).replace(".", ",")%></td>
        </tr>
</table>
</body>
</html>