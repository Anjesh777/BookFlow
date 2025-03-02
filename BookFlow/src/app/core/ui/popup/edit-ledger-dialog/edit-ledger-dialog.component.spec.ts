import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditLedgerDialogComponent } from './edit-ledger-dialog.component';

describe('EditLedgerDialogComponent', () => {
  let component: EditLedgerDialogComponent;
  let fixture: ComponentFixture<EditLedgerDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditLedgerDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditLedgerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
