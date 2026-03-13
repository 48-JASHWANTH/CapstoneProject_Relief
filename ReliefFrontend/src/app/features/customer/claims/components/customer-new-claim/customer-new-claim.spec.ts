import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerNewClaim } from './customer-new-claim';

describe('CustomerNewClaim', () => {
  let component: CustomerNewClaim;
  let fixture: ComponentFixture<CustomerNewClaim>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerNewClaim]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerNewClaim);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
