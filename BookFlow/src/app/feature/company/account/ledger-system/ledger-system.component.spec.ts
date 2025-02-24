import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LedgerSystemComponent } from './ledger-system.component';

describe('LedgerSystemComponent', () => {
  let component: LedgerSystemComponent;
  let fixture: ComponentFixture<LedgerSystemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LedgerSystemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LedgerSystemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
