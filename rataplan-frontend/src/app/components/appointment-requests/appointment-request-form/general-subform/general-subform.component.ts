import {Component, Input, OnChanges, SimpleChanges, ViewChild, ElementRef, ViewEncapsulation} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {IMyDateModel} from 'mydatepicker';
import {DatepickerOptionsService} from "../../../../services/datepicker-service/datepicker-options.service";

@Component({
  selector: 'rp-general-subform',
  templateUrl: './general-subform.component.html',
  styleUrls: ['./general-subform.component.css']
})
export class GeneralSubformComponent implements OnChanges {
  @Input() general: FormGroup;
  @Input() setFocus: string;

  @ViewChild('title') titleInput: ElementRef;
  @ViewChild('organizerMail') organizerMailInput: ElementRef;

  constructor(public _datepickerService: DatepickerOptionsService) {  }

  ngOnChanges(change: SimpleChanges) {
    if (change['setFocus']) {
      switch (this.setFocus) {
        case 'title':
        this.titleInput.nativeElement.focus();
        break;
        case 'organizerMail':
        this.organizerMailInput.nativeElement.focus();
        break;
        default:
        this.titleInput.nativeElement.focus();
      }
    }
  }

  public hasErrors(controlName: string): boolean {
    let control = this.general.controls[controlName];
    return !control.valid && control.touched;
  }

  public hasErrorsOrNull(controlName: string): boolean {
    let control = this.general.controls[controlName];
    return (!control.valid || control.value == null) && control.touched;
  }

  public updateDatepicker(dpModel: IMyDateModel) {
    this._datepickerService.setDeadline(new Date(dpModel.date.year, dpModel.date.month - 1, dpModel.date.day));
  }
}
