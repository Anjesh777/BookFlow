import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelledQueComponent } from './cancelled-que.component';

describe('CancelledQueComponent', () => {
  let component: CancelledQueComponent;
  let fixture: ComponentFixture<CancelledQueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelledQueComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancelledQueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
