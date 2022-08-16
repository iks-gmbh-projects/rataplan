import { TestBed } from '@angular/core/testing';

import { AppointmentRequestFormService } from './appointment-request-form.service';

describe('AppointmentRequestFormService', () => {
  let service: AppointmentRequestFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppointmentRequestFormService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
