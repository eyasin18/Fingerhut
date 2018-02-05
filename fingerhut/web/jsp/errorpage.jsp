<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Es ist ein Fehler aufgetreten...</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}../css/material.green-light_green.min.css">
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
            <img src="${pageContext.request.contextPath}../res/images/Tom.png" alt="Hier müsste eigentlich ein Bild sein ..">
            <h2>Entschuldige das hätte nicht passieren dürfen ....</h2>
            <p>Bitte lade die Seite erneut. Sollte dieser Fehler weiterhin erscheinen, melde dich bitt bei der FutureCentralBank</p>
        </div>
    </main>
</div>
</body>
</html>
