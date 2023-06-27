import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { FormErrorMessageService } from '../../services/form-error-message-service/form-error-message.service';
import { ConfirmAccountService } from '../confirm-account-service';

@Component({
  selector: 'app-resend-account-confirmation-email',
  templateUrl: './resend-account-confirmation-email.component.html',
  styleUrls: ['./resend-account-confirmation-email.component.css']
})
export class ResendAccountConfirmationEmailComponent  {

  form!: FormGroup;

  constructor(public errorMessageService: FormErrorMessageService, private confirmAccountService: ConfirmAccountService) {
    this.form = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email])
    });
  }

  submit() {
    if (this.form.valid) this.confirmAccountService.resendConfirmationEmail(this.form.get('email')?.value);
  }

}
