import { TestBed } from '@angular/core/testing';

import { UsernameEmailValidatorsService } from './username-email-validators.service';

describe('UsernameEmailValidatorsService', () => {
  let service: UsernameEmailValidatorsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UsernameEmailValidatorsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
