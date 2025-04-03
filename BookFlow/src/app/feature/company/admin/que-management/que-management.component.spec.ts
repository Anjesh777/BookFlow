import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QueManagementComponent } from './que-management.component';

describe('QueManagementComponent', () => {
  let component: QueManagementComponent;
  let fixture: ComponentFixture<QueManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QueManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QueManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
