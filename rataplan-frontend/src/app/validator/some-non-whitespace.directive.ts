import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from "@angular/forms";
import { ExtraValidators } from "./validators";

@Directive({
  selector: '[someNonWhitespace]',
  providers: [{provide: NG_VALIDATORS, useExisting: SomeNonWhitespaceDirective, multi:true}],
})
export class SomeNonWhitespaceDirective implements Validator{
  @Input('someNonWhitespace') enabled: boolean|string = true;

  constructor() { }

  validate(control: AbstractControl): ValidationErrors | null {
    if(this.enabled !== "" && (!this.enabled || this.enabled === "false")) return null;
    return ExtraValidators.containsSomeWhitespace(control);
  }
}
