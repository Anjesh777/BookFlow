import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookflowNotificationComponent } from './bookflow-notification.component';




describe('BookflowNotificationComponent', () => {
  let component: BookflowNotificationComponent;
  let fixture: ComponentFixture<BookflowNotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookflowNotificationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookflowNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
