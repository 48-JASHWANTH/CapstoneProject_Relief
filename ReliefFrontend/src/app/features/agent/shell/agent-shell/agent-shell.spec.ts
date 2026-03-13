import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentShell } from './agent-shell';

describe('AgentShell', () => {
  let component: AgentShell;
  let fixture: ComponentFixture<AgentShell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgentShell]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgentShell);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
