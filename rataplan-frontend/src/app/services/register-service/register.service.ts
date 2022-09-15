import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { FrontendUser } from '../../components/register/register.component';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient) {
  }

  public registerUser(frontendUser: FrontendUser) {
    const url = 'http://localhost:8080/v1/users/register';

    return this.http.post<any>(url, frontendUser);
  }

  public checkIfMailExists(mail: string) {
    const url = 'http://localhost:8080/v1/users/mailExists';

    return this.http.post<string>(url, mail, { headers: new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' }) });
  }

  public checkIfUsernameExists(username: string) {
    const url = 'http://localhost:8080/v1/users/usernameExists';

    return this.http.post<string>(url, username, { headers: new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' }) });
  }
}
