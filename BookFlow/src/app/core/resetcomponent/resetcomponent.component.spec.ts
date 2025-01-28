import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResetcomponentComponent } from './resetcomponent.component';

describe('ResetcomponentComponent', () => {
  let component: ResetcomponentComponent;
  let fixture: ComponentFixture<ResetcomponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResetcomponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResetcomponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
