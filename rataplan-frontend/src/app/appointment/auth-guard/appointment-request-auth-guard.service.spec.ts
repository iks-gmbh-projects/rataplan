import { TestBed } from '@angular/core/testing';

import { AppointmentRequestAuthGuard } from './appointment-request-auth-guard.service';

describe('AppointmentRequestAuthGuard', () => {
  let service: AppointmentRequestAuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppointmentRequestAuthGuard);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
