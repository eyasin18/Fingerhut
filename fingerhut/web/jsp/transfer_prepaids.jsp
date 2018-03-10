<%@ page import="java.util.List" %>
<%@ page import="de.repictures.fingerhut.Datastore.Account" %>
<%@ page import="de.repictures.fingerhut.Web.MainTools" %><%--
  Created by IntelliJ IDEA.
  User: Fabian Schmid
  Date: 10.03.2018
  Time: 20:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

</body>
</html>
    <%
        List<Account> prepaidAccounts = MainTools.getPrepaidAccounts();
        for (Account account : prepaidAccounts){
    %>
    <tr>
        <td><%=account.getAccountnumber()%></td>
        <td><%=account.getBalance()%></td>
    </tr>
    <%
        }
    %>

<button onclick="onButtonClick()">Click mich Senpai</button>
<script>
    function onButtonClick() {
        <%
        for (Account account : prepaidAccounts){
        %>
            var url = "https://fingerhut388.appspot.com/admin/doadmintransfer?sender=<%=account.getAccountnumber()%>&receiver=0098&amount=<%=account.getBalance()%>";
            httpAsync(url, "GET");
        <%
        }
        %>
    }

    function httpAsync(theUrl, method) {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (xmlHttp.readyState === 4 && xmlHttp.status === 200){
                console.log(xmlHttp.responseText);
            }
        };
        xmlHttp.open(method, theUrl, true); // true for asynchronous
        xmlHttp.send(null);
    }
</script>