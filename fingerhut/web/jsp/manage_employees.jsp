<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mitarbeiter verwalten</title>
</head>
<body>
    <div>
        <input type="number" id="company_number_text_field">
        <label for="company_number_text_field">Kontomnummer des Unternehmens</label>
    </div>
    <div>
        <input type="number" id="account_number_text_field">
        <label for="account_number_text_field">Kontonummer des Bürgers</label>
    </div>
    <div>
        <input type="checkbox" id="is_ceo_checkbox">
        <label for="is_ceo_checkbox">Ist Unternehmensleiter</label>
    </div>
    <div>
        <input type="radio" id="remove_radio" onclick="onRemoveClicked()">
        <label for="remove_radio">Entfernen</label>
    </div>
    <div>
        <input type="radio" id="add_radio" checked onclick="onAddChecked()">
        <label for="add_radio">Hinzufügen</label>
    </div>
    <button id="finish_button" onclick="onButtonClick()">
        Abschließen
    </button>

    <script>
        var companynumberField = document.getElementById("company_number_text_field");
        var accountnumberField = document.getElementById("account_number_text_field");
        var isCeoCheckbox = document.getElementById("is_ceo_checkbox");
        var removeRadio = document.getElementById("remove_radio");
        var addRadio = document.getElementById("add_radio");

        function onRemoveClicked() {
            addRadio.checked = false;
        }

        function onAddChecked() {
            removeRadio.checked = false;
        }

        function onButtonClick() {
            var url = "https://fingerhut388.appspot.com/admin/addemployee?accountnumber=" + accountnumberField.value.toString() + "&companynumber=" + companynumberField.value.toString();
            if(isCeoCheckbox.checked === true){
                url += "&isceo=true";
            }
            if(removeRadio.checked === true){
                url += "&remove=true";
            }

            window.location = encodeURI(url);
        }
    </script>
</body>
</html>