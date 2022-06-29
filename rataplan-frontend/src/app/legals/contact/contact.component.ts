import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ContactService} from "../../services/contact-service/contact.service"



@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit {

  submitted: boolean = false;
  senderMail = new FormControl('', [Validators.required, Validators.email]);
  subject = new FormControl('', [Validators.required]);
  content = new FormControl('', [Validators.required]);
  contact = this.formBuilder.group({
    senderMail: this.senderMail,
    subject: this.subject,
    content: this.content,
  });

  getEmailErrorMessage() {
    if (this.senderMail.hasError('required')) {
      return 'You must enter a value';
    }

    return this.senderMail.hasError('email') ? 'Not a valid email' : '';
  }

  getSubjectErrorMessage() {
    return this.subject.hasError('required') ? 'You must enter a value' : '';
  }

  getMessageErrorMessage() {
    return this.content.hasError('required') ? 'You must enter a value' : '';
  }

  constructor(private contactService: ContactService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
  }

  submit(){
    let contactData: ContactData = {
      senderMail: this.senderMail.value,
      subject: this.subject.value,
      content: this.content.value,
    }

    this.contactService.contact(contactData).subscribe(responseData => {
      console.log(responseData);
      this.submitted = responseData;
    });
  }
}

export interface ContactData {
  subject: string;
  content: string;
  senderMail: string;
}
