import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResendAccountConfirmationEmailComponent } from './resend-account-confirmation-email.component';

describe('ResendAccountConfirmationEmailComponent', () => {
  let component: ResendAccountConfirmationEmailComponent;
  let fixture: ComponentFixture<ResendAccountConfirmationEmailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResendAccountConfirmationEmailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResendAccountConfirmationEmailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
