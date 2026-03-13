import { TestBed } from '@angular/core/testing';

import { CustomerDashboard } from './customer-dashboard';

describe('CustomerDashboard', () => {
  let service: CustomerDashboard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomerDashboard);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
