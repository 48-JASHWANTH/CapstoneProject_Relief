import { TestBed } from '@angular/core/testing';

import { AgentDashboard } from './agent-dashboard';

describe('AgentDashboard', () => {
  let service: AgentDashboard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AgentDashboard);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
