import { Injectable } from "@angular/core";
import { Actions, concatLatestFrom, createEffect, ofType } from "@ngrx/effects";
import { appState } from "../app.reducers";
import { Store } from "@ngrx/store";
import {
  AppointmentActions,
  InitAppointmentRequestAction,
  InitAppointmentRequestErrorAction,
  InitAppointmentRequestSuccessAction,
  PostAppointmentRequestErrorAction,
  PostAppointmentRequestSuccessAction
} from "./appointment.actions";
import { catchError, delayWhen, from, of, switchMap, take } from "rxjs";
import { BackendUrlService } from "../services/backend-url-service/backend-url.service";
import { HttpClient } from "@angular/common/http";
import { filter, map } from "rxjs/operators";
import { AppointmentRequestModel } from "../models/appointment-request.model";
import { ActivatedRoute, Router } from "@angular/router";

@Injectable({
  providedIn: "root",
})
export class AppointmentRequestEffects {
  constructor(
    private readonly actions$: Actions,
    private readonly store: Store<appState>,
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly activeRoute: ActivatedRoute,
    private readonly urlService: BackendUrlService
  ) {
  }

  initAppointmentRequest = createEffect(() => this.actions$.pipe(
    ofType(AppointmentActions.INIT),
    switchMap((action: InitAppointmentRequestAction) => {
      if (!action.id) return of(new InitAppointmentRequestSuccessAction({
        title: "",
        deadline: "",
        appointmentRequestConfig: {
          appointmentConfig: {
            startDate: true,
            startTime: false,
            endDate: false,
            endTime: false,
            description: false,
            url: false,
          },
          decisionType: "DEFAULT",
        },
        appointments: [],
        appointmentMembers: [],
        consigneeList: [],
      }));
      else return this.urlService.appointmentURL$.pipe(
        switchMap(url => this.http.get<AppointmentRequestModel>(url + "/appointmentRequests/edit/" + action.id)),
        map(request => new InitAppointmentRequestSuccessAction(request)),
        catchError(err => of(new InitAppointmentRequestErrorAction(err)))
      );
    })
  ));

  postAppointmentRequest = createEffect(() => this.actions$.pipe(
    ofType(AppointmentActions.POST),
    concatLatestFrom(() => this.store.select("appointmentRequest")),
    map(([, state]) => state),
    filter(state => !!state.complete),
    map(state => ({request: state.appointmentRequest!, appointmentsEdited: state.appointmentsChanged})),
    delayWhen(() => this.store.select("auth").pipe(
      filter(authState => !authState.busy),
      take(1)
    )),
    concatLatestFrom(() => this.urlService.appointmentURL$),
    map(([request, url]) => {
      if (request.request.editToken) {
        const sanatizedRequest: Partial<AppointmentRequestModel> = {...request.request};
        if(!request.appointmentsEdited) {
          delete sanatizedRequest.appointments;
          delete sanatizedRequest.appointmentMembers;
        }
        return this.http.put<AppointmentRequestModel>(url + "/appointmentRequests/edit/" + request.request.editToken, sanatizedRequest, {withCredentials: true});
      }
      return this.http.post<AppointmentRequestModel>(url + "/appointmentRequests", request.request, {withCredentials: true});
    }),
    switchMap(request => request.pipe(
      map(created => new PostAppointmentRequestSuccessAction(created)),
      catchError(err => of(new PostAppointmentRequestErrorAction(err)))
    ))
  ));

  successFullPost = createEffect(() => this.actions$.pipe(
    ofType(AppointmentActions.POST_SUCCESS),
    map((action: PostAppointmentRequestSuccessAction) => this.router.navigate(["..", "links"], {
      relativeTo: this.activeRoute,
      queryParams: {
        participationToken: action.created.participationToken,
        editToken: action.created.editToken,
      },
    })),
    switchMap(from)
  ), {dispatch: false});
}
