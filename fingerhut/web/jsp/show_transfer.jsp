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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/show_transfer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}../css/material.green-light_green.min.css">
    <script defer src="${pageContext.request.contextPath}../js/material.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script type="application/javascript" src="../res/values/strings.js"></script>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}../res/images/favicon.ico">
    <link rel="apple-touch-icon" href="${pageContext.request.contextPath}../res/images/apple-touch-icon.png">
    <title>Fingerhut</title>
</head>
<body>
    <div class="demo-layout mdl-layout mdl-layout--fixed-header mdl-js-layout mdl-color--grey-100">
        <header class="demo-header mdl-layout__header mdl-layout__header--scroll mdl-color--grey-100 mdl-color-text--grey-800">
            <div class="mdl-layout__header-row">
                <span class="mdl-layout-title">Fingerhut</span>
                <div class="mdl-layout-spacer"></div>
            </div>
        </header>
        <div class="demo-ribbon"></div>
        <main class="demo-main mdl-layout__content">
            <div class="demo-container mdl-grid">
                <div class="mdl-cell mdl-cell--2-col mdl-cell--hide-tablet mdl-cell--hide-phone"></div>
                <div class="demo-content mdl-color--white mdl-shadow--4dp content mdl-color-text--grey-800 mdl-cell mdl-cell--8-col" id="transfers">
                    <h3 id="heading">Überweisungen</h3>
                    <table class="mdl-data-table mdl-js-data-table" id="transfer_table">
                        <thead>
                        <tr>
                            <th>Zeitpunkt</th>
                            <th>Sender</th>
                            <th>Betrag</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                    <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" id="back_button" onclick="backToMain()">Zurück</button>
                </div>
            </div>
            <footer class="demo-footer mdl-mini-footer">
                <div class="mdl-mini-footer--left-section">
                    <ul class="mdl-mini-footer--link-list">
                        <li><a href="https://fingerhut388.appspot.com/">Login</a></li>
                        <li><a href="http://stromberg-gymnasium-saz.de/">SaZ-Homepage</a></li>
                    </ul>
                </div>
            </footer>
        </main>
    </div>
</body>
</html>

<script>
    function backToMain() {
        window.history.back();
    }
    function fillTable() {
        var table = document.getElementById("transfer_table");
        for(var i; i < 0;i++){
            var row = table.insertRow(table.rows.length);
            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            cell1.innerHTML =
            cell2.innerHTML =
            cell3.innerHTML =
        }
    }
</script>
