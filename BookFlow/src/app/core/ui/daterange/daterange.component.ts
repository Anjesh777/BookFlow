import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-daterange',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule], 
  template: ` 
     <form [formGroup]="dateRangeForm" class="flex flex-wrap gap-4">
      <div class="flex items-center gap-2">
        <label class="text-sm font-medium text-gray-700">From:</label>
        <input
          type="date"
          formControlName="fromDate"
          class="px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none text-black"
          [max]="maxDate"
          (input)="onFromDateChange($event)"
        >
      </div>
      <div class="flex items-center gap-2">
        <label class="text-sm font-medium text-gray-700">To:</label>
        <input
          type="date"
          formControlName="toDate"
          class="px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none text-black"
          [min]="dateRangeForm.get('fromDate')?.value"
          [max]="maxDate"
          (input)="onToDateChange($event)"
        >
      </div>
    </form>`
})
export class DaterangeComponent implements OnInit {

  dateRangeForm: FormGroup;
  maxDate: string;
  @Output() dateRangeChange = new EventEmitter<{fromDate: string, toDate: string}>();

  constructor(private fb: FormBuilder) {
    this.maxDate = new Date().toISOString().split('T')[0];
    
    this.dateRangeForm = this.fb.group({
      fromDate: [''],
      toDate: ['']
    });
  }

  ngOnInit() {
    this.dateRangeForm.patchValue({
      toDate: this.maxDate
    });

    this.dateRangeForm.valueChanges.subscribe(value => {
      if (value.fromDate && value.toDate) {
        this.dateRangeChange.emit(value);
      }
    });
  }

  onFromDateChange(event: Event) {
    const selectedDate = (event.target as HTMLInputElement).value;
    if (selectedDate > this.maxDate) {
      this.dateRangeForm.patchValue({
        fromDate: this.maxDate
      });
    }
  }

  onToDateChange(event: Event) {
    const selectedDate = (event.target as HTMLInputElement).value;
    if (selectedDate > this.maxDate) {
      this.dateRangeForm.patchValue({
        toDate: this.maxDate
      });
    }
  }


}