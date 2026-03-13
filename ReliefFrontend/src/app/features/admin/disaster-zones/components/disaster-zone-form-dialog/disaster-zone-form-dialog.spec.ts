import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisasterZoneFormDialog } from './disaster-zone-form-dialog';

describe('DisasterZoneFormDialog', () => {
  let component: DisasterZoneFormDialog;
  let fixture: ComponentFixture<DisasterZoneFormDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisasterZoneFormDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DisasterZoneFormDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
