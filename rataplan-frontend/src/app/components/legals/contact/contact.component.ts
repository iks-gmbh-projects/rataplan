import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

import { ContactService } from '../../../services/contact-service/contact.service';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit {

  snackbarMessage = 'Nachricht erfolgreich versandt!';
  snackbarNoAction: undefined;
  senderMail = new FormControl('', [Validators.required, Validators.email]);
  subject = new FormControl('', [Validators.required]);
  content = new FormControl('', [Validators.required]);

  contact = this.formBuilder.group({
    senderMail: this.senderMail,
    subject: this.subject,
    content: this.content,
  });


  constructor(private contactService: ContactService,
              private formBuilder: FormBuilder,
              private _snackBar: MatSnackBar) { }

  ngOnInit(): void {
  }

  submit(){
    const contactData: ContactData = {
      senderMail: this.senderMail.value,
      subject: this.subject.value,
      content: this.content.value,
    };

    this.contactService.contact(contactData).subscribe(responseData => {
      if (responseData){
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
