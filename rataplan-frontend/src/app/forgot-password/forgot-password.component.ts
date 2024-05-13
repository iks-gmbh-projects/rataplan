import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';

import { ForgotPasswordService } from '../services/forgot-password-service/forgot-password.service';
import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  mail = new FormControl<string>('', [Validators.required, Validators.email]);
  busy: boolean = false;
  done: boolean = false;
  forgotPasswordForm = this.formBuilder.group({
    mail: this.mail,
  });

  constructor(
    private formBuilder: FormBuilder,
    private forgotPasswordService: ForgotPasswordService,
    public readonly errorMessageService: FormErrorMessageService,
  ) {

  }

  ngOnInit(): void {
    this.done = false;
    this.busy = false;
  }

  submit() {
    if(this.mail.value === null || this.busy || this.done) return;
    this.busy = true;
    this.forgotPasswordService.forgotPassword(this.mail.value).subscribe({
      error: () => {
        this.busy = false;
        this.done = false;
      },
      complete: () => {
        this.busy = false;
        this.done = true;
      }
    });
  }

}