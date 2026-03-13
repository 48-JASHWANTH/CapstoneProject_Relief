import { TestBed } from '@angular/core/testing';

import { CustomerClaims } from './customer-claims';

describe('CustomerClaims', () => {
  let service: CustomerClaims;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomerClaims);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
