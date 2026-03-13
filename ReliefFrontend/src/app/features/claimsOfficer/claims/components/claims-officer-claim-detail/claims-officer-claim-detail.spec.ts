import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClaimsOfficerClaimDetail } from './claims-officer-claim-detail';

describe('ClaimsOfficerClaimDetail', () => {
  let component: ClaimsOfficerClaimDetail;
  let fixture: ComponentFixture<ClaimsOfficerClaimDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClaimsOfficerClaimDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClaimsOfficerClaimDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
