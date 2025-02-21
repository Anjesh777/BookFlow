import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCashbookDialogComponent } from './edit-cashbook-dialog.component';

describe('EditCashbookDialogComponent', () => {
  let component: EditCashbookDialogComponent;
  let fixture: ComponentFixture<EditCashbookDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditCashbookDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditCashbookDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
