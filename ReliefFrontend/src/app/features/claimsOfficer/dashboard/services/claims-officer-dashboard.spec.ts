import { TestBed } from '@angular/core/testing';

import { ClaimsOfficerDashboard } from './claims-officer-dashboard';

describe('ClaimsOfficerDashboard', () => {
  let service: ClaimsOfficerDashboard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClaimsOfficerDashboard);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
