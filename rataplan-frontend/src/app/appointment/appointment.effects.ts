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
import { AppointmentRequestModel, deserializeAppointmentRequestModel } from "../models/appointment-request.model";
import { ActivatedRoute, Router } from "@angular/router";
import { DecisionType } from "./appointment-request-form/decision-type.enum";

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
          decisionType: DecisionType.DEFAULT,
        },
        appointments: [],
        appointmentMembers: [],
        consigneeList: [],
      }));
      else return this.urlService.appointmentURL$.pipe(
        switchMap(url => this.http.get<AppointmentRequestModel<true>>(url + "/appointmentRequests/edit/" + action.id)),
        map(deserializeAppointmentRequestModel),
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
      if (request.request.id) {
        const sanatizedRequest: Partial<AppointmentRequestModel> = {...request.request};
        if(!request.appointmentsEdited) {
          delete sanatizedRequest.appointments;
          delete sanatizedRequest.appointmentMembers;
        }
        return {editToken: request.request.editToken || request.request.id?.toString(), request: this.http.put<AppointmentRequestModel<true>>(url + "/appointmentRequests/edit/" + (request.request.editToken || request.request.id), sanatizedRequest, {withCredentials: true})};
      }
      return {request: this.http.post<AppointmentRequestModel<true>>(url + "/appointmentRequests", request.request, {withCredentials: true})};
    }),
    switchMap(({request, editToken}) => request.pipe(
      map(deserializeAppointmentRequestModel),
      map(created => new PostAppointmentRequestSuccessAction(created, editToken)),
      catchError(err => of(new PostAppointmentRequestErrorAction(err)))
    ))
  ));

  successFullPost = createEffect(() => this.actions$.pipe(
    ofType(AppointmentActions.POST_SUCCESS),
    map((action: PostAppointmentRequestSuccessAction) => this.router.navigate(["/vote/links"], {
      queryParams: {
        participationToken: action.created.participationToken || action.created.id,
        editToken: action.created.editToken || action.created.id,
      },
    })),
    switchMap(from)
  ), {dispatch: false});
}
