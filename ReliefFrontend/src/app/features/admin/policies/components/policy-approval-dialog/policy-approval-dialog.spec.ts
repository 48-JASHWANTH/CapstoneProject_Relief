import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PolicyApprovalDialog } from './policy-approval-dialog';

describe('PolicyApprovalDialog', () => {
  let component: PolicyApprovalDialog;
  let fixture: ComponentFixture<PolicyApprovalDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PolicyApprovalDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PolicyApprovalDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
