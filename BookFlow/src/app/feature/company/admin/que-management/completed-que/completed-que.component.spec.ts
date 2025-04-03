import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompletedQueComponent } from './completed-que.component';

describe('CompletedQueComponent', () => {
  let component: CompletedQueComponent;
  let fixture: ComponentFixture<CompletedQueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompletedQueComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompletedQueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
