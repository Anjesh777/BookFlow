import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookingPortelComponent } from './booking-portel.component';

describe('BookingPortelComponent', () => {
  let component: BookingPortelComponent;
  let fixture: ComponentFixture<BookingPortelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookingPortelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookingPortelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
