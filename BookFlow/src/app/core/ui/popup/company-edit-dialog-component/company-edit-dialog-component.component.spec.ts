import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyEditDialogComponentComponent } from './company-edit-dialog-component.component';

describe('CompanyEditDialogComponentComponent', () => {
  let component: CompanyEditDialogComponentComponent;
  let fixture: ComponentFixture<CompanyEditDialogComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanyEditDialogComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompanyEditDialogComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
