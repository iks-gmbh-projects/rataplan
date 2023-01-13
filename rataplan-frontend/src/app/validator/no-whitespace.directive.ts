import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from "@angular/forms";
import { ExtraValidators } from "./validators";

@Directive({
  selector: '[noWhitespace]',
  providers: [{provide: NG_VALIDATORS, useExisting: NoWhitespaceDirective, multi:true}],
})
export class NoWhitespaceDirective implements Validator {
  @Input('noWhitespace') enabled: boolean = true;

  constructor() { }

  validate(control: AbstractControl): ValidationErrors | null {
    if(!this.enabled) return null;
    return ExtraValidators.cannotContainWhitespace(control);
  }
}
