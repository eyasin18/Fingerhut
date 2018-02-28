<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- Import die Tools/Methoden/Funktionen aus den Java-Klassen -->
<%@ page import="de.repictures.fingerhut.Web.CompanyTools" %>
<%@ page import="de.repictures.fingerhut.Web.MainTools" %>
<%@ page import="de.repictures.fingerhut.Datastore.Product" %>
<%@ page import="de.repictures.fingerhut.Datastore.PurchaseOrder"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="de.repictures.fingerhut.Datastore.Tax" %>
<%@ page import="java.lang.reflect.Array" %>
<%@ page errorPage="errorpage.jsp" %> <!-- gibt die Seite an, die im Fehlerfall angezeigt werden soll -->
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>

<%
    String code = request.getParameter("webstring");
    String accountnumber = request.getParameter("accountnumber");
    String companynumber = request.getParameter("companynumber");
    MainTools mainTools = new MainTools(accountnumber);
    if (!mainTools.isAuthentificated(code)){
        response.sendRedirect("https://fingerhut388.appspot.com/");
    }
    CompanyTools companyTools = new CompanyTools(accountnumber);
    List<PurchaseOrder> purchaseOrders = companyTools.queryPurchasOrders(companynumber, request);

    double balance = companyTools.getBalance(companynumber);
    String balancestring = String.format("%.2f", balance);
%>

<!doctype html>
<html>
    <head>
        <meta charset="utf-8">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/company.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}../css/icon.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}../css/material.green-light_green.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}../css/css.css" type="text/css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}../css/getmdl-select.min.css">
        <script defer src="../js/sjcl.js"></script>
        <script defer src="${pageContext.request.contextPath}../js/getmdl-select.min.js"></script>
        <script defer src="${pageContext.request.contextPath}../js/material.min.js"></script>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}../res/images/favicon.ico">
        <link rel="apple-touch-icon" href="${pageContext.request.contextPath}../res/images/apple-touch-icon.png">
        <title>
            <%=
            companyTools.getOwner(companynumber)
            %>
        </title>
    </head>
<body>
    <div class="mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header">
        <!-- Der Header der den Firmenname anzeigt -->
        <header class="mdl-layout__header">
            <div class="mdl-layout__header-row">
                <span class="mdl-layout-title">
                    <!-- Name der Firma wird ermittelt -->
                <%=
                    companyTools.getOwner(companynumber)
                %>
                </span>
                <div class="mdl-layout-spacer"></div>
                <button onclick="signoff()" id="logout_button" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent mdl-color-text--white">
                    Ausloggen
                </button>
            </div>
        </header>

        <!--  Navigation -->
        <div class="mdl-layout__drawer">
            <nav class="mdl-navigation">
                <a class="mdl-navigation__link" href="#short_purchase_orders">Kaufaufträge</a>
                <a class="mdl-navigation__link" href="#statistics">Statistiken</a>
                <a class="mdl-navigation__link" href="#products">Produkte</a>
                <a class="mdl-navigation__link" href="#employees">Mitarbeiter</a>
            </nav>
        </div>

        <main class="mdl-layout__content">
            <div class="page-content">
                <div class="mdl-grid">
                    <!-- Kaufauftragskarte(Ausschnitt) -->
                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="short_purchase_orders">
                        <h4 class="mdl-typography--headline" id="short_purchase_heading">Kaufaufträge</h4>
                        <table class="mdl-data-table mdl-js-data-table" id="short_purchase_table">
                            <thead>
                            <tr>
                                <th>Datum/Uhrzeit</th>
                                <th>Käufer</th>
                                <th>Betrag</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                        <div class="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" onclick="addPurchase()" id="add_purchase_button_short">Hinzufügen</button>
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" onclick="showAllPurchaseOrders()">Alle anzeigen</button>
                        </div>
                    </div>
                    <!-- Kaufauftragskarte(Alle) -->
                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="purchase_orders">
                            <h4 class="mdl-typography--headline" id="purchase_heading">Kaufaufträge</h4>
                            <table class="mdl-data-table mdl-js-data-table" id="purchase_table">
                            <thead>
                                <tr>
                                    <th>Datum/Uhrzeit</th>
                                    <th>Käufer</th>
                                    <th>Betrag</th>
                                </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                        <div class="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" onclick="addPurchase()" id="add_purchase_button">Hinzufügen</button>
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" onclick="hideAllPurchaseOrders()">Verstecken</button>
                        </div>
                    </div>

                    <!-- Mehr Informationen zum Kaufauftrag Karte -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="purchase_order">
                        <div id="table_div">
                        </div>
                        <div class="wrapper">
                            <h6 id="confirm_error" class="title"></h6>
                        </div>
                        <div class ="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="backOrder()" id="back_button_order">Fertig</button>
                        </div>
                    </div>

                    <!-- Kaufauftrag hinzufügen Karte -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="add_purchase">
                        <div class="wrapper">
                            <div class="mdl-card__title-text mdl-textfield mdl-js-textfield mdl-textfield--floating-label wrapper" id="accountnumber_textfield">
                                <input class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?" id="add_purchase_accountnumber_textfield">
                                <label class="mdl-textfield__label" for="add_purchase_accountnumber_textfield">Kontonummer</label>
                                <span class="mdl-textfield__error" id="accountnumber_error"></span>
                            </div>
                        </div>
                        <div class="wrapper">
                            <div class="mdl-card__title-text mdl-textfield mdl-js-textfield mdl-textfield--floating-label wrapper">
                                <input class="mdl-textfield__input" type="password" pattern="-?[0-9]*(\.[0-9]+)?" id="add_purchase_pin_textfield">
                                <label class="mdl-textfield__label" for="add_purchase_pin_textfield">Pin des Käufers</label>
                                <span class="mdl-textfield__error" id="pin_error"></span>
                            </div>
                        </div>
                        <div class="wrapper">
                            <h6 id="purchase_order_error" class="title"></h6>
                        </div>
                        <div id ="add_purchase_div">

                        </div>
                        <div class="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" onclick="addProductToPurchase()" id="add_product_to_purchase_button">Hinzufügen</button>
                        </div>
                        <h6 class="title wrapper" id="purchase_order_price_sum">Preis (brutto): </h6>
                        <h6 class="title wrapper" id="tax">Mehrwertsteuer: <%=Tax.getVAT() %> %</h6>
                        <h6 class="title wrapper" id="purchase_order_taxable">Preis (netto): </h6>
                        <div class="wrapper">
                                <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" onclick="addPurchaseToTable()" id="finish_button">
                                    Fertig
                                </button>
                                <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" id="cancel_purchase_button" onclick="cancelPurchase()">
                                    Abbrechen
                                </button>
                        </div>
                    </div>

                    <!-- Produkte zu Kaufauftrag hinzufügen Karte -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="add_product_to_purchase">
                        <h4 class="mdl-typography--headline" id="add_product_to_purchase_heading">Neues Produkt zum Kaufauftrag hinzufügen</h4>
                        <div class="wrapper">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?" id="product_amount_field">
                                <label class="mdl-textfield__label" for="product_amount_field">Anzahl</label>
                                <span class="mdl-textfield__error" id="product_amount_error"></span>
                            </div>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select getmdl-select__fix-height" id="dropdown_wrapper">
                                <input type="text" value="" class="mdl-textfield__input" id="dropdown_product_field"
                                       readonly>
                                <input type="hidden" value="" name="dropdown_field">
                                <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                                <label for="dropdown_product_field" class="mdl-textfield__label">Produkt</label>
                                <ul for="dropdown_field" class="mdl-menu mdl-menu--bottom-left mdl-js-menu" id="dropdown_list">
                                </ul>
                            </div>
                        </div>
                        <div class="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" id="add_to_purchase_button" onclick="addProductToPurchaseTable()">
                                Hinzufügen
                            </button>
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" id="cancel_add_t_purchase_button" onclick="cancelProductToPurchase()">
                                Abbrechen
                            </button>
                        </div>
                    </div>

                    <!-- Hier beginnt der Teil der für die Karte Statistiken zuständig ist -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="statistics">
                        <h4 class="mdl-typography--headline" id="statistics_heading">Statistiken</h4>
                        <div class="mdl-card__supporting-text">
                           <h3 id="company_balance">Kontostand ihres Unternehmens: <%= balancestring %> S</h3>
                        </div>
                    </div>

                    <!-- Hier beginnt der Teil der für die Karte Produkte zuständig ist ist -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="products">
                        <h4 class="mdl-typography--headline" id="products_heading">Produkte</h4>
                        <table class="mdl-data-table mdl-js-data-table" id="producttable">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Produktcode</th>
                                <th>Preis</th>
                            </tr>
                            </thead>
                            <tbody id="products_table">
                                <%
                                    List<Product> products = companyTools.querySellingProducts(companynumber);
                                    for(Product product : products){
                                        String nameStr = product.getName();
                                        double priceStr = product.getPrice();
                                        String codeStr = product.getCode();
                                %>
                                <tr onclick="editProducts(this.rowIndex)">
                                    <th><%= nameStr %></th>
                                    <th><%= codeStr %></th>
                                    <th><%= priceStr + " S"%></th>
                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                        <div class="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" onclick="addProduct()" id="add_product_button">Hinzufügen</button>
                        </div>
                    </div>
                    <!-- Karte zum editieren eines Produkts-->
                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="product">
                        <div class="wrapper">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input id="textfield1" class="mdl-textfield__input" type="text">
                                <label class="mdl-textfield__label" for="textfield1" id="label1">Name</label>
                                <span class="mdl-textfield__error"></span>
                            </div>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input id="textfield2" class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?[^-]">
                                <label class="mdl-textfield__label" for="textfield2" id="label2">Preis</label>
                                <span class="mdl-textfield__error">Eingabe muss eine Zahl sein!</span>
                            </div>
                            <div>
                                <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-1">
                                    <input type="checkbox" id="checkbox-1" class="mdl-checkbox__input" value="true">
                                    <span class="mdl-checkbox__label">Kunden können dieses Produkt selbst kaufen</span>
                                </label>
                                <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-2">
                                    <input type="checkbox" id="checkbox-2" class="mdl-checkbox__input" value="true">
                                    <span class="mdl-checkbox__label">Kunden können dieses Produkt kaufen</span>
                                </label>
                            </div>
                        </div>
                        <h6 id="edit_product_error"></h6>
                        <div class ="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="finishEditProduct()" id="back_button_product">Fertig</button>
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="cancelProduct()" id="cancel_button_product">Abbrechen</button>
                        </div>
                    </div>
                    <!-- Karte zum hinzufügen eines Produktes-->
                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="addProduct">
                        <div class="wrapper">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input id="textfield3" class="mdl-textfield__input" type="text">
                                <label class="mdl-textfield__label" for="textfield3" id="label3">7 - 13 Stelliger Code</label>
                                <span class="mdl-textfield__error"></span>
                            </div>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input id="textfield4" class="mdl-textfield__input" type="text">
                                <label class="mdl-textfield__label" for="textfield4" id="label4">Name</label>
                                <span class="mdl-textfield__error"></span>
                            </div>
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input id="textfield5" class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?[^-]">
                                <label class="mdl-textfield__label" for="textfield5" id="label5">Preis</label>
                                <span class="mdl-textfield__error">Eingabe muss eine Zahl sein!</span>
                            </div>
                            <div>
                                <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-3">
                                    <input type="checkbox" id="checkbox-3" class="mdl-checkbox__input" value="true">
                                    <span class="mdl-checkbox__label">Kunden können dieses Produkt selbst kaufen</span>
                                </label>
                                <label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="checkbox-4">
                                    <input type="checkbox" id="checkbox-4" class="mdl-checkbox__input" value="true">
                                    <span class="mdl-checkbox__label">Kunden können dieses Produkt kaufen</span>
                                </label>
                            </div>
                        </div>
                        <h6 id="add_product_error"></h6>
                        <div class="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" id="back_button_addProduct" onclick="finishAddProduct()">Fertig</button>
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="cancelProduct()" id="cancel_button_addProduct">Abbrechen</button>
                        </div>
                    </div>
                <!-- Mitarbeiter Karte -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="employees">
                        <h4 class="mdl-typography--headline" id="employee_heading">Mitarbeiter</h4>
                        <table class="mdl-data-table mdl-js-data-table" id="employees_table">
                            <thead>
                            <tr>
                                <th>Kontonummer</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>

                <!-- Mitarbeiter bearbeiten und mehr Informationen Karte -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="employee">
                        <h6 class="mdl-typography--title" id="employee_accountnumber">0004</h6>
                        <div class="wrapper">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                                <input class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?" id="bruttolohn" onchange="setNetWage()">
                                <label class="mdl-textfield__label" for="bruttolohn">Bruttolohn</label>
                                <span class="mdl-textfield__error">Eingabe muss eine Zahl sein!</span>
                            </div>
                            <h6 id="nettolohn">Nettolohn: </h6>
                        </div>
                        <div class="wrapper">
                            <h6 class="mdl-typography--title">Arbeitszeiten: </h6>
                        </div>
                        <div id="work_times_table_wrapper">
                        </div>
                            <div class ="wrapper">
                                <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="addNewWorkTime()" id="new_work_time_button">Arbeitszeit hinzufügen</button>
                            </div>
                        <div class="wrapper">
                            <h6 class="mdl-typography--title">Berechtigungen</h6>
                        </div>
                        <div class="wrapper">
                            <div id="checkbox_wrapper">
                            </div>
                            <h6 id="save_employee_changes_error" class="title"></h6>
                            <div class ="wrapper">
                                <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="saveEmployeeChanges()" id="save_employee_changes">Speichern</button>
                                <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="cancelEmployeeChanges()" id="cancel_employee_changes">Abbrechen</button>
                            </div>
                        </div>
                    </div>

                    <!-- Arbeitszeiten hinzufügen Karte -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="work_times">
                        <h4 class="mdl-typography--headline" id="work_time_heading">Arbeitszeiten hinzufügen</h4>
                        <div class="wrapper">
                            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select getmdl-select__fix-height" id="days_wrapper">
                                <input type="text" value="" class="mdl-textfield__input" id="days_input"
                                       readonly>
                                <input type="hidden" value="" name="days_dropdown">
                                <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                                <label for="days_input" class="mdl-textfield__label">Tag</label>
                                <ul for="days_dropdown" class="mdl-menu mdl-menu--bottom-left mdl-js-menu">
                                    <li class="mdl-menu__item">Mo</li>
                                    <li class="mdl-menu__item">Di</li>
                                    <li class="mdl-menu__item">Mi</li>
                                    <li class="mdl-menu__item">Do</li>
                                    <li class="mdl-menu__item">Fr</li>
                                    <li class="mdl-menu__item">Sa</li>
                                </ul>
                            </div>
                        </div>
                        <div class="wrapper">
                            <table id="add_work_times_table">
                                <tr>
                                    <td>
                                        <h6>Von</h6>
                                    </td>
                                    <td>
                                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select getmdl-select__fix-height" id="begin_hours_wrapper">
                                            <input type="text" value="" class="mdl-textfield__input" id="begin_hours_input"
                                                   readonly>
                                            <input type="hidden" value="" name="begin_hours_dropdown">
                                            <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                                            <label for="begin_hours_input" class="mdl-textfield__label">Stunde</label>
                                            <ul for="begin_hours_dropdown" class="mdl-menu mdl-menu--bottom-left mdl-js-menu">
                                                <li class="mdl-menu__item">08</li>
                                                <li class="mdl-menu__item">09</li>
                                                <li class="mdl-menu__item">10</li>
                                                <li class="mdl-menu__item">11</li>
                                                <li class="mdl-menu__item">12</li>
                                                <li class="mdl-menu__item">13</li>
                                                <li class="mdl-menu__item">14</li>
                                            </ul>
                                        </div>
                                    </td>
                                    <td>
                                        <h6>:</h6>
                                    </td>
                                    <td>
                                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select getmdl-select__fix-height" id="begin_minutes_wrapper">
                                            <input type="text" value="" class="mdl-textfield__input" id="begin_minutes_input"
                                                   readonly>
                                            <input type="hidden" value="" name="begin_minutes_dropdown">
                                            <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                                            <label for="begin_minutes_input" class="mdl-textfield__label">Minuten</label>
                                            <ul for="begin_minutes_dropdown" class="mdl-menu mdl-menu--bottom-left mdl-js-menu">
                                                <li class="mdl-menu__item">00</li>
                                                <li class="mdl-menu__item">30</li>
                                            </ul>
                                        </div>
                                    </td>
                                    <td>
                                        <h6>Uhr</h6>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <h6>Bis</h6>
                                    </td>
                                    <td>
                                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select getmdl-select__fix-height" id="end_hours_wrapper">
                                            <input type="text" value="" class="mdl-textfield__input" id="end_hours_input"
                                                   readonly>
                                            <input type="hidden" value="" name="end_hours_dropdown">
                                            <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                                            <label for="end_hours_input" class="mdl-textfield__label">Stunde</label>
                                            <ul for="end_hours_dropdown" class="mdl-menu mdl-menu--bottom-left mdl-js-menu">
                                                <li class="mdl-menu__item">09</li>
                                                <li class="mdl-menu__item">10</li>
                                                <li class="mdl-menu__item">11</li>
                                                <li class="mdl-menu__item">12</li>
                                                <li class="mdl-menu__item">13</li>
                                                <li class="mdl-menu__item">14</li>
                                                <li class="mdl-menu__item">15</li>
                                            </ul>
                                        </div>
                                    </td>
                                    <td>
                                        <h6>:</h6>
                                    </td>
                                    <td>
                                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select getmdl-select__fix-height" id="end_minutes_wrapper">
                                            <input type="text" value="" class="mdl-textfield__input" id="end_minutes_input"
                                                   readonly>
                                            <input type="hidden" value="" name="end_minutes_dropdown">
                                            <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                                            <label for="end_minutes_input" class="mdl-textfield__label">Minuten</label>
                                            <ul for="end_minutes_dropdown" class="mdl-menu mdl-menu--bottom-left mdl-js-menu">
                                                <li class="mdl-menu__item">00</li>
                                                <li class="mdl-menu__item">30</li>
                                            </ul>
                                        </div>
                                    </td>
                                    <td>
                                        <h6>Uhr</h6>
                                    </td>
                                </tr>
                            </table>
                            <h6 id="add_new_work_time_error" class="title"></h6>
                            <div class="wrapper">
                                <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="saveNewWorkTime()" id="save_new_work_time">Speichern</button>
                                <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="cancelNewWorkTime()" id="cancel_new_work_time">Abbrechen</button>
                            </div>
                        </div>
                    </div>

                    <!-- Arbeitszeiten bearbeiten Karte -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="edit_work_times">
                        <h4 class="mdl-typography--headline" id="edit_work_time_heading">Arbeitszeiten löschen?</h4>
                        <div class="wrapper">
                            <h6 id="edit_start_time"></h6>
                        </div>
                        <div class="wrapper">
                            <h6 id="edit_end_time"></h6>
                        </div>
                        <div class="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="cancelWorkTime()" id="cancel_work_time">Abbrechen</button>
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="deleteWorkTime()" id="delete_work_time">Löschen</button>
                        </div>
                    </div>
                </div>
            </div>
            <!--<div id="example" class="mdl-js-snackbar mdl-snackbar">
                <div class="mdl-snackbar__text"></div>
            </div>-->
        </main>
    </div>
</body>
<script src="${pageContext.request.contextPath}../js/pojo.js" ></script>
    <script>
    var accountnumber = "<%= accountnumber %>";
    var companynumber = "<%= companynumber %>";

    //Kaufaufträge betreffend
    var PurchaseOrder = document.getElementById("purchase_order");
    var PurchaseOrders = document.getElementById("purchase_orders");
    var ShortPurchaseOrders = document.getElementById("short_purchase_orders");
    var AddPurchase = document.getElementById("add_purchase");
    var AddProductToPurchase = document.getElementById("add_product_to_purchase");
    var decodedServerTime;
    var hashedSaltedPassword;
    var encodedServerTime;
    var accountnumber_textfield;
    var pin_textfield;
    var purchaseOrderPosition;

    //Statistiken betreffend
    var Statistics = document.getElementById("statistics");//Karte der Statistiken

    //Produkte betreffend
    var Products = document.getElementById("products");//Produkte Karte
    var Product = document.getElementById("product");//Produkte bearbeiten Karte
    var addProductCard = document.getElementById("addProduct");//Karte zum Hinzufügen von Produkten
    var currentProductPosition;//globale Variable zum Speichern von der Position des Produktes, welches gerade bearbeitet wird
    var checkbox1 = document.getElementById("checkbox-1");//Checkbox ob ein Produkt "Selfbuy" ist
    var checkbox2 = document.getElementById("checkbox-2");//Checkbox ob ein Proddukt "Buyable" ist
    var checkbox3 = document.getElementById("checkbox-3");
    var checkbox4 = document.getElementById("checkbox-4");
    var snackbarContainer = document.querySelector('#example');
    var addError = document.getElementById("add_product_error");
    var editError = document.getElementById("edit_product_error");

    //Mitarbeiter betreffend
    var Employees = document.getElementById("employees");
    var Employee = document.getElementById("employee");
    var WorkTimes = document.getElementById("work_times");
    var EditWorkTimes = document.getElementById("edit_work_times");
    var EmployeePosition;
    var TimePosition;
    var EmployeeError = document.getElementById("save_employee_changes_error");

    cancelProduct();
    PurchaseOrder.style.display = "none";
    PurchaseOrders.style.display = "none";
    AddPurchase.style.display = "none";
    AddProductToPurchase.style.display = "none";
    Employee.style.display = "none";
    WorkTimes.style.display = "none";
    EditWorkTimes.style.display = "none";

    //füllt den Productarray mit Produktobjekten die über die Attribute Name, Preis und Code verfügen
    var product = pojo('name', 'price', 'code', 'amount' , 'selfBuy', 'buyable');
    var productarray = [];
    var iterate = 0;
    <%
        for(int i = 0; i < products.size(); i++){
            %>
            var getName = '<%= products.get(i).getName() %>';
            var getPrice = <%= products.get(i).getPrice() %>;
            var getCode = '<%= products.get(i).getCode() %>';
            var getSelfBuy = <%= products.get(i).getSelfBuy() %>;
            var getBuyable = <%= products.get(i).getBuyable()%>;
            productarray[iterate] = product(
                getName,
                getPrice,
                getCode,
                getSelfBuy,
                getBuyable
            );
            iterate++;
            <%
        }
    %>

    //füllt den purchase_order_array mit Purchase Order Objekten die über die unten stehenden Attribute verfügen (fast alle properties der PurchaseOrder Entitäten)
    var purchase_order = pojo('buyer_accountnumber', 'completed', 'date_time', 'is_self_buy_list', 'number', 'product_codes_list');
    var purchase_order_array = [];
    var iterate1 = 0;
    <%
        for(int i = 0; i < purchaseOrders.size(); i++){
            %>
        var getBuyerAccountnumber = "<%= purchaseOrders.get(i).getBuyerAccountnumber() %>";
        var getCompleted = <%= purchaseOrders.get(i).getCompleted() %>;
        <%
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", request.getLocale());
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(sdf.parse(purchaseOrders.get(i).getDateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sdf = new SimpleDateFormat("E HH:mm", request.getLocale());
            String dateTimeStr = sdf.format(calendar.getTime()) + " Uhr";
        %>
        var getDateTime = '<%= dateTimeStr %>';
        var getIsSelfBuyList = <%= purchaseOrders.get(i).getIsSelfBuyList() %>;
        var getNumber = <%= purchaseOrders.get(i).getNumber() %>;
        var getPricesList = <%= purchaseOrders.get(i).getPricesList() %>;
        var getProductCodesList = [];
        <%
        for (String barcode : purchaseOrders.get(i).getProductCodesList()){
            %>
            getProductCodesList.push("<%=barcode%>");
            <%
        }
        %>
        purchase_order_array[iterate1] = purchase_order(
            getBuyerAccountnumber,
            getCompleted,
            getDateTime,
            getIsSelfBuyList,
            getNumber,
            getProductCodesList
        );
    iterate1++;
    <%
}
%>
    <%
        for(int iterator = 0; iterator < purchaseOrders.size(); iterator++){
            %>
            var iterator = <%= iterator%>;
            purchase_order_array[iterator].prices_list = <%= purchaseOrders.get(iterator).getPricesList() %>;
            purchase_order_array[iterator].amounts_list = <%= purchaseOrders.get(iterator).getAmountsList() %>;
        <%
        }
    %>

    //Füllt einen array für die Lohnsteuer
    var wageTaxes = [];
    <%
    for(int i = 0; i < companyTools.wageTaxes.size(); i++) {
      %> wageTaxes[<%=i%>] = <%= companyTools.wageTaxes.get(i)%>;<%;
    }
    %>

//erstellt ein Objekt mit den Daten aller Mitarbeiter

    var employeesJsonStr = '<%= companyTools.getEmployeesJsonStr(companynumber) %>';
    var employeesObject = JSON.parse(employeesJsonStr);

    <%List<Double> pricesList = new ArrayList<>();
    if (purchaseOrders.size() > 0) pricesList = purchaseOrders.get(0).getPricesList();%>

    if (purchase_order_array.length > 0) purchase_order_array[0].prices_list =  <%= pricesList %>;
    fillDropdown();
    fillShortPurchaseTable();
    fillPurchaseTable();
    fillEmployees();

    //Funktion zum hinzufügen eines neuen Kaufauftrags
    function newTableEntryOrder(date,account,amount){
        var table = document.getElementById("purchase_table");
        var row = table.insertRow(document.getElementById("purchase_table").rows.length);
        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);
        var cell3 = row.insertCell(2);
        var cell4 = row.insertCell(3);
        cell1.innerHTML = date;
        cell2.innerHTML = account;
        cell3.innerHTML = amount;
        cell4.innerHTML = "<button class='mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored' onclick='edit(this.parentNode.parentNode.rowIndex)'>Edit</button>";
    }

    function newTableEntryProduct(name,prize){
        var table = document.getElementById("producttable");
        var row = table.insertRow(document.getElementById("producttable").rows.length);
        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);
        var cell3 = row.insertCell(2);
        cell1.innerHTML = name;
        cell2.innerHTML = prize;
        cell3.innerHTML = "<button class='mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored' onclick='edit(this.parentNode.parentNode.rowIndex)'>Edit</button>";
    }

    function editPurchaseorders(position){
        purchaseOrderPosition = position;
        ShortPurchaseOrders.style.display = "none";
        PurchaseOrders.style.display = "none";
        PurchaseOrder.style.display = "block";
        AddPurchase.style.display = "none";
        AddProductToPurchase.style.display = "none";
        Statistics.style.display = "none";
        Employees.style.display = "none";
        Products.style.display = "none";
        if(purchase_order_array[position].completed){
            document.getElementById("table_div").innerHTML = "<table class=\"mdl-data-table mdl-js-data-table\" id=\"purchase_info_table\">\n" +
                "                                <thead>\n" +
                "                                    <tr>\n" +
                "                                        <th>Menge</th>\n" +
                "                                        <th>Produkt</th>\n" +
                "                                        <th>Barcode</th>\n" +
                "                                        <th>Preis</th>\n" +
                "                                    </tr>\n" +
                "                                </thead>\n" +
                "                                <tbody>\n" +
                "                                </tbody>\n" +
                "                            </table>";
        }
        else{
            document.getElementById("table_div").innerHTML = "<table class=\"mdl-data-table mdl-js-data-table\" id=\"purchase_info_table\">\n" +
                "                                <thead>\n" +
                "                                    <tr>\n" +
                "                                        <th>Menge</th>\n" +
                "                                        <th>Produkt</th>\n" +
                "                                        <th>Barcode</th>\n" +
                "                                        <th>Preis</th>\n" +
                "                                    </tr>\n" +
                "                                </thead>\n" +
                "                                <tbody>\n" +
                "                                </tbody>\n" +
                "                            </table>\n" +
                "<div id=\"confirm_div\" >\n" +
                "<h6 class=\"title wrapper\">Der Kaufauftrag ist noch nicht bestätigt.</h6>\n" +
                "                        <div class=\"wrapper\">\n" +
                "                            <button class=\"mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored\" onclick=\"confirmPurchaseOrder(purchaseOrderPosition)\" id=\"confirm_button\">Bestätigen</button>\n" +
                "                        </div>" +
                "                    </div>";
        }
        var purchaseOrderInfoTable = document.getElementById("purchase_info_table");
        for(var i = 0;i < purchase_order_array[position].product_codes_list.length; i++) {
            var row = purchaseOrderInfoTable.insertRow(document.getElementById("purchase_info_table").rows.length);
            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            var cell4 = row.insertCell(3);
            cell1.innerHTML = purchase_order_array[position].amounts_list[i];
            cell2.innerHTML = getNameThroughCode(purchase_order_array[position].product_codes_list[i]);
            cell3.innerHTML = purchase_order_array[position].product_codes_list[i];
            cell4.innerHTML = parseFloat(purchase_order_array[position].prices_list[i]) * parseFloat(purchase_order_array[position].amounts_list[i]) + " S";
        }
    }

    function confirmPurchaseOrder(position) {
        document.getElementById("confirm_button").disabled = "true";
        var JsonObject = JSON.stringify({
            "webstring": "<%=code%>",
            "buyeraccountnumber": String(purchase_order_array[position].buyer_accountnumber),
            "companynumber": "<%=companynumber%>",
            "purchaseOrderNumber": purchase_order_array[position].number,
            "selleraccountnumber": "<%=accountnumber%>",
            "isselfbuy": purchase_order_array[position].is_self_buy_list,
            "prices": purchase_order_array[position].prices_list,
            "amounts": purchase_order_array[position].amounts_list,
            "productcodes": purchase_order_array[position].product_codes_list
        });
        var url = "https://fingerhut388.appspot.com/completepurchaseorder?json=" + encodeURIComponent(JsonObject);
        console.log(url);
        httpAsync(url,"POST",2);
    }

    function editProducts(position){//Öffnet die Editieren-Karte mit den Werten der Ursprungskarte in Textfeldern
        editError.style.display = "none";
        currentProductPosition = position - 1;
        Products.style.display = "none";
        Product.style.display = "block";
        AddPurchase.style.display = "none";
        AddProductToPurchase.style.display = "none";
        ShortPurchaseOrders.style.display = "none";
        Employees.style.display = "none";
        Statistics.style.display = "none";
        var textfield1 = document.getElementById("textfield1");
        var textfield2 = document.getElementById("textfield2");
        //if (productarray[currentProductPosition].selfbuy){checkbox1.setAttribute("checked","checked");}
        //if (productarray[currentProductPosition].buyable){checkbox2.setAttribute("checked","checked");}
        textfield1.parentElement.classList.add("is-focused");
        textfield2.parentElement.classList.add("is-focused");
        textfield1.value = productarray[(position - 1)].name;
        textfield2.value = productarray[(position - 1)].price;
    }

    function cancelProduct(){// Funktion die beim Aufrufen der Abbrechenbuttons der Produkte-Karten ausgeführt wird
        Products.style.display = "flex";
        Product.style.display = "none";
        addProductCard.style.display = "none";
        ShortPurchaseOrders.style.display = "block";
        Employees.style.display = "block";
        Statistics.style.display = "block";
    }

    function backOrder() {
        PurchaseOrder.style.display = "none";
        ShortPurchaseOrders.style.display = "block";
        Statistics.style.display = "block";
        Employees.style.display = "block";
        Products.style.display = "block";
    }

    function finishEditProduct(){
        var name = document.getElementById("textfield1").value;
        var price = parseFloat(document.getElementById("textfield2").value);
        var theURL = "https://fingerhut388.appspot.com/updateproduct?" + "code=" + productarray[currentProductPosition].code + "&companynumber=" + companynumber;
        if (typeof name === "string" && name !== "") {
            if (typeof price === "number" && !isNaN(price) && price !== undefined){
                theURL += "&name=" + name;
                theURL += "&price=" + price;
                if (checkbox1.checked == true) {
                    theURL += "&selfbuy=true";
                }
                else {
                    theURL += "&selfbuy=false";
                }
                if (checkbox2.checked == true) {
                    theURL += "&buyable=true";
                }
                else {
                    theURL += "&buyable=false";
                }
                theURL = encodeURI(theURL);
                httpAsync(theURL, "POST", 3);
                editError.style.display = "none";
                cancelProduct();
            }
            else{
                editError.style.display = "block";
                editError.innerText = "Das Nummernfeld muss ausgefüllt werden"
            }
        }
        else{
            editError.style.display = "block";
            editError.innerText = "Das Namensfeld muss ausgefüllt werden"
        }
    }

    function finishAddProduct(){
        var barcode = document.getElementById("textfield3").value;
        var price = parseFloat(document.getElementById("textfield5").value);
        var name = document.getElementById("textfield4").value;
        if(barcode.length > 7 && barcode.length < 13) {
            if (typeof barcode === "string" && barcode !== "" && barcode !== null && barcode !== undefined) {
                if(typeof name === "string" && name !== "" && name !== null && name !== undefined && !isNaN(name)) {
                    if(typeof price === "number" && !(isNaN(price)) && price !== undefined && price !== null){
                        var usedURL = "https://fingerhut388.appspot.com/getproduct?";
                        usedURL += "code=" + barcode;
                        usedURL += "&accountnumber=" + companynumber;
                        usedURL += "&name=" + name;
                        usedURL += "&price=" + price;
                        if (checkbox3.checked = true){usedURL += "&selfbuy=true";}
                        else {usedURL += "&selfbuy=false";}
                        if (checkbox4.checked = true){usedURL += "&buyable=true";}
                        else {usedURL += "&buyable=false";}
                        usedURL = encodeURI(usedURL);
                        httpAsync(usedURL, "GET", 5);
                    }
                    else{
                        addError.style.display = "block";
                        addError.innerText = "Das Preis-Feld muss ausgefüllt werden";
                    }
                }
                else{
                    addError.style.display = "block";
                    addError.innerText = "Das Namens-Feld muss ausgefüllt werden";
                }
            }
            else{
                addError.style.display = "block";
                addError.innerText = "Das Barcode-Feld muss ausgefüllt werden";
            }
        }
        else{
            addError.style.display = "block";
            addError.innerText = "Der Barcode muss zwichen 7 und 13 Zeichen lang sein";
        }
    }

    //function addPurchaseOrderItem(position) {}

    function registerFCMTopicAsync() {
        var url = "https://iid.googleapis.com/iid/v1/" + registrationToken + "/rel/topics/<%= companynumber %>-shoppingRequests";
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.setRequestHeader("Content-Type", "application/json");
        xmlHttp.setRequestHeader("Authorization", "key=AAAATqpEAqE:APA91bHDNQ6rnzBLpMgpuM_FZyrArDP5Fdu8nYtlEwIJ6PIAKxzaaoEcp4X0NYMok3A-BCjbRrLoCMZWZauGjkZ1wyx7NuQxliu08cZUPz1CvK5JFp3U72IrBWWNqGNxJMljc6e6vlQD");
        xmlHttp.onreadystatechange = function() {
            console.log(xmlHttp.status);
            if (xmlHttp.readyState === 4 && xmlHttp.status === 200){
                console.log(xmlHttp.responseText);
            }
        };
        xmlHttp.open("POST", url, true); // true for asynchronous
        xmlHttp.send(null);
        PurchaseOrders.style.display = "none";
        ShortPurchaseOrders.style.display = "block";
        PurchaseOrder.style.display = "none";
        AddPurchase.style.display = "none";
        AddProductToPurchase.style.display = "none";
    }

    function addPurchase() {
        document.getElementById("purchase_order_error").innerText = "";
        AddPurchase.style.display = "inline-block";
        PurchaseOrders.style.display = "none";
        ShortPurchaseOrders.style.display = "none";
        PurchaseOrder.style.display = "none";
        AddProductToPurchase.style.display = "none";
        Statistics.style.display = "none";
        Products.style.display = "none";
        Employees.style.display = "none";
        document.getElementById("add_purchase_div").innerHTML = "<table class=\"mdl-data-table mdl-js-data-table\" id=\"add_purchase_table\">\n" +
            "                            <thead>\n" +
            "                                <tr>\n" +
            "                                    <th>Anzahl</th>\n" +
            "                                    <th>Produkt</th>\n" +
            "                                    <th>Preis</th>\n" +
            "                                </tr>\n" +
            "                            </thead>\n" +
            "                            <tbody id=\"add_purchase_table_body\">\n" +
            "\n" +
            "                            </tbody>\n" +
            "                        </table>"
    }

    function addProduct(){
        addError.style.display = "none";
        addProductCard.style.display = "block";
        Products.style.display = "none";
        AddPurchase.style.display = "none";
        AddProductToPurchase.style.display = "none";
        ShortPurchaseOrders.style.display = "none";
        Employees.style.display = "none";
        Statistics.style.display = "none";
        }

    function addProductToPurchase() {
        AddPurchase.style.display = "none";
        PurchaseOrders.style.display = "none";
        ShortPurchaseOrders.style.display = "none";
        PurchaseOrder.style.display = "none";
        AddProductToPurchase.style.display = "block";
    }

    function addProductToPurchaseTable() {
        var purchase_price_sum = 0;
        if (isNaN(document.getElementById("product_amount_field").value) || document.getElementById("product_amount_field").value < 1) {
            document.getElementById('product_amount_error').textContent = "Eingabe ist keine Zahl!";
        } else {
            if(document.getElementById("dropdown_product_field").value !== "") {
                AddPurchase.style.display = "flex";
                PurchaseOrders.style.display = "none";
                ShortPurchaseOrders.style.display = "none";
                PurchaseOrder.style.display = "none";
                AddProductToPurchase.style.display = "none";
                var table = document.getElementById("add_purchase_table");
                var product = document.getElementById("dropdown_product_field").value;
                var amount = document.getElementById("product_amount_field").value;
                var row = table.insertRow(document.getElementById("add_purchase_table").rows.length);
                var cell1 = row.insertCell(0);
                var cell2 = row.insertCell(1);
                var cell3 = row.insertCell(2);
                var tax = <%=Tax.getVAT() %>;
                cell2.innerHTML = product;
                cell1.innerHTML = amount;
                cell3.innerHTML = String(getPriceThroughName(product) * parseFloat(amount));
                document.getElementById("product_amount_field").value = "";
                    for (var r = 1, n = table.rows.length; r < n; r++) {
                            purchase_price_sum += parseFloat(table.rows[r].cells[2].innerHTML);
                    }
                document.getElementById("purchase_order_price_sum").innerText =  "Preis (brutto): " + String(purchase_price_sum) + "S";
                document.getElementById("tax").innerText = "Mehrwertsteuer: " + String(tax) + "%";
                document.getElementById("purchase_order_taxable").innerText =  "Preis (netto): " + String(purchase_price_sum + (purchase_price_sum * (tax / 100))) + "S";
            }
        }
    }


    function getPriceThroughName(name){//ermittelt den Preis eines Produktes indem der Name übergeben wird
        for(var j = 0; j < productarray.length; j++){
            if (name === productarray[j].name){
                return parseFloat(productarray[j].price);
            }
        }
    }

    function getNameThroughCode(code){//ermittelt den Name eines Produktes indem der Barcode übergeben wird
        for(var j = 0; j < productarray.length; j++){
            if (code === productarray[j].code){
                return productarray[j].name;
            }
        }
    }

    function getCodeThroughName(name) {//ermittelt den Code eines Produktes indem der Name übergeben wird
        for(var j = 0; j < productarray.length; j++){
            if (name === productarray[j].name){
                return productarray[j].code;
            }
        }
    }

    function getSelfBuyThroughName(name) {//ermittelt den SelfBuy Wert eines Produktes indem der Name übergeben wird
        for(var j = 0; j < productarray.length; j++){
            if (name === productarray[j].name){
                return productarray[j].selfBuy;
            }
        }
    }

    function fillDropdown(){
        var dropdown_list = document.getElementById("dropdown_list");
        for(var i = 0;i < productarray.length; i++){
            var line = document.createElement("li");
            line.classList.add("mdl-menu__item");
            line.innerHTML = productarray[i].name;
            dropdown_list.appendChild(line);
        }
    }

    function cancelPurchase() {
        AddPurchase.style.display = "none";
        PurchaseOrders.style.display = "none";
        ShortPurchaseOrders.style.display = "block";
        PurchaseOrder.style.display = "none";
        AddProductToPurchase.style.display = "none";
        Statistics.style.display = "flex";
        Products.style.display = "flex";
        Employees.style.display = "flex";
        document.getElementById("add_purchase_div").innerHTML = "<table class=\"mdl-data-table mdl-js-data-table\" id=\"add_purchase_table\">\n" +
            "                            <thead>\n" +
            "                                <tr>\n" +
            "                                    <th>Anzahl</th>\n" +
            "                                    <th>Produkt</th>\n" +
            "                                    <th>Preis</th>\n" +
            "                                </tr>\n" +
            "                            </thead>\n" +
            "                            <tbody id=\"add_purchase_table_body\">\n" +
            "\n" +
            "                            </tbody>\n" +
            "                        </table>";
        document.getElementById("purchase_order_price_sum").innerText =  "Preis (brutto):";
        document.getElementById("purchase_order_taxable").innerText =  "Preis (netto):";
        document.getElementById("add_purchase_accountnumber_textfield").value =  null;
        document.getElementById("add_purchase_accountnumber_textfield").parentElement.classList.remove("is-dirty");
        document.getElementById("add_purchase_pin_textfield").value =  null;
        document.getElementById("add_purchase_pin_textfield").parentElement.classList.remove("is-dirty");
    }

    function cancelProductToPurchase() {
        AddPurchase.style.display = "flex";
        PurchaseOrders.style.display = "none";
        ShortPurchaseOrders.style.display = "none";
        PurchaseOrder.style.display = "none";
        AddProductToPurchase.style.display = "none";
        document.getElementById("product_amount_field").value = "";

    }

    //Kaufaugträge hinzufügen

    function addPurchaseToTable() {
        accountnumber_textfield = document.getElementById("add_purchase_accountnumber_textfield").value;
        pin_textfield = document.getElementById("add_purchase_pin_textfield").value;
        if(document.getElementById("add_purchase_table").rows.length > 1){
            if((accountnumber_textfield.length === 4)&&(pin_textfield.length === 4)) {
                document.getElementById("purchase_order_error").innerText = "";
                document.getElementById("finish_button").disabled = true;
                document.getElementById("cancel_purchase_button").disabled = true;
                addNewPurchase();
            }
            else{
                document.getElementById("purchase_order_error").innerText = "Die Kontonummer oder die Pin haben nicht das richtige Format!";
            }
        }
        else{
            document.getElementById("purchase_order_error").innerText = "Es sind keine Produkte ausgewählt!";
        }
    }

    function getShoppingList(){
        var productCodesArray = [];
        var pricesArray = [];
        var isSelfBuyArray = [];
        var amountsArray = [];
        for (var i = 1; i < document.getElementById("add_purchase_table").rows.length; i++) {
            var name = document.getElementById("add_purchase_table").rows[i].cells[1].innerHTML;
            productCodesArray[i - 1] = getCodeThroughName(name);
            pricesArray[i - 1] = String(getPriceThroughName(name));
            isSelfBuyArray[i - 1] = "true";
            amountsArray[i - 1] = document.getElementById("add_purchase_table").rows[i].cells[0].innerHTML;
        }
        return JSON.stringify({
            "product_codes": productCodesArray,
            "prices_array": pricesArray,
            "is_self_buy": isSelfBuyArray,
            "amounts": amountsArray
        })
    }

    function httpAsync(theUrl, method, callerid) {
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() {
            if (xmlHttp.readyState === 4 && xmlHttp.status === 200){
                console.log(xmlHttp.responseText);
                if (method === "GET") processGetResponse(decodeURIComponent(xmlHttp.responseText), callerid);
                else if (method === "POST") processPostResponse(decodeURIComponent(xmlHttp.responseText), callerid);
            }
            else if (xmlHttp.readyState === 4 && xmlHttp.status === 206){
                document.getElementById("purchase_order_error").innerText = "Melde dich bitte erneut an!";
                document.getElementById("finish_button").disabled = false;
                document.getElementById("cancel_purchase_button").disabled = false;
            }
        };
        xmlHttp.open(method, theUrl, true); // true for asynchronous
        xmlHttp.send(null);
    }

    function processGetResponse(responseText, callerid) {
        switch (callerid) {
            case 1:
                var responseSplit = responseText.split("ò");
                var response = parseInt(responseSplit[0]);
                switch (response) {
                    case 3:
                        document.getElementById("purchase_order_error").innerText = "Dieser Account existiert nicht!";
                        document.getElementById("finish_button").disabled = false;
                        document.getElementById("cancel_purchase_button").disabled = false;
                        break;
                    case 2:
                        document.getElementById("purchase_order_error").innerText = "Der Käufer hat nicht genug Geld für das Produkt!";
                        document.getElementById("finish_button").disabled = false;
                        document.getElementById("cancel_purchase_button").disabled = false;
                        break;
                    case 1:document.getElementById("purchase_order_error").innerText = "Der Kaufauftrag wurde erfolgreich ausgeführt!";
                        document.getElementById("finish_button").disabled = false;
                        document.getElementById("cancel_purchase_button").disabled = false;
                        AddPurchase.style.display = "none";
                        PurchaseOrders.style.display = "none";
                        ShortPurchaseOrders.style.display = "block";
                        PurchaseOrder.style.display = "none";
                        AddProductToPurchase.style.display = "none";
                        Statistics.style.display = "flex";
                        Products.style.display = "flex";
                        Employees.style.display = "flex";
                        document.getElementById("purchase_order_price_sum").innerText =  "Preis (brutto):";
                        document.getElementById("purchase_order_taxable").innerText =  "Preis (netto):";
                        document.getElementById("add_purchase_accountnumber_textfield").value =  "";
                        document.getElementById("add_purchase_pin_textfield").value =  "";
                        break;
                    case -1:
                        document.getElementById("purchase_order_error").innerText = "Melde dich bitte erneut an!";
                        document.getElementById("finish_button").disabled = false;
                        document.getElementById("cancel_purchase_button").disabled = false;
                        break;
                }
                break;
            case 2:
                var hash = sjcl.hash.sha256.hash(pin_textfield);
                var hashHex = sjcl.codec.hex.fromBits(hash).toUpperCase();
                encodedServerTime = responseText.trim();
                decodedServerTime = decodeURIComponent(responseText.replace("+", " ").trim());
                var combinedString = hashHex + decodedServerTime;
                var hashedSaltedPasswordBits = sjcl.hash.sha256.hash(combinedString);
                hashedSaltedPassword = sjcl.codec.hex.fromBits(hashedSaltedPasswordBits).toUpperCase();
                var postUrlStr = "https://fingerhut388.appspot.com/confirmlogin?accountnumber="
                    + accountnumber_textfield
                    + "&sessionaccountnumber=<%=accountnumber%>&webstring=<%=code%>"
                    + "&password=" + hashedSaltedPassword + "&servertimestamp=" + encodedServerTime;
                httpAsync(postUrlStr,"POST",1);
                break;
            case 3:
                switch(responseText) {
                    case 0:
                        //TODO: vernünftige Fehlermeldung
                    case 1:
                        //TODO: vernünftige Erfolgsmeldung
                }
                break;
            case 4:
                window.location.replace("https://fingerhut388.appspot.com");
                break;
            case 5 : switch(parseInt(responseText)) {
                case 2:
                    console.log("Gibt's schon");
                    break;
                case 1:
                    addError.style.display = "none";
                    cancelProduct();
                    location.reload(true);
                    break;
            }
        }
    }

    function processPostResponse(responseText, callerid) {
        switch (callerid){
            case 1: switch (parseInt(responseText)){
                case 1:
                    var getUrl = "https://fingerhut388.appspot.com" + "/getshoppingrequest?code=<%=code%>&authaccountnumber=<%=accountnumber%>"
                        +"&accountnumber=" + accountnumber_textfield
                        + "&madebyuser=true"
                        + "&completed=true"
                        + "&companynumber=<%=companynumber%>&shoppinglist=" + encodeURIComponent(getShoppingList());
                    console.log(getUrl);
                    httpAsync(getUrl, "GET", 1);
                    break;
                case 2:
                    document.getElementById("purchase_order_error").innerText = "Melde dich bitte erneut an!";
                    document.getElementById("finish_button").disabled = false;
                    document.getElementById("cancel_purchase_button").disabled = false;
                    break;
                case 3:
                    document.getElementById("purchase_order_error").innerText = "Der Pin ist falsch!";
                    document.getElementById("finish_button").disabled = false;
                    document.getElementById("cancel_purchase_button").disabled = false;
                    break;
                case 4:
                    document.getElementById("purchase_order_error").innerText = "Der Account existiert nicht!";
                    document.getElementById("finish_button").disabled = false;
                    document.getElementById("cancel_purchase_button").disabled = false;
                    break;
            }
                break;
            case 2: switch (parseInt(responseText)){
                case 0:
                    document.getElementById("confirm_error").innerText = "Die Daten wurden nicht richtig übertragen!";
                    document.getElementById("confirm_button").disabled = "false";
                    break;
                case 1:
                    document.getElementById("confirm_error").innerText = "Der Kaufauftrag wurde bestätigt.";
                    document.getElementById("confirm_div").innerHTML = "";
                    break;
                case 2:
                    document.getElementById("confirm_error").innerText = "Bitte logge dich neu ein!";
                    document.getElementById("confirm_button").disabled = "false";
                    break;
                case 3:
                    document.getElementById("confirm_error").innerText = "Der Kunde hat nicht genug Geld um den Kaufauftrag abzuschließen!";
                    document.getElementById("confirm_button").disabled = "false";
                    break;
            }
                break;
            case 3: switch (parseInt(responseText)){
                case 0:
                    EmployeeError.innerText = "Die Daten wurden nicht richtig übergeben!";
                    document.getElementById("save_employee_changes").disabled = false;
                    break;
                case 1:
                    EmployeeError.innerText = "Bitte logge dich neu ein!";
                    document.getElementById("save_employee_changes").disabled = false;
                    break;
                case 2:
                    EmployeeError.innerText = "";
                    Employees.style.display = "block";
                    Employee.style.display = "none";
                    ShortPurchaseOrders.style.display = "block";
                    Statistics.style.display = "block";
                    Products.style.display = "block";
                    document.getElementById("save_employee_changes").disabled = false;
                    break;
                case 3:
                    EmployeeError.innerText = "Du hast nicht die Berechtigung Mitarbeiter zu ändern!";
                    document.getElementById("save_employee_changes").disabled = false;
                    break;
            }
            case 4:
                location.reload(true);
                break;
        }
    }

    function addNewPurchase() {
        var getUrlStr = "https://fingerhut388.appspot.com/confirmlogin?accountnumber="
            + accountnumber_textfield
            + "&sessionaccountnumber=<%=accountnumber%>&webstring=<%=code %>";
        httpAsync(getUrlStr, "GET", 2)
    }

    function showAllPurchaseOrders() {
        PurchaseOrders.style.display = "block";
        ShortPurchaseOrders.style.display = "none";
    }
    function hideAllPurchaseOrders() {
        PurchaseOrders.style.display = "none";
        ShortPurchaseOrders.style.display = "block";
        window.location.href = "#short_purchase_orders";
    }
    function fillShortPurchaseTable() {
        var table = document.getElementById("short_purchase_table");
        for(var i = 0; i<10; i++) {
            if (purchase_order_array[i] != null) {
                var row = table.insertRow(document.getElementById("short_purchase_table").rows.length);
                var cell1 = row.insertCell(0);
                var cell2 = row.insertCell(1);
                var cell3 = row.insertCell(2);
                var priceSum = 0;
                for (var j = 0; j < purchase_order_array[i].prices_list.length; j++) {
                    priceSum += (purchase_order_array[i].prices_list[j] * purchase_order_array[i].amounts_list[j]);
                }
                var priceSumStr = priceSum.toFixed(2) + " S";
                cell1.innerHTML = purchase_order_array[i].date_time;
                cell2.innerHTML = purchase_order_array[i].buyer_accountnumber;
                cell3.innerHTML = priceSumStr;
                row.onclick = function () {
                    editPurchaseorders(this.rowIndex - 1)
                };
                if (!purchase_order_array[i].completed) {
                    row.style.backgroundColor = "#8BC349";
                }
            }
            else{
                break;
            }
        }

    }
    function fillPurchaseTable() {
        var table = document.getElementById("purchase_table");
        if(purchase_order_array.length > 0 && purchase_order_array[0].prices_list != null){
            for(var i = 0; i<purchase_order_array.length; i++) {
                var row = table.insertRow(document.getElementById("purchase_table").rows.length);
                var cell1 = row.insertCell(0);
                var cell2 = row.insertCell(1);
                var cell3 = row.insertCell(2);
                var priceSum = 0;
                for (var j = 0; j < purchase_order_array[i].prices_list.length; j++) {
                    priceSum += (purchase_order_array[i].prices_list[j] * purchase_order_array[i].amounts_list[j]);
                }
                var priceSumStr = priceSum.toFixed(2) + " S";
                cell1.innerHTML = purchase_order_array[i].date_time;
                cell2.innerHTML = purchase_order_array[i].buyer_accountnumber;
                cell3.innerHTML = priceSumStr;
                row.onclick = function () {
                    editPurchaseorders(this.rowIndex - 1)
                };
                if (!purchase_order_array[i].completed) {
                    row.style.backgroundColor = "#8BC349";
                }
            }
        }
    }
    function fillEmployees() {
        console.log(employeesObject);
        var table = document.getElementById("employees_table");
        for(var i = 0; i< employeesObject.accountnumbers.length; i++) {
            var row = table.insertRow(document.getElementById("employees_table").rows.length);
            var cell1 = row.insertCell(0);
            cell1.innerHTML = employeesObject.accountnumbers[i];
            row.onclick = function(){editEmployee(this.rowIndex-1)};
        }
    }
    function editEmployee(position) {
        EmployeePosition = position;
        Employees.style.display = "none";
        Employee.style.display = "block";
        ShortPurchaseOrders.style.display = "none";
        PurchaseOrders.style.display = "none";
        Statistics.style.display = "none";
        Products.style.display = "none";
        var div = document.getElementById("work_times_table_wrapper");
        div.innerHTML = "<table class=\"mdl-data-table mdl-js-data-table\" id=\"work_times_table\">\n" +
            "                                <thead>\n" +
            "                                <tr>\n" +
            "                                    <th>Von</th>\n" +
            "                                    <th>Bis</th>\n" +
            "                                </tr>\n" +
            "                                </thead>\n" +
            "                                <tbody>\n" +
            "                                </tbody>\n" +
            "                            </table>";
        var table = document.getElementById("work_times_table");
        var bruttolohn = document.getElementById("bruttolohn");
        var nettolohn = document.getElementById("nettolohn");
        var employeeAccountnumber = document.getElementById("employee_accountnumber");
        employeeAccountnumber.innerText = employeesObject.accountnumbers[position];
        bruttolohn.parentElement.classList.add("is-focused");
        bruttolohn.value = employeesObject.wages[position];
        nettolohn.innerText = "Nettolohn: " + getNetWage(employeesObject.wages[position]) + " S";
        for(var i = 0; i < employeesObject.start_times[position].length; i++) {
            var row = table.insertRow(document.getElementById("work_times_table").rows.length);
            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            cell1.innerHTML = employeesObject.start_times[position][i];
            cell2.innerHTML = employeesObject.end_times[position][i];
            row.onclick = function(){editWorkTime(this.rowIndex)};
        }
        var index = employeesObject.accountnumbers.indexOf(accountnumber);
        var wrapper = document.getElementById("checkbox_wrapper");
        wrapper.innerHTML = "";
        for(var j = 0; j < employeesObject.features[index].length; j++){
            switch(employeesObject.features[index][j]){
                case 0:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_products\" checked><label for=\"manage_products\">Produkte bearbeiten</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_products\"><label for=\"manage_products\">Produkte bearbeiten</label><br>";
                    }
                    break;
                case 1:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_auth_codes\" checked><label for=\"manage_auth_codes\">Authentifizierungs QR-Codes lesen und schreiben</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_auth_codes\"><label for=\"manage_auth_codes\">Authentifizierungs QR-Codes lesen und schreiben</label><br>";
                    }
                    break;
                case 2:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_purchase_orders\" checked><label for=\"manage_purchase_orders\">Kaufaufträge</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_purchase_orders\"><label for=\"manage_purchase_orders\">Kaufaufträge</label><br>";
                    }
                    break;
                case 3:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_employees\" checked><label for=\"manage_employees\">Mitarbeiter verwalten</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_employees\"><label for=\"manage_employees\">Mitarbeiter verwalten</label><br>";
                    }
                    break;
                case 4:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_statistics\" checked><label for=\"manage_statistics\">Statistiken</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_statistics\"><label for=\"manage_statistics\">Statistiken</label><br>";
                    }
                    break;
                case 5:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_change\" checked><label for=\"manage_change\">Geld wechseln</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_change\"><label for=\"manage_change\">Geld wechseln</label><br>";
                    }
                    break;
                case 6:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_add_employees\" checked><label for=\"manage_add_employees\">Mitarbeiter hinzufügen</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_add_employees\"><label for=\"manage_add_employees\">Mitarbeiter hinzufügen</label><br>";
                    }
                    break;
                case 7:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_prepaid\" checked><label for=\"manage_prepaid\">Prepaidkonto hinzufügen</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"manage_prepaid\"><label for=\"manage_prepaid\">Prepaidkonto hinzufügen</label><br>";
                    }
                    break;
                case 8:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"scan_id\" checked><label for=\"scan_id\">Ausweise scannen</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"scan_id\"><label for=\"scan_id\">Ausweise scannen</label><br>";
                    }
                    break;
                case 9:
                    if(employeesObject.features[position].includes(employeesObject.features[index][j])){
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"item_import\" checked><label for=\"item_import\">Wareneinfuhr</label><br>";
                    }
                    else{
                        wrapper.innerHTML += "<input type=\"checkbox\" id=\"item_import\"><label for=\"item_import\">Wareneinfuhr</label><br>";
                    }
                    break;
            }
        }

    }
    function cancelEmployeeChanges() {
        Employees.style.display = "block";
        Employee.style.display = "none";
        ShortPurchaseOrders.style.display = "block";
        Statistics.style.display = "block";
        Products.style.display = "block";
        document.getElementById("save_employee_changes").disabled = false;
        document.getElementById("save_employee_changes_error").innerText = "";
    }
    function addNewWorkTime() {
        WorkTimes.style.display = "block";
        Employee.style.display = "none";
    }
    function cancelNewWorkTime() {
        WorkTimes.style.display = "none";
        Employee.style.display = "block";
    }
    function saveNewWorkTime() {
        var table = document.getElementById("work_times_table");
        var day = document.getElementById("days_input").value;
        var beginHours = document.getElementById("begin_hours_input").value;
        var beginMinutes = document.getElementById("begin_minutes_input").value;
        var endHours = document.getElementById("end_hours_input").value;
        var endMinutes = document.getElementById("end_minutes_input").value;
        var error = document.getElementById("add_new_work_time_error");
        if(beginHours !== "" && beginMinutes !== "" && endHours !== "" && endMinutes !== "" && day !== "") {
            if (parseInt(beginHours) <= parseInt(endHours)) {
                var row = table.insertRow(document.getElementById("work_times_table").rows.length);
                var cell1 = row.insertCell(0);
                var cell2 = row.insertCell(1);
                cell1.innerHTML = day + " " + beginHours + ":" + beginMinutes + "Uhr";
                cell2.innerHTML = day + " " + endHours + ":" + endMinutes + "Uhr";
                row.onclick = function(){editWorkTime(this.rowIndex)};
                WorkTimes.style.display = "none";
                Employee.style.display = "block";
            }
            else {
                error.innerText = "Die Startzeit muss kleiner als die Endzeit sein!";
            }
        }
        else{
            error.innerText = "Alle Felder müssen ausgefüllt sein!";
        }
    }
    function cancelWorkTime() {
        Employee.style.display = "block";
        EditWorkTimes.style.display = "none";
    }
    function deleteWorkTime() {
        Employee.style.display = "block";
        EditWorkTimes.style.display = "none";
        var table = document.getElementById("work_times_table");
        table.deleteRow(TimePosition);
    }
    function editWorkTime(position) {
        Employee.style.display = "none";
        EditWorkTimes.style.display = "block";
        TimePosition = position;
        var table = document.getElementById("work_times_table");
        var startTime = document.getElementById("edit_start_time");
        var endTime = document.getElementById("edit_end_time");
        startTime.innerHTML = "Von: " + table.rows[position].cells[0].innerHTML;
        endTime.innerHTML = "Bis: " + table.rows[position].cells[1].innerHTML;
    }
    function saveEmployeeChanges() {
        document.getElementById("save_employee_changes").disabled = true;
        var startTimesArray = [];
        var endTimesArray = [];
        var featuresArray = employeesObject.features[EmployeePosition];
        console.log(featuresArray);
        var table = document.getElementById("work_times_table");
        var wrapper = document.getElementById("checkbox_wrapper");
        var children = wrapper.children;
        for (var j = 0; j < children.length; j++) {
            if(!children[j].checked){
                switch (children[j].id){
                    case "manage_products":
                        var index = featuresArray.indexOf(0);
                        if (index !== -1) featuresArray.splice(index, 1);
                        break;
                    case "manage_auth_codes":
                        var index1 = featuresArray.indexOf(1);
                        if (index1 !== -1) featuresArray.splice(index1, 1);
                        break;
                    case "manage_purchase_orders":
                        var index2 = featuresArray.indexOf(2);
                        if (index2 !== -1) featuresArray.splice(index2, 1);
                        break;
                    case "manage_employees":
                        var index3 = featuresArray.indexOf(3);
                        if (index3 !== -1) featuresArray.splice(index3, 1);
                        break;
                    case "manage_statistics":
                        var index4 = featuresArray.indexOf(4);
                        if (index4 !== -1) featuresArray.splice(index4, 1);
                        break;
                    case "manage_change":
                        var index5 = featuresArray.indexOf(5);
                        if (index5 !== -1) featuresArray.splice(index5, 1);
                        break;
                    case "manage_add_employees":
                        var index6 = featuresArray.indexOf(6);
                        if (index6 !== -1) featuresArray.splice(index6, 1);
                        break;
                    case "manage_prepaid":
                        var index7 = featuresArray.indexOf(7);
                        if (index7 !== -1) featuresArray.splice(index7, 1);
                        break;
                    case "scan_id":
                        var index8 = featuresArray.indexOf(8);
                        if (index8 !== -1) featuresArray.splice(index8, 1);
                        break;
                    case "item_import":
                        var index9 = featuresArray.indexOf(9);
                        if (index9 !== -1) featuresArray.splice(index9, 1);
                        break;
                }
            }
            else{
                switch (children[j].id) {
                    case "manage_products":
                        featuresArray.push(0);
                        break;
                    case "manage_auth_codes":
                        featuresArray.push(1);
                        break;
                    case "manage_purchase_orders":
                        featuresArray.push(2);
                        break;
                    case "manage_employees":
                        featuresArray.push(3);
                        break;
                    case "manage_statistics":
                        featuresArray.push(4);
                        break;
                    case "manage_change":
                        featuresArray.push(5);
                        break;
                    case "manage_add_employees":
                        featuresArray.push(6);
                        break;
                    case "manage_prepaid":
                        featuresArray.push(7);
                        break;
                    case "scan_id":
                        featuresArray.push(8);
                        break;
                    case "item_import":
                        featuresArray.push(9);
                        break;
                }
            }
        }
        console.log(featuresArray);
        for(var i = 1; i < table.rows.length; i++){
            var startTimeString = table.rows[i].cells[0].innerHTML;
            var endTimeString = table.rows[i].cells[1].innerHTML;
            var startSplit = startTimeString.split(" ");
            var endSplit = endTimeString.split(" ");
            var startTimeSplit = startSplit[1].split(":");
            var endTimeSplit = endSplit[1].split(":");
            switch(startSplit[0]){
                case "Mo":
                    startTimesArray.push((parseInt(startTimeSplit[0]) * 60) + parseInt(startTimeSplit[1]));
                    endTimesArray.push((parseInt(endTimeSplit[0]) * 60) + parseInt(endTimeSplit[1]));
                    break;
                case "Di":
                    startTimesArray.push(1440 + (parseInt(startTimeSplit[0]) * 60) + parseInt(startTimeSplit[1]));
                    endTimesArray.push(1440 + (parseInt(endTimeSplit[0]) * 60) + parseInt(endTimeSplit[1]));
                    break;
                case "Mi":
                    startTimesArray.push((1440 * 2) + (parseInt(startTimeSplit[0]) * 60) + parseInt(startTimeSplit[1]));
                    endTimesArray.push((1440 * 2) + (parseInt(endTimeSplit[0]) * 60) + parseInt(endTimeSplit[1]));
                    break;
                case "Do":
                    startTimesArray.push((1440 * 3) + (parseInt(startTimeSplit[0]) * 60) + parseInt(startTimeSplit[1]));
                    endTimesArray.push((1440 * 3) + (parseInt(endTimeSplit[0]) * 60) + parseInt(endTimeSplit[1]));
                    break;
                case "Fr":
                    startTimesArray.push((1440 * 4) + (parseInt(startTimeSplit[0]) * 60) + parseInt(startTimeSplit[1]));
                    endTimesArray.push((1440 * 4) + (parseInt(endTimeSplit[0]) * 60) + parseInt(endTimeSplit[1]));
                    break;
                case "Sa":
                    startTimesArray.push((1440 * 5) + (parseInt(startTimeSplit[0]) * 60) + parseInt(startTimeSplit[1]));
                    endTimesArray.push((1440 * 5) + (parseInt(endTimeSplit[0]) * 60) + parseInt(endTimeSplit[1]));
                    break;

            }
        }
        var wage = document.getElementById("bruttolohn").value;
            if(!isNaN(wage) && parseFloat(wage) >= 1) {
                var jsonObject = JSON.stringify({
                    "accountnumber": employeesObject.accountnumbers[EmployeePosition],
                    "wage": parseFloat(wage).toFixed(2),
                    "start_times": startTimesArray,
                    "end_times": endTimesArray,
                    "features": featuresArray
                });
                var url = "https://fingerhut388.appspot.com/getemployee?companynumber=" + "<%=companynumber%>"
                + "&body=" + encodeURIComponent(jsonObject)
                + "&editoraccoutnumber=" + accountnumber
                + "&authstring=" + "<%=code%>";
                httpAsync(url, "POST", 3);
            }
            else{
                EmployeeError.innerText = "Der Bruttolohn muss mindestens einen Stromer betragen!";
            }
    }
    function getNetWage(grossWage) {
        var fractionalPart = grossWage%1;
        var integralPart = grossWage-fractionalPart;

        //Prozentsatz berechnen
        var integralPercentage = 0;
        for (var i = 0; i < integralPart; i++){
            if(i < wageTaxes.length) integralPercentage += wageTaxes[i];
            else integralPercentage += 100;
        }
        integralPercentage = (integralPercentage/integralPart);
        var fractionPercentage = 0;
        if (integralPart < wageTaxes.length) fractionPercentage = wageTaxes[integralPart];
        else fractionPercentage = 100;

        var tax = ((integralPart * (integralPercentage/100)) + (fractionalPart * (fractionPercentage/100)));
        return (grossWage - tax).toFixed(2);
    }
    //Nettogehalt eintragen
    function setNetWage() {
        var wage = document.getElementById("bruttolohn").value;
        if(!isNaN(wage) && parseFloat(wage) >= 1) {
            document.getElementById("nettolohn").innerText = "Nettolohn: " + getNetWage(parseFloat(wage)) + " S";
        }
        else{
            EmployeeError.innerText = "Der Bruttolohn muss mindestens einen Stromer betragen!";
        }
    }
    function signoff(){
        var theurl =  "https://fingerhut388.appspot.com/signoff?accountnumber=<%= accountnumber %>&webstring=<%= code %>";
        httpAsync(theurl, "GET",4);
    }
    </script>
</html>