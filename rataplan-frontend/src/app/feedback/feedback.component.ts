import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { catchError, of } from 'rxjs';

import { FormErrorMessageService } from '../services/form-error-message-service/form-error-message.service';
import { FeedbackCategory, FeedbackService } from './feedback.service';

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css'],
})
export class FeedbackComponent {
  readonly form = new FormGroup({
    title: new FormControl<string>('', Validators.required),
    text: new FormControl<string>('', Validators.required),
    rating: new FormControl<number>(0, Validators.required),
    category: new FormControl<FeedbackCategory>(FeedbackCategory.GENERAL, Validators.required),
  });
  
  readonly categories = FeedbackCategory;
  readonly ratings = [1, 2, 3, 4, 5] as const;
  
  constructor(
    private readonly feedbackService: FeedbackService,
    readonly errorMessageService: FormErrorMessageService,
    private matSnackBar: MatSnackBar,
  )
  {
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
          if(next) this.matSnackBar.open('Abgabe erflogreich', '', config);
          else this.matSnackBar.open('Abgabe fehlgeschlagen', '', config);
        },
      );
  }
}
