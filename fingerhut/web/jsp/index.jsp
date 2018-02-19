<!-- Imports-->
<%@ page import="java.io.IOException" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>

<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<%@ page import="java.util.Locale" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page errorPage="errorpage.jsp" %> <!-- gibt die Seite an, die im Fehlerfall angezeigt werden soll -->

<% //String number = null; Integer.parseInt(number); %> <!-- erzeugt Error zum testen der Errorpage -->

<html>
    <head>
        <meta charset="utf-8">
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/index.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}../css/icon.css">
            <link rel="stylesheet" href="${pageContext.request.contextPath}../css/material.green-light_green.min.css">
            <script defer src="../js/sjcl.js"></script>
            <script defer src="${pageContext.request.contextPath}../js/material.min.js"></script>
            <script type="application/javascript" src="../res/values/strings.js"></script>
            <title>Fingerhut</title>

        <meta name="viewport" content="width=device-width, initial-scale=1.0"><!-- Mobile-Device-Skalierung -->

    </head>
    <body onkeypress="return enterPressed(event);" class="center-cropped" style="background-image: url('/res/images/stromberg.jpg');">
        <div class="mdl-layout mdl-js-layout mdl-color--green-light_blue-100">
            <main class="mdl-layout__content">
                <div class="mdl-card mdl-shadow--6dp">
                    <div id="title_card" class="mdl-card__title mdl-color--primary mdl-color-text--white">
                        <h2 class="mdl-card__title-text">Fingerhut</h2>
                    </div>
                    <div class="mdl-card__supporting-text">
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" id="username_view">
                            <input class="mdl-textfield__input" type="text" id="username" pattern="-?[0-9]*(\.[0-9]+)?" />
                            <label class="mdl-textfield__label" for="username" id="username_label"></label>
                            <span class="mdl-textfield__error" id="username_error"></span>
                        </div>
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" id="userpass_view">
                            <input class="mdl-textfield__input" type="password" id="userpass" pattern="-?[0-9]*(\.[0-9]+)?" />
                            <label class="mdl-textfield__label" for="userpass" id="userpass_label"></label>
                            <span class="mdl-textfield__error" id="userpass_error"></span>
                        </div>
                        <div class="mdl-card__actions">
                            <br>
                            <button onclick="onButtonClick()" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent mdl-color-text--white" type="submit" id="submit_button"></button>
                        </div>
                        <div class="mdl-spinner mdl-js-spinner is-active" id="submit_spinner"></div>
                    </div>
                </div>
                <footer class="demo-footer mdl-mini-footer">
                    <div class="mdl-mini-footer--left-section">
                        <ul class="mdl-mini-footer--link-list">
                            <li><a target="_blank" href="http://stromberg-gymnasium-saz.de/">SaZ-Homepage</a></li>
                            <li><a target="_blank" href="https://fingerhut388.appspot.com/datenschutz">Datenschutzerklärung</a></li>
                        </ul>
                    </div>
                </footer>
            </main>
        </div>
        <script src="${pageContext.request.contextPath}../js/indexscript.js"></script>
  </body>
</html>

