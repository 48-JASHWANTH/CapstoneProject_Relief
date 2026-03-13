import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClaimsOfficerShell } from './claims-officer-shell';

describe('ClaimsOfficerShell', () => {
  let component: ClaimsOfficerShell;
  let fixture: ComponentFixture<ClaimsOfficerShell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClaimsOfficerShell]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClaimsOfficerShell);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
