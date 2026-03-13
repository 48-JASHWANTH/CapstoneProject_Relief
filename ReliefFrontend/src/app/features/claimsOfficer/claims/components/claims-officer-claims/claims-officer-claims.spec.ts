import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClaimsOfficerClaims } from './claims-officer-claims';

describe('ClaimsOfficerClaims', () => {
  let component: ClaimsOfficerClaims;
  let fixture: ComponentFixture<ClaimsOfficerClaims>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClaimsOfficerClaims]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClaimsOfficerClaims);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
