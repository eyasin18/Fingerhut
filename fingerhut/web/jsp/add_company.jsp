<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Unternehmen hinzufügen</title>
</head>
<body>
    <div>
        <input type="number" id="account_number_text_field">
        <label for="account_number_text_field">Kontomnummer</label>
    </div>
    <div>
        <input type="text" id="name_text_field">
        <label for="name_text_field">Name</label>
    </div>
    <div>
        <input type="number" id="sector_text_field">
        <label for="sector_text_field">Branche</label>
    </div>
    <button id="finish_button" onclick="onButtonClick()">
        Abschließen
    </button>

    <script>
        var accountnumberField = document.getElementById("account_number_text_field");
        var nameField = document.getElementById("name_text_field");
        var sectorField = document.getElementById("sector_text_field");

        function onButtonClick() {
            var url = "https://fingerhut388.appspot.com/admin/savecompany?accountnuber=" + accountnumberField.value.toString() + "&name=" + nameField.value.toString() + "&sector=" + sectorField.value.toString();
            window.location = encodeURI(url);
        }
    </script>
</body>
</html>