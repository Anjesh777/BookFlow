import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCashbookDialogComponent } from './add-cashbook-dialog.component';

describe('AddCashbookDialogComponent', () => {
  let component: AddCashbookDialogComponent;
  let fixture: ComponentFixture<AddCashbookDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddCashbookDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddCashbookDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
