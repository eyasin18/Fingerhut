<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<div id="employees">
    <div>
        <input type="text" id="2">
        <label for="2">Kontomnummer</label>
    </div>
    <button id="3" onclick="onButtonClick()">
        Abschließen
    </button>
</div>

<script>
    var textField2 = document.getElementById("2");

    function onButtonClick() {
        var uri = "https://fingerhut388.appspot.com/admin/saveaccount?accountnumber=" + textField2.value.toString();
        window.location = encodeURI(uri);
    }

</script>
</body>
</html>