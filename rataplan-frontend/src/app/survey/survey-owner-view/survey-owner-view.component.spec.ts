import { ClipboardModule } from '@angular/cdk/clipboard';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';

import { SurveyOwnerViewComponent } from './survey-owner-view.component';

describe('SurveyOwnerViewComponent', () => {
  let component: SurveyOwnerViewComponent;
  let fixture: ComponentFixture<SurveyOwnerViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SurveyOwnerViewComponent ],
      imports: [ RouterTestingModule, MatSnackBarModule, ClipboardModule, MatCardModule, MatButtonModule, MatIconModule ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyOwnerViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
