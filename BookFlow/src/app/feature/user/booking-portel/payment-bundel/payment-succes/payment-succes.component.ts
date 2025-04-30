import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router'; 
import { PaymentService } from '../../../../../core/auth/service/Payment-Service/payment.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-payment-succes',
  standalone: true,
  imports: [],
  template: `
   <div class="flex flex-col items-center justify-center min-h-screen bg-gray-50 p-6">
      <div class="bg-white rounded-lg shadow-lg border border-gray-100 p-8 max-w-md w-full text-center">
        <div class="flex justify-center mb-4">
          <span class="material-icons text-green-500 text-6xl">check_circle</span>
        </div>
        <h2 class="text-2xl font-semibold text-gray-800 mb-4">Payment Successful!</h2>
        <p class="text-gray-600 mb-6">
          Your booking payment has been processed successfully. You can now view your booking details.
        </p>
        <div class="flex flex-col space-y-4">
          <button (click)="goToBookings()" class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
            View My Bookings
          </button>
          <button (click)="goToHome()" class="px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors">
            Return to Home
          </button>
        </div>
      </div>
    </div>
  `,
})
export class PaymentSuccesComponent implements OnInit{

  paymentStatus: string = 'Processing...';


  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['data']) {
        try {
          const decodedData = JSON.parse(atob(params['data']));
          console.log('Decoded payment data:', decodedData);
          
          this.paymentService.verifyPayment(decodedData).subscribe({
            next: (response) => {
              console.log('Payment verification successful:', response);
              this.paymentStatus = 'Payment successful';
            },
            error: (error) => {
              console.error('Payment verification failed:', error);
              this.paymentStatus = 'Payment verification failed';
            }
          });
        } catch (error) {
          console.error('Error processing payment data:', error);
          this.paymentStatus = 'Payment verification failed';
        }
      }
    });
  }

  

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService
  ) {}

  goToBookings(): void {
    this.router.navigate(['/user/booking']);
  }
  
  goToHome(): void {
    this.router.navigate(['/user/dashboard']);
  }


}
