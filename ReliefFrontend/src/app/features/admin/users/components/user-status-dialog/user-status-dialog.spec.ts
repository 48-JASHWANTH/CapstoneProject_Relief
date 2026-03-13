import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserStatusDialog } from './user-status-dialog';

describe('UserStatusDialog', () => {
  let component: UserStatusDialog;
  let fixture: ComponentFixture<UserStatusDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserStatusDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserStatusDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
