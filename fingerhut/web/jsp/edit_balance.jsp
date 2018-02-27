
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
            <div>
                <label for="diff_input">Differenz</label>
                <input type="text" id="diff_input">
            </div>
            <button id="3" onclick="onButtonClick()">
                Button
            </button>
        </div>

        <script>
            var amountTextField = document.getElementById("amount_input");
            var differenceTextField = document.getElementById("diff_input");
            var accountnumberTextField = document.getElementById("accountnumber_input");

            function onButtonClick() {
                var uri = "https://fingerhut388.appspot.com/admin/editbalanceservlet?accountnumber=" + accountnumberTextField.value.toString();
                if (amountTextField.value.toString().length > 1){
                    uri += ("&amount=" + amountTextField.value.toString());
                }
                if (differenceTextField.value.toString().length > 1){
                    uri += ("&diff=" + differenceTextField.value.toString());
                }
                window.location = encodeURI(uri);
            }
        </script>
    </body>
</html>
