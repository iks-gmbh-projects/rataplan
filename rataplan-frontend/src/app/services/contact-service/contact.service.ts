import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { EnvironmentService } from '../environment-service/environment.service';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import * as _ from 'lodash';
import { ErrorHandlerService } from '../error-handler-service/error-handler.service';

@Injectable()
export class ContactService {

  private requestOptions: RequestOptions = new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) });

  constructor(private http: Http,
              private environmentService: EnvironmentService,
              private errorHandlerService: ErrorHandlerService) {
  }

  public contact(contact) {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    let url = this.environmentService.backendUrl + '/contacts';

    return this.http.post(url, JSON.stringify(contact), options)
      .map(res => res.json())
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error));
      });
  }

}
