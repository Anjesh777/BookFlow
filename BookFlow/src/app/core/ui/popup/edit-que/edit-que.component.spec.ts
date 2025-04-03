import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditQueComponent } from './edit-que.component';

describe('EditQueComponent', () => {
  let component: EditQueComponent;
  let fixture: ComponentFixture<EditQueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditQueComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditQueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
