<%@ page import="java.io.IOException" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>

<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<%@ page import="java.util.Locale" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%-- Toller Kommentar --%>

<html>
    <head>
        <meta charset="utf-8">
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}../css/index.css">
            <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
            <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.green-light_blue.min.css">
            <script defer src="../js/sjcl.js"></script>
            <script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
            <script type="application/javascript" src="../res/values/strings.js"></script>

        <meta name="viewport" content="width=device-width, initial-scale=1.0"><!-- Mobile-Device-Skalierung -->
    </head>
    <body>
        <div class="mdl-layout mdl-js-layout mdl-color--green-light_blue-100">
            <main class="mdl-layout__content">
                <div class="mdl-card mdl-shadow--6dp">
                    <div id="title_card" class="mdl-card__title mdl-color--primary mdl-color-text--white">
                        <h2 class="mdl-card__title-text">Fingerhut</h2>
                    </div>
                    <div class="mdl-card__supporting-text">
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" id="username_view">
                            <input class="mdl-textfield__input" type="text" id="username" pattern="-?[0-9]*(\.[0-9]+)?" />
                            <label class="mdl-textfield__label" for="username" id="username_label"></label>
                            <span class="mdl-textfield__error" id="username_error"></span>
                        </div>
                        <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label" id="userpass_view">
                            <input class="mdl-textfield__input" type="password" id="userpass" pattern="-?[0-9]*(\.[0-9]+)?" />
                            <label class="mdl-textfield__label" for="userpass" id="userpass_label"></label>
                            <span class="mdl-textfield__error" id="userpass_error"></span>
                        </div>
                        <div class="mdl-card__actions">
                            <br>
                            <button onclick="onButtonClick()" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent mdl-color-text--white" type="submit" id="submit_button"></button>
                        </div>
                        <div class="mdl-spinner mdl-js-spinner is-active" id="submit_spinner"></div>
                    </div>
                </div>
            </main>
        </div>
        <script type="application/javascript">
            document.getElementById('username_label').textContent = strings.accountnumber;
            document.getElementById('username_error').textContent = strings.noNumberInputError;
            document.getElementById('userpass_label').textContent = strings.pin;
            document.getElementById('userpass_error').textContent = strings.noNumberInputError;

            var submitSpinner = document.getElementById('submit_spinner');
            var submitButton = document.getElementById('submit_button');
            submitButton.textContent = strings.loginButtonText;
            var buttonWidth = window.getComputedStyle(submitButton, null).getPropertyValue("width");
            submitButton.style.setProperty("width", buttonWidth, "");
            var buttonHeight = window.getComputedStyle(submitButton, null).getPropertyValue("height");
            var rect = submitButton.getBoundingClientRect();
            var spinnerHeight = parseInt(buttonHeight, 10) - 12;
            submitSpinner.style.height = spinnerHeight + "px";
            submitSpinner.style.width = spinnerHeight + "px";
            submitSpinner.style.top = (rect.top + 6) + "px";
            var spinnerLeftInt = rect.left + parseInt(buttonWidth)/2 - spinnerHeight/2;
            submitSpinner.style.left = spinnerLeftInt + "px";
            submitSpinner.style.visibility = 'hidden';

            var accountnumber;

            function onButtonClick() {
                console.log("clicked");
                var usernameInput = document.getElementById('username');
                var passwordInput = document.getElementById('userpass');

                var usernameError = document.getElementById('username_error');
                usernameError.parentElement.className = usernameError.parentElement.className.replace(" is-invalid", "");
                usernameError.textContent = '';

                var userpassError = document.getElementById('userpass_error');
                userpassError.parentElement.className = userpassError.parentElement.className.replace(" is-invalid", "");
                userpassError.textContent = '';

                if(usernameInput.value === null || usernameInput.value.length < 1){
                    return;
                }

                if(passwordInput.value === null || passwordInput.value.length < 1){
                    return;
                }

                var hash = sjcl.hash.sha256.hash(passwordInput.value);
                var hashHex = sjcl.codec.hex.fromBits(hash);
                accountnumber = usernameInput.value;

                var urlStr = "https://fingerhut388.appspot.com/web/login?accountnumber=" + accountnumber + "&password=" + hashHex;

                submitButton.textContent = '';
                submitSpinner.style.visibility = 'visible';
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
                        submitSpinner.style.visibility = 'hidden';
                        submitButton.textContent = strings.loginButtonText;
                        ele = document.getElementById('username_error');
                        ele.parentElement.className += ' is-invalid';
                        ele.textContent = strings.serverError;
                        break;
                    case 1:
                        console.log("yay");
                        window.location = "https://fingerhut388.appspot.com/main?code=" + responses[1] + "&accountnumber=" + accountnumber;
                        break;
                    case 2:
                        submitSpinner.style.visibility = 'hidden';
                        submitButton.textContent = strings.loginButtonText;
                        ele = document.getElementById('userpass_error');
                        ele.parentElement.className += ' is-invalid';
                        ele.textContent = strings.pinError;
                        break;
                    case 3:
                        submitSpinner.style.visibility = 'hidden';
                        submitButton.textContent = strings.loginButtonText;
                        ele = document.getElementById('username_error');
                        ele.parentElement.className += ' is-invalid';
                        ele.textContent = strings.accountnumberError;
                        break;
                    default:
                        submitSpinner.style.visibility = 'hidden';
                        submitButton.textContent = strings.loginButtonText;
                        console.log("hups");
                        break;
                }
            }
            function unquote(string) {
                var count = string.length - 1;
                var pair = string.charAt(0) + string.charAt(count);
                return (pair === '""' || pair === "''") ? string.slice(1, count) : string;
            }
        </script>
  </body>
</html>

