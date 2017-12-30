<%@ page import="de.repictures.fingerhut.Web.MainTools" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="de.repictures.fingerhut.Web.CompanyTools" %>

<%
    String code = request.getParameter("webstring");
    String accountnumber = request.getParameter("accountnumber");
    String companynumber = request.getParameter("companynumber");
    MainTools mainTools = new MainTools(accountnumber);
    if (!mainTools.isAuthentificated(code)){
        response.sendRedirect("https://fingerhut388.appspot.com/");
    }
    CompanyTools companyTools = new CompanyTools(accountnumber);
%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/company.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.green-light_green.min.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700" type="text/css">
    <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-drawer
            mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">
            <%=
                companyTools.getOwner(companynumber)
            %>
            </span>
        </div>
    </header>
    <div class="mdl-layout__drawer">
        <nav class="mdl-navigation">
            <a class="mdl-navigation__link" href="#purchase_orders">Kaufaufträge</a>
            <a class="mdl-navigation__link" href="#statistics">Statistiken</a>
            <a class="mdl-navigation__link" href="#products">Produkte</a>
            <a class="mdl-navigation__link" href="#employees">Mitarbeiter</a>
        </nav>
    </div>
    <main class="mdl-layout__content">
        <div class="page-content">
            <div class="mdl-grid">
                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="purchase_orders">
                    <h2 class="mdl-card__title-text" id="purchase_heading">Kaufaufträge</h2>
                    <table class="mdl-data-table mdl-js-data-table" id="purchase_table">
                        <thead>
                        <tr>
                            <th>Datum/Uhrzeit</th>
                            <th>Nummer</th>
                            <th>Betrag</th>
                            <th>Mehr Information</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                    <div class="mdl-card__menu">
                        <button class="mdl-button mdl-js-button mdl-button--fab mdl-js-ripple-effect mdl-button--colored" onclick="newTableEntry(10,1111,100);">
                            <i class="material-icons">add</i>
                        </button>
                    </div>
                </div>
                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="statistics">
                    <h2 class="mdl-card__title-text" id="statistics_heading">Statistiken</h2>
                    <div class="mdl-card__supporting-text">
                        Hier können sie die Statistiken ihres Unternehmens einsehen.
                    </div>

                </div>
                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="products">
                    <h2 class="mdl-card__title-text" id="products_heading">Produkte</h2>
                    <table class="mdl-data-table mdl-js-data-table">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Preis</th>
                            <th>Verkauft</th>
                            <th>Bearbeiten</th>
                        </tr>
                        </thead>
                        <tbody id="products_table">
                        </tbody>
                    </table>
                    <div class="mdl-card__menu">
                        <button class="mdl-button mdl-js-button mdl-button--fab mdl-js-ripple-effect mdl-button--colored">
                            <i class="material-icons">add</i>
                        </button>
                    </div>
                </div>
                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="employees">
                    <h2 class="mdl-card__title-text" id="employee_heading">Mitarbeiter</h2>
                    <table class="mdl-data-table mdl-js-data-table">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Kontonummer</th>
                            <th>Gehalt</th>
                            <th>Mehr Information</th>
                        </tr>
                        </thead>
                        <tbody id="employees_table">
                        </tbody>
                    </table>
                    <div class="mdl-card__menu">
                        <button class="mdl-button mdl-js-button mdl-button--fab mdl-js-ripple-effect mdl-button--colored">
                            <i class="material-icons">add</i>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
<script>
    //Funktion zum hinzufügen eines neuen Kaufauftrags
    function newTableEntry(date,account,amount){
        var table = document.getElementById("purchase_table");
        var row = table.insertRow(document.getElementById("purchase_table").rows.length);
        //var btn = document.createElement('input');
        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);
        var cell3 = row.insertCell(2);
        var cell4 = row.insertCell(3);
        /*btn.type = "button";
        btn.className = "btn";
        btn.classList.add("mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored");
        btn.value = "Bearbeiten";
        btn.onclick = edit();*/
        cell1.innerHTML = date;
        cell2.innerHTML = account;
        cell3.innerHTML = amount;
        cell4.innerHTML = "<button class='mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored' onclick='edit()'>Edit</button>";
    }

    function edit(){
        alert()
    }
</script>
</html>