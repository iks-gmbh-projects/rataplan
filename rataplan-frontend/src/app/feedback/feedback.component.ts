import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatLegacySnackBar as MatSnackBar, MatLegacySnackBarConfig as MatSnackBarConfig } from '@angular/material/legacy-snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, of, Subscription } from 'rxjs';

import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';
import { FeedbackCategory, FeedbackService } from './feedback.service';

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css'],
})
export class FeedbackComponent implements OnInit, OnDestroy {
  readonly form = new FormGroup({
    title: new FormControl<string>('', Validators.required),
    text: new FormControl<string>('', Validators.required),
    rating: new FormControl<number>(0, Validators.required),
    category: new FormControl<FeedbackCategory>(FeedbackCategory.GENERAL, Validators.required),
  });
  
  readonly categories = FeedbackCategory;
  readonly ratings = [1, 2, 3, 4, 5] as const;
  private routeSub?: Subscription;
  editToken: string | undefined;
  participationToken: string | undefined;
  submissionUnsuccessful = false;
  
  constructor(
    private readonly feedbackService: FeedbackService,
    readonly errorMessageService: FormErrorMessageService,
    private readonly matSnackBar: MatSnackBar,
    private readonly route: ActivatedRoute,
    private router: Router,
  )
  {
  }
  
  public ngOnInit(): void {
    this.routeSub = this.route.queryParams.subscribe(({rating, category, editToken, participationToken}: {
      [name: string]: string | undefined
    }) => {
      const patch: {
        rating?: number,
        category?: FeedbackCategory,
      } = {};
      if(/[0-5]/.test(rating!)) patch.rating = Number(rating);
      if(/[0-9]+/.test(category!)) {
        patch.category = Number(category);
      } else {
        const catStr = category?.toUpperCase();
        switch(catStr) {
        case 'GENERAL':
        case 'VOTE':
        case 'SURVEY':
          patch.category = FeedbackCategory[catStr];
          break;
        }
      }
      this.editToken = editToken;
      this.participationToken = participationToken;
      this.form.patchValue(patch);
    });
  }
  
  public ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
  }
  
  setRating(rating: number) {
    const ratingControl = this.form.get('rating')!;
    if(ratingControl.value === rating) ratingControl.setValue(0);
    else ratingControl.setValue(rating);
  }
  
  submit() {
    if(!this.form.valid) return;
    const val = this.form.value;
    const config = new MatSnackBarConfig();
    config.duration = 10000;
    this.feedbackService.submitFeedback({
      title: val.title!,
      text: val.text!,
      rating: val.rating!,
      category: val.category!,
    }).pipe(catchError(() => of(false)))
      .subscribe((next) => {
          if(next) {
            this.matSnackBar.open('Abgabe erflogreich', '', config);
            if(this.editToken && this.participationToken) this.router.navigate(
              ['/vote/links'],
              {queryParams: {editToken: this.editToken, participationToken: this.participationToken}},
            );
          } else {
            if(this.editToken && this.participationToken) this.submissionUnsuccessful = true;
            this.matSnackBar.open('Abgabe fehlgeschlagen', '', config);
          }
        },
      );
  }
}
