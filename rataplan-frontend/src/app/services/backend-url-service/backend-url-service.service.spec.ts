import { TestBed } from '@angular/core/testing';

import { BackendUrlService } from './backend-url.service';

describe('BackendUrlServiceService', () => {
  let service: BackendUrlService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BackendUrlService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
