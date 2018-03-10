<%--
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
<script>
    var prepaidArray;
    for(var i = 0; i < prepaidArray.length; i++) {
        var amount = 1;
        var sender = 1;
        var uri = "https://fingerhut388.appspot.com/admin/doadmintransfer?sender=" + sender + senderTextField.value.toString() + "&receiver=0098" + "&amount=" + amount;
        window.location = encodeURI(uri);
    }
</script>