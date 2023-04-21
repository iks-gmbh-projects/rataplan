import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import { ExtraValidators } from './validators';

@Directive({
  selector: '[integer]',
  providers: [{provide: NG_VALIDATORS, useExisting: IntegerDirective, multi:true}],
})
export class IntegerDirective implements Validator {
  @Input('integer') enabled: boolean|''|'true'|'false' = true;

  constructor() { }

  validate(control: AbstractControl): ValidationErrors | null {
    if((!this.enabled && this.enabled !== '') || this.enabled == 'false') return null;
    return ExtraValidators.integer(control);
  }

}
