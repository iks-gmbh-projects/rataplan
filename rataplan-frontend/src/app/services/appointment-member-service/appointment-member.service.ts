import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import * as _ from 'lodash';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { AppointmentMember } from '../../models/appointment-requests/appointment-member';
import { EnvironmentService } from '../environment-service/environment.service';

@Injectable()
export class AppointmentMemberService {
  private requestOptions: RequestOptions = new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) });

  constructor(private http: Http, private environmentService: EnvironmentService) {
  }

  public addAppointmentMember(appointmentMember: AppointmentMember, accessToken?: string) {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    if (accessToken) {
      options.headers.append('accesstoken', accessToken);
    }

    return this.http.post(this.environmentService.backendUrl + '/appointmentRequests/' + appointmentMember.appointmentRequestId + '/appointmentMembers', JSON.stringify(appointmentMember), options)
      .map(res => res.json())
      .catch((error: any) => {
        if (error.status === 403) {
          return Observable.throw('Forbidden');
        } else {
          return Observable.throw(error.json().error || 'Server error');
        }
      });
  }

  public updateAppointmentMember(appointmentMember: AppointmentMember, accessToken?: string) {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    if (accessToken) {
      options.headers.append('accesstoken', accessToken);
    }

    return this.http.put(this.environmentService.backendUrl + '/appointmentRequests/' + appointmentMember.appointmentRequestId + '/appointmentMembers/' + appointmentMember.id,
      JSON.stringify(appointmentMember), options)
      .map(res => res.json())
      .catch((error: any) => {
        return Observable.throw('Update failed');
      });
  }

  public deleteAppointmentMember(appointmentMember: AppointmentMember, accessToken?: string) {
    let options: RequestOptions = _.cloneDeep(this.requestOptions);
    options.withCredentials = true;

    if (accessToken) {
      options.headers.append('accesstoken', accessToken);
    }

    return this.http.delete(this.environmentService.backendUrl + '/appointmentRequests/' + appointmentMember.appointmentRequestId + '/appointmentMembers/' + appointmentMember.id, options);
  }
}
