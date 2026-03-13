import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentFormDialog } from './agent-form-dialog';

describe('AgentFormDialog', () => {
  let component: AgentFormDialog;
  let fixture: ComponentFixture<AgentFormDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgentFormDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgentFormDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
