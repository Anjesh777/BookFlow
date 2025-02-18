import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditServiceDilogComponent } from './edit-service-dilog.component';

describe('EditServiceDilogComponent', () => {
  let component: EditServiceDilogComponent;
  let fixture: ComponentFixture<EditServiceDilogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditServiceDilogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditServiceDilogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
