import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from "@angular/forms";
import { ExtraValidators } from "./validators";

@Directive({
  selector: '[checkboxCountMin]',
  providers: [{provide: NG_VALIDATORS, useExisting: CheckboxCountMinDirective, multi:true}],
})
export class CheckboxCountMinDirective implements Validator {
  @Input('checkboxCountMin') checkboxCountMin: number|string = 0;

  constructor() { }

  validate(control: AbstractControl): ValidationErrors | null {
    const count = ExtraValidators.countEntries(control.value, x => x);
    if(count < +this.checkboxCountMin) {
      return {selectMin: {expected: +this.checkboxCountMin, actual: count}};
    } else {
      return null;
    }
  }
}
