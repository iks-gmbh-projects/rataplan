import { TestBed } from '@angular/core/testing';

import { VoteFormService } from './vote-form.service';

describe('VoteFormService', () => {
  let service: VoteFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VoteFormService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
