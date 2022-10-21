import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";

@Injectable({providedIn: "root"})

export class ChangeDisplayNameService {

  constructor(private httpClient: HttpClient) {
  }


  changeDisplayName(displayName: String){
    const url = 'http://localhost:8080/v1/users/profile/changeDisplayName'

    const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

    return this.httpClient.post<any>(url,displayName, httpOptions)
    }


}
