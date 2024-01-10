import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-confirm-account-instruction',
  templateUrl: './confirm-account-instruction.component.html',
  styleUrls: ['./confirm-account-instruction.component.css']
})
export class ConfirmAccountInstructionComponent  {

  constructor(private router:Router) {}


  navigateToResendConfirmationEmail(){
    this.router.navigate(['resend-confirmation-email']);
  }




}
