import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterTestingModule } from '@angular/router/testing';

import { SurveyService } from '../survey/survey.service';
import { VoteListComponent } from './vote-list.component';

describe('SurveyListComponent', () => {
  let component: VoteListComponent;
  let fixture: ComponentFixture<VoteListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VoteListComponent ],
      imports: [ RouterTestingModule, HttpClientTestingModule, MatCardModule, MatButtonModule, MatProgressSpinnerModule, MatIconModule ],
      providers: [ SurveyService ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VoteListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
