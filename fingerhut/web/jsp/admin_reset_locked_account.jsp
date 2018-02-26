<%--
  Created by IntelliJ IDEA.
  User: eyasi
  Date: 26.02.2018
  Time: 21:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Gesperrtes Konto freigeben</title>
</head>
<body>
<div>
    <label for="accountnumber_input">Kontonummer</label>
    <input type="text" id="accountnumber_input">
</div>
<button id="3" onclick="onButtonClick()">
    Button
</button>

<script>
    var accountnumberTextField = document.getElementById("accountnumber_input");

    function onButtonClick() {
        var uri = "https://fingerhut388.appspot.com/admin/doresetlockedaccount?accountnumber=" + accountnumberTextField.value.toString();
        window.location = encodeURI(uri);
    }
</script>
</body>
</html>
