import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormsModule } from '@angular/forms';
import { QueServiceService } from '../../../../../core/auth/service/Admin-Service/que-service.service';
import { QueResponse } from '../../../../../core/auth/model/que';
import { CommonModule } from '@angular/common';
import { BookingResponse } from '../../../../../core/auth/model/booking';


@Component({
  selector: 'app-cancelled-que',
  standalone: true,
  imports: [RouterModule, FormsModule, MatIconModule, MatProgressSpinnerModule,CommonModule],
  templateUrl: './cancelled-que.component.html',
  styleUrl: './cancelled-que.component.css'
})
export class CancelledQueComponent implements OnInit {

  cancelledBookings: BookingResponse[] = [];
  isLoading: boolean = true;
  startDate: Date = new Date(new Date().setMonth(new Date().getMonth() - 3)); 
  endDate: Date = new Date();

  constructor(private queService: QueServiceService) {}

  ngOnInit(): void {
    this.loadRecentCancelledBookings();
  }


  loadRecentCancelledBookings(): void {
    this.isLoading = true;
    this.queService.getRecentCancelledBookings().subscribe({
      next: (bookings) => {
        this.cancelledBookings = bookings;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading cancelled bookings:', err);
        this.isLoading = false;
      }
    });
  }

  filterByDateRange(): void {
    this.isLoading = true;
    this.queService.getCancelledBookingsByDateRange(this.startDate, this.endDate).subscribe({
      next: (bookings) => {
        this.cancelledBookings = bookings;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error filtering cancelled bookings:', err);
        this.isLoading = false;
      }
    });
  }

  resetFilters(): void {
    this.startDate = new Date(new Date().setMonth(new Date().getMonth() - 3));
    this.endDate = new Date();
    this.loadRecentCancelledBookings();
  }



  

}
