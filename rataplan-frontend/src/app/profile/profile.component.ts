import { Component, OnInit } from '@angular/core';
import {LoginService} from "../services/login.service/login.service";
import {FormControl, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ChangeEmailService} from "../services/change-profile-service/change-email-service";
import {ChangeDisplayNameService} from "../services/change-profile-service/change-displayName-service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  constructor(private loginService: LoginService,
              private router: Router,
              private route: ActivatedRoute,
              private changeEmailService: ChangeEmailService,
              private updateUser: LoginService,
              private changeDisplayNameService: ChangeDisplayNameService) { }

  ngOnInit(): void {
  }


  setDisplayName() {
    return localStorage.getItem('displayname');

  }

  setEmail() {
    return localStorage.getItem('mail');

  }

  payloadArray={
    enabled:false
  }

  displayNameField = new FormControl('', [Validators.required, Validators.minLength(3)])
  emailField = new FormControl('', [Validators.required , Validators.minLength(3), this.loginService.cannotContainWhitespace, Validators.email])




  displayNameFieldError(){
    if (this.displayNameField.hasError('cannotContainWhitespace')){
      return 'Zeichen nicht erlaubt'
    }
    return this.displayNameField.hasError("minLength") ? '' : 'Der Anzeigename muss mindstens 3 Zeichen lang sein!'
  }
  emailFieldError(){
    if (this.emailField.hasError('cannotContainWhitespace')){
      return 'Zeichen nicht erlaubt'
    }else if (this.emailField.hasError('email')){
      return 'Keine valide Email'
    }
    return this.emailField.hasError('minLength') ? 'Diese Email ist zu kurz!' : ''
  }
  redirect(){
    this.router.navigateByUrl('/change-password')
  }

  changeDisplayName(){
      const displayName = this.displayNameField.value
    if (this.displayNameField.valid) {
      this.changeDisplayNameService.changeDisplayName(displayName).subscribe(res => {
        this.displayNameField.markAsUntouched()
        this.displayNameField.setValue('');
        this.updateUser.getUserData().subscribe(res => {
          console.log(res)
        }, error => {
          console.log(error)
        })
      })
    }

  }
  changeEmail() {
    const email = this.emailField.value
    if (this.emailField.valid){
      this.changeEmailService.changeEmail(email).subscribe({next: res => {
          this.emailField.setValue('');
          this.emailField.markAsUntouched()
          this.updateUser.getUserData().subscribe(res => {
            console.log(res)
          }, error => {
            console.log(error)
          })
        }, error: error => {
          console.log(error)
        }})
    }
  }



}
