import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

import { MemberDecisionSubformComponent } from './member-decision-subform/member-decision-subform.component';


@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.css']
})
export class AppointmentComponent implements OnInit {

  appointmentMember = 0;
  appointments = ['Tennis', 'Basketball', 'Fußball', 'Schwimmen'];
  appointmentDecision = [0, 0, 0, 0];
  memberName : string | null = null;
  isVoted = false;

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
    if (localStorage.getItem('member')) {
      this.memberName = localStorage.getItem('member');
    }
  }

  saveVote() {
    this.appointmentMember++;
    this.isVoted = true;
    this.saveName();
  }

  openDialog(appointmentTitle: string, index: number) {
    this.dialog.open(MemberDecisionSubformComponent, {
      data: {
        name: this.memberName,
        appointments: appointmentTitle,
        appointmentDecision: this.appointmentDecision[index],
        isVoted: this.isVoted,
      }
    });
  }

  setVoteDecision() {
  }

  saveName() {
    if (this.memberName != null && this.memberName.length != 0) {
      localStorage.setItem('member', this.memberName);
    } else {
      localStorage.removeItem('member');
    }
  }

  acceptAppointment(appointment: string) {
    const position = this.appointments.indexOf(appointment);
    this.appointmentDecision[position] = 1;
  }

  rejectAppointment(appointment: string) {
    const position = this.appointments.indexOf(appointment);
    this.appointmentDecision[position] = 3;
  }
}
