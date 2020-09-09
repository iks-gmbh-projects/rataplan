import {FormGroup, FormControl, AbstractControl, ValidatorFn, Validators} from '@angular/forms';

export class CustomValidators {

  static email(control: FormControl) {
    let regExpression = /^$|^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$|null/;
    let valid = regExpression.test(control.value);
    return valid ? null : {email: true};
  }

  static isEmail(value: string) {
    let regExpression = /^$|^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return regExpression.test(value);
  }

  static requiredIf(condition: boolean): ValidatorFn {
    return (control: AbstractControl): {[key: string]: any} => {
      if (!condition) {
        return null;
      }
      // const value = control.value;
      // const notPresent = isNullOrUndefined(value) || value == '';
      // return notPresent ? {'required': {value}} : null;
      return Validators.required(control);
    };
  }

  static url(control: FormControl) {
    let valid = CustomValidators.validateUrl(control.value);
    return valid ? null : {url: true};
  }

  static validateUrl(url: any): boolean {
    let regExpression = /^$|^https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)|[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)$|null/;
    return regExpression.test(url);
  }

  static addHTTPToUrl(url: string): string {
    let regExpression = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)|null/;
    let valid = regExpression.test(url);
    if (valid) {
      return url;
    } else {

      url = 'http://' + url;
      if (CustomValidators.validateUrl(url)) {
        return url;
      }
      return null;
    }
  }

  static passwordOrNull(control: FormControl) {
    let regExpression = /^$|^[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 _+-.,!@#$%^&*();\/|<>"']{4,32}$|null/;
    let valid =  regExpression.test(control.value);
    return valid ? null : {password: true};
  }
}
