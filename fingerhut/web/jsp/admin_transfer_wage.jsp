<%--
  Created by IntelliJ IDEA.
  User: eyasi
  Date: 27.02.2018
  Time: 19:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Lohnauszahlung</title>
</head>
<body onkeypress="return enterPressed(event);">
<div>
    <label for="employee_input">Arbeitnehmerkonto</label>
    <input type="number" id="employee_input">
</div>
<div>
    <label for="company_input">Unternehmenskonto</label>
    <input type="number" id="company_input">
</div>
<div>
    <label for="amount_input">Betrag</label>
    <input type="number" id="amount_input">
</div>
<div>
    <label for="hours_input">Stunden</label>
    <input type="number" id="hours_input">
</div>
<button id="3" onclick="onButtonClick()">
    Button
</button>

<script>
    var employeeTextField = document.getElementById("employee_input");
    var amountTextField = document.getElementById("amount_input");
    var companyTextField = document.getElementById("company_input");
    var hoursTextField = document.getElementById("hours_input");

    function enterPressed(event) {
        if (event.keyCode === 13){
            onButtonClick();
        }
    }

    function onButtonClick() {
        var uri = "https://fingerhut388.appspot.com/admin/doadmintransferwage?employee=" + employeeTextField.value.toString() + "&company=" + companyTextField.value.toString() + "&amount=" + amountTextField.value.toString() + "&hours=" + hoursTextField.value.toString();
        window.location = encodeURI(uri);
    }
</script>
</body>
</html>
