import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { delay, Observable, of, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { voteFormAction } from './state/vote-form.action';
import { voteFormFeature } from './state/vote-form.feature';

@Component({
  selector: 'app-vote-form',
  templateUrl: './vote-form.component.html',
  styleUrls: ['./vote-form.component.css'],
})
export class VoteFormComponent implements OnInit, OnDestroy {
  readonly busy$: Observable<boolean>;
  readonly delayedBusy$: Observable<boolean>;
  error?: any;
  editing: boolean = false;
  readonly redirectParams: Observable<{redirect: string}>;
  private routeSub?: Subscription;
  private storeSub?: Subscription;
  
  constructor(
    private activeRoute: ActivatedRoute,
    private store: Store,
  )
  {
    this.redirectParams = activeRoute.url.pipe(
      map(segments => segments.join('/')),
      map(url => (
        {redirect: url}
      )),
    );
    this.busy$ = this.store.select(voteFormFeature.selectBusy);
    this.delayedBusy$ = this.busy$.pipe(
      switchMap(v => v ? of(v).pipe(delay(1000)) : of(v)),
    );
  }
  
  ngOnInit() {
    this.routeSub = this.activeRoute.params.pipe(
      map(params => params['id']),
    ).subscribe(id => {
      this.editing = !!id;
      this.store.dispatch(voteFormAction.init({id}));
    });
    this.storeSub = this.store.select(voteFormFeature.selectVoteFormState)
      .subscribe(state => {
        if(state.vote) delete this.error;
        else this.error = state.error;
      });
  }
  
  refetchData(): void {
    const id = this.activeRoute.snapshot.params['id'];
    this.editing = !!id;
    this.store.dispatch(voteFormAction.init({id}));
  }
  
  ngOnDestroy() {
    this.routeSub?.unsubscribe();
    this.storeSub?.unsubscribe();
  }
}