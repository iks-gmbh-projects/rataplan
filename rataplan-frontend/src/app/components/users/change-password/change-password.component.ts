import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ChangePassword } from '../../../models/users/change-password';
import { UserService } from '../../../services/user-service/user.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  public hasAccess = false;
  public changePasswordForm: FormGroup;
  public alreadySubmitted = false;
  public wrongPassword = false;

  constructor(private _userService: UserService,
              private _router: Router,
              private _fb: FormBuilder) {

    this.changePasswordForm = this._fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', Validators.required],
      repetition: ['', Validators.required]
    });
  }

  ngOnInit() {
    this._userService.getUserData().subscribe(res => {
      this.hasAccess = true;

    }, err => {
      this._router.navigateByUrl('/login');
    });
  }

  public changePassword() {
    if (this.changePasswordForm.controls['oldPassword'].value.length < 4) {
      return;
    }

    if (this.alreadySubmitted) {
      return;
    }
    this.alreadySubmitted = true;
    if (this.passwordsMatching() && this.changePasswordForm.controls['oldPassword'].value !== this.changePasswordForm.controls['newPassword'].value) {

      let passwords = new ChangePassword(
        this.changePasswordForm.controls['oldPassword'].value,
        this.changePasswordForm.controls['newPassword'].value
      );

      this._userService.changePassword(passwords).subscribe((e) => {
        this._router.navigateByUrl('/profile');
        console.log(e);
      }, (err) => {
        this.wrongPassword = true;
        console.log(err);
      }, () => {
        this.alreadySubmitted = false;
      });
    }
  }

  public passwordsMatching(): boolean {
    let newPassword = this.changePasswordForm.controls['newPassword'].value;
    let repetition = this.changePasswordForm.controls['repetition'].value;
    return newPassword.length >= 4 && repetition.length >= 4 && newPassword === repetition;
  }
}
