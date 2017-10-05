<%@ page import="java.io.IOException" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>

<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<%@ page import="java.util.Objects" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="de.repictures.fingerhut.Web.MainTools" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String code = request.getParameter("code");
    String accountnumber = request.getParameter("accountnumber");
    MainTools mainTools = new MainTools(accountnumber, code);
    if (!mainTools.isAuthentificated()){
        response.sendRedirect("https://fingerhut388.appspot.com/");
    }
    %>

<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/main.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.green-light_green.min.css">
    <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header mdl-layout--fixed-tabs">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <!-- Title -->
            <span class="mdl-layout-title mdl-color-text--white">Fingerhut</span>
            <div class="mdl-layout-spacer"></div>
            <form action= "https://fingerhut388.appspot.com" >
                <input type="submit" value="Ausloggen" id="logout_button" class= "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent mdl-color-text--white"/>
            </form>
        </div>
        <!-- Tabs -->
        <div class="mdl-layout__tab-bar mdl-js-ripple-effect">
            <a href="#scroll-tab-1" class="mdl-layout__tab is-active mdl-color-text--white">Kontoübersicht</a>
            <a href="#scroll-tab-2" class="mdl-layout__tab mdl-color-text--white">Überweisen</a>
            <a href="#scroll-tab-3" class="mdl-layout__tab mdl-color-text--white">Unternehmen</a>
        </div>
    </header>
    <main class="mdl-layout__content">
        <section class="mdl-layout__tab-panel is-active" id="scroll-tab-1">
            <div class="page-content">
                <div class="content-grid mdl-grid">
                    <div class="mdl-card mdl-shadow--4dp mdl-cell mdl-cell--12-col content-grid mdl-grid">
                        <div class="mdl-card mdl-cell mdl-cell--6-col">
                            <div class="mdl-card__title">
                                <script>
                                    <%
                                    String balancestring = mainTools.getBalance(accountnumber);
                                    float balancenumber = java.lang.Float.parseFloat(balancestring);
                                    balancestring = String.format("%.2f", balancenumber);
                                    %>
                                </script>
                                <h3>Kontostand: <%= balancestring %></h3>
                            </div>
                        </div>
                        <div class="mdl-card mdl-cell mdl-cell--6-col">
                            <div class="mdl-card__title mdl-color-text--green" id="kontostand">
                                <h1>Girokonto</h1>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <section class="mdl-layout__tab-panel" id="scroll-tab-2">
            <div class="page-content">
                <div class="content-grid mdl-grid">
                    <div class="mdl-card mdl-shadow--4dp mdl-cell mdl-cell--12-col">
                        <div class="mdl-card__title" id="transfer_heading">
                            <h2>Überweisen</h2>
                        </div>
                        <div id="cash_icon">
                            <img src="../res/images/cash.svg" alt="cash_icon" style="width:128px;height:128px;">
                        </div>
                        <div id="form" class="mdl-card">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" id="receiver">
                                <label class="mdl-textfield__label" for="receiver">Begünstigter</label>
                            </div>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?[^-]" id="accountnumber">
                                <label class="mdl-textfield__label" for="accountnumber">Kontonummer</label>
                                <span class="mdl-textfield__error" id="accountnumber_error"></span>
                            </div>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?[^-]" id="amount">
                                <label class="mdl-textfield__label" for="amount">Betrag</label>
                                <span class="mdl-textfield__error" id="amount_error"></span>
                            </div>
                            <div class="mdl-card__actions">
                                <button id="transfer_button" onclick="onButtonClick()" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" type="button">
                                    Überweisen
                                </button>
                                <div id="toast" class="mdl-js-snackbar mdl-snackbar">
                                    <div class="mdl-snackbar__text"></div>
                                    <button class="mdl-snackbar__action" type="button"></button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <section class="mdl-layout__tab-panel" id="scroll-tab-3">
            <div class="page-content">
                <div class="mdl-card mdl-shadow--4dp mdl-cell mdl-cell--12-col">
                    <div class="mdl-card__title" id="company_heading">
                        <h2>Unternehmen</h2>
                    </div>
                    <div id="business_icon">
                        <img src="../res/images/ic_business_black_48px.svg" alt="business_icon" style="width:128px;height:128px;">
                    </div>
                </div>
            </div>
        </section>
    </main>
</div>
<script type="application/javascript">
    var receiveraccountnumber;
    var amount;
    var url = "https://fingerhut388.appspot.com";
    var getURL;


    function onButtonClick(){
        document.getElementById('transfer_button');

        var accountnumberError = document.getElementById('accountnumber_error');
        accountnumberError.parentElement.className = accountnumberError.parentElement.className.replace(" is-invalid", "");
        accountnumberError.textContent = '';

        var amountError = document.getElementById('amount_error');
        amountError.parentElement.className = amountError.parentElement.className.replace(" is-invalid", "");
        amountError.textContent = '';

        receiveraccountnumber = document.getElementById('accountnumber').value;
        if (isNaN(receiveraccountnumber) || receiveraccountnumber < 0){
            var element1 = document.getElementById('accountnumber_error');
            element1.parentElement.className += ' is-invalid';
            element1.textContent = strings.receiveraccountnumberFormatError;
            return;
        }
        amount = document.getElementById('amount').value;
        if (isNaN(amount) || amount < 0){
            var element2 = document.getElementById('accountnumber_error');
            element2.parentElement.className += ' is-invalid';
            element2.textContent = strings.amountFormatError;
            return;
        }
        amount = Math.round(amount * 100) / 100;
        document.getElementById("amount").value = amount;
        getURL = url + "/transfer?receiveraccountnumber=" + receiveraccountnumber + "&senderaccountnumber=<%= accountnumber %>&webstring=<%= code %>";
        httpAsync(getURL,"GET");
        document.getElementById('transfer_button').removeAttribute("disabled");

    }

    function httpAsync(theUrl, method) {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (xmlHttp.readyState === 4 && xmlHttp.status === 200){
                console.log(xmlHttp.responseText);
                if (method === "GET") processGetResponse(decodeURIComponent(xmlHttp.responseText));
                else if (method === "POST") processPostResponse(decodeURIComponent(xmlHttp.responseText));
            }
        };
        xmlHttp.open(method, theUrl, true); // true for asynchronous
        xmlHttp.send(null);
    }

    function processGetResponse(responseStr){
        var responses = responseStr.split("ò");
        switch (parseInt(responses[0])){
            case 1:
                var postUrl = url + "/transfer?amount=" + amount
                    + "&receiveraccountnumber=" + receiveraccountnumber
                    + "&senderaccountnumber=<%= accountnumber%>"
                    + "&code=<%=code%>";
                httpAsync(postUrl, "POST");
                break;
            case 2:
                //TODO: Nutzer sagen er muss sich nochmal anmelden
                break;
            case 3:
                var ele = document.getElementById('accountnumber_error');
                ele.parentElement.className += ' is-invalid';
                ele.textContent = strings.pinError;
                break;
            default:
                break;
        }
    }

    function processPostResponse(responseStr) {
        console.log(responseStr);
        var notification = document.querySelector('#toast');
        notification.MaterialSnackbar.showSnackbar(
            {
                message: 'Überweisung erfolgreich',
                timeout: 3000
            }
        );
        window.location.reload();
    }
</script>
</body>
</html>