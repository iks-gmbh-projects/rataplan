import { TestBed } from '@angular/core/testing';

import { VoteAuthGuard } from './vote-auth-guard.service';

describe('VoteAuthGuard', () => {
  let service: VoteAuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VoteAuthGuard);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
