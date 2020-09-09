import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { EnvironmentService } from '../environment-service/environment.service';
import { AppointmentRequest } from '../../models/appointment-requests/appointment-request';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';
import * as _ from 'lodash';
import { ErrorHandlerService } from '../error-handler-service/error-handler.service';

@Injectable()
export class AppointmentRequestService {

  private requestOptions: RequestOptions = new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) });

  constructor(private http: Http,
              private environmentService: EnvironmentService,
              private errorHandlerService: ErrorHandlerService) {
  }

  public createAppointmentRequest(appointmentRequest: AppointmentRequest) {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    let url = this.environmentService.backendUrl + '/appointmentRequests';

    return this.http.post(url, appointmentRequest.toJSONTransferObject(), options)
      .map(res => res.json())
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error));
      });
  }

  public getAppointmentRequest(id: number, requestToken?: string) {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    if (requestToken) {
      options.headers.append('accesstoken', requestToken);
    }

    return this.http.get(this.environmentService.backendUrl + '/appointmentRequests/' + id, options)
      .map(res => {
        return res.json();
      })
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error, id));
      });
  }

  public getAppointmentRequestForEdit(id: number, requestToken?: string) {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    if (requestToken) {
      options.headers.append('accesstoken', requestToken);
    }

    return this.http.get(this.environmentService.backendUrl + '/appointmentRequests/' + id + '/edit', options)
      .map(res => {
        return res.json();
      })
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error, id));
      });
  }

  public getAppointmentRequestsCreatedbyUser() {
    let options: RequestOptions = this.requestOptions;
    options.withCredentials = true;

    return this.http.get(this.environmentService.backendUrl + '/users/appointmentRequests/creations', options)
      .map(res => {
        return res.json();
      })
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error));
      });
  }

  public getParticipateAppointmentRequests() {
    let options: RequestOptions = this.requestOptions;
    options.withCredentials = true;

    return this.http.get(this.environmentService.backendUrl + '/users/appointmentRequests/participations', options)
      .map(res => {
        return res.json();
      })
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error));
      });
  }

  public updateAppointmentRequest(appointmentRequest: AppointmentRequest, accessToken?: string) {
    let options = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    if (accessToken) {
      options.headers.append('accesstoken', accessToken);
    }

    return this.http.put(this.environmentService.backendUrl + '/appointmentRequests/' + appointmentRequest.id,
      appointmentRequest.toJSONTransferObject(), options)
      .map(res => res.json())
      .catch((error: any) => {
        return Observable.throw(this.errorHandlerService.handle(error, appointmentRequest.id));
      });
  }

}
