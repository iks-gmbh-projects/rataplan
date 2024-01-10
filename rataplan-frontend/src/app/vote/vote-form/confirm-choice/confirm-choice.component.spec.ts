import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmChoiceComponent } from './confirm-choice.component';

describe('ConfirmChoiceComponent', () => {
  let component: ConfirmChoiceComponent;
  let fixture: ComponentFixture<ConfirmChoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfirmChoiceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmChoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
