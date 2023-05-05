import { TestBed } from '@angular/core/testing';

import { AppointmentRequestResolver } from './appointment-request.resolver';

describe('AppointmentRequestResolver', () => {
  let service: AppointmentRequestResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppointmentRequestResolver);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
