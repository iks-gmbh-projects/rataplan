import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';

import { ForgotPasswordService } from '../services/forgot-password-service/forgot-password.service';
import { UsernameEmailValidatorsService } from '../services/username-email-validators-service/username-email-validators.service';
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  mail: FormControl = new FormControl('', [Validators.required, Validators.email],
    ctrl => this.registerService.mailNotExists(ctrl));

  forgotPasswordForm = this.formBuilder.group({
    mail: this.mail,
  });

  constructor(private formBuilder: FormBuilder,
              private registerService: UsernameEmailValidatorsService,
              private forgotPasswordService: ForgotPasswordService,
              public readonly errorMessageService: FormErrorMessageService) {

  }

  ngOnInit(): void {
  }

  submit() {

    this.forgotPasswordService.forgotPassword(this.mail.value).subscribe({});
  }

}
