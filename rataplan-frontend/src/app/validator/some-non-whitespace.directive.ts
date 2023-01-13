import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from "@angular/forms";
import { ExtraValidators } from "./validators";

@Directive({
  selector: '[someNonWhitespace]',
  providers: [{provide: NG_VALIDATORS, useExisting: SomeNonWhitespaceDirective, multi:true}],
})
export class SomeNonWhitespaceDirective implements Validator{
  @Input('someNonWhitespace') enabled: boolean = true;

  constructor() { }

  validate(control: AbstractControl): ValidationErrors | null {
    if(!this.enabled) return null;
    return ExtraValidators.containsSomeWhitespace(control);
  }
}
