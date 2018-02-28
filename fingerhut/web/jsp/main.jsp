<!-- Imports verschiedener Java-Resourcen-->
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
<%@ page import="de.repictures.fingerhut.Web.SignOff" %>
<%@ page import="static de.repictures.fingerhut.Datastore.Tax.getVAT" %>
<%@ page import="de.repictures.fingerhut.Datastore.Account" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="errorpage.jsp" %> <!-- gibt die Seite an, die im Fehlerfall angezeigt werden soll -->

<%
    String code = request.getParameter("code");
    String accountnumber = request.getParameter("accountnumber");
    MainTools mainTools = new MainTools(accountnumber);
    if (!mainTools.isAuthentificated(code)){
        response.sendRedirect("https://fingerhut388.appspot.com/");
    }
    %>

<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}../css/icon.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}../css/material.green-light_green.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}../css/getmdl-select.min.css">
    <script defer src="${pageContext.request.contextPath}../js/material.min.js"></script>
    <script defer src="${pageContext.request.contextPath}../js/getmdl-select.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script language="JavaScript" type="text/javascript" src="../js/jsbn.js"></script>
    <script language="JavaScript" type="text/javascript" src="../js/prng4.js"></script>
    <script language="JavaScript" type="text/javascript" src="../js/rng.js"></script>
    <script language="JavaScript" type="text/javascript" src="../js/rsa.js"></script>
    <script language="JavaScript" type="text/javascript" src="../js/base64.js"></script>
    <script language="JavaScript" type="text/javascript" src="../js/platform.js"></script>
    <script defer src="../js/sjcl.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}../js/index.js"></script>
    <script type="application/javascript" src="../res/values/strings.js"></script>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}../res/images/favicon.ico">
    <link rel="apple-touch-icon" href="${pageContext.request.contextPath}../res/images/apple-touch-icon.png">
    <title>Fingerhut</title>
</head>
<body onpageshow="checkWebstring()">
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header mdl-layout--fixed-tabs">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <!-- Title -->
            <span class="mdl-layout-title mdl-color-text--white">Fingerhut</span>
            <div class="mdl-layout-spacer"></div>
            <button onclick="signoff()" id="logout_button" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent mdl-color-text--white">
                Ausloggen
            </button>
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
                        <div id="stuff_wrapper" class="mdl-card__supporting-text">
                            <h1 id="account_type"></h1>
                            <h3 class="title" id="balance_field"></h3>
                            <h3 class="title">Mehrwertsteuer : <%= getVAT() %> %</h3>
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
                            <h3>Überweisen</h3>
                        </div>
                        <div class="wrapper">
                            <button onclick="showTransfers()" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" id="show_transfers">Überweisungen ansehen</button>
                        </div>
                        <div id="cash_icon">
                            <img src="../res/images/cash.svg" alt="cash_icon" style="width:100px;height:100px;">
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
                            <h6 id="transfer_error" class="title"></h6>
                            <div class="mdl-card__actions">
                                <button id="transfer_button" onclick="onButtonClick()" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" type="button">
                                    Überweisen
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <section class="mdl-layout__tab-panel" id="scroll-tab-3">
            <div class="page-content">
                <div class="mdl-layout mdl-js-layout mdl-color--green-light_blue-100" id="company_layout">
                    <main class="mdl-layout__content" id="company_card">
                        <div class="mdl-card mdl-shadow--6dp">
                            <div id="title_card" class="mdl-card__title mdl-color--primary mdl-color-text--white">
                                <h2 class="mdl-card__title-text">Unternehmen</h2>
                            </div>
                            <div class="mdl-card__supporting-text">
                                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select getmdl-select__fix-height" id="dropdown_wrapper">
                                    <input type="text" value="" class="mdl-textfield__input" id="dropdown_company_field" readonly>
                                    <input type="hidden" value="" name="dropdown_field">
                                    <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                                    <label for="dropdown_company_field" class="mdl-textfield__label">Unternehmen</label>
                                    <ul for="dropdown_field" class="mdl-menu mdl-menu--bottom-left mdl-js-menu" id="dropdown_list">
                                    </ul>
                                </div>
                                <h6 class="title" id="companynumber_error"></h6>
                                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" id="companypass_view" onkeypress="return companyLoginEnterPressed(event);">
                                    <input class="mdl-textfield__input" type="password" id="companypass" pattern="-?[0-9]*(\.[0-9]+)?"/>
                                    <label class="mdl-textfield__label" for="companypass" id="companypass_label">Passwort</label>
                                    <span class="mdl-textfield__error" id="companypass_error"></span>
                                </div>
                                <div class="mdl-card__actions">
                                    <br>
                                    <button onclick="companyLogin()" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent mdl-color-text--white" type="submit" id="submit_button">Einloggen</button>
                                </div>
                                <div class="mdl-spinner mdl-js-spinner is-active" id="submit_spinner"></div>
                            </div>
                        </div>
                    </main>
                </div>
            </div>
        </section>
    </main>
</div>
<script type="application/javascript">
    var receiveraccountnumber;
    var amount;
    var url = "https://fingerhut388.appspot.com";
    var encryptedPurpose = "";
    var getURL;

    var companypass = document.getElementById('companypass');
    var companypassError = document.getElementById('companypass_error');
    var companynumberError = document.getElementById('companynumber_error');
    var companynumber;
    var companyNumbers = [];

    var submitSpinner = document.getElementById('submit_spinner');
    var submitButton = document.getElementById('submit_button');
    submitButton.textContent = strings.loginButtonText;
    var buttonWidth = window.getComputedStyle(submitButton, null).getPropertyValue("width");
    submitButton.style.setProperty("width", buttonWidth, "");
    var buttonHeight = window.getComputedStyle(submitButton, null).getPropertyValue("height");
    var rect = submitButton.getBoundingClientRect();
    var spinnerHeight = parseInt(buttonHeight, 10) - 12;
    submitSpinner.style.height = spinnerHeight + "px";
    submitSpinner.style.width = spinnerHeight + "px";
    submitSpinner.style.top = (rect.top + 6) + "px";
    var spinnerLeftInt = rect.left + parseInt(buttonWidth)/2 - spinnerHeight/2;
    submitSpinner.style.left = spinnerLeftInt + "px";
    submitSpinner.style.visibility = 'hidden';

    fillDropdown();
    window.onpageshow = function(){checkWebstring()};
    if(<%=mainTools.isPrepaid()%>){
        document.getElementById("account_type").innerText = "Prepaidkonto";
    }
    else {
        document.getElementById("account_type").innerText = "Bürgerkonto";
    }
    getBalance();

    function onButtonClick(){
        document.getElementById("transfer_error").innerText = "";

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
        var postUrl = url + "/transfer?amount=" + amount
            + "&receiveraccountnumber=" + receiveraccountnumber
            + "&senderaccountnumber=<%= accountnumber%>"
            + "&code=<%=code%>";
        document.getElementById('transfer_button').disabled = true;
        httpAsync(postUrl, "POST", 1);
        document.getElementById('transfer_button').removeAttribute("disabled");
    }

    function httpAsync(theUrl, method, callerid) {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (xmlHttp.readyState === 4 && xmlHttp.status === 200){
                console.log(xmlHttp.responseText);
                if (method === "GET") processGetResponse(decodeURIComponent(xmlHttp.responseText), callerid);
                else if (method === "POST") processPostResponse(decodeURIComponent(xmlHttp.responseText), callerid);
            }
        };
        xmlHttp.open(method, theUrl, true); // true for asynchronous
        xmlHttp.send(null);
    }

    function processGetResponse(responseStr, callerid){
        switch (callerid) {
            case 2:
                submitButton.textContent = strings.loginButtonText;
                submitSpinner.style.visibility = 'hidden';
                switch (parseInt(responseStr)){
                    case 0:
                        //Unternehmen existiert nicht
                        companypassError.parentElement.className += ' is-invalid';
                        companypassError.textContent = "Unternehmen existiert nicht";
                        break;
                    case 3:
                        //Passwörter stimmen nicht überein
                        companypassError.parentElement.className += ' is-invalid';
                        companypassError.textContent = strings.pinError;
                        break;
                    case 2:
                        //Webstring nicht aktuell
                        companypassError.parentElement.className += ' is-invalid';
                        companypassError.textContent = "Bitte logge dich neu ein";
                        break;
                    case 1:
                        //Alles gut
                        processCompanyLogin();
                        break;
                }
                break;
            case 3:
                window.location.replace(url);
                break;
            case 4:
                if (parseInt(responseStr) === 0){
                    window.location.replace(url);
                }
                break
        }
    }

    function processPostResponse(responseStr, callerid) {
        switch (parseInt(responseStr)){
            case 1:
                document.getElementById("transfer_error").innerText = "Du hast nicht genug Geld um den Kaufauftrag auszuführen.";
                break;
            case 2:
                document.getElementById("transfer_error").innerText = "Der Empfänger existiert nicht.";
                break;
            case 3:
                document.getElementById("transfer_error").innerText = "Der Kaufauftrag wurde erfolgreich durchgeführt.";
                getBalance();
                location.reload();
                break;
            case 4:
                document.getElementById("transfer_error").innerText = "Du kannst dir nicht selbst Geld überweisen.";
                break;
            case 5:
                document.getElementById("transfer_error").innerText = "Bitte logge dich neu ein.";
                break;
            case 6:
                document.getElementById("transfer_error").innerText = "Der Empfänger existiert nicht.";
                break;
        }
        document.getElementById("receiver").value = "";
        document.getElementById("receiver").parentElement.classList.remove("is-dirty");
        document.getElementById("amount").value = "";
        document.getElementById("amount").parentElement.classList.remove("is-dirty");
        document.getElementById("accountnumber").value = "";
        document.getElementById("accountnumber").parentElement.classList.remove("is-dirty");
        document.getElementById('transfer_button').disabled = false;
    }

    function companyLogin(){
        //TODO: Spinner ist weg :o
        submitSpinner.style.visibility = 'visible';
        //submitButton.textContent = '';
        companypassError.parentElement.className = companypassError.parentElement.className.replace(" is-invalid", "");
        companypassError.textContent = '';
        companynumber = document.getElementById("dropdown_company_field").value;
        if(companynumber !== "") {
            var hash = sjcl.hash.sha256.hash(companypass.value);
            var encryptedPassword = sjcl.codec.hex.fromBits(hash);
            var isAdmins = [];
            <%
            for (boolean isAdmin : mainTools.getIsAdmins()){
                %> isAdmins.push(<%=isAdmin%>); <%
            }
            %>
            var companyLoginUrl = "https://fingerhut388.appspot.com/companylogin?companynumber=" + companynumber
                + "&accountnumber=<%=accountnumber%>&password=" + encryptedPassword
                + "&webstring=<%=code%>";
                if (!isAdmins[companyNumbers.indexOf(companynumber)]) {
                    companypassError = document.getElementById('companypass_error');
                    companypassError.parentElement.className += ' is-invalid';
                    companypassError.textContent = "Sie haben nicht die nötigen Berechtigungen um sich bei der Unternehmensseite anmelden zu können.";
                }
                else {
                    httpAsync(companyLoginUrl, "GET", 2);
                }
        }
        else{
            companynumberError.textContent = "Bitte wählen sie eine Kontonummer aus!";
        }
    }

    function processCompanyLogin() {
        window.location = "https://fingerhut388.appspot.com/company?accountnumber=<%= accountnumber%>&companynumber=" + companynumber + "&webstring=<%= code %>";
    }

    function companyLoginEnterPressed(event) {
        if (event.keyCode === 13){
            companyLogin();
        }
    }

    function signoff(){
        var theurl = url + "/signoff?accountnumber=<%= accountnumber %>&webstring=<%= code %>";
        httpAsync(theurl, "GET",3);
    }

    function fillDropdown() {
        <%
            List<String> companyNumbers = mainTools.getCompanyNumbers(accountnumber);
            for (String companyNumber : companyNumbers) {
                %>
                companyNumbers.push("<%= companyNumber%>");
                <%

            }
        %>
        var dropdown_list = document.getElementById("dropdown_list");
        for (var i = 0; i < companyNumbers.length; i++) {
            var line = document.createElement("li");
            line.classList.add("mdl-menu__item");
            line.innerText = String(companyNumbers[i]);
            dropdown_list.appendChild(line);
        }
    }

    function checkWebstring() {
        var theUrl = url + "/checkwebstring?accountnumber=<%=accountnumber%>&webstring=<%=code%>";
        console.log(theUrl);
        httpAsync(theUrl, "GET", 4);
        var date = new Date();
        console.log(date.getTime());
    }

    function getBalance() {
        <%
        String balancestring = mainTools.getBalance(accountnumber);
        float balancenumber = java.lang.Float.parseFloat(balancestring);
        balancestring = String.format("%.2f", balancenumber);
    %>
        document.getElementById("balance_field").innerText = "Kontostand: " + "<%=balancestring%>";
    }
    function showTransfers() {
        window.location = "https://fingerhut388.appspot.com/showtransfers?accountnumber=<%=accountnumber%>";
    }
</script>
</body>
</html>