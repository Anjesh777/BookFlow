import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-payment-failure',
  standalone: true,
  imports: [],
  template: `
  <div class="flex flex-col items-center justify-center min-h-screen bg-gray-50 p-6">
      <div class="bg-white rounded-lg shadow-lg border border-gray-100 p-8 max-w-md w-full text-center">
        <div class="flex justify-center mb-4">
          <span class="material-icons text-red-500 text-6xl">error</span>
        </div>
        <h2 class="text-2xl font-semibold text-gray-800 mb-4">Payment Failed</h2>
        <p class="text-gray-600 mb-6">
          Unfortunately, your payment could not be processed. Please try again or contact support if the issue persists.
        </p>
        <div class="flex flex-col space-y-4">
          <button (click)="goToBookings()" class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
            Return to Bookings
          </button>
          <button (click)="goToHome()" class="px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors">
            Return to Home
          </button>
        </div>
      </div>
    </div>
  
  
  `
})
export class PaymentFailureComponent {




  constructor(private router: Router) {}

  goToBookings(): void {
    this.router.navigate(['/user/booking']);
  }
  
  goToHome(): void {
    this.router.navigate(['/user/dashboard']);
  }




}
