import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookflowNotificationEditDialogComponent } from './bookflow-notification-edit-dialog.component';

describe('BookflowNotificationEditDialogComponent', () => {
  let component: BookflowNotificationEditDialogComponent;
  let fixture: ComponentFixture<BookflowNotificationEditDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookflowNotificationEditDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookflowNotificationEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
