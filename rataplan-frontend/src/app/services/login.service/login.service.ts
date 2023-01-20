import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'
import { FrontendUser } from './user.model';
import { LocalstorageService } from "../localstorage-service/localstorage.service";
import { catchError, exhaustMap, map } from 'rxjs/operators';
import { BackendUrlService } from "../backend-url-service/backend-url.service";


@Injectable({providedIn: 'root'})

export class LoginService {


  constructor(private httpClient: HttpClient,
              public localStorageService: LocalstorageService,
              private urlService: BackendUrlService) {
  }


  public loginUser(frontendUser: FrontendUser) {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        let url = authURL + 'users/login'

        const requestOptions = {
          params: new HttpParams()
        };

        requestOptions.params.set('Content-Type', 'application/json');


        return this.httpClient.post<FrontendUser>(url, frontendUser, {withCredentials: true});
      })
    );
  }

  public logoutUser() {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        let url = authURL + 'users/logout'

        return this.httpClient.get<any>(url, {withCredentials: true});
      })
    ).subscribe(console.log);

  }


  public getUserData() {
    return this.urlService.authURL$.pipe(
      exhaustMap(authURL => {
        let url = authURL + 'users/profile'


        const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

        return this.httpClient.get<FrontendUser>(url, httpOptions)
      }),
      map(res => {
        this.localStorageService.setLocalStorage(res)
      }),
      catchError(err => {
        console.log(err)
        return err;
      }));

  }

}
