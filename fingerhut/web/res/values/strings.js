var strings = setStrings();

function setStrings() {
    switch (navigator.language){
        case "de":
            return deStrings();
            break;
        case "en":
            return enStrings();
            break;
        default:
            return enStrings();
            break;
    }
}

function deStrings() {
    return {
        accountnumber: "Kontonummer",
        pin: "Pin",
        accountnumberError: "Diese Kontonummer existiert nicht",
        pinError: "Falsches Passwort",
        receiveraccountnumberFormatError: "Kontonummer muss eine Zahl sein",
        amountFormatError: "Formatfehler",
        serverError: "Server Fehler",
        noNumberInputError: "Eingabe muss eine Zahl sein",
        loginButtonText : "Anmelden",
        serverIsClosed : "Der Server ist aktuell gesperrt",
        accountLocked : "Dein Konto ist gesperrt, begib dich zur Zentralbank"
    };
}

function enStrings() {
    return {
        accountnumber : "Account number",
        pin : "Pin",
        accountnumberError : "Account number not valid",
        pinError : "Password not valid",
        serverError : "Server error",
        noNumberInputError: "Input has to be a number",
        loginButtonText : "Log in",
        serverIsClosed : "The server is closed.",
        accountLocked : "Your account is locked"
    };
}