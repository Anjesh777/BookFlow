import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { BookflowService } from '../../../auth/service/BookFlow-Service/bookflow.service';
import { UserService2Service } from '../../../auth/service/User-Service/user-service2.service';
import { BookingRequest, BookingResponse, BookingStatus } from '../../../auth/model/booking';
import { HttpErrorResponse } from '@angular/common/http';
import { Service } from '../../../auth/model/admin';

@Component({
  selector: 'app-add-booking-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
  
  <div class="fixed inset-0 p-4 flex flex-wrap justify-center items-center w-full h-full z-[1000] before:fixed before:inset-0 before:w-full before:h-full before:bg-[rgba(0,0,0,0.5)] overflow-auto font-[sans-serif]">
    <div class="w-full max-w-lg bg-white shadow-lg rounded-lg p-6 relative">
        <!-- Close Button -->
        <svg xmlns="http://www.w3.org/2000/svg" (click)="onCancel()" class="w-3.5 cursor-pointer shrink-0 fill-gray-400 hover:fill-red-500 float-right" viewBox="0 0 320.591 320.591">
            <path d="M30.391 318.583a30.37 30.37 0 0 1-21.56-7.288c-11.774-11.844-11.774-30.973 0-42.817L266.643 10.665c12.246-11.459 31.462-10.822 42.921 1.424 10.362 11.074 10.966 28.095 1.414 39.875L51.647 311.295a30.366 30.366 0 0 1-21.256 7.288z"></path>
            <path d="M287.9 318.583a30.37 30.37 0 0 1-21.257-8.806L8.83 51.963C-2.078 39.225-.595 20.055 12.143 9.146c11.369-9.736 28.136-9.736 39.504 0l259.331 257.813c12.243 11.462 12.876 30.679 1.414 42.922-.456.487-.927.958-1.414 1.414a30.368 30.368 0 0 1-23.078 7.288z"></path>
        </svg>

        <h4 class="text-xl text-gray-800 font-semibold mb-6">Book a Service</h4>

        <form [formGroup]="bookingForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Service Selection -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Service</label>
                <select formControlName="serviceId" (change)="onServiceChange()"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                    <option value="">Select a Service</option>
                    <option *ngFor="let service of services" [value]="service.service_id">
                        {{service.serviceName}} (रु {{service.price}})
                    </option>
                </select>
                <span *ngIf="bookingForm.get('serviceId')?.hasError('required') && bookingForm.get('serviceId')?.touched"
                    class="text-sm text-red-500">Service is required</span>
            </div>

            <!-- Duration Selection -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Duration</label>
                <div class="flex items-center gap-2">
                    <div class="flex-1">
                        <input type="number" formControlName="durationHours" min="0" max="12"
                            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <div class="text-xs text-gray-500 mt-1">Hours</div>
                    </div>
                    <div class="flex-1">
                        <select formControlName="durationMinutes"
                            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                            <option value="0">0 min</option>
                            <option value="15">15 min</option>
                            <option value="30">30 min</option>
                            <option value="45">45 min</option>
                        </select>
                        <div class="text-xs text-gray-500 mt-1">Minutes</div>
                    </div>
                </div>
                <div *ngIf="selectedService" class="text-xs text-gray-500 mt-1">
                    Base price: रु {{selectedService.price}} × {{calculateDurationInHours()}} hours
                    = रु {{calculateExpectedAmount()}}
                </div>
            </div>

            <!-- Appointment Date -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Appointment Date</label>
                <input type="date" formControlName="appointmentDate" 
                    [min]="minDate"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                <span *ngIf="bookingForm.get('appointmentDate')?.hasError('required') && bookingForm.get('appointmentDate')?.touched"
                    class="text-sm text-red-500">Appointment date is required</span>
            </div>

            <!-- Appointment Time -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Appointment Time</label>
                <div class="flex items-center gap-2">
                    <div class="flex-1">
                        <select formControlName="appointmentHour"
                            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                            <option value="">Hour</option>
                            <option value="9">9 AM</option>
                            <option value="10">10 AM</option>
                            <option value="11">11 AM</option>
                            <option value="12">12 PM</option>
                            <option value="13">1 PM</option>
                            <option value="14">2 PM</option>
                            <option value="15">3 PM</option>
                            <option value="16">4 PM</option>
                            <option value="17">5 PM</option>
                        </select>
                    </div>
                    <div class="flex-1">
                        <select formControlName="appointmentMinute"
                            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                            <option value="0">00</option>
                            <option value="15">15</option>
                            <option value="30">30</option>
                            <option value="45">45</option>
                        </select>
                    </div>
                </div>
                <span *ngIf="(bookingForm.get('appointmentHour')?.hasError('required') && bookingForm.get('appointmentHour')?.touched)"
                    class="text-sm text-red-500">Appointment time is required</span>
            </div>

            <!-- Payment Status -->
            <div class="space-y-2">
                <label class="flex items-center space-x-2">
                    <input type="checkbox" formControlName="paymentStatus"
                        class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500">
                    <span class="text-sm text-gray-700 font-medium">Payment Completed</span>
                </label>
            </div>

            <!-- Booking Notes -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Booking Notes</label>
                <textarea formControlName="bookingNotes" rows="3"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Any special requests or notes"></textarea>
            </div>

            <!-- Footer Actions -->
            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="onCancel()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" [disabled]="!bookingForm.valid || isLoading"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    {{isLoading ? 'Booking...' : 'Book Service'}}
                </button>
            </div>
        </form>
    </div>
  </div>
  
  `
 
})
export class AddBookingDialogComponent implements OnInit {
  bookingForm: FormGroup;
  isLoading = false;
  today: string = new Date().toISOString().split('T')[0];
  minDate: string;

  
  
  services: Service[] = [];
  selectedService: Service | null = null;

  bookflow = inject(BookflowService);

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddBookingDialogComponent>,
    private userService: UserService2Service
  ) {

    const dayAfterTomorrow = new Date();
    dayAfterTomorrow.setDate(dayAfterTomorrow.getDate() + 2);
    this.minDate = dayAfterTomorrow.toISOString().split('T')[0];

    this.bookingForm = this.fb.group({
      serviceId: ['', Validators.required],
      durationHours: [1, [Validators.required, Validators.min(0), Validators.max(12)]],
      durationMinutes: [0, Validators.required],
      appointmentDate: [this.minDate, Validators.required],
      appointmentHour: ['', Validators.required],
      appointmentMinute: [0, Validators.required],
      paymentStatus: [false],
      bookingNotes: ['']
    });
  }

  ngOnInit(): void {
    this.loadServices();
  }

  loadServices(): void {
    this.userService.getService().subscribe({
      next: (data) => {
        this.services = data;
      },
      error: (error) => {
        console.error('Failed to load services:', error);
      }
    });
  }
  
  onServiceChange(): void {
    const serviceId = this.bookingForm.get('serviceId')?.value;
    if (serviceId) {
      this.selectedService = this.services.find(s => s.service_id === serviceId) || null;
    } else {
      this.selectedService = null;
    }
  }
  
  calculateDurationInHours(): number {
    const hours = Number(this.bookingForm.get('durationHours')?.value || 0);
    const minutes = Number(this.bookingForm.get('durationMinutes')?.value || 0);
    return hours + (minutes / 60);
  }
  
  calculateExpectedAmount(): number {
    if (!this.selectedService) return 0;
    
    const durationInHours = this.calculateDurationInHours();
    return Math.round(this.selectedService.price * durationInHours);
  }

  onSubmit() {
    if (this.bookingForm.valid) {
      this.isLoading = true;
      const formData = this.bookingForm.value;
      
      const appointmentDate = new Date(formData.appointmentDate);
      appointmentDate.setHours(
        parseInt(formData.appointmentHour), 
        parseInt(formData.appointmentMinute)
      );
      
      const durationHours = Number(formData.durationHours || 0);
      const durationMinutes = Number(formData.durationMinutes || 0);
      const totalDuration = durationHours + (durationMinutes / 60);
      
      const endTime = new Date(appointmentDate);
      endTime.setMinutes(endTime.getMinutes() + (totalDuration * 60));
      
      const bookingData: BookingRequest = {
        serviceId: formData.serviceId,
        appointmentDate: appointmentDate.toISOString(),
        bookingDate: new Date().toISOString(),
        paymentStatus: formData.paymentStatus,
        bookingNotes: formData.bookingNotes,
        duration: totalDuration.toString() 
      };
  
      this.userService.createBooking(bookingData).subscribe({
        next: (response: BookingResponse) => {
          console.log('Booking created successfully:', response);
          this.dialogRef.close({
            success: true,
            booking: response
          });
        },
        error: (error: HttpErrorResponse) => {
          console.error('Failed to create booking:', error);
          this.isLoading = false;
        }
      });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}