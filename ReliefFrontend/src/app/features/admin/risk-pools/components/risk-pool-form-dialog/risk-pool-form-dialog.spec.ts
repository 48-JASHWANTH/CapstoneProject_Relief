import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RiskPoolFormDialog } from './risk-pool-form-dialog';

describe('RiskPoolFormDialog', () => {
  let component: RiskPoolFormDialog;
  let fixture: ComponentFixture<RiskPoolFormDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RiskPoolFormDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RiskPoolFormDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
