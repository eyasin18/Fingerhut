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
    <title>Datenschutzerklärung</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}../css/material.green-light_green.min.css">
    <script defer src="${pageContext.request.contextPath}../js/material.min.js"></script>

</head>
<body>
    <div style="margin-left: 35%; margin-right: 35%; margin-top: 100px">
        <p>Datenschutzerkl&auml;rung</p>
        <p>Das Bezahlsystem Fingerhut wurde durch Yasin Ekinci, Max Buchholz<br />und Fabian Schmid erstellt. Bei der Nutzung der Fingerhut App und<br />Webapp werden keine Personenbezogenen Daten gespeichert. Der<br />Zugriff auf das Konto erfolgt einzig und allein &uuml;ber eine vierstellige<br />Kontonummer sowie einer vierstelligen Pin, welche in keinster Weise<br />mit dem Kontoinhaber in Verbindung zu bringen sind.<br />Zur Nutzung ben&ouml;tigt die Android App folgende Zugriffsrechte:<br /> Internet<br /> Kamera: Um QR- und Barcodes zur Authentifizierung und zum<br />Produkte kaufen nutzen zu k&ouml;nnen ben&ouml;tigt die App Zugriff auf<br />die Kamera des Ger&auml;tes.<br />Alle Eingaben die der Nutzer in der Android App sowie Webapp<br />selbst macht, also z.B. Verwendungszwecke werden auf unbestimmte<br />Zeit gespeichert. Kann &uuml;ber diese Eingaben ein Personenbezug<br />hergestellt werden liegt dies in der Verantwortung des Nutzers und<br />wir &uuml;bernehmen keine Haftung.<br />In der Android App wird der Firebase Service verwendet, dessen<br />Datenschutzerkl&auml;rung ist unter folgendem Link erreichbar:<br />https://www.google.com/policies/privacy/<br />Die einzige M&ouml;glichkeit dieser Datenschutzerkl&auml;rung zu<br />wiedersprechen ist diese Applikation nicht zu nutzen. Die vom Nutzer<br />angegebenen Daten k&ouml;nnen im Nachhinein mindestens bis zum<br />03.03.2018 nicht gel&ouml;scht werden. Jegliche Anfragen der Nutzer zum<br />L&ouml;schen ihrer Daten werden bis dahin also ignoriert.</p>
        <p>Kontaktdaten: support@stromberg-gymnasium- saz.de</p>
    </div>
</body>
</html>
