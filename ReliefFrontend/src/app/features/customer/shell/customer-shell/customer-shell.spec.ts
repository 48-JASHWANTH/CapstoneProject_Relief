import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerShell } from './customer-shell';

describe('CustomerShell', () => {
  let component: CustomerShell;
  let fixture: ComponentFixture<CustomerShell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerShell]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerShell);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
