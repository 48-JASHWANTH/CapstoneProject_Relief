import { TestBed } from '@angular/core/testing';

import { CustomerPayment } from './customer-payment';

describe('CustomerPayment', () => {
  let service: CustomerPayment;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomerPayment);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
