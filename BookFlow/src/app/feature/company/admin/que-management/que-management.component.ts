import { Component, OnInit } from '@angular/core';
import { BookingResponse, BookingStatus, PaymentInformation, PaymentStatus } from '../../../../core/auth/model/booking';
import { QueServiceService } from '../../../../core/auth/service/Admin-Service/que-service.service';
import { QueResponse } from '../../../../core/auth/model/que';
import { CommonModule } from '@angular/common';
import { EditQueComponent } from '../../../../core/ui/popup/edit-que/edit-que.component';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-que-management',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatProgressSpinnerModule, RouterModule, FormsModule],
  templateUrl: './que-management.component.html',
  

  styleUrl: './que-management.component.css'
})
export class QueManagementComponent implements OnInit {
  // Bookings data
  imminentBookings: QueResponse[] = [];
  activeBookings: QueResponse[] = [];
  filteredActiveBookings: QueResponse[] = [];
  
  // Loading states
  loadingImminentBookings: boolean = false;
  loadingActiveBookings: boolean = false;
  
  // Filter properties
  searchQuery: string = '';
  startDate: string = '';
  endDate: string = '';
  minDate: string='';

  BookingStatus = BookingStatus;


  maxEndDate: string = '';
  dateError: string | null = null;

  constructor(
    private queService: QueServiceService,
    private dialog: MatDialog
  ) {}


  ngOnInit(): void {
    const today = new Date();
    const twoDaysLater = new Date(today);
    twoDaysLater.setDate(today.getDate() + 2);
    
    // Set minDate to current date + 2 days
    this.minDate = this.formatDateForInput(twoDaysLater);
    
    // Initialize startDate and endDate to minDate
    this.startDate = this.minDate;
    this.endDate = this.minDate;
    
    // No max date restriction
    this.maxEndDate = ''; 
    
    this.loadAllBookings();
}

getPaymentStatusText(status: string): string {
  // Make sure this method is comparing the exact string "PENDING"
  console.log('Payment status in method:', status, typeof status);

  switch (status) {
    case 'SUCCESS':
      return 'Paid';
    case 'PENDING':  // Confirm this is exactly 'PENDING' not 'pending' or different case
      return 'Pending';
    case 'FAILURE':
      return 'Failed';
    default:
      return 'Unpaid';
  }
}






  onEndDateChange(): void {
    this.validateDates();
    this.onDateRangeChange();
  }

  validateDates(): void {
    if (!this.startDate || !this.endDate) {
        this.dateError = null;
        return;
    }
    
    const start = new Date(this.startDate);
    const end = new Date(this.endDate);
    
    if (end < start) {
        this.dateError = 'End date cannot be before start date';
        this.endDate = this.startDate;
    } else {
        this.dateError = null;
    }
}


  formatDateForInput(date: Date): string {
    const pad = (num: number) => num.toString().padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

  loadAllBookings(): void {
    this.loadImminentBookings();
    this.loadActiveBookings();
  }

  loadActiveBookings(): void {
    this.loadingActiveBookings = true;
    this.queService.getActiveBookings().subscribe({
      next: (bookings) => {
        this.activeBookings = bookings;
        this.filteredActiveBookings = [...bookings];
        this.applySearchFilter(); // Apply search filter when new data loads
        this.loadingActiveBookings = false;
      },
      error: (error) => {
        console.error('Error loading active bookings:', error);
        this.loadingActiveBookings = false;
      }
    });
  }

  loadImminentBookings(): void {
    this.loadingImminentBookings = true;
    this.queService.getImminentBookings().subscribe({
      next: (bookings) => {
        this.imminentBookings = bookings;
        this.loadingImminentBookings = false;
      },
      error: (error) => {
        console.error('Error loading imminent bookings:', error);
        this.loadingImminentBookings = false;
      }
    });
  }

  applyFilters(): void {
    if (!this.startDate || !this.endDate) {
      this.loadActiveBookings();
      return;
    }
  
    this.loadingActiveBookings = true;
    
    const startDate = this.startDate;
    const endDate = this.endDate;
    
    this.queService.getBookingsByDateRange(startDate, endDate).subscribe({
      next: (bookings) => {
        this.activeBookings = bookings;
        this.filteredActiveBookings = [...bookings];
        this.applySearchFilter(); // Apply search filter after date filtering
        this.loadingActiveBookings = false;
      },
      error: (error) => {
        console.error('Error loading filtered bookings:', error);
        this.loadingActiveBookings = false;
      }
    });
  }

  applySearchFilter(): void {
    if (!this.searchQuery.trim()) {
      this.filteredActiveBookings = [...this.activeBookings];
      return;
    }

    const query = this.searchQuery.toLowerCase().trim();
    this.filteredActiveBookings = this.activeBookings.filter(booking => 
      (booking.userName?.toLowerCase().includes(query) || 
       booking.serviceName?.toLowerCase().includes(query) ||
      (booking.bookingId?.toString().includes(query))
    ));
  }

  resetFilters(): void {
    const twoDaysLater = new Date();
    twoDaysLater.setDate(twoDaysLater.getDate() + 2);
    
    this.startDate = this.formatDateForInput(twoDaysLater);
    this.endDate = this.formatDateForInput(twoDaysLater);
    this.searchQuery = '';
    this.dateError = null;
    this.loadActiveBookings();
}

  onDateRangeChange(): void {
    this.applyFilters();
  }

  onSearchInput(): void {
    this.applySearchFilter();
  }

  getCardClass(booking: QueResponse): string {
    return booking.bookingStatus === BookingStatus.PENDING 
      ? 'bg-white-50 border border-red-400 rounded-lg p-4' 
      : 'bg-white border border-gray-200 rounded-lg p-4';
  }

  
  getPaymentStatusClass(status: string): string {
    switch (status) {
      case 'SUCCESS':
        return 'bg-green-100 text-green-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'FAILURE':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getStatusClass(status: BookingStatus): string {
    switch (status) {
      case BookingStatus.CONFIRMED: return 'bg-green-100 text-green-800';
      case BookingStatus.PENDING: return 'bg-yellow-100 text-yellow-800';
      case BookingStatus.CANCELLED: return 'bg-red-100 text-red-800';
      case BookingStatus.COMPLETED: return 'bg-blue-100 text-blue-800';
      case BookingStatus.RESCHEDULED: return 'bg-purple-100 text-purple-800';
      case BookingStatus.NO_SHOW: return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }

  openEditDialog(booking: QueResponse): void {
    this.dialog.open(EditQueComponent, {
      width: '500px',
      data: { booking }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.loadingActiveBookings = true;
        this.queService.updateBookingFull(result).subscribe({
          next: (response) => {
            console.log('Booking updated successfully', response);
            this.loadActiveBookings();
          },
          error: (error) => {
            console.error('Error updating booking', error);
            this.loadingActiveBookings = false;
          }
        });
      }
    });
  }

  changeStatus(booking: QueResponse): void {
    // Implement status change logic
  }



}