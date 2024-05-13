import { Injectable } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Injectable({
  providedIn: 'root',
})
export class FormErrorMessageService {
  constructor() {
  }
  
  public genericFormError(element: AbstractControl | null): string | null {
    if(!element) return 'Interner Fehler der Seite';
    if(element.hasError('matDatepickerParse')) return 'Kein gültiges Datum';
    if(element.hasError('matDatetimePickerParse')) return 'Kein gültiger Zeitpunkt';
    if(element.hasError('required')) return 'Dieses Feld darf nicht leer bleiben.';
    if(element.hasError('email')) return 'Keine gültige E-Mail';
    if(element.hasError('integer')) return 'Muss ganzzahlig sein';
    if(element.hasError('cannotContainWhitespace')) return 'Darf keine Leerzeichen enthalten.';
    if(element.hasError('mustContainSomeWhitespace')) return 'Muss mindestens ein sichtbares Zeichen enthalten.';
    if(element.hasError('minlength')) return 'Benötigt mindestens ' + element.getError('minlength').requiredLength +
      ' Zeichen';
    if(element.hasError('maxlength')) return 'Darf maximal ' + element.getError('maxlength').requiredLength +
      ' Zeichen haben';
    if(element.hasError('usernameExists')) return 'Benutzername wird bereits verwendet.';
    if(element.hasError('mailExists')) return 'E-Mail wird bereits verwendet.';
    if(element.hasError('mailDoesNotExist')) return 'Es gibt keinen Benutzer mit dieser E-Mail.';
    if(element.hasError('wrongPassword')) return 'Passwort ist falsch.';
    if(element.hasError('passwordMatch')) return 'Passwort stimmt nicht überein.';
    if(element.hasError('invalidCredentials')) return 'Benutzername/Email oder Passwort ist falsch.';
    if(
      element.hasError('matDatepickerMin')
      || element.hasError('mtxDatetimepickerMin')
    ) return 'Zu früh';
    if(
      element.hasError('matDatepickerMax')
      || element.hasError('mtxDatetimepickerMax')
    ) return 'Zu spät';
    if(element.hasError('min')) return 'Muss größer sein';
    if(element.hasError('max')) return 'Muss kleiner sein';
    if(element.hasError('index')) return 'So viele Möglichkeiten gibt es nicht.';
    return null;
  }
}