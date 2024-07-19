import { AbstractControl, FormArray, FormGroup, UntypedFormArray, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export class ExtraValidators {
  static valueMatching(control: AbstractControl): ValidatorFn {
    return ctrl => ctrl.value == control.value ? null : {
      passwordMatch: {},
    };
  }
  
  static valueGreaterThan(control: AbstractControl, allowEqual: boolean = true): ValidatorFn {
    return ctrl => (
      ctrl.value > control.value || (
        allowEqual && ctrl.value == control.value
      )
    ) ? null : {min: {min: control.value, actual: ctrl.value}};
  }
  
  static valueLessThan(control: AbstractControl, allowEqual: boolean = true): ValidatorFn {
    return ctrl => (
      ctrl.value < control.value || (
        allowEqual && ctrl.value == control.value
      )
    ) ? null : {max: {max: control.value, actual: ctrl.value}};
  }
  
  static indexValue(
    array: FormArray | UntypedFormArray,
    allowLength: boolean     = false,
    allowAnyOnEmpty: boolean = true,
  ): ValidatorFn {
    return ctrl => (
      ctrl.value < array.length || (
        allowLength && ctrl.value == array.length
      ) || (
        allowAnyOnEmpty && array.length == 0
      )
    ) ? null : {index: {length: array.length, index: ctrl.value}};
  }
  
  static cannotContainWhitespace(control: AbstractControl): ValidationErrors | null {
    if(/\s/.test(control.value)) {
      return {cannotContainWhitespace: true};
    }
    return null;
  }
  
  static containsSomeWhitespace(control: AbstractControl): ValidationErrors | null {
    if(control.value == '' || /\S/.test(control.value)) return null;
    return {mustContainSomeWhitespace: true};
  }
  
  static countMin(min: number): ValidatorFn {
    return ctrl => Object.keys(ctrl.value).length < min ? {selectMin: true} : null;
  }
  
  static countMax(max: number): ValidatorFn {
    return ctrl => Object.keys(ctrl.value).length > max ? {selectMax: true} : null;
  }
  
  static countEntries(object: any, predicate: (value: any) => any = x => x): number {
    return Object.entries(object)
      .reduce((c, x) => predicate(x[1]) ? c + 1 : c, 0);
  }
  
  static filterCountMin(min: number, predicate: (value: any) => any = x => x): ValidatorFn {
    return ctrl => ExtraValidators.countEntries(ctrl.value, predicate) < min ? {selectMin: true} : null;
  }
  
  static filterCountMax(max: number, predicate: (value: any) => any = x => x): ValidatorFn {
    return ctrl => ExtraValidators.countEntries(ctrl.value, predicate) > max ? {selectMax: true} : null;
  }
  
  static integer(ctrl: AbstractControl): ValidationErrors | null {
    return /^[-+]?\d*$/.test('' + ctrl.value) ? null : {integer: true};
  }
  
  static yesAnswerLimitMoreThanZeroOrNull(): ValidatorFn {
    return (c) => {
      if(c.parent?.get('yesLimitActive')?.value) return c.value > 0 ? null : {'invalid yes answer limit': true};
      else return null;
    };
  }
  
  static oneValid(): ValidatorFn {
    return (c) => {
      const fg: FormGroup = c as FormGroup;
      return Object.entries(fg.controls)
        .map(a => a[1].value !== null && a[1].value)
        .reduce((a, b) => a || b) ? null : {'oneValid': true};
    };
    
  }
  
  static participantLimitMoreThanZeroOrNull(): ValidatorFn {
    return (c) => {
      if(c.parent?.get('participantLimitActive')?.value) return c.value > 0 ?
        null :
        {'Teilnehmer Anzahl muss mehr als 0 sein': true};
      else return null;
    };
  }
}