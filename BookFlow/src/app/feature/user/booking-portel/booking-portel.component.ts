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
import { PaymentService } from '../../../core/auth/service/Payment-Service/payment.service';


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
      private paymentService: PaymentService
      

    ) {}

  ngOnInit(): void {
    console.log('Booking portal initialized');
    this.getAllServices();
    this.getUpcomingBookings();
    this.getPastBookings();
    this.checkPaymentReturn();

  }
  

  checkPaymentReturn(): void {
    const queryParams = new URLSearchParams(window.location.search);
    if (queryParams.has('reference_id') && queryParams.has('transaction_uuid') && queryParams.has('status')) {
      const params = {
        reference_id: queryParams.get('reference_id'),
        transaction_uuid: queryParams.get('transaction_uuid'),
        status: queryParams.get('status')
      };
      
      this.handlePaymentReturn(params)
        .then(success => {
          if (success) {
            alert('Payment successful!');
          } else {
            alert('Payment failed. Please try again.');
          }
          history.replaceState({}, document.title, window.location.pathname);
          this.getUpcomingBookings();
        })
        .catch(error => {
          console.error('Error handling payment return:', error);
          alert('Error processing payment. Please contact support.');
          history.replaceState({}, document.title, window.location.pathname);
        });
    }
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

  scrollToServices() {
    const element = document.getElementById('servicesGrid');
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
  
  makePayment(booking: BookingResponse): void {
    this.processPayment(booking.bookingId);
  }
 



processPayment(bookingId: string): void {
  this.loading = true; 
  this.paymentService.initiatePayment(bookingId).subscribe({
    next: (paymentData) => {
      if (!paymentData || !paymentData.payment_url) {
        console.error('Invalid payment data received:', paymentData);
        alert('Invalid payment configuration. Please contact support.');
        this.loading = false;
        return;
      }
      this.redirectToESewa(paymentData);
    },
    error: (error) => {
      console.error('Payment initiation failed:', error);
      let errorMessage = 'Failed to initiate payment. Please try again.';
      if (error.error?.message) {
        errorMessage = error.error.message;
      }
      alert(errorMessage);
      this.loading = false;
    }
  });
}

  private redirectToESewa(paymentData: any): void {
    const paymentUrl = new URL(paymentData.payment_url);
    
    const form = document.createElement('form');
    form.method = paymentData.method || 'POST';
    form.action = paymentUrl.toString();
    form.target = '_self'; // Force same window
    form.style.display = 'none';
  
    for (const key in paymentData) {
      if (key !== 'method' && key !== 'payment_url') {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = paymentData[key];
        form.appendChild(input);
      }
    }
  
    document.body.appendChild(form);
    form.submit();
    
    // Clean up the form after submission
    setTimeout(() => {
      document.body.removeChild(form);
    }, 100);
  }


  handlePaymentReturn(queryParams: any): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.paymentService.verifyPayment(queryParams).subscribe({
        next: (response) => {
          if (response.status === 'success') {
            resolve(true);
          } else {
            resolve(false);
          }
        },
        error: (error) => {
          console.error('Payment verification failed:', error);
          reject(error);
        }
      });
    });
  }


  
}
