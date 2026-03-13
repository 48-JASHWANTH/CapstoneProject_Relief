import { TestBed } from '@angular/core/testing';

import { AdminDisasterZone } from './admin-disaster-zone';

describe('AdminDisasterZone', () => {
  let service: AdminDisasterZone;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminDisasterZone);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
