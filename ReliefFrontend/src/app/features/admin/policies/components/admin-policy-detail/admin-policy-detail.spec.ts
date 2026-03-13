import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPolicyDetail } from './admin-policy-detail';

describe('AdminPolicyDetail', () => {
  let component: AdminPolicyDetail;
  let fixture: ComponentFixture<AdminPolicyDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPolicyDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPolicyDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
