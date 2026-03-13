import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdjustPremiumDialog } from './adjust-premium-dialog';

describe('AdjustPremiumDialog', () => {
  let component: AdjustPremiumDialog;
  let fixture: ComponentFixture<AdjustPremiumDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdjustPremiumDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdjustPremiumDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
