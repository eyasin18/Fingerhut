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
                    Das Bezahlsystem Fingerhut wurde durch Yasin Ekinci, Max Buchholz und Fabian Schmid erstellt. Bei der Nutzung der Fingerhut App und Webapp werden keine personenbezogenen Daten gespeichert. Der Zugriff auf das Konto erfolgt einzig und allein über eine vierstellige Kontonummer sowie einer vierstelligen Pin. Das Konto auf dem Server und der Bürger dem das Konto gehört stehen in keinem direkten Zusammenhang.
                </p>
                <p>
                    Zur Nutzung benötigt die Android App folgende Zugriffsrechte:
                </p>
                <ul>
                    <li>Internet</li>
                    <li>Kamera: Um QR- und Barcodes zur Authentifizierung und zum Produkte kaufen nutzen zu können benötigt die App Zugriff auf die Kamera des Gerätes.</li>
                </ul>
                <p>
                    Es werden folgende Daten gespeichert:
                </p>
                <ul>
                    <li>Bei Überweisungen:</li>
                        <ul>
                            <li>Betrag</li>
                            <li>Zeitpunkt</li>
                            <li>Verschlüsselte Eingabe des Empfängers sowie Sender Felds</li>
                            <li>Der Verschlüsselte Verwendungszweck</li>
                            <li>Der Typ der Überweisung</li>
                            <li>Kontonummern des Empfängers und Senders</li>
                        </ul>
                    <li>Bei Konten:</li>
                        <ul>
                            <li>Kontonummer</li>
                            <li>Kontostand</li>
                            <li>Unternehmen in denen der Besitzer arbeitet</li>
                            <li>Berechtigungen in diesen Unternehmen</li>
                            <li>Ob das Konto Grundeinkommen erhalten hat</li>
                            <li>Kontotyp</li>
                            <li>Anwesenheit und Anwesenheitszeit</li>
                            <li>Fehlgeschlagene Login Versuche</li>
                            <li>Das gehashte, also absolut unkenntlich gemachte, Passwort</li>
                            <li>Eine Liste der durchgeführten Transaktionen</li>
                            <li>Einkommen</li>
                            <li>Arbeitszeiten</li>
                        </ul>
                    <li>Bei Unternehmenskonten:</li>
                        <ul>
                            <li>Kontonummer</li>
                            <li>Kontostand</li>
                            <li>Listen der vergangenen Kontostände sowie die Zeitpunkte an denen sie gespeichert wurden</li>
                            <li>Kontonummer des Unternehmensleiters</li>
                            <li>Das gehashte Passwort, also absolut unkenntlich gemachte, Psaswort</li>
                            <li>Die Produkte des Unternehmens</li>
                            <li>Die Branche des Unternehmens</li>
                            <li>Eine Liste aller Überweisungen des Unternehmens</li>
                        </ul>
                    <li>Bei Kaufaufträgen:</li>
                        <ul>
                            <li>Eine Liste mit den Anzahlen der Produkte im Kaufauftrag</li>
                            <li>Die Kontonummer des Käufers</li>
                            <li>Ob der Kaufauftrag abgeschlossen ist</li>
                            <li>Der Zeitpunkt des Kaufauftrags</li>
                            <li>Eine Liste der Produktbarcodes</li>
                        </ul>
                    <li>Bei Produkten:</li>
                        <ul>
                            <li>Ob das Produkt kaufbar ist</li>
                            <li>Der Barcode des Produkts</li>
                            <li>Der Name des Produkts</li>
                            <li>Der Preis des Produkts</li>
                            <li>Ob der Kunde sich das Produkt selbst kaufen kann</li>
                            <li>Das Unternehmen welches das Produkt verlauft</li>
                        </ul>
                </ul>
                <p>
                    Alle Eingaben die der Nutzer in der  Android App sowie Web-app selbst macht, also z.B. Verwendungszwecke werden auf unbestimmte Zeit gespeichert. Kann über diese Eingaben ein Personenbezug hergestellt werden liegt dies in der Verantwortung des Nutzers und wir übernehmen keine Haftung.
                </p>
                <p>
                    In der Android App wird der Firebase Service verwendet, dessen Datenschutzerklärung ist unter folgendem Link erreichbar:
                </p>
                <p><a href="https://www.google.com/policies/privacy/" style="color: blue">https://www.google.com/policies/privacy/</a></p>
                <p>
                    Die einzige Möglichkeit dieser Datenschutzerklärung zu widersprechen ist diese Applikation nicht zu nutzen. Die vom Nutzer angegebenen Daten können im Nachhinein mindestens bis zum 03.03.2018 nicht gelöscht werden. Jegliche Anfragen der Nutzer zum Löschen ihrer Daten werden bis dahin also ignoriert.
                </p>
                <p>
                    Kontaktdaten: support@stromberg-gymnasium-saz.de
                </p>
                <p>
                    <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="goBack()">
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
<script>
    function goBack() {
        window.history.back();
    }
</script>