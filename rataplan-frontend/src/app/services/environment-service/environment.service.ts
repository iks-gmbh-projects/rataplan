import { Injectable } from '@angular/core';

import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { environment } from 'environments/environment';

@Injectable()
export class EnvironmentService {
  public backendUrl: string;
  public baseUrl: string;

  constructor(private http: Http) {
  }

  load(): Promise<any> {
    let promise: Promise<any>;
    this.backendUrl = environment.backendUrl;
    this.baseUrl = environment.baseUrl;

    if (environment.heroku) {
      promise = this.http.get(document.location.origin + '/environment')
        .map(res => res.json()).toPromise()
        .then(res => {
          this.backendUrl = res.backendUrl;
          this.baseUrl = document.location.origin;
        }).catch(error => error.json().error);
    } else {
      promise = Promise.resolve();
    }
    return promise;
  }
}
