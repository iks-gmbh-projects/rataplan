import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomValidators } from '../../../utilities/custom-validators';
import { User } from '../../../models/users/user';
import { UserService } from '../../../services/user-service/user.service';
import { ErrorCode } from '../../../models/error-codes/error-codes.component';

@Component({
  selector: 'rp-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css']
})
export class UserRegistrationComponent implements OnInit {
  public userRegistrationForm: FormGroup;

  public user: User;
  public alreadySubmitted = false;
  public confirmPassword: string;
  public mailInUse = false;
  public usernameInUse = false;

  public spinnerStyles = { 'padding': '10px 15px', 'width': '81px'};

  constructor(private fb: FormBuilder,
              private router: Router,
              private userService: UserService) {
    this.userRegistrationForm = this.fb.group({
      username: ['', Validators.required],
      mail: ['', [Validators.required, CustomValidators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
      firstname: [''],
      lastname: [''],
      termsAndConditions: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.user = new User();
    this.user.mail = '';
  }

  public hasErrors(controlName: string): boolean {
    let control = this.userRegistrationForm.controls[controlName];
    return !control.valid && control.touched;
  }

  private touchFormControls() {
    for (let controlName in this.userRegistrationForm.controls) {
      this.userRegistrationForm.controls[controlName].markAsTouched();
    }
  }

  public passwordsMatching() {
    let password = this.userRegistrationForm.controls['password'];
    let confirmPassword = this.userRegistrationForm.controls['confirmPassword'];

    return password.value === confirmPassword.value || password.untouched || confirmPassword.untouched;
  }

  public isAGBChecked() {
    if (this.userRegistrationForm.controls['termsAndConditions'].value === '') {
      return false;
    }
    return !this.userRegistrationForm.controls['termsAndConditions'].value;
  }

  public saveUser() {
    this.touchFormControls();

    if (this.userRegistrationForm.valid && this.passwordsMatching() && this.userRegistrationForm.controls['termsAndConditions'].value) {

      this.user.username = this.userRegistrationForm.controls['username'].value;
      this.user.mail = this.userRegistrationForm.controls['mail'].value;
      this.user.password = this.userRegistrationForm.controls['password'].value;
      this.user.firstname = this.userRegistrationForm.controls['firstname'].value;
      this.user.lastname = this.userRegistrationForm.controls['lastname'].value;

      if (this.alreadySubmitted) {
        return;
      }
      this.alreadySubmitted = true;
      this.userService.registerUser(this.user)
        .subscribe(res => {
          this.router.navigateByUrl('/profile');
        },
        err => {
          if (err.errorCode === ErrorCode[ErrorCode.USERNAME_IN_USE]) {
            this.usernameInUse = true;
            this.mailInUse = false;
          } else if (err.errorCode === ErrorCode[ErrorCode.MAIL_IN_USE]) {
            this.usernameInUse = false;
            this.mailInUse = true;
          }
          this.alreadySubmitted = false;
        }, () => {
            this.alreadySubmitted = false;
        });
    }
  }

  public changeTermsAndConditions() {
    this.userRegistrationForm.controls['termsAndConditions'].setValue(!this.userRegistrationForm.controls['termsAndConditions'].value);
  }

}
