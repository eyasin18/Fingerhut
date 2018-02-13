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
<%@ page import="de.repictures.fingerhut.Web.Signoff" %>
<%@ page import="static de.repictures.fingerhut.Datastore.Tax.getVAT" %>
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
</head>
<body>
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
                        <div class="mdl-card mdl-cell mdl-cell--6-col">
                            <div id="balance">
                                <script>
                                    <%
                                    String balancestring = mainTools.getBalance(accountnumber);
                                    float balancenumber = java.lang.Float.parseFloat(balancestring);
                                    balancestring = String.format("%.2f", balancenumber);
                                    %>
                                </script>
                                <h3>Kontostand: <%= balancestring %></h3>
                                <br>
                                <h3>Mehrwertsteuer : <%= getVAT() %> %</h3>
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
                <div class="mdl-layout mdl-js-layout mdl-color--green-light_blue-100" id="company_layout">
                    <main class="mdl-layout__content" id="company_card">
                        <div class="mdl-card mdl-shadow--6dp">
                            <div id="title_card" class="mdl-card__title mdl-color--primary mdl-color-text--white">
                                <h2 class="mdl-card__title-text">Unternehmen</h2>
                            </div>
                            <div class="mdl-card__supporting-text">
                                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select getmdl-select__fix-height" id="dropdown_wrapper">
                                    <input type="text" value="" class="mdl-textfield__input" id="dropdown_company_field"
                                           readonly>
                                    <input type="hidden" value="" name="dropdown_field">
                                    <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                                    <label for="dropdown_company_field" class="mdl-textfield__label">Unternehmen</label>
                                    <ul for="dropdown_field" class="mdl-menu mdl-menu--bottom-left mdl-js-menu" id="dropdown_list">
                                    </ul>
                                </div>
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
    var isOpera = (platform.name === 'Opera') && (platform.version >= 50);
    var isFirefox = ((platform.name === 'Firefox') && (platform.version >= 44)) || ((platform.name === 'Firefox for iOS') && (platform.version >= 37));
    var isChrome = (platform.name === 'Chrome') || (platform.name === 'Chrome Mobile');
    var isAdmin = <%= mainTools.isCompanyAdmin("0002") %>;

    var companypass = document.getElementById('companypass');
    var companypassError = document.getElementById('companypass_error');

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
        getURL = url + "/transfer?receiveraccountnumber=" + receiveraccountnumber + "&senderaccountnumber=<%= accountnumber %>&webstring=<%= code %>&keyssnumbers=true";
        httpAsync(getURL,"GET", 1);
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
            case 1:
                var responses = responseStr.split("ò");
                switch (parseInt(responses[0])) {
                    case 1:
                        /*var senderModulus = responses[1];
                        var senderExponent = responses[2];
                        var receiverModulus = responses[3];
                        var receiverExponent = responses[4];

                        //TODO: Schlüssel zufällig generieren
                        var senderAesKey = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16];
                        var receiverAesKey = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16];
                        var purpose = "Hallo Welt! Ich bin hier!";

                        var purposeBytes = aesjs.utils.utf8.toBytes(purpose);
                        var aesSenderEcb = new aesjs.ModeOfOperation.ecb(senderAesKey);

                        var encryptedSenderBytes = aesSenderEcb.encrypt(purposeBytes);
                        var encryptedSenderPurposeHex = aesjs.utils.hex.fromBytes(encryptedSenderBytes);
                        console.log(encryptedSenderPurposeHex);

                        var aesReceiverEcb = new aesjs.ModeOfOperation.ecb(receiverAesKey);
                        var encryptedReceiverBytes = aesReceiverEcb.encrypt(purposeBytes);
                        var encryptedReceiverPurposeHex = aesjs.utils.hex.fromBytes(encryptedReceiverBytes);
                        console.log(encryptedReceiverPurposeHex);

                        var senderAesKeyHex = aesjs.utils.hex.fromBytes(senderAesKey);
                        var receiverAesKeyHex = aesjs.utils.hex.fromBytes(receiverAesKey);

                        var senderRSA = new RSAKey();
                        senderRSA.setPublic(senderModulus, senderExponent);
                        String.fromCharCode.apply(null, senderAesKey);
                        var encryptedSenderAESKey = senderRSA.encrypt(senderAesKey);

                        var receiverRSA = new RSAKey();
                        receiverRSA.setPublic(receiverModulus, receiverExponent);
                        var encryptedReceiverAESKey = receiverRSA.encrypt(receiverAesKey);*/

                        var postUrl = url + "/transfer?amount=" + amount
                            + "&receiveraccountnumber=" + receiveraccountnumber
                            + "&senderaccountnumber=<%= accountnumber%>"
                            + "&code=<%=code%>"/*
                    + "&senderpurpose=" + encryptedSenderPurposeHex
                    + "&senderkey=" + encryptedSenderAESKey
                    + "&receiverpurpose=" + encryptedReceiverPurposeHex
                    + "&receiverkey=" + encryptedReceiverAESKey*/;
                        httpAsync(postUrl, "POST", 1);
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
                break;

            case 2:
                submitButton.textContent = strings.loginButtonText;
                submitSpinner.style.visibility = 'hidden';
                console.log(responseStr);
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
                        console.log("Webstring nicht aktuell");
                        break;
                    case 1:
                        //Alles gut
                        processCompanyLogin();
                        break;
                }
                break;
            case 3: window.location.replace(url);
        }
    }

    function processPostResponse(responseStr, callerid) {
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

    function companyLogin(){
        //TODO: Spinner ist weg :o
        submitSpinner.style.visibility = 'visible';
        submitButton.textContent = '';
        companypassError.parentElement.className = companypassError.parentElement.className.replace(" is-invalid", "");
        companypassError.textContent = '';

        var hash = sjcl.hash.sha256.hash(companypass.value);
        var encryptedPassword = sjcl.codec.hex.fromBits(hash);

        var companyLoginUrl = "https://fingerhut388.appspot.com/companylogin?companynumber=0002"
            + "&accountnumber=<%=accountnumber%>&password=" + encryptedPassword
            + "&webstring=<%=code%>";

        console.log("Is Admin? " + isAdmin);
        console.log(platform.name);
        if(!isChrome && !isFirefox && !isOpera){
            if(!isAdmin) {
                companypassError = document.getElementById('companypass_error');
                companypassError.parentElement.className += ' is-invalid';
                companypassError.textContent = "Sie müssen Chrome, Firefox oder Opera benutzen um sich auf der Unternehmensseite anmelden zu können.";
            } else {
                if (confirm("Kaufaufträge können sie nur mit Chrome, Firefox und Opera einsehen und bearbeiten.") === true) {
                    httpAsync(companyLoginUrl, "GET", 2);
                } else {
                    httpAsync(companyLoginUrl, "GET", 2);
                }
            }
        } else {
            httpAsync(companyLoginUrl, "GET", 2);
        }
    }

    function processCompanyLogin() {
        window.location = "https://fingerhut388.appspot.com/company?accountnumber=<%= accountnumber%>&companynumber=0002&webstring=<%= code %>";
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

    /*function fillDropdown() {
        var dropdown_list = document.getElementById("dropdown_list");
        for(var i = 0;i < ; i++){
            var line = document.createElement("li");
            line.classList.add("mdl-menu__item");
            line.innerHTML = ;
            dropdown_list.appendChild(line);
    }*/

</script>
</body>
</html>