import { TestBed } from '@angular/core/testing';

import { FormErrorMessageService } from './form-error-message.service';

describe('FormErrorMessageService', () => {
  let service: FormErrorMessageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormErrorMessageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
