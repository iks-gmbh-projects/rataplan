import { AbstractControl, FormArray, ValidationErrors, ValidatorFn } from "@angular/forms";

export class ExtraValidators {
  static valueMatching(control: AbstractControl): ValidatorFn {
    return ctrl => ctrl.value == control.value ? null : {
      passwordMatch: {},
    };
  }

  static valueGreaterThan(control: AbstractControl, allowEqual: boolean = true): ValidatorFn {
    return ctrl => (ctrl.value > control.value || (allowEqual && ctrl.value == control.value)) ? null : {min:{min: control.value, actual: ctrl.value}};
  }

  static valueLessThan(control: AbstractControl, allowEqual: boolean = true): ValidatorFn {
    return ctrl => (ctrl.value < control.value || (allowEqual && ctrl.value == control.value)) ? null : {max:{max: control.value, actual: ctrl.value}};
  }

  static indexValue(array: FormArray, allowLength: boolean = false, allowAnyOnEmpty: boolean = true): ValidatorFn {
    return ctrl => (ctrl.value < array.length || (allowLength && ctrl.value == array.length) || (allowAnyOnEmpty && array.length == 0)) ? null : {index:{length: array.length, index: ctrl.value}};
  }

  static cannotContainWhitespace(control: AbstractControl): ValidationErrors | null {
    if (/\s/.test(control.value)) {
      return {cannotContainWhitespace: true};
    }
    return null;
  }

  static containsSomeWhitespace(control: AbstractControl): ValidationErrors | null {
    if(control.value == "" || /\S/.test(control.value)) return null;
    return {mustContainSomeWhitespace: true};
  }
}
