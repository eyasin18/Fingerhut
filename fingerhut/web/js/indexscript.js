
var url = "https://fingerhut388.appspot.com"; //URL-Addresse der Index.jsp

//Füllt die Textfelder mit Beschriftungen
document.getElementById('username_label').textContent = strings.accountnumber;
document.getElementById('username_error').textContent = strings.noNumberInputError;
document.getElementById('userpass_label').textContent = strings.pin;
document.getElementById('userpass_error').textContent = strings.noNumberInputError;

//Aufnehmen der Elemente in Variablen
var submitSpinner = document.getElementById('submit_spinner');
var submitButton = document.getElementById('submit_button');
//Text des Buttons aus der String.js beziehen
submitButton.textContent = strings.loginButtonText;
//ermittelt die Höhe und Breite des Anmeldebuttons
var buttonWidth = window.getComputedStyle(submitButton, null).width;
var buttonHeight = window.getComputedStyle(submitButton, null).height;
submitButton.style.setProperty("width", buttonWidth, "");
var spinnerHeight = parseInt(buttonHeight, 10) - 12;
var rect = submitButton.getBoundingClientRect();

submitSpinner.style.height = spinnerHeight + "px";
submitSpinner.style.width = spinnerHeight + "px";
submitSpinner.style.top = (rect.top + 6) + "px";
var spinnerLeftInt = rect.left + parseInt(buttonWidth)/2 - spinnerHeight/2;
submitSpinner.style.left = spinnerLeftInt + "px";
submitSpinner.style.visibility = 'hidden';



var accountnumber;

window.addEventListener('popstate', function() {checkWebstring()});

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

    var urlStr = url + "/web/login?accountnumber=" + accountnumber + "&password=" + hashHex;

    submitButton.textContent = '';
    //submitSpinner.style.visibility = 'visible';
    httpPostAsync(urlStr);
}

function httpPostAsync(theUrl) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200){
            console.log(xmlHttp.responseText);
            processPostResponse(xmlHttp.responseText);
        }
    };
    xmlHttp.open("POST", theUrl, true); // true for asynchronous
    xmlHttp.send(null);
}

function httpGetAsync(theUrl, callerid) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200){
            console.log(xmlHttp.responseText);
            processGetResponse(xmlHttp.responseText, callerid);
        }
    };
    xmlHttp.open("GET", theUrl, true); // true for asynchronous
    xmlHttp.send(null);
}

function processPostResponse(responseStr) {
    var responses = decodeURIComponent(responseStr).split("ò");
    var ele;

    switch (parseInt(responses[0])){
        case -2:
            //Server ist gesperrt
            break;
        case 5:
            //response[1]: Zeit wenn Cooldown abgelaufen ist in dd.MM.yyyy HH:mm:ss.SSSS z
            break;
        case 6:
            //Konto gesperrt! Geh zu FCB
            break;
        case 0:
            submitSpinner.style.visibility = 'hidden';
            submitButton.textContent = strings.loginButtonText;
            ele = document.getElementById('username_error');
            ele.parentElement.className += ' is-invalid';
            ele.textContent = strings.serverError;
            break;
        case 1:
            console.log("yay");
            window.location = url + "/main?code=" + responses[1] + "&accountnumber=" + accountnumber;
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

//Sorgt für das aulösen des Buttons bei klicken der Enter-Taste
function enterPressed(event) {
    if (event.keyCode === 13){
        onButtonClick();
    }
}

function checkWebstring() {
    var theUrl = url + "/checkwebstring?accountnumber=<%=accountnumber%>&webstring=<%=code%>";
    console.log(theUrl);
    //httpGetAsync(theUrl, 4);
    var date = new Date();
    console.log(date.getTime());
}

function processGetResponse(responseStr, callerid){
    switch (callerid){
        case 4:
            console.log(responseStr);
            if(parseInt(responseStr) === 1){
                history.back();
            } else {
                history.pushState(null, null, window.location.pathname);
            }
            break;
    }
}