import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ResetPasswordDataModel } from '../models/reset-password-data.model';
import { ResetPasswordService } from '../services/reset-password-service/reset-password.service';
import { ExtraValidators } from "../validator/validators";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  password = new FormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword = new FormControl('', [Validators.required, ExtraValidators.valueMatching(this.password)]);
  hide = true;
  hideConfirm = true;

  resetPasswordForm = this.formBuilder.group({
    password: this.password,
    confirmPassword: this.confirmPassword
  });

  constructor(private formBuilder: FormBuilder,
              private resetPasswortService: ResetPasswordService,
              private route: ActivatedRoute,
              public readonly errorMessageService: FormErrorMessageService) {
  }

  ngOnInit(): void {

  }

  resetPassword() {
    let token: string;
    this.route.queryParams.subscribe(params => {
      token = params['token'];
      const resetPassword = new ResetPasswordDataModel(token, this.password.value);
      this.resetPasswortService.resetPassword(resetPassword).subscribe({});
    });
  }

}
