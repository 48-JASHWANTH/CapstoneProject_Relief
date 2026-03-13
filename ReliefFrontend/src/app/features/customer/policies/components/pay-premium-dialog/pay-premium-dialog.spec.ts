import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PayPremiumDialog } from './pay-premium-dialog';

describe('PayPremiumDialog', () => {
  let component: PayPremiumDialog;
  let fixture: ComponentFixture<PayPremiumDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PayPremiumDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PayPremiumDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
