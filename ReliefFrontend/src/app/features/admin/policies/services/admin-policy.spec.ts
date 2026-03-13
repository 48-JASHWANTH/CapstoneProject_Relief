import { TestBed } from '@angular/core/testing';

import { AdminPolicy } from './admin-policy';

describe('AdminPolicy', () => {
  let service: AdminPolicy;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminPolicy);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
