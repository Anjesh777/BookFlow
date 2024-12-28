import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestttComponent } from './testtt.component';

describe('TestttComponent', () => {
  let component: TestttComponent;
  let fixture: ComponentFixture<TestttComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestttComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TestttComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
