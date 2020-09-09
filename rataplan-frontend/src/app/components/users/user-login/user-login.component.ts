import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../../../models/users/user';
import { UserService } from '../../../services/user-service/user.service';
import { CustomValidators } from '../../../utilities/custom-validators';
import { isNullOrUndefined } from 'util';

@Component({
  selector: 'rp-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent implements OnInit {
  public userLoginForm: FormGroup;
  private mailControl;
  private usernameControl;
  public wrongCombination = false;
  private wrongEntry: string;

  public user: User;
  public alreadySubmitted = false;

  public spinnerStyles = { 'padding': '10px 15px', 'width': '36px'};

  constructor(private fb: FormBuilder,
              private router: Router,
              private userService: UserService) {
    this.userLoginForm = this.fb.group({
      username: ['', Validators.required],
      mail: ['', CustomValidators.email],
      password: ['', Validators.required]
    });
  }

  ngOnInit() {
    if (!isNullOrUndefined(JSON.parse(localStorage.getItem('rp_username')))) {
      this.router.navigateByUrl('/profile');
    }
    this.user = new User();
    this.mailControl = this.userLoginForm.controls['mail'];
    this.usernameControl = this.userLoginForm.controls['username'];
  }

  public hasErrors(controlName: string): boolean {
    let control = this.userLoginForm.controls[controlName];

    // validator for username
    if (controlName === 'username') {
      if (this.mailControl.value !== '') {
        control.disable();
        control.markAsUntouched();
        return false;
      } else {
        control.enable();
      }

      if (control.value === '' && this.mailControl.value === '' && control.touched && this.mailControl.touched) {

        return true;
      }
      return false;
    }

    // validator for mail
    if (controlName === 'mail') {
      if (this.usernameControl.value !== '') {
        control.disable();
        control.markAsUntouched();
        return false;
      } else {
        control.enable();
      }

      // validator for password
      if (control.value === '' && this.usernameControl.value === '' && control.touched && this.usernameControl.touched) {
        return true;
      }
    }

    return !control.valid && control.touched;
  }

  public touchFormControls() {
    for (let control in this.userLoginForm.controls) {
      this.userLoginForm.controls[control].markAsTouched();
    }
  }

  public login() {
    this.touchFormControls();
    let usernameFormControl = this.userLoginForm.controls['username'];
    let emailFormControl = this.userLoginForm.controls['mail'];
    let passwordFormControl = this.userLoginForm.controls['password'];

    if (passwordFormControl.invalid) {
      return;
    }

    this.user.password = this.userLoginForm.controls['password'].value;

    if (usernameFormControl.valid) {
      this.user.mail = '';
      this.user.username = this.userLoginForm.controls['username'].value;
      this.submit();
      return;
    }

    if (emailFormControl.valid && emailFormControl.value !== '') {
      this.user.username = '';
      this.user.mail = this.userLoginForm.controls['mail'].value;
      this.submit();
      return;
    }

  }

  private submit() {
    if (this.alreadySubmitted) {
      return;
    }
    this.alreadySubmitted = true;
    this.userService.loginUser(this.user)
      .subscribe(res => {
          this.router.navigateByUrl('/appointmentrequests');
        },
        err => {
          this.wrongCombination = true;
          this.alreadySubmitted = false;
          if (this.user.username.length === 0) {
            this.user.username = '';
            this.wrongEntry = 'E-Mail';
          } else {
            this.user.mail = '';
            this.wrongEntry = 'Benutzername';
          }
        }, () => {
        this.alreadySubmitted = false;
        }
      );
  }

}
