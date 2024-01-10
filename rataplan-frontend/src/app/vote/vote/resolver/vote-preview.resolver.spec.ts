import { TestBed } from '@angular/core/testing';

import { VotePreviewResolver } from './vote-preview.resolver';

describe('VotePreviewResolver', () => {
  let service: VotePreviewResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VotePreviewResolver);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
