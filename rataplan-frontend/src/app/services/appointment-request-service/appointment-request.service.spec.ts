import { TestBed, async, inject } from '@angular/core/testing';
import { AppointmentRequestService } from './appointment-request.service';
import { HttpModule } from '@angular/http';
import { EnvironmentService } from '../environment-service/environment.service';

describe('Service: AppointmentRequest', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [AppointmentRequestService, EnvironmentService]
    });
  });

  it('should ...', inject([AppointmentRequestService], (service: AppointmentRequestService) => {
    expect(service).toBeTruthy();
  }));
});
