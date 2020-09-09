import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'rp-appointment-config-subform',
  templateUrl: './appointment-config-subform.component.html',
  styleUrls: ['./appointment-config-subform.component.css']
})
export class AppointmentConfigSubformComponent implements OnChanges {

  @Input() appointmentConfiguration: FormGroup;
  @Input() hasAppointments;

  constructor() {
  }

  ngOnChanges(change: SimpleChanges) {
    if (change['hasAppointments']) {
      this.hasAppointments ? this.appointmentConfiguration.disable() : this.appointmentConfiguration.enable();
    }
  }

  public isOneChecked() {
    for (let key of Object.keys(this.appointmentConfiguration.value)) {
      if (this.appointmentConfiguration.get(key).value != false) {
        return true;
      }
    }
    return false;
  }

}
