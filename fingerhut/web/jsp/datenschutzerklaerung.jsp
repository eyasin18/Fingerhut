<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 04.10.2017
  Time: 20:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="errorpage.jsp" %> <!-- gibt die Seite an, die im Fehlerfall angezeigt werden soll -->

<html>
<head>
    <meta charset="utf-8">
    <title>Datenschutzerklärung</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}../css/material.green-light_green.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/datenschutzerklaerung.css">
    <script defer src="${pageContext.request.contextPath}../js/material.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0">
    <style>
        #view-source {
            position: fixed;
            display: block;
            right: 0;
            bottom: 0;
            margin-right: 40px;
            margin-bottom: 40px;
            z-index: 900;
        }
    </style>
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
            <div class="demo-content mdl-color--white mdl-shadow--4dp content mdl-color-text--grey-800 mdl-cell mdl-cell--8-col">
                <h3 id="heading">Datenschutzerklärung</h3>
                <p>
                    Das Bezahlsystem Fingerhut wurde durch Yasin Ekinci, Max Buchholz und Fabian Schmid erstellt. Bei der Nutzung der Fingerhut App und Webapp werden keine Personenbezogenen Daten gespeichert. Der Zugriff auf das Konto erfolgt einzig und allein über eine vierstellige Kontonummer sowie einer vierstelligen Pin, welche in keinster Weise mit dem Kontoinhaber in Verbindung zu bringen sind.
                </p>
                <p>
                    Zur Nutzung benötigt die Android App folgende Zugriffsrechte:
                </p>
                <ul>
                    <li>Internet</li>
                    <li>Kamera: Um QR- und Barcodes zur Authentifizierung und zum Produkte kaufen nutzen zu können benötigt die App Zugriff auf die Kamera des Gerätes.</li>
                </ul>
                <p>
                    Alle Eingaben die der Nutzer in der  Android App sowie Webapp selbst macht, also z.B. Verwendungszwecke werden auf unbestimmte Zeit gespeichert. Kann über diese Eingaben ein Personenbezug hergestellt werden liegt dies in der Verantwortung des Nutzers und wir übernehmen keine Haftung.
                </p>
                <p>
                    In der Android App wird der Firebase Service verwendet, dessen Datenschutzerklärung ist unter folgendem Link erreichbar:
                </p>
                <p><a name="https://www.google.com/policies/privacy/">https://www.google.com/policies/privacy/</a></p>
                <p>
                    Die einzige Möglichkeit dieser Datenschutzerklärung zu wiedersprechen ist diese Applikation nicht zu nutzen. Die vom Nutzer angegebenen Daten können im Nachhinein mindestens bis zum 03.03.2018 nicht gelöscht werden. Jegliche Anfragen der Nutzer zum Löschen ihrer Daten werden bis dahin also ignoriert.
                </p>
                <p>
                    Kontaktdaten: support@stromberg-gymnasium-saz.de
                </p>
                <p>
                    <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="window.location.href='https://fingerhut388.appspot.com'">
                        Alles klar!
                    </button>
                </p>
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
