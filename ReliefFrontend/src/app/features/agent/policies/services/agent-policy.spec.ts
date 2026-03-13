import { TestBed } from '@angular/core/testing';

import { AgentPolicy } from './agent-policy';

describe('AgentPolicy', () => {
  let service: AgentPolicy;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AgentPolicy);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
