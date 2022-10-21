import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../environments/environment";

@Injectable({providedIn: "root"})

export class ChangeDisplayNameService {

  constructor(private httpClient: HttpClient) {
  }


  changeDisplayName(displayName: String){
    const url = environment.authBackendURL+ 'users/profile/changeDisplayName'

    const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

    return this.httpClient.post<any>(url,displayName, httpOptions)
    }


}
