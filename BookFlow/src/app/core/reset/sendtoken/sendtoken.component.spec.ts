import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SendtokenComponent } from './sendtoken.component';

describe('SendtokenComponent', () => {
  let component: SendtokenComponent;
  let fixture: ComponentFixture<SendtokenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SendtokenComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SendtokenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
