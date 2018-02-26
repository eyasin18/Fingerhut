<%--
  Created by IntelliJ IDEA.
  User: eyasi
  Date: 26.02.2018
  Time: 21:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Administrator Überweisungen</title>
</head>
<body>

<div>
    <label for="sender_input">Senderkontonummer</label>
    <input type="text" id="sender_input">
</div>
<div>
    <label for="receiver_input">Empfängerkontonummer</label>
    <input type="text" id="receiver_input">
</div>
<div>
    <label for="amount_input">Betrag</label>
    <input type="text" id="amount_input">
</div>
<button id="3" onclick="onButtonClick()">
    Button
</button>

<script>
    var receiverTextField = document.getElementById("receiver_input");
    var amountTextField = document.getElementById("amount_input");
    var senderTextField = document.getElementById("sender_input");

    function onButtonClick() {
        var uri = "https://fingerhut388.appspot.com/admin/doadmintransfer?sender=" + senderTextField.value.toString() + "&receiver=" + receiverTextField.value.toString() + "&amount=" + amountTextField.value.toString();
        window.location = encodeURI(uri);
    }
</script>
</body>
</html>
