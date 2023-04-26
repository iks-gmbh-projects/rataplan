import { TestBed } from '@angular/core/testing';

import { AppointmentRequestPreviewResolver } from './appointment-request-preview.resolver';

describe('AppointmentRequestPreviewResolver', () => {
  let service: AppointmentRequestPreviewResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppointmentRequestPreviewResolver);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
