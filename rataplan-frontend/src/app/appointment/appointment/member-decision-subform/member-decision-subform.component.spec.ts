import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberDecisionSubformComponent } from './member-decision-subform.component';

describe('MemberDecisionSubformComponent', () => {
  let component: MemberDecisionSubformComponent;
  let fixture: ComponentFixture<MemberDecisionSubformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MemberDecisionSubformComponent ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MemberDecisionSubformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
