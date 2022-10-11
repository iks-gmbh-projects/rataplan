import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

import { FrontendUser } from '../../register/register.component';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient) {
  }

  public registerUser(frontendUser: FrontendUser) {
    const url = environment.rataplanBackendURL+'users/register';

    return this.http.post<any>(url, frontendUser);
  }

  public checkIfMailExists(mail: string) {
    const url = environment.rataplanBackendURL+'users/mailExists';

    return this.http.post<string>(url, mail, { headers: new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' }) });
  }

  public checkIfUsernameExists(username: string) {
    const url = environment.rataplanBackendURL+'users/usernameExists';

    return this.http.post<string>(url, username, { headers: new HttpHeaders({ 'Content-Type': 'application/json;charset=utf-8' }) });
  }
}
