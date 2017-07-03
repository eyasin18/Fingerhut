<%@ page import="java.io.IOException" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>

<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>

<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>

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
    <!-- Header -->
    <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header mdl-layout--fixed-tabs">
      <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
          <!-- Titel -->
          <span class="mdl-layout-title">Bezahlsystem SaZ</span>
        </div>
    </header>
      <main class="mdl-layout__content">
      </main>
      </div>
    </body>
</html>

