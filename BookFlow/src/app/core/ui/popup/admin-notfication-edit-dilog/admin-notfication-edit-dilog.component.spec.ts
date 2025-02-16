import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminNotficationEditDilogComponent } from './admin-notfication-edit-dilog.component';

describe('AdminNotficationEditDilogComponent', () => {
  let component: AdminNotficationEditDilogComponent;
  let fixture: ComponentFixture<AdminNotficationEditDilogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminNotficationEditDilogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminNotficationEditDilogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
