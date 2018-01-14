<%--
  Created by IntelliJ IDEA.
  User: Fabian Schmid
  Date: 14.01.2018
  Time: 16:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<div id="employees">
    <div>
        <input type="text" id="1">
        <label for="1">Name</label>
    </div>
    <div>
        <input type="text" id="2">
        <label for="2">Kontomnummer</label>
    </div>
    <button id="3" onclick="onButtonClick()">
        Button
    </button>
</div>

<script>

    var textField1 = document.getElementById("1");
    var textField2 = document.getElementById("2");

    function onButtonClick() {
        var uri = "https://3-dot-fingerhut388.appspot.com/saveaccount?accountnumber=" + textField2.value.toString() + "&name=" + textField1.value.toString();
        window.location = encodeURI(uri);
    }

</script>
</body>
</html>