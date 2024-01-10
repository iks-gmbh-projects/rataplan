import { TestBed } from '@angular/core/testing';

import { VoteResolver } from './vote.resolver';

describe('VoteResolver', () => {
  let service: VoteResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VoteResolver);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
