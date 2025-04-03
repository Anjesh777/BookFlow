import { CommonModule } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { QueResponse } from '../../../../core/auth/model/que';
import { BookingResponse, BookingStatus, BookingUpdateFullRequest, BookingUpdateRequest, PaymentMethod, PaymentStatus } from '../../../../core/auth/model/booking';

@Component({
  selector: 'app-edit-que',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule
  ],
  template: `
  <div class="fixed inset-0 p-4 flex flex-wrap justify-center items-center w-full h-full z-[1000] before:fixed before:inset-0 before:w-full before:h-full before:bg-[rgba(0,0,0,0.5)] overflow-auto font-[sans-serif]">
    <div class="w-full max-w-lg bg-white shadow-lg rounded-lg p-6 relative">
        <!-- Close Button -->
        <svg xmlns="http://www.w3.org/2000/svg" (click)="onCancel()" class="w-3.5 cursor-pointer shrink-0 fill-gray-400 hover:fill-red-500 float-right" viewBox="0 0 320.591 320.591">
            <path d="M30.391 318.583a30.37 30.37 0 0 1-21.56-7.288c-11.774-11.844-11.774-30.973 0-42.817L266.643 10.665c12.246-11.459 31.462-10.822 42.921 1.424 10.362 11.074 10.966 28.095 1.414 39.875L51.647 311.295a30.366 30.366 0 0 1-21.256 7.288z"></path>
            <path d="M287.9 318.583a30.37 30.37 0 0 1-21.257-8.806L8.83 51.963C-2.078 39.225-.595 20.055 12.143 9.146c11.369-9.736 28.136-9.736 39.504 0l259.331 257.813c12.243 11.462 12.876 30.679 1.414 42.922-.456.487-.927.958-1.414 1.414a30.368 30.368 0 0 1-23.078 7.288z"></path>
        </svg>

        <h4 class="text-xl text-gray-800 font-semibold mb-6">Edit Booking</h4>

        <form [formGroup]="bookingForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Date Field -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Appointment Date</label>
                <input [matDatepicker]="picker" formControlName="appointmentDate"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                <mat-datepicker-toggle [for]="picker"></mat-datepicker-toggle>
                <mat-datepicker #picker></mat-datepicker>
                <span *ngIf="bookingForm.get('appointmentDate')?.hasError('required') && bookingForm.get('appointmentDate')?.touched"
                    class="text-sm text-red-500">Appointment date is required</span>
            </div>

            <!-- Time Field -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Appointment Time</label>
                <input type="time" formControlName="appointmentTime" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                <span *ngIf="bookingForm.get('appointmentTime')?.hasError('required') && bookingForm.get('appointmentTime')?.touched"
                    class="text-sm text-red-500">Appointment time is required</span>
            </div>

            <!-- Booking Status -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Booking Status</label>
                <select formControlName="bookingStatus"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                  <option *ngFor="let status of bookingStatusOptions" [value]="status">
                    {{status}}
                  </option>
                </select>
            </div>

            <!-- Payment Status -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Payment Status</label>
                <select formControlName="paymentStatus"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                  <option *ngFor="let status of paymentStatusOptions" [value]="status">
                    {{status}}
                  </option>
                </select>
            </div>

            <!-- Payment Method -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Payment Method</label>
                <select formControlName="paymentMethod"
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                  <option *ngFor="let method of paymentMethodOptions" [value]="method">
                    {{method}}
                  </option>
                </select>
            </div>

            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Booking Notes</label>
                <textarea formControlName="bookingNotes" rows="3"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter any additional notes"></textarea>
            </div>

            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="onCancel()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" [disabled]="!bookingForm.valid"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    Update Booking
                </button>
            </div>
        </form>
    </div>
  </div>
  `
})
export class EditQueComponent implements OnInit {
  bookingForm: FormGroup;
  bookingStatusOptions = Object.values(BookingStatus);
  paymentStatusOptions = Object.values(PaymentStatus);
  paymentMethodOptions = Object.values(PaymentMethod);
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditQueComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { booking: BookingResponse | null }
  ) {
    const booking = data.booking;
    
    this.bookingForm = this.fb.group({
      bookingId: [booking?.bookingId || ''],
      serviceId: [booking?.serviceId || ''],
      appointmentDate: [booking?.appointmentDate ? new Date(booking.appointmentDate) : new Date(), Validators.required],
      appointmentTime: [this.extractTimeFromISOString(booking?.appointmentDate) || this.getCurrentTime(), Validators.required],
      bookingStatus: [booking?.bookingStatus || BookingStatus.PENDING],
      paymentStatus: [this.getPaymentStatusFromBooking(booking) || PaymentStatus.PENDING],
      paymentMethod: [booking?.paymentMethod || PaymentMethod.CASH], 
      bookingNotes: [booking?.bookingNotes || '']
    });
  }

  ngOnInit(): void {
    console.log('Initial form values:', this.bookingForm.value);
  }

  onSubmit(): void {
    if (this.bookingForm.valid) {
      this.isSubmitting = true;
      const formValues = this.bookingForm.value;
      
      const dateObj = new Date(formValues.appointmentDate);
      const [hours, minutes] = formValues.appointmentTime.split(':');
      dateObj.setHours(Number(hours), Number(minutes), 0, 0);
      const appointmentDateISO = dateObj.toISOString();
      
      const updateRequest: BookingUpdateFullRequest = {
        bookingId: formValues.bookingId,
        serviceId: formValues.serviceId,
        appointmentDate: appointmentDateISO,
        bookingNotes: formValues.bookingNotes,
        bookingStatus: formValues.bookingStatus,
        paymentStatus: formValues.paymentStatus,
        paymentMethod: formValues.paymentMethod
      };

      console.log('Submitting data:');
      console.log('Payment Status:', updateRequest.paymentStatus);
      console.log('Payment Method:', updateRequest.paymentMethod);
      console.log('Full Update Request:', JSON.stringify(updateRequest));
      
      this.dialogRef.close(updateRequest);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  private extractTimeFromISOString(isoString?: string): string {
    if (!isoString) return this.getCurrentTime();
    const date = new Date(isoString);
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  }
  
  private getCurrentTime(): string {
    const now = new Date();
    return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
  }
  
  private getPaymentStatusFromBooking(booking?: BookingResponse | null): PaymentStatus {
    if (!booking) return PaymentStatus.PENDING;
    
    if (typeof booking.paymentStatus === 'string' && 
        Object.values(PaymentStatus).includes(booking.paymentStatus as PaymentStatus)) {
      return booking.paymentStatus as PaymentStatus;
    }
    
    if (typeof booking.paymentStatus === 'boolean') {
      return booking.paymentStatus ? PaymentStatus.SUCCESS : PaymentStatus.PENDING;
    }
        return PaymentStatus.PENDING;
  }
}