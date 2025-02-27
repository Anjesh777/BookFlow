import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddLedgerDialogComponent } from './add-ledger-dialog.component';

describe('AddLedgerDialogComponent', () => {
  let component: AddLedgerDialogComponent;
  let fixture: ComponentFixture<AddLedgerDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddLedgerDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddLedgerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
