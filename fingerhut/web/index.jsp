<%@ page import="java.io.IOException" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>

<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- Toller Kommentar --%>

<html>
  <head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.green-light_green.min.css">
      <script defer src="js/sjcl.js"></script>
    <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"><!-- Mobile-Device-Skalierung -->
  </head>
  <body>
  <div class="mdl-layout mdl-js-layout mdl-color--green-light_green-100">
    <main class="mdl-layout__content">
      <div class="mdl-card mdl-shadow--6dp">
        <div class="mdl-card__title mdl-color--primary mdl-color-text--white">
          <h2 class="mdl-card__title-text">Fingerhut</h2>
        </div>
        <div class="mdl-card__supporting-text">
          <form action="#">
            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" id="username_view">
              <input class="mdl-textfield__input" type="text" id="username" pattern="-?[0-9]*(\.[0-9]+)?" />
              <label class="mdl-textfield__label" for="username" id="username_label">Kontonummer</label>
              <span class="mdl-textfield__error" id="username_error">Eingabe muss eine Zahl sein!</span>
            </div>
            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" id="userpass_view">
              <input class="mdl-textfield__input" type="password" id="userpass" pattern="-?[0-9]*(\.[0-9]+)?" />
              <label class="mdl-textfield__label" for="userpass">Pin</label>
              <span class="mdl-textfield__error" id="userpass_error">Eingabe muss eine Zahl sein!</span>
            </div>
            <div class="mdl-card__actions mdl-card--border">
              <br>
              <button onclick="onButtonClick()" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent" type="submit" id="submit_button">Einloggen</button>
            </div>
          </form>
        </div>
      </div>
    </main>
  </div>
  <script type="application/javascript">
      function onButtonClick() {
          var usernameInput = document.getElementById('username');
          var passwordInput = document.getElementById('userpass');

          var usernameError = document.getElementById('username_error');
          usernameError.parentElement.className = usernameError.parentElement.className.replace(" is-invalid", "");
          usernameError.textContent = '';

          var userpassError = document.getElementById('userpass_error');
          userpassError.parentElement.className = userpassError.parentElement.className.replace(" is-invalid", "");
          userpassError.textContent = '';

          var hash = sjcl.hash.sha256.hash(passwordInput.value);
          var hashHex = sjcl.codec.hex.fromBits(hash);

          var urlStr = "https://fingerhut388.appspot.com/web/login?accountnumber=" + usernameInput.value + "&password=" + hashHex;

          httpGetAsync(urlStr);
      }

      function httpGetAsync(theUrl) {
          var xmlHttp = new XMLHttpRequest();
          xmlHttp.onreadystatechange = function() {
              if (xmlHttp.readyState === 4 && xmlHttp.status === 200){
                  console.log(xmlHttp.responseText);
                  processResponse(xmlHttp.responseText);
              }
          };
          xmlHttp.open("POST", theUrl, true); // true for asynchronous
          xmlHttp.send(null);
      }

      function processResponse(responseStr) {
          var responses = responseStr.split("~");
          var ele;

          switch (parseInt(responses[0])){
              case 0:
                  ele = document.getElementById('username_error');
                  ele.parentElement.className += ' is-invalid';
                  ele.textContent = 'Serverfehler';
                  break;
              case 1:
                  console.log("yay");
                  break;
              case 2:
                  ele = document.getElementById('username_error');
                  ele.parentElement.className += ' is-invalid';
                  ele.textContent = 'Error Message';
                  break;
              case 3:
                  ele = document.getElementById('userpass_error');
                  ele.parentElement.className += ' is-invalid';
                  ele.textContent = 'Error Message';
                  break;
              default:
                  console.log("hups");
                  break;
          }
      }
  </script>
  </body>
</html>

