
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
    <head>
        <title></title>
    </head>
    <body>
        <div id="employees">
            <div>
                <label for="accountnumber_input">Kontonummer</label>
                <input type="text" id="accountnumber_input">
            </div>
            <div>
                <label for="amount_input">Betrag</label>
                <input type="text" id="amount_input">
            </div>
            <button id="3" onclick="onButtonClick()">
                Button
            </button>
        </div>

        <script>
            var amountTextField = document.getElementById("amount_input");
            var accountnumberTextField = document.getElementById("accountnumber_input");

            function onButtonClick() {
                var uri = "https://2-dot-fingerhut388.appspot.com/admin/editbalanceservlet?accountnumber=" + accountnumberTextField.value.toString() + "&amount=" + amountTextField.value.toString();
                window.location = encodeURI(uri);
            }
        </script>
    </body>
</html>
