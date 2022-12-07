import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { ResetPasswordDataModel } from '../../models/reset-password-data.model';
import { BackendUrlService } from "../backend-url-service/backend-url.service";
import { exhaustMap } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {

  constructor(private httpClient: HttpClient, private urlService: BackendUrlService) {
  }

  resetPassword(resetPasswordData: ResetPasswordDataModel) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        const url = authURL + 'users/resetPassword';

        return this.httpClient.post<any>(url, resetPasswordData, {headers: new HttpHeaders({'Content-Type': 'application/json;charset=utf-8'})});
      })
    );
  }
}
