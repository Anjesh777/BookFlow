import { Component, OnInit } from '@angular/core';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { UiServiceService } from '../../../core/ui/ui-service.service'; 
import { UserService2Service } from '../../../core/auth/service/User-Service/user-service2.service';
import { Service } from '../../../core/auth/model/admin';
import { AddBookingDialogComponent } from '../../../core/ui/popup/add-booking-dialog/add-booking-dialog.component';
import { BookingResponse } from '../../../core/auth/model/booking';


@Component({
  selector: 'app-booking-portel',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './booking-portel.component.html',
  styleUrl: './booking-portel.component.css'
})
export class BookingPortelComponent implements OnInit {


  loading: boolean = false;
  service: Service[] = [];
  error: string | null = null;
  upcomingBookings: BookingResponse[] = [];
  pastBookings: BookingResponse[] = [];


  
    constructor(
      public uiService: UiServiceService,
      public dialog: MatDialog,
      private fb: FormBuilder,
      private userservice: UserService2Service,
    ) {}

  ngOnInit(): void {
    this.getAllServices();
    this.getUpcomingBookings();
    this.getPastBookings();
  }
  

  getAllServices() {
    this.loading = true;
    this.error = null;

    this.userservice.getService().subscribe({
      next: (response) => {
        this.service = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching services', error);
        this.error = 'Failed to load services. Please try again.';
        this.loading = false;
      },
    });
  }

  openBookingDialog(service: any) {
    const dialogRef = this.dialog.open(AddBookingDialogComponent, {
      width: '500px',
      data: { service: service }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.success) {
        console.log('Booking created:', result.booking);
      }
      this.getAllServices();
      this.getUpcomingBookings();
      this.getPastBookings();
    });
  }

  getUpcomingBookings() {
    this.userservice.getUpcomingBookings().subscribe({
      next: (response) => {
        this.upcomingBookings = response;
      },
      error: (error) => {
        console.error('Error fetching upcoming bookings', error);
      },
    });
  }
  
  getPastBookings() {
    this.userservice.getPastBookings().subscribe({
      next: (response) => {
        this.pastBookings = response;
      },
      error: (error) => {
        console.error('Error fetching past bookings', error);
      },
    });
  }

  canCancelBooking(booking: BookingResponse): boolean {
    const appointmentTime = new Date(booking.appointmentDate);
    const currentTime = new Date();
    const timeDifference = appointmentTime.getTime() - currentTime.getTime();
    const hoursDifference = timeDifference / (1000 * 60 * 60);
    
    return hoursDifference >= 12;
  }
  
  showCancellationPolicy(): void {
    alert('Bookings can only be canceled at least 12 hours before the appointment time. Please contact you company admin to cancel the booking.');
  }
  
 
  cancelBooking(booking: BookingResponse): void {
    if (confirm('Are you sure you want to cancel this booking?')) {
      this.userservice.cancelBooking(booking.bookingId).subscribe({
        next: (response) => {
          this.upcomingBookings = this.upcomingBookings.filter(b => b.bookingId !== booking.bookingId);
          alert('Booking canceled successfully');
          
        },
        error: (error) => {
          console.error('Cancel booking error:', error);
          let errorMessage = 'Failed to cancel booking';
          
          if (error.error && typeof error.error === 'object' && error.error.message) {
            errorMessage = error.error.message;
          } else if (error.message) {
            errorMessage = error.message;
          }
          
          alert(errorMessage);
        }
      });
    }
  }
  
  

  
}
