import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FormGroup} from "@angular/forms";
import { isNullOrUndefined } from 'util';
import { CustomValidators } from '../../../../utilities/custom-validators';

@Component({
  selector: 'rp-email-subform',
  templateUrl: './email-subform.component.html',
  styleUrls: ['./email-subform.component.css']
})
export class EmailSubformComponent implements OnInit {

  @Input() emailConfig: FormGroup;
  @Input() emailList: string[];

  private consignee;

  constructor() { }

  ngOnInit() {
    this.consignee = this.emailConfig.controls['consignee'];
  }

  onKeyDown(event: KeyboardEvent) {
    let enteredKey = event['key'].toString();
    if (enteredKey === ',' || enteredKey === ';' || enteredKey === ' ') {
      return this.validateValue(this.consignee.value);
    } else if (event.keyCode === 8 && (this.consignee.value === '' || isNullOrUndefined(this.consignee.value))) {
      this.deleteConsignee(this.emailList.length - 1);
    }
  }

  onPaste(event: ClipboardEvent) {
    let pastedValue: string = event.clipboardData.getData('text');

    return this.validateValue(pastedValue);
  }

  onBlur() {
    this.validateValue(this.consignee.value)
  }

  onEnter() {
    this.validateValue(this.consignee.value)
  }

  validateValue(value: string) {
    if (isNullOrUndefined(value) || value.length === 0) {
      return;
    }

    let splittedValues: string[] = value.split(/([, ;])/g);

    let failList = '';

    for (let email of splittedValues) {
      if (email.length > 2 && CustomValidators.isEmail(email)) {

        // ist email schon vorhanden?
        if (this.emailIsUnique(email)) {
          this.emailList.push(email);
        } else {
          failList += ' ' + email;
        }

      } else if (email.length > 2) {
        failList += ' ' + email;
      }
    }

    this.consignee.reset();

    if (failList.length > 0) {
      this.consignee.setValue(failList.trim());
    }

    return false;
  }

  deleteConsignee(idx: any) {
    this.emailList.splice(idx, 1);
  }

  emailIsUnique(enteredEmail: string): boolean {
    return this.emailList.filter(mail => mail.toLowerCase() === enteredEmail.toLowerCase()).length === 0;
  }

}
