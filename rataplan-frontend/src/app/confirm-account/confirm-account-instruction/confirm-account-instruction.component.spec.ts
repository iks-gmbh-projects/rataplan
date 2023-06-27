import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmAccountInstructionComponent } from './confirm-account-instruction.component';

describe('ConfirmAccountWallComponent', () => {
  let component: ConfirmAccountInstructionComponent;
  let fixture: ComponentFixture<ConfirmAccountInstructionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfirmAccountInstructionComponent ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmAccountInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
