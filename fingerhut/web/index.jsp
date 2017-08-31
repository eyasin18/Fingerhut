<%@ page import="java.io.IOException" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>

<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>

<%@ page import="de.repictures.fingerhut.Datastore.Accounts" %>
<%@ page import="de.repictures.fingerhut.Website.Login" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- Toller Kommentar --%>

<html>
  <head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="css/index.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">
    <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"><!-- Mobile-Device-Skalierung -->
  </head>
  <body>
    <!-- Header -->
    <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header mdl-layout--fixed-tabs">
      <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
          <!-- Titel -->
          <span class="mdl-layout-title">Bezahlsystem SaZ</span>
        </div>
        <div class="mdl-card__supporting-text">
          <form action="#">
            <div class="mdl-textfield mdl-js-textfield">
              <input class="mdl-textfield__input" type="text" id="username" pattern="-?[0-9]*(\.[0-9]+)?"/>
              <label class="mdl-textfield__label" for="username">Kontonummer</label>
              <span class="mdl-textfield__error">Eingabe muss eine Zahl sein!</span>
            </div>
            <div class="mdl-textfield mdl-js-textfield">
              <input class="mdl-textfield__input" type="password" id="userpass" pattern="-?[0-9]*(\.[0-9]+)?"/>
              <label class="mdl-textfield__label" for="userpass">Pin</label>
              <span class="mdl-textfield__error">Eingabe muss eine Zahl sein!</span>
            </div>
            <div class="mdl-card__actions mdl-card--border ">
              <br>
              <input class="mdl-button mdl-js-button mdl-button--raised mdl-button--accent" type="submit" value="Anmelden" id="submit_button">
            </div>
          </form>
        </div>
      </div>
    </main>
  </div>
  </body>
</html>

