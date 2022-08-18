import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ResetPasswordData} from "../../models/resetPasswordData";

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {

  constructor(private httpClient: HttpClient) {
  }

  resetPassword(resetPasswordData: ResetPasswordData) {
    let url = 'http://localhost:8080/v1/users/resetPassword';

    return this.httpClient.post<any>(url, resetPasswordData, {headers: new HttpHeaders({"Content-Type": "application/json;charset=utf-8"})})
  }
}
