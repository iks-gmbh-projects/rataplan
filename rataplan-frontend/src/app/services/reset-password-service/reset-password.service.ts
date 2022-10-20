import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { ResetPasswordDataModel } from '../../models/reset-password-data.model';

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {

  constructor(private httpClient: HttpClient) {
  }

  resetPassword(resetPasswordData: ResetPasswordDataModel) {
    const url = environment.authBackendURL+'users/resetPassword';

    return this.httpClient.post<any>(url, resetPasswordData, { headers: new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' }) });
  }
}
