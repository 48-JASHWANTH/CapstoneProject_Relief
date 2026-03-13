import { TestBed } from '@angular/core/testing';

import { ClaimsOfficerClaims } from './claims-officer-claims';

describe('ClaimsOfficerClaims', () => {
  let service: ClaimsOfficerClaims;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClaimsOfficerClaims);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
