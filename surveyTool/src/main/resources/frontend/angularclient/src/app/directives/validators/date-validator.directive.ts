import {Directive} from '@angular/core';
import {NG_VALIDATORS} from "@angular/forms";

// export function dateValidator(startDate: Date, endDate: Date): ValidatorFn {
//   return (control: AbstractControl): ValidationErrors | null => {
//
//   };
// }

@Directive({
  selector: '[date-validator]',
  providers: [{
    provide: NG_VALIDATORS,
    useExisting: DateValidatorDirective,
    multi: true
  }]
})
export class DateValidatorDirective {
  constructor() {
  }

  // validate(): ValidationErrors | null {
  //
  // }
}
