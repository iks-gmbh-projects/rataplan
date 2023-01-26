import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from "@angular/forms";
import { ExtraValidators } from "./validators";

@Directive({
  selector: '[checkboxCountMax]',
  providers: [{provide: NG_VALIDATORS, useExisting: CheckboxCountMaxDirective, multi:true}],
})
export class CheckboxCountMaxDirective implements Validator {
  @Input('checkboxCountMax') checkboxCountMax: number|string = Number.POSITIVE_INFINITY;

  constructor() { }

  validate(control: AbstractControl): ValidationErrors | null {
    const count = ExtraValidators.countEntries(control.value, x => x);
    if(count > +this.checkboxCountMax) {
      return {selectMax: {expected: +this.checkboxCountMax, actual: count}};
    } else {
      return null;
    }
  }
}
