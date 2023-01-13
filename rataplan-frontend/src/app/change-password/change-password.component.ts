import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from "@angular/forms";
import { PasswordChangeModel } from "../models/password-change.model";
import { ChangePasswordService } from "../services/change-password/change-password.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { OnlyDirtyErrorStateMatcher } from "../services/error-state-matcher/only-dirty.error-state-matcher";
import { FormErrorMessageService } from "../services/form-error-message-service/form-error-message.service";
import { ExtraValidators } from "../validators";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  hideOldPassword = true;
  hideNewPassword = true;
  hideConfirmPassword = true;

  oldPassword: FormControl = new FormControl('', Validators.required);
  newPassword: FormControl = new FormControl('', [Validators.required, Validators.minLength(3)]);
  confirmPassword: FormControl = new FormControl('', [Validators.required, ExtraValidators.valueMatching(this.newPassword)]);

  changePasswordForm = this.formBuilder.group({
    oldPassword: this.oldPassword.value,
    newPassword: this.newPassword.value,
    confirmPassword: this.confirmPassword.value,
  });


  constructor(private formBuilder: FormBuilder,
              private changePasswordService: ChangePasswordService,
              private snackBar: MatSnackBar,
              public readonly errorStateMatcher: OnlyDirtyErrorStateMatcher,
              public readonly errorMessageService: FormErrorMessageService) {
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
      },
      error: () => {
        this.oldPassword.setErrors({'wrongPassword': true});
      },
    });
  }

}
