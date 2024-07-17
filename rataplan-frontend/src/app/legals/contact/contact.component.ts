import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, Validators } from '@angular/forms';
import { MatLegacySnackBar as MatSnackBar } from '@angular/material/legacy-snack-bar';

import { ContactService } from '../../services/contact-service/contact.service';
import { ExtraValidators } from "../../validator/validators";
import { FormErrorMessageService } from "../../services/form-error-message-service/form-error-message.service";

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit {

  snackbarMessage = 'Nachricht erfolgreich versandt!';
  snackbarNoAction: undefined;
  senderMail = new UntypedFormControl('', [Validators.required, Validators.email]);
  subject = new UntypedFormControl('', [Validators.required, ExtraValidators.containsSomeWhitespace]);
  content = new UntypedFormControl('', [Validators.required, ExtraValidators.containsSomeWhitespace]);

  contact = this.formBuilder.group({
    senderMail: this.senderMail,
    subject: this.subject,
    content: this.content,
  });


  constructor(
    private contactService: ContactService,
    private formBuilder: UntypedFormBuilder,
    private _snackBar: MatSnackBar,
    public readonly errorMessageService: FormErrorMessageService
  ) {
  }

  ngOnInit(): void {
  }

  submit() {
    const contactData: ContactData = {
      senderMail: this.senderMail.value,
      subject: this.subject.value,
      content: this.content.value,
    };

    this.contactService.contact(contactData).subscribe(responseData => {
      if (responseData) {
        this.openSnackBar(this.snackbarMessage);
      }
    });
    this.contact.reset();
  }

  openSnackBar(message: string) {
    this._snackBar.open(message, this.snackbarNoAction, {
      panelClass: ['mat-toolbar', 'mat-primary'],
      duration: 3000
    });
  }

  getEmailErrorMessage() {
    if (this.senderMail.hasError('required')) {
      return 'Dieses Feld darf nicht leer bleiben';
    }

    return this.senderMail.hasError('email') ? 'Keine gültige email' : '';
  }

  getSubjectErrorMessage() {
    return this.subject.hasError('required') ? 'Dieses Feld darf nicht leer bleiben' : '';
  }

  getMessageErrorMessage() {
    return this.content.hasError('required') ? 'Dieses Feld darf nicht leer bleiben' : '';
  }

}

export interface ContactData {
  subject: string;
  content: string;
  senderMail: string;
}
