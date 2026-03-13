import { TestBed } from '@angular/core/testing';

import { AdminRiskPool } from './admin-risk-pool';

describe('AdminRiskPool', () => {
  let service: AdminRiskPool;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminRiskPool);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
