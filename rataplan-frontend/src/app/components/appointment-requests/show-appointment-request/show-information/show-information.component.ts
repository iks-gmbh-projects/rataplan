import { Component, Input, OnInit } from '@angular/core';
import { AppointmentRequest } from '../../../../models/appointment-requests/appointment-request';
import { ExportService } from '../../../../services/export-service/export.service';

@Component({
  selector: 'rp-show-information',
  templateUrl: './show-information.component.html',
  styleUrls: ['./show-information.component.css', './../show-appointment-request.component.css']
})
export class ShowInformationComponent implements OnInit {

  @Input() appointmentRequest;
  @Input() dateFormat;

  constructor(private _exportService: ExportService) { }

  ngOnInit() {
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
