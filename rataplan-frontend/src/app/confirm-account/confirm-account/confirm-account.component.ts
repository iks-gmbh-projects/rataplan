import { Component, OnInit } from '@angular/core';
import { MatLegacySnackBar as MatSnackBar, MatLegacySnackBarConfig as MatSnackBarConfig } from '@angular/material/legacy-snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap } from 'rxjs';

import { ConfirmAccountService, ConfirmationStatus } from '../confirm-account-service';

@Component({
  selector: 'app-confirm-account',
  templateUrl: './confirm-account.component.html',
  styleUrls: ['./confirm-account.component.css']
})
export class ConfirmAccountComponent implements OnInit {

  confirmationFailed = false;

  constructor(private route: ActivatedRoute, private confirmAccountService: ConfirmAccountService, private router: Router, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.route.params.pipe(
      switchMap(params => this.confirmAccountService.confirmAccount(params['token']))
    ).subscribe(confirmed => {
      const snackbarConfig = new MatSnackBarConfig();
      snackbarConfig.duration = 10000;
      switch (confirmed) {
        case ConfirmationStatus.ACCOUNT_CONFIRMATION_SUCCESSFUL:
          this.router.navigate(['/login']);
          this.snackBar.open('Konto Bestätigung erfolgreich', '', snackbarConfig);
          break;
        case ConfirmationStatus.ACCOUNT_PREVIOUSLY_CONFIRMED:
          this.router.navigate(['/login']);
          this.snackBar.open('Konto wurde schon bestätigt', '', snackbarConfig);
          break;
        case ConfirmationStatus.ACCOUNT_CONFIRMATION_UNSUCCESSFUL:
          this.confirmationFailed = true;
          break;
      }
    });
  }


}
