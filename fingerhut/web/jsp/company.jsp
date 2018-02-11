<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- Import die Tools/Methoden/Funktionen aus den Java-Klassen -->
<%@ page import="de.repictures.fingerhut.Web.CompanyTools" %>
<%@ page import="de.repictures.fingerhut.Web.MainTools" %>
<%@ page import="de.repictures.fingerhut.Datastore.Product" %>
<%@ page import="de.repictures.fingerhut.Datastore.PurchaseOrder" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
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
        <link rel="manifest" href="../json/manifest.json">
        <script defer src="../js/sjcl.js"></script>
        <script defer src="${pageContext.request.contextPath}../js/getmdl-select.min.js"></script>
        <script defer src="${pageContext.request.contextPath}../js/material.min.js"></script>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
            </div>
        </header>

        <!--  Navigation -->
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
                            <%
                                PurchaseOrder[] purchaseOrders = companyTools.queryPurchasOrders(companynumber, request);
                                for (PurchaseOrder purchaseOrder : purchaseOrders){
                                    //Preis berechnen
                                    double priceSum = 0.0;
                                    List<Long> amountsList = purchaseOrder.getAmountsList();
                                   List<Double> pricesList = purchaseOrder.getPricesList();
                                   if(amountsList!=null && pricesList!=null)
                                         for (int o = 0; o < amountsList.size(); o++){
                                            priceSum += (amountsList.get(o) * pricesList.get(o));
                                         }
                                    String priceSumStr = new DecimalFormat("0.00").format(priceSum) + " S";
                                    //DateTime anpassen
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", request.getLocale());
                                    Calendar calendar = Calendar.getInstance();
                                    try {
                                        calendar.setTime(sdf.parse(purchaseOrder.getDateTime()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    sdf = new SimpleDateFormat("E HH:mm", request.getLocale());
                                    String dateTimeStr = sdf.format(calendar.getTime()) + " Uhr";
                            %>
                                <tr onclick="editPurchaseorders(this.rowIndex-1)">
                                    <th><%= dateTimeStr %></th>
                                    <th><%= purchaseOrder.getBuyerAccountnumber() %></th>
                                    <th><%= priceSumStr %></th>
                                </tr>
                            <%
                                }
                            %>
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
                           Hier können sie die Statistiken ihres Unternehmens einsehen.
                        </div>
                    </div>

                    <!-- Hier beginnt der Teil der für die Karte Produkt zuständig ist ist -->

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="products">
                        <h4 class="mdl-typography--headline" id="products_heading">Produkte</h4>
                        <table class="mdl-data-table mdl-js-data-table" id="producttable">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Preis</th>
                            </tr>
                            </thead>
                            <tbody id="products_table">
                                <%
                                    Product[] products = companyTools.querySellingProducts(companynumber);
                                    for(Product product : products){
                                        String nameStr = product.getName();
                                        double priceStr = product.getPrice();
                                %>
                                <tr onclick="editProducts(this.rowIndex)">
                                    <th><%= nameStr %></th>
                                    <th><%= priceStr + " S"%></th>
                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                        <div class="mdl-card__menu">
                            <button class="mdl-button mdl-js-button mdl-button--fab mdl-js-ripple-effect mdl-button--colored">
                                <i class="material-icons">add</i>
                            </button>
                        </div>
                    </div>

                    <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="product">
                        <table class="mdl-data-table mdl-js-data-table" id="product_info_table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Preis</th>
                                </tr>
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                        <div class ="wrapper">
                            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="backProduct()" id="back_button_product">Fertig</button>
                        </div>
                    </div>

                <!-- Hier beginnt der Teil der für die Karte Mitarbeiter zuständig ist -->

                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="employees">
                    <h4 class="mdl-typography--headline" id="employee_heading">Mitarbeiter</h4>
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
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
<script src="${pageContext.request.contextPath}../js/pojo.js" ></script>
    <script>
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


    //Statistiken betreffend
    var Statistics = document.getElementById("statistics");

    //Produkte betreffend
    var Products = document.getElementById("products");
    var Product = document.getElementById("product");

    //Mitarbeiter betreffend
    var Employees = document.getElementById("employees");

    PurchaseOrder.style.display = "none";
    PurchaseOrders.style.display = "none";
    ShortPurchaseOrders.style.display = "block";
    AddPurchase.style.display = "none";
    AddProductToPurchase.style.display = "none";
    Product.style.display = "none";
    Employees.style.display = "block";
    Statistics.style.display = "block";


    //füllt den Productarray mit Produktobjekten die über die Attribute Name, Preis und Code verfügen
    var product = pojo('name', 'price', 'code', 'amount' , 'selfBuy');
    var productarray = [];
    var iterate = 0;
    <%
        for(int i = 0; i < products.length; i++){
            %>
            var getName = '<%= products[i].getName() %>';
            var getPrice = <%= products[i].getPrice() %>;
            var getCode = <%= products[i].getCode() %>;
            var getSelfBuy = <%= products[i].getSelfBuy() %>;
            productarray[iterate] = product(
                getName,
                getPrice,
                getCode,
                getSelfBuy
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
        for(int i = 0; i < purchaseOrders.length; i++){
            %>
        var getBuyerAccountnumber = "<%= purchaseOrders[i].getBuyerAccountnumber() %>";
        var getCompleted = <%= purchaseOrders[i].getCompleted() %>;
        var getDateTime = '<%= purchaseOrders[i].getDateTime() %>';
        var getIsSelfBuyList = <%= purchaseOrders[i].getIsSelfBuyList() %>;
        var getNumber = <%= purchaseOrders[i].getNumber() %>;
        var getPricesList = <%= purchaseOrders[i].getPricesList() %>;
        var getProductCodesList = <%= purchaseOrders[i].getProductCodesList() %>;
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
        for(int iterator = 0; iterator < purchaseOrders.length; iterator++){
            %>
            var iterator = <%= iterator%>;
            purchase_order_array[iterator].prices_list = <%= purchaseOrders[iterator].getPricesList() %>;
            purchase_order_array[iterator].amounts_list = <%= purchaseOrders[iterator].getAmountsList() %>;
        <%
        }
    %>
    fillDropdown();
    fillShortPurchaseTable();
    purchase_order_array[0].prices_list = <%= purchaseOrders[0].getPricesList() %>;



    /*if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('../js/firebase-messaging-sw.js', { scope: '/js/' }).then(function(reg) {

            if(reg.installing) {
                console.log('Service worker installing');
            } else if(reg.waiting) {
                console.log('Service worker installed');
            } else if(reg.active) {
                console.log('Service worker active');
            }

        }).catch(function(error) {
            // registration failed
            console.log('Registration failed with ' + error);
        });

    var config = {
        apiKey: "AIzaSyCDc9cZesVuUdSgb1eJiTv1Pj_Rq3BzFTA",
        authDomain: "fingerhut388.firebaseapp.com",
        databaseURL: "https://fingerhut388.firebaseio.com",
        projectId: "fingerhut388",
        storageBucket: "fingerhut388.appspot.com",
        messagingSenderId: "337864032929"
    };

    firebase.initializeApp(config);
    var messaging = firebase.messaging();
    var registrationToken;

    messaging.getToken()
        .then(function(currentToken) {
            if (currentToken) {
                console.log("Token received: " + currentToken);
                registrationToken = currentToken;
            } else {
                // Show permission request.
                console.log('No Instance ID token available. Request permission to generate one.');
                messaging.requestPermission()
                    .then(function() {
                        console.log('Notification permission granted.');
                        console.log("Token: " + messaging.getToken());
                        // TODO(developer): Retrieve an Instance ID token for use with FCM.
                        // [START_EXCLUDE]
                        // In many cases once an app has been granted notification permission, it
                        // should update its UI reflecting this.
                        resetUI();
                        // [END_EXCLUDE]
                    })
                    .catch(function(err) {
                        console.log('Unable to get permission to notify.', err);
                    });
                //messaging.refreshAuthToken();
                console.log("Token received: " + currentToken);
            }
        })
        .catch(function(err) {
            console.log('An error occurred while retrieving token. ', err);
        });

    // Callback fired if Instance ID token is updated.
    messaging.onTokenRefresh(function() {
        messaging.getToken()
            .then(function(refreshedToken) {
                console.log('Token refreshed.');
                console.log("Refreshed token: " + refreshedToken);
            })
            .catch(function(err) {
                console.log('Unable to retrieve refreshed token ', err);
            });
    });

    messaging.onMessage(function (payload) {
        console.log("Message received", payload);
    });
    }*/

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
        ShortPurchaseOrders.style.display = "none";
        PurchaseOrders.style.display = "none";
        PurchaseOrder.style.display = "block";
        AddPurchase.style.display = "none";
        AddProductToPurchase.style.display = "none";
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
                "                            </table>" +
                "<h6 class=\"title wrapper\">Der Kaufauftrag ist noch nicht bestätigt.</h6>\n" +
                "                        <div class=\"wrapper\">\n" +
                "                            <button class=\"mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored\" onclick=\"confirmPurchaseOrder()\" id=\"confirm_button\">Bestätigen</button>\n" +
                "                        </div>";
        }
        var purchaseOrderInfoTable = document.getElementById("purchase_info_table");
        console.log(purchase_order_array[position].product_codes_list.length);
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
    function confirmPurchaseOrder() {

    }
    function editProducts(position){
        Products.style.display = "none";
        Product.style.display = "block";
        AddPurchase.style.display = "none";
        AddProductToPurchase.style.display = "none";
        ShortPurchaseOrders.style.display = "none";
        Employees.style.display = "none";
        Statistics.style.display = "none";
        var productInfoTable = document.getElementById("product_info_table" );
        <%

        %>
    }

    function backOrder() {
        PurchaseOrder.style.display = "none";
        ShortPurchaseOrders.style.display = "block";
    }

    function backProduct(){
        Products.style.display = "flex";
        Product.style.display = "none";
        ShortPurchaseOrders.style.display = "block";
        Employees.style.display = "block";
        Statistics.style.display = "block";
    }

    function addPurchaseOrderItem(position) {

    }

    function registerFCMTopicAsync() {
        var url = "https://iid.googleapis.com/iid/v1/" + registrationToken + "/rel/topics/<%=companynumber%>-shoppingRequests";
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
        document.getElementById("add_purchase_pin_textfield").value =  null;
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
        }
    }

    function processPostResponse(responseText, callerid) {
        switch (parseInt(responseText)){
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
    }
    function fillShortPurchaseTable() {
        var table = document.getElementById("short_purchase_table");
        for(var i = 0; i<10; i++) {
            var row = table.insertRow(document.getElementById("short_purchase_table").rows.length);
            var cell1 = row.insertCell(0);
            var cell2 = row.insertCell(1);
            var cell3 = row.insertCell(2);
            var price_sum = 0;
            for(var j = 0; j < purchase_order_array[i].prices_list.length; j++){
                price_sum += (purchase_order_array[i].prices_list[j] * purchase_order_array[i].amounts_list[j]);
            }
            cell1.innerHTML = purchase_order_array[i].date_time;
            cell2.innerHTML = purchase_order_array[i].buyer_accountnumber;
            cell3.innerHTML = price_sum;
            row.onclick = function(){editPurchaseorders(this.rowIndex-1)};
        }
    }
</script>
</html>
