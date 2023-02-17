import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from "rxjs";
import { ActivatedRoute } from "@angular/router";
import { appState } from "../../app.reducers";
import { Store } from "@ngrx/store";
import { map } from "rxjs/operators";
import { InitAppointmentRequestAction } from "../appointment.actions";

@Component({
  selector: 'app-appointment-request-form',
  templateUrl: './appointment-request-form.component.html',
  styleUrls: ['./appointment-request-form.component.css'],
})
export class AppointmentRequestFormComponent implements OnInit, OnDestroy {
  busy: boolean = false;
  editing: boolean = false;
  private routeSub?: Subscription;
  private storeSub?: Subscription;
  constructor(
    private activeRoute: ActivatedRoute,
    private store: Store<appState>
  ) {
  }

  ngOnInit() {
    this.routeSub = this.activeRoute.params.pipe(
      map(params => params['id'])
    ).subscribe(id => {
      this.editing = !!id;
      this.store.dispatch(new InitAppointmentRequestAction(id));
    })
    this.storeSub = this.store.select('appointmentRequest')
      .subscribe(state => this.busy = state.busy);
  }

  ngOnDestroy() {
    this.routeSub?.unsubscribe();
    this.storeSub?.unsubscribe();
  }
}
