import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminRiskPools } from './admin-risk-pools';

describe('AdminRiskPools', () => {
  let component: AdminRiskPools;
  let fixture: ComponentFixture<AdminRiskPools>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminRiskPools]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminRiskPools);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
