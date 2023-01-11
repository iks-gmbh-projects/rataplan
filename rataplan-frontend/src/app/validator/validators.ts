import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export class ExtraValidators {
  static valueMatching(control: AbstractControl): ValidatorFn {
    return ctrl => ctrl.value == control.value ? null : {
      passwordMatch: {},
    };
  }

  static cannotContainWhitespace(control: AbstractControl): ValidationErrors | null {
    if (/\s/.test(control.value)) {
      return {cannotContainWhitespace: true}
    }
    return null;
  }
}
