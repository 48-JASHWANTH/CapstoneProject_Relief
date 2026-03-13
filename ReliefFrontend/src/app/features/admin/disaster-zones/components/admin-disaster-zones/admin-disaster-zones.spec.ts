import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDisasterZones } from './admin-disaster-zones';

describe('AdminDisasterZones', () => {
  let component: AdminDisasterZones;
  let fixture: ComponentFixture<AdminDisasterZones>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDisasterZones]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDisasterZones);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
