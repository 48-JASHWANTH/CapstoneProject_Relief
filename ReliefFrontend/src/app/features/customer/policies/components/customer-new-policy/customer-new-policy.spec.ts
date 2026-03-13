import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerNewPolicy } from './customer-new-policy';

describe('CustomerNewPolicy', () => {
  let component: CustomerNewPolicy;
  let fixture: ComponentFixture<CustomerNewPolicy>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerNewPolicy]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerNewPolicy);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
