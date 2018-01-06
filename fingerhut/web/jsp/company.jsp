<%@ page import="de.repictures.fingerhut.Web.MainTools" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="de.repictures.fingerhut.Web.CompanyTools" %>
<%@ page import="de.repictures.fingerhut.Datastore.Product" %>
<%@ page import="de.repictures.fingerhut.Datastore.PurchaseOrder" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.util.logging.Logger" %>

<%
    String code = request.getParameter("webstring");
    String accountnumber = request.getParameter("accountnumber");
    String companynumber = request.getParameter("companynumber");
    Logger log = Logger.getLogger("company.jsp");
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}../css/getmdl-select.min.css">
    <script defer src="${pageContext.request.contextPath}../js/getmdl-select.min.js"></script>
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
                                String priceSumStr = new DecimalFormat("#.00").format(priceSum) + " S";
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
                            <tr onclick="editPurchaseorders()">
                                <th><%= dateTimeStr %></th>
                                <th><%= purchaseOrder.getNumber() %></th>
                                <th><%= priceSumStr %></th>
                            </tr>
                        <%
                            }
                        %>
                        </tbody>
                    </table>
                    <div class="mdl-card__menu">
                        <button class="mdl-button mdl-js-button mdl-button--fab mdl-js-ripple-effect mdl-button--colored" onclick="addPurchase()">
                            <i class="material-icons">add</i>
                        </button>
                    </div>
                </div>
                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="purchase_order">
                    <table class="mdl-data-table mdl-js-data-table" id="purchase_info_table">
                        <thead>
                            <tr>
                                <th>Produkt</th>
                                <th>Preis</th>
                                <th>Barcode</th>
                                <th>Menge</th>
                            </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                    <div class ="wrapper">
                        <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored" onclick="back()" id="back_button">Fertig</button>
                    </div>
                </div>
                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="add_purchase">
                    <div class="mdl-card__menu">
                        <button class="mdl-button mdl-js-button mdl-button--fab mdl-js-ripple-effect mdl-button--colored" onclick="addProductToPurchase()">
                            <i class="material-icons">add</i>
                        </button>
                    </div>
                    <div class="wrapper">
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label wrapper" id="add_purchase_textfield">
                            <input class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?" id="sample4">
                            <label class="mdl-textfield__label" for="sample4">Kontonummer</label>
                            <span class="mdl-textfield__error">Eingabe ist keine Zahl</span>
                        </div>
                    </div>
                    <table class="mdl-data-table mdl-js-data-table" id="add_purchase_table">
                        <thead>
                        <tr>
                            <th>Anzahl</th>
                            <th>Produkt</th>
                            <th>Preis</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                    <div class="wrapper">
                        <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" onclick="addPurchaseToTable()" id="finish_button">
                            Fertig
                        </button>
                    </div>
                </div>
                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="add_product_to_purchase">
                    <div class="wrapper">
                    <div class="mdl-card__title">
                        <h2 class="mdl-card__title-text" id="add_product_to_purchase_heading">Neues Produkt zum Kaufauftrag hinzufügen</h2>
                    </div>
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                        <input class="mdl-textfield__input" type="text" pattern="-?[0-9]*(\.[0-9]+)?" id="sample5">
                        <label class="mdl-textfield__label" for="sample5">Anzahl</label>
                        <span class="mdl-textfield__error">Eingabe ist keine Zahl!</span>
                    </div>
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label getmdl-select getmdl-select__fix-height">
                            <input type="text" value="" class="mdl-textfield__input" id="sample6" readonly>
                            <input type="hidden" value="" name="sample6">
                            <i class="mdl-icon-toggle__label material-icons">keyboard_arrow_down</i>
                            <label for="sample6" class="mdl-textfield__label">Produkt</label>
                            <ul for="sample6" class="mdl-menu mdl-menu--bottom-left mdl-js-menu" id="dropdown_list">
                            </ul>
                        </div>
                    </div>
                    <div class="wrapper">
                        <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored button" id="add_to_purchase_button" onclick="addProductToPurchaseTable()">
                            Hinzufügen
                        </button>
                    </div>
                </div>


                <!-- Hier beginnt der Teil der für de Karte Statistiken zuständig ist -->


                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="statistics">
                    <h2 class="mdl-card__title-text" id="statistics_heading">Statistiken</h2>
                    <div class="mdl-card__supporting-text">
                        Hier können sie die Statistiken ihres Unternehmens einsehen.
                    </div>
                </div>
                <div class="mdl-card mdl-shadow--3dp mdl-cell mdl-cell--12-col" id="products">
                    <h2 class="mdl-card__title-text" id="products_heading">Produkte</h2>
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
                            <tr>
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
<script src="${pageContext.request.contextPath}../js/product.js" ></script>
<script>
    var PurchaseOrder = document.getElementById("purchase_order");
    var Products = document.getElementById("");
    var PurchaseOrders = document.getElementById("purchase_orders");
    var AddPurchase = document.getElementById("add_purchase");
    var AddProductToPurchase = document.getElementById("add_product_to_purchase");
    var productarray = [];
    var product = pojo('name', 'price', 'code','amount');
    PurchaseOrder.style.display = "none";
    AddPurchase.style.display = "none";
    AddProductToPurchase.style.display = "none";

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

    function editPurchaseorders(){
        PurchaseOrders.style.display = "none";
        PurchaseOrder.style.display = "block";
        AddPurchase.style.display = "none";
        AddProductToPurchase.style.display = "none";
    }

    function editProducts(){

    }

    function back() {
        PurchaseOrders.style.display = "flex";
        PurchaseOrder.style.display = "none";
        AddPurchase.style.display = "none";
        AddProductToPurchase.style.display = "none";
    }

    function addPurchase() {
        AddPurchase.style.display = "inline-block";
        PurchaseOrders.style.display = "none";
        PurchaseOrder.style.display = "none";
        AddProductToPurchase.style.display = "none";
    }

    function addProductToPurchase() {
        var list = document.getElementById("dropdown_list");
        AddPurchase.style.display = "none";
        PurchaseOrders.style.display = "none";
        PurchaseOrder.style.display = "none";
        AddProductToPurchase.style.display = "inline-block";
        for(var k = 0; k < productarray.length; k++)
        {
            var entry = document.createElement('li');
            entry.appendChild(document.createTextNode(productarray[k].name));
            list.appendChild(entry);
        }
    }

    function addProductToPurchaseTable() {
        AddPurchase.style.display = "flex";
        PurchaseOrders.style.display = "none";
        PurchaseOrder.style.display = "none";
        AddProductToPurchase.style.display = "none";
        var table = document.getElementById("add_purchase_table");
        var Product = document.getElementById("sample6").value;
        var Amount = document.getElementById("sample5").value;
        var row = table.insertRow(document.getElementById("add_purchase_table").rows.length);
        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);
        var cell3 = row.insertCell(2);
        cell2.innerHTML = Product;
        cell1.innerHTML = Amount;
        cell3.innerHTML = getPricethroughName(Product) * parseFloat(Amount);
    }

    function addPurchaseToTable() {
        AddPurchase.style.display = "none";
        PurchaseOrders.style.display = "flex";
        PurchaseOrder.style.display = "none";
        AddProductToPurchase.style.display = "none";
    }

    <% int i = 0;%>
    for(i = 0;i < <%= products.length %>; i++) {

        productarray[i] = pojo(
            '<%= products[i].getName()%>',
            '<%= products[i].getPrice()%>',
            '<%= products[i].getCode() %>'
        );
        <% i++;%>
    }

    function getPricethroughName(name){
        for(var j = 0; j < productarray.length; j++){
            if (name == productarray[j].name){
                return parseFloat(productarray[j].price);
            }
        }
    }

</script>
</html>