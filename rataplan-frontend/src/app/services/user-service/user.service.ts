import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Router } from '@angular/router';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/map';
import { EnvironmentService } from '../environment-service/environment.service';
import { User } from '../../models/users/user';
import { isNullOrUndefined } from 'util';
import { Observable } from 'rxjs/Observable';
import * as _ from 'lodash';
import { ErrorHandlerService } from '../error-handler-service/error-handler.service';
import { ChangePassword } from '../../models/users/change-password';

@Injectable()
export class UserService {
  private backendUrl: string = this.environmentService.backendUrl;
  private requestOptions: RequestOptions = new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) });

  private subject = new Subject<any>();

  constructor(private http: Http,
              private router: Router,
              private environmentService: EnvironmentService,
              private errorHandlerService: ErrorHandlerService) {
  }

  public registerUser(user: User) {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    return this.http.post(this.backendUrl + '/users/register', JSON.stringify(user), options)
      .map(res => {
        this.setLocalStorage(res.json());
        this.router.navigateByUrl('/profile');
      })
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error));
      });
  }

  public loginUser(user: User) {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    return this.http.post(this.backendUrl + '/users/login', JSON.stringify(user), options)
      .map(res => {
        this.setLocalStorage(res.json());
        return;
      })
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error));
      });
  }

  public getUserData() {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    return this.http.get(this.backendUrl + '/users/profile', options)
      .map(res => {
        this.setLocalStorage(res.json());
        return res.json();
      })
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error));
      });
  }

  public logout() {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    return this.http.get(this.backendUrl + '/users/logout', options)
      .map(res => {
        this.subject.next();
        localStorage.removeItem('rp_username');
        localStorage.removeItem('rp_id');
        if (isNullOrUndefined(localStorage.getItem('rp_username'))) {
          return true;
        }
        return false;
      })
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error));
      });
  }

  public changePassword(passwords: ChangePassword) {

    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    return this.http.post(this.backendUrl + '/users/profile/changePassword', JSON.stringify(passwords), options)
      .map(res => {

      })
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error));
      });
  }

  public hasUser(): boolean {
    return !isNullOrUndefined(JSON.parse(localStorage.getItem('rp_username')));
  }

  private setLocalStorage(user) {
    localStorage.setItem('rp_username', JSON.stringify(user.username));
    localStorage.setItem('rp_id', JSON.stringify(user.id));
  }

}
