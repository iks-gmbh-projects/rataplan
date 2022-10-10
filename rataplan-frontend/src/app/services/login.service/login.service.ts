import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams, HttpRequest} from '@angular/common/http'
import {FrontendUser} from '../../login/login.component';
import {AbstractControl, ValidationErrors} from "@angular/forms";
import {LocalstorageService} from "../localstorage-service/localstorage.service";
import {catchError, map} from 'rxjs/operators';


@Injectable({providedIn: 'root'})

export class LoginService {






  constructor(private httpClient: HttpClient,
              public localStorageService: LocalstorageService) {}




  public loginUser (frontendUser: FrontendUser){

    let url = 'http://localhost:8080/v1/users/login'

    const requestOptions = {
      params: new HttpParams()
    };

    requestOptions.params.set('Content-Type','application/json');


    return this.httpClient.post<any> (url,frontendUser,{withCredentials: true});
    }

    public logoutUser () {
    let url = 'http://localhost:8080/v1/users/logout'

      return this.httpClient.get<any>(url,{withCredentials: true}).subscribe(console.log);

    }


    public getUserData() {
    let url = 'http://localhost:8080/v1/users/profile'


      const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

    return this.httpClient.get<FrontendUser>(url, httpOptions).pipe(
      map(res => {
          this.localStorageService.setLocalStorage(res)
    }),
      catchError(err => {
        console.log(err)
    return err;
    }))

    }







    cannotContainWhitespace(control: AbstractControl) : ValidationErrors | null {
    if ((control.value as string).indexOf(' ') >= 0) {
      return {cannotContainWhitespace: true}
    }
    return null
  }

}
