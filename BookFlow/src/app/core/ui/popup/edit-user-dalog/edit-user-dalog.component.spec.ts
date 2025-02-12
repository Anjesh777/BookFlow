import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditUserDalogComponent } from './edit-user-dalog.component';

describe('EditUserDalogComponent', () => {
  let component: EditUserDalogComponent;
  let fixture: ComponentFixture<EditUserDalogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditUserDalogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditUserDalogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
