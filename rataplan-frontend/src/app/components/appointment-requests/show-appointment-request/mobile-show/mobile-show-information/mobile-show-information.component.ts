import { Component, Input, OnInit } from '@angular/core';
import { AppointmentRequest } from '../../../../../models/appointment-requests/appointment-request';
import { ExportService } from '../../../../../services/export-service/export.service';
import { buildDateFormatString } from '../../../../../utilities/utilities';

@Component({
  selector: 'rp-mobile-show-information',
  templateUrl: './mobile-show-information.component.html',
  styleUrls: ['./mobile-show-information.component.css']
})
export class MobileShowInformationComponent implements OnInit {

  @Input() appointmentRequest;
  public dateFormat = [];

  public hasStartDate: boolean;
  public hasStartTime: boolean;
  public hasEndDate: boolean;
  public hasEndTime: boolean;
  public hasUrl: boolean;
  constructor(private _exportService: ExportService) { }

  ngOnInit() {

    this.hasStartDate = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.startDate;
    this.hasStartTime = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.startTime;
    this.hasEndDate = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.endDate;
    this.hasEndTime = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.endTime;
    this.hasUrl = this.appointmentRequest.appointmentRequestConfig.appointmentConfig.url &&
      this.appointmentRequest.appointments.some(appointment => appointment.url !== undefined);

    this.dateFormat[0] = buildDateFormatString(this.hasStartDate, this.hasStartTime);
    this.dateFormat[1] = buildDateFormatString(this.hasEndDate, this.hasEndTime);
    this.dateFormat[2] = 'HH:mm [Uhr]';
  }

  public exportPdf(appointmentRequest: AppointmentRequest) {
    let content = this._exportService.exportAsCsv(appointmentRequest, this.dateFormat);
    let fileName = appointmentRequest.title + '.csv';

    if (window.navigator.msSaveOrOpenBlob) {  // IE hack; see http://msdn.microsoft.com/en-us/library/ie/hh779016.aspx
      let blob = new Blob([content], { type: 'text/plain;charset=utf-8;' });
      window.navigator.msSaveBlob(blob, fileName);
    } else {

      let pom = document.createElement('a');
      pom.setAttribute('href', 'data:text/csv;charset=utf-8,\uFEFF' + encodeURIComponent(content));
      pom.setAttribute('download', fileName);

      if (document.createEvent) {
        let event = document.createEvent('MouseEvents');
        event.initEvent('click', true, true);
        pom.dispatchEvent(event);
      } else {
        pom.click();
      }

    }
  }

}
