import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subscription } from "rxjs";
import { ActivatedRoute } from "@angular/router";
import { appState } from "../../app.reducers";
import { Store } from "@ngrx/store";
import { map } from "rxjs/operators";
import { InitVoteAction } from "../vote.actions";

@Component({
  selector: 'app-vote-form',
  templateUrl: './vote-form.component.html',
  styleUrls: ['./vote-form.component.css'],
})
export class VoteFormComponent implements OnInit, OnDestroy {
  busy: boolean = false;
  error?: any;
  editing: boolean = false;
  readonly redirectParams: Observable<{ redirect: string }>;
  private routeSub?: Subscription;
  private storeSub?: Subscription;

  constructor(
    private activeRoute: ActivatedRoute,
    private store: Store<appState>,
  ) {
    this.redirectParams = activeRoute.url.pipe(
      map(segments => segments.join('/')),
      map(url => ({ redirect: url })),
    );
  }

  ngOnInit() {
    this.routeSub = this.activeRoute.params.pipe(
      map(params => params['id']),
    ).subscribe(id => {
      this.editing = !!id;
      this.store.dispatch(new InitVoteAction(id));
    });
    this.storeSub = this.store.select('vote')
      .subscribe(state => {
        this.busy = state.busy;
        if (state.vote) delete this.error;
        else this.error = state.error;
      });
  }

  refetchData(): void {
    const id = this.activeRoute.snapshot.params['id'];
    this.editing = !!id;
    this.store.dispatch(new InitVoteAction(id));
  }

  ngOnDestroy() {
    this.routeSub?.unsubscribe();
    this.storeSub?.unsubscribe();
  }
}
