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


<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String code = request.getParameter("code");
    String accountnumber = request.getParameter("accountnumber");
    MainTools mainTools = new MainTools(accountnumber, code);
    if (!mainTools.isAuthentificated()){
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
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header mdl-layout--fixed-tabs">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <!-- Title -->
            <span class="mdl-layout-title mdl-color-text--white">Fingerhut</span>
            <div class="mdl-layout-spacer"></div>
            <form action="https://2-dot-fingerhut388.appspot.com/">
                <input type="submit" value="Ausloggen" id="logout_button" class= "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent mdl-color-text--white"/>
            </form>
        </div>
        <!-- Tabs -->
        <div class="mdl-layout__tab-bar mdl-js-ripple-effect">
            <a href="#scroll-tab-1" class="mdl-layout__tab is-active mdl-color-text--white">Kontoübersicht</a>
            <a href="#scroll-tab-2" class="mdl-layout__tab mdl-color-text--white">Überweisen</a>
            <a href="#scroll-tab-3" class="mdl-layout__tab mdl-color-text--white">Unternehmen</a>>
        </div>
    </header>
    <main class="mdl-layout__content">
        <section class="mdl-layout__tab-panel is-active" id="scroll-tab-1">
            <div class="page-content">
                <div class="content-grid mdl-grid">
                    <div class="mdl-card mdl-shadow--4dp mdl-cell mdl-cell--12-col content-grid mdl-grid">
                        <div class="mdl-card mdl-cell mdl-cell--6-col">
                            <div class="mdl-card__title">
                                <script>
                                    <%
                                    String balance = mainTools.getBalance(accountnumber);
                                    %>
                                </script>
                                <h3>Kontostand: <%= balance %></h3>
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
                        <form action="#">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" id="receiver">
                                <label class="mdl-textfield__label" for="receiver">Begünstigter</label>
                            </div>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?" id="accountnumber">
                                <label class="mdl-textfield__label" for="accountnumber">Kontonummer</label>
                                <span class="mdl-textfield__error">Eingabe muss eine Zahl sein!</span>
                            </div><br>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?" id="amount">
                                <label class="mdl-textfield__label" for="amount">Betrag</label>
                                <span class="mdl-textfield__error">Eingabe muss eine Zahl sein!</span>
                            </div>
                            <br>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <textarea class="mdl-textfield__input" type="text" rows="2" id="usage" ></textarea>
                                <label class="mdl-textfield__label" for="usage">Verwendungszweck</label>
                            </div>
                            <div class="mdl-card__actions">
                                <a class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white">
                                    Überweisen
                                </a>
                            </div>
                        </form>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <section class="mdl-layout__tab-panel" id="scroll-tab-3">
            <div class="page-content">
                <div class="mdl-card mdl-shadow--4dp mdl-cell mdl-cell--12-col">
                    <div class="mdl-card__title" id="company_heading">
                        <h2>Unternehmen</h2>
                    </div>
                    <div id="business_icon">
                        <img src="../res/images/ic_business_black_48px.svg" alt="business_icon" style="width:128px;height:128px;">
                    </div>
                </div>
            </div>
        </section>
    </main>
</div>
</body>
</html>