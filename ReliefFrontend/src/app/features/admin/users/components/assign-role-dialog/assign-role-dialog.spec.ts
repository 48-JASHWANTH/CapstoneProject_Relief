import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignRoleDialog } from './assign-role-dialog';

describe('AssignRoleDialog', () => {
  let component: AssignRoleDialog;
  let fixture: ComponentFixture<AssignRoleDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssignRoleDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignRoleDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
