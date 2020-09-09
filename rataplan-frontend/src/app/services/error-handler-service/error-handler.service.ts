import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable()
export class ErrorHandlerService {

  constructor(private router: Router) {
  }

  public handle(error, id?) {
    switch (error.status) {

      // CONNECTION_REFUSED
      case 0:
        this.router.navigateByUrl('/unavailable');
        break;

      // FORBIDDEN
      case 403:
        return error.json();

      // NOT_FOUND
      case 404:
        if (window.location.pathname.indexOf('appointmentrequest') !== -1) {
          this.router.navigateByUrl('/not-found/' + id);
        }

        return error;

      // GONE
      case 410:
        return error.json();

      // SERVICE_UNAVAILABLE
      case 503:
        this.router.navigateByUrl('/unavailable');
        break;
    }
  }

}
