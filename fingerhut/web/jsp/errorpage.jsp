<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Es ist ein Fehler aufgetreten...</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/errorpage.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}../css/material.green-light_green.min.css">
    <script defer src="${pageContext.request.contextPath}../js/material.min.js"></script>
</head>
<body>
<!-- Always shows a header, even in smaller screens. -->
<div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <header class="mdl-layout__header">
        <div class="mdl-layout__header-row">
            <!-- Title -->
            <span class="mdl-layout-title">Es ist ein Fehler aufgetreten ...</span>
            <!-- Add spacer, to align navigation to the right -->
            <div class="mdl-layout-spacer"></div>
        </div>
    </header>
    <main class="mdl-layout__content">
        <div class="page-content">
            <img src="${pageContext.request.contextPath}../res/images/Tom.png" alt="Hier müsste eigentlich ein Bild sein .." id="image">
            <h2 class="center">Entschuldige, das hätte nicht passieren dürfen...</h2>
            <p class="center">Bitte lade die Seite erneut. Sollte dieser Fehler weiterhin erscheinen, melde dich bitte bei der FutureCentralBank</p>
        </div>
    </main>
</div>
</body>
</html>
