<%--
  Created by IntelliJ IDEA.
  User: yasin
  Date: 29.06.17
  Time: 15:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <!-- Header mit Tabs -->
    <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header mdl-layout--fixed-tabs">
      <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
          <!-- Titel -->
          <span class="mdl-layout-title">Bezahlsystem SaZ</span>
        </div>
        <!-- Tabs -->
        <div class="mdl-layout__tab-bar mdl-js-ripple-effect">
          <a href="#fixed-tab-1" class="mdl-layout__tab is active">Home</a>
          <a href="#fixed-tab-2" class="mdl-layout__tab">Überweisen</a>
          <a href="#fixed-tab-3" class="mdl-layout__tab">Posteingang</a>
          <a href="#fixed-tab-4" class="mdl-layout__tab">Unternehmen</a>
          <a href="#fixed-tab-5" class="mdl-layout__tab">Einzahlen</a>
        </div>
      </header>
      <main class="mdl-layout__content">
        <section class="mdl-layout__tab-panel" id="fixed-tab-1">
          <div class="page-content">
            <ul class="demo-list-item mdl-list">
              <li class="mdl-list__item">
        <span class="mdl-list__item-primary-content">
          Platzhalter1
        </span>
              </li>
              <li class="mdl-list__item">
        <span class="mdl-list__item-primary-content">
          Platzhalter2
        </span>
              </li>
              <li class="mdl-list__item">
        <span class="mdl-list__item-primary-content">
          Platzhalter3
        </span>
              </li>
            </ul>
          </div>
        </section>
        <section class="mdl-layout__tab-panel" id="fixed-tab-2">
          <div class="page-content"></div>
        </section>
        <section class="mdl-layout__tab-panel" id="fixed-tab-3">
          <div class="page-content"></div>
        </section>
      </main>
    </div>
  </body>
</html>
