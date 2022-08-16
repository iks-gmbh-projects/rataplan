import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateOverviewSubformComponent } from './date-overview-subform.component';

describe('DateOverviewSubformComponent', () => {
  let component: DateOverviewSubformComponent;
  let fixture: ComponentFixture<DateOverviewSubformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DateOverviewSubformComponent],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateOverviewSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
