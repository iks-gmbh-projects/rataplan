import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { ResetPasswordDataModel } from '../../models/reset-password-data.model';

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {

  constructor(private httpClient: HttpClient) {
  }

  resetPassword(resetPasswordData: ResetPasswordDataModel) {
    const url = 'http://localhost:8080/v1/users/resetPassword';

    return this.httpClient.post<any>(url, resetPasswordData, { headers: new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' }) });
  }
}
