import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DaybookEntriesComponent } from './daybook-entries.component';

describe('DaybookEntriesComponent', () => {
  let component: DaybookEntriesComponent;
  let fixture: ComponentFixture<DaybookEntriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DaybookEntriesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DaybookEntriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
