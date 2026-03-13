import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerClaimDetail } from './customer-claim-detail';

describe('CustomerClaimDetail', () => {
  let component: CustomerClaimDetail;
  let fixture: ComponentFixture<CustomerClaimDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerClaimDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerClaimDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
