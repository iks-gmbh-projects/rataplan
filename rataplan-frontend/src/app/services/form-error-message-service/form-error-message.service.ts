import { Injectable } from '@angular/core';
import { AbstractControl } from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class FormErrorMessageService {
  constructor() {
  }

  public genericFormError(element: AbstractControl): string | null {
    if (element.hasError("required")) return "Dieses Feld darf nicht leer bleiben.";
    if (element.hasError("email")) return "Keine gültige E-Mail";
    if (element.hasError("minlength")) return "Benötigt mindestens " + element.getError("minlength").requiredLength + " Zeichen";
    if (element.hasError("cannotContainWhitespace")) return "Darf keine Leerzeichen enthalten.";
    if (element.hasError("usernameExists")) return "Benutzername wird bereits verwendet.";
    if (element.hasError("mailExists")) return "E-Mail wird bereits verwendet.";
    if (element.hasError("mailDoesNotExist")) return "Es gibt keinen Benutzer mit dieser E-Mail.";
    if (element.hasError("wrongPassword")) return "Passwort ist falsch.";
    if (element.hasError("passwordMatch")) return "Passwort stimmt nicht überein.";
    if (element.hasError("matDatepickerMin")) return "Zu früh";
    if (element.hasError("matDatepickerMax")) return "Zu spät";
    if (element.invalid) console.log(element.errors);
    return null;
  }
}
