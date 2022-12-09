import { TestBed } from '@angular/core/testing';

import { VoteListService } from './vote-list.service';

describe('DashboardService', () => {
  let service: VoteListService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VoteListService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
