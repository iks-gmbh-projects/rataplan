import { TestBed } from '@angular/core/testing';

import { DeleteProfileService } from './delete-profile.service';

describe('DeleteProfileService', () => {
  let service: DeleteProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DeleteProfileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
