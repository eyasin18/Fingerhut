<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/company.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.green-light_green.min.css">
    <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Roboto:300,400,500,700" type="text/css">
    <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-drawer
            mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Unternehmensseite</span>
            <div class="mdl-layout-spacer"></div>
        </div>
    </header>
    <div class="mdl-layout__drawer">
        <span class="mdl-layout-title">Unternehmensname</span>
        <nav class="mdl-navigation">
            <a class="mdl-navigation__link" href="#statistics">Statistiken</a>
            <a class="mdl-navigation__link" href="#products">Produkte</a>
            <a class="mdl-navigation__link" href="#employees">Mitarbeiter</a>
        </nav>
    </div>
    <main class="mdl-layout__content">
        <div class="page-content">
            <div class="mdl-grid">
                <div class="mdl-cell mdl-cell--10-col" id="statistics">
                    <div class="mdl-card mdl-shadow--3dp">
                        <h1>Statistiken</h1>
                    </div>
                </div>
                <div class="mdl-cell mdl-cell--10-col" id="products">
                    <div class="mdl-card mdl-shadow--3dp">
                        <h1>Produkte</h1>
                    </div>
                </div>
                <div class="mdl-cell mdl-cell--10-col" id="employees">
                    <div class="mdl-card mdl-shadow--3dp">
                        <h1>Mitarbeiter</h1>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>
</body>