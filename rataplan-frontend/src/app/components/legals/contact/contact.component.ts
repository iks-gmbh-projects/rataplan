import { Component, OnInit, ViewChild } from '@angular/core';
import { ContactService } from '../../../services/contact-service/contact.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CustomValidators } from '../../../utilities/custom-validators';
import { isNullOrUndefined } from 'util';
import { Modal } from 'ngx-modal';

@Component({
  selector: 'rp-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit {

  public contact: FormGroup;
  private alreadySubmitted = false;
  public modalTitle: string;
  public modalContent: string;
  public success = false;
  private showErrors = false;

  @ViewChild(Modal) modal: Modal;

  constructor(private formBuilder: FormBuilder,
              private contactService: ContactService) {

    this.contact = this.formBuilder.group({
      subject: ['', Validators.required],
      content: ['', Validators.required],
      senderMail: ['', [Validators.required, CustomValidators.email]]
    });
  }

  ngOnInit() {
    this.contact.setValue({
      subject: '',
      content: '',
      senderMail: '',
    });
  }

  submit() {
    let contactData: ContactData = new ContactData();
    contactData.subject = this.contact.value.subject;
    contactData.content = this.contact.value.content;
    contactData.senderMail = this.contact.value.senderMail;
    this.showErrors = true;
    if (!this.contactFormIsValid()) {
      return;
    }

    if (this.alreadySubmitted) {
      return;
    }
    this.alreadySubmitted = true;

    this.contactService.contact(contactData)
      .subscribe(res => {
        this.showErrors = false;
        this.contact.reset();
        this.success = true;
        this.openModal('Contact Success', 'Kontaktanfrage gestellt', 5000)
      }, err => {
        console.log('contact fail');
      }, () => {
        this.alreadySubmitted = false;
      });
  }

  private contactFormIsValid(): boolean {
    for (let controlName in this.contact.controls) {
      this.contact.controls[controlName].markAsTouched();
      if (!isNullOrUndefined(this.contact.controls[controlName].value)) {
        this.contact.controls[controlName].setValue(this.contact.controls[controlName].value.trim());
      }
      if (!this.contact.controls[controlName].valid) {
        return false;
      }
    }

    return true;
  }

  public hasErrors(controlName: string): boolean {
    let control = this.contact.controls[controlName];
    return !control.valid && this.showErrors;
  }

  public openModal(title, message, time) {
    this.modalTitle = title;
    this.modalContent = message;
    this.modal.open();
    setTimeout(() => {
      this.modal.close();
    }, time);
  }

  public onCloseModal() {
    this.alreadySubmitted = false;
  }
}

export class ContactData {
  subject: string;
  content: string;
  senderMail: string;
}
