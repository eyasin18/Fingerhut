<%@ page import="de.repictures.fingerhut.Datastore.Company" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Unternehmensdaten</title>
</head>
<body>
<table>
    <%
        Company company = new Company(request.getParameter("num"));
        List<Number> balanceDates = company.getBalanceDevelopmentTimes();
        List<Number> balances = company.getBalanceDevelopment();
        boolean dates = Boolean.parseBoolean(request.getParameter("d"));
        String value;
        for (int i = 0; i < balanceDates.size(); i++) {
    %>
    <tr>
        <%
            if (dates){
                value = String.valueOf(balanceDates.get(i).intValue()).replace(".", ",");
            } else {
                value = String.valueOf(balances.get(i).doubleValue()).replace(".", ",");
            }
        %>
        <td><%=value%></td>
    </tr>
    <%
        }
    %>
</table>
</body>
</html>