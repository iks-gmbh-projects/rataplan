import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import { ExtraValidators } from './validators';

@Directive({
  selector: '[wholeNumber]',
  providers: [{provide: NG_VALIDATORS, useExisting: WholeNumberDirective, multi:true}],
})
export class WholeNumberDirective implements Validator {
  @Input("wholeNumber") enabled?: boolean|string;

  constructor() { }

  validate(control: AbstractControl): ValidationErrors | null {
    if((!this.enabled && this.enabled !== undefined) || this.enabled == "false") return null;
    return ExtraValidators.wholeNumber(control);
  }

}
