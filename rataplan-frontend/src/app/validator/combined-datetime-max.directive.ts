import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';
import { combineDateTime } from '../appointment/appointment-request-form/appointment-request-form.service';

@Directive({
  selector: '[combinedDatetimeMax]',
  providers: [{provide: NG_VALIDATORS, useExisting: CombinedDatetimeMaxDirective, multi: true}],
})
export class CombinedDatetimeMaxDirective implements Validator {
  @Input('combinedDatetimeMax') referenceTime: string | AbstractControl | null | undefined | false = null;
  @Input('referenceDate') referenceDate: AbstractControl | string | Date | null | undefined | false = null;
  @Input('date') ownDate: AbstractControl | string | Date | null | undefined | false = null;
  @Input('ignoreDate') ignoreDate: boolean | 'true' | 'false' | '' | null | undefined = false;

  constructor() {
  }

  validate(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    if (!this.referenceTime) return null;
    let time: string | null;
    if (typeof this.referenceTime === 'string') time = this.referenceTime;
    else time = this.referenceTime.value;
    if (!time) return null;

    if (this.ignoreDate && this.ignoreDate !== 'false') {
      return time > control.value ? null : {matTimepickerMax: true};
    }

    if (!this.referenceDate) return null;
    if (!this.ownDate) return null;
    let date: string | null;
    if (typeof this.referenceDate === 'string') date = this.referenceDate;
    else if (this.referenceDate instanceof Date) date = this.referenceDate.toISOString();
    else {
      const d: string | Date | null = this.referenceDate.value;
      if (d instanceof Date) date = d.toISOString();
      else date = d;
    }
    const reference = combineDateTime(date, time);
    if (typeof this.ownDate === 'string') date = this.ownDate;
    else if (this.ownDate instanceof Date) date = this.ownDate.toISOString();
    else {
      const d: string | Date | null = this.ownDate.value;
      if (d instanceof Date) date = d.toISOString();
      else date = d;
    }
    const self = combineDateTime(date, control.value);
    if (!self || !reference) return null;
    return Date.parse(self) < Date.parse(reference) ? null : {matTimepickerMax: true};
  }
}
