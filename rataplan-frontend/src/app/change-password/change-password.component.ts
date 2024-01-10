import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, Validators } from "@angular/forms";
import { PasswordChangeModel } from "../models/password-change.model";
import { ChangePasswordService } from "../services/change-password/change-password.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validator/validators";
import { Router } from "@angular/router";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  hideOldPassword = true;
  hideNewPassword = true;
  hideConfirmPassword = true;

  oldPassword: UntypedFormControl = new UntypedFormControl('', Validators.required);
  newPassword: UntypedFormControl = new UntypedFormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword: UntypedFormControl = new UntypedFormControl('', [Validators.required, ExtraValidators.valueMatching(this.newPassword)]);

  changePasswordForm = this.formBuilder.group({
    oldPassword: this.oldPassword.value,
    newPassword: this.newPassword.value,
    confirmPassword: this.confirmPassword.value,
  });


  constructor(
    private formBuilder: UntypedFormBuilder,
    private changePasswordService: ChangePasswordService,
    private snackBar: MatSnackBar,
    public readonly errorMessageService: FormErrorMessageService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
  }

  submit() {
    const passwordChange = new PasswordChangeModel(this.oldPassword.value, this.newPassword.value);
    this.changePasswordService.changePassword(passwordChange).subscribe({
      next: () => {
        this.snackBar.open('Passwort erfolgreich geändert', '', {
          duration: 3000
        });
        this.router.navigate(["/view-profile"]);
      },
      error: () => {
        this.oldPassword.setErrors({'wrongPassword': true});
      },
    });
  }

}
