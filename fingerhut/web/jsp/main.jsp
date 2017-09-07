<%@ page import="java.io.IOException" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>

<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<%@ page import="java.util.Objects" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="de.repictures.fingerhut.Web.Authenticate" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String code = request.getParameter("code");
    String accountnumber = request.getParameter("accountnumber");
    Authenticate authenticate = new Authenticate(accountnumber, code);
    if (!authenticate.isAuthentificated()){
        response.sendRedirect("https://fingerhut388.appspot.com/");
    }
    %>

<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/main.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.green-light_green.min.css">
    <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title mdl-color-text--white">Fingerhut</span>
        </div>
    </header>
    <main class="mdl-layout__content">
        <div class="content-grid mdl-grid">
            <div class="mdl-card mdl-shadow--4dp mdl-cell mdl-cell--12-col content-grid mdl-grid">
                <div class="mdl-card mdl-cell mdl-cell--6-col">
                    <div class="mdl-card__title">
                        <h3>Girokonto</h3>
                    </div>
                </div>
                <div class="mdl-card mdl-cell mdl-cell--6-col">
                    <div class="mdl-card__title mdl-color-text--green" id="kontostand">
                        <h1>Kontostand</h1>
                    </div>
                </div>
            </div>
            <div class="mdl-card mdl-shadow--4dp mdl-cell mdl-cell--6-col">
                <div class="mdl-card__title" id="transfer_heading">
                    <h3>Überweisen</h3>
                </div>
            </div>
            <div class="mdl-card mdl-shadow--4dp mdl-cell mdl-cell--6-col">
                <div class="mdl-card__title" id="company_heading">
                    <h3>Unternehmen</h3>
                </div>
                <div id="icon">
                    <img src="res/images/ic_business_black_48px.svg" alt="business_icon" style="width:128px;height:128px;">
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>

