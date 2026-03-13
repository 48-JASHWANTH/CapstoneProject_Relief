import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClaimDecisionDialog } from './claim-decision-dialog';

describe('ClaimDecisionDialog', () => {
  let component: ClaimDecisionDialog;
  let fixture: ComponentFixture<ClaimDecisionDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClaimDecisionDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClaimDecisionDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
