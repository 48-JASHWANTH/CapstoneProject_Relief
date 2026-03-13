import { TestBed } from '@angular/core/testing';

import { AdminRole } from './admin-role';

describe('AdminRole', () => {
  let service: AdminRole;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminRole);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
