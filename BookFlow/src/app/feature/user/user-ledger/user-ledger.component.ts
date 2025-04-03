import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { LedgerEntry, LedgerSummary } from '../../../core/auth/model/account';
import { DaterangeComponent } from '../../../core/ui/daterange/daterange.component';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import { AccountServiceService } from '../../../core/auth/service/Account-Service/account-service.service';
import { AuthService } from '../../../core/auth/service/auth.service';
import { TimeAgoPipe } from '../../../core/pipe/shared/pipes/time-ago.pipe';
import { UserService2Service } from '../../../core/auth/service/User-Service/user-service2.service';



@Component({
  selector: 'app-user-ledger',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    RouterModule,
    TimeAgoPipe,
    MatProgressSpinnerModule,
    DaterangeComponent
  ],
  templateUrl: './user-ledger.component.html',
  styleUrl: './user-ledger.component.css'
})
export class UserLedgerComponent implements OnInit, OnDestroy {

  today: string = new Date().toISOString().split('T')[0];
  isLoading = false;
  userId: string = '';
  userName: string = '';
  ledgerSummary: LedgerSummary | null = null;

  ledgerEntries: LedgerEntry[] = [];
  private destroy$ = new Subject<void>();

  startDateControl = new FormControl('');
  endDateControl = new FormControl('');

  constructor(
    private accountService: AccountServiceService,
    private User2Service: UserService2Service,

    private authService: AuthService
  ) {}
  
  ngOnInit() {
    this.loadUserLedger();
  }
  
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  filterByDateRange(): void {
    if (!this.userId) {
      return;
    }
    
    const startDate = this.startDateControl.value;
    const endDate = this.endDateControl.value;
    
    if (!startDate || !endDate) {
      console.error('Both start and end dates are required');
      return;
    }
    
    this.isLoading = true;
    this.accountService.getUserLedgerEntriesByDateRange(
      this.userId, 
      startDate, 
      endDate
    ).pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (entries) => {
        this.ledgerEntries = entries;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error filtering ledger by date range:', error);
        this.isLoading = false;
      }
    });
  }

  clearDateFilter(): void {
    this.startDateControl.setValue('');
    this.endDateControl.setValue('');
    this.loadUserLedger();
  }

  loadUserLedger(): void {
  
    this.isLoading = true;
  
    this.accountService.getUserLedgerSummary(this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (summary) => {
          this.ledgerSummary = summary;
          summary.outstandingBalance = Math.max(summary.totalDebits - summary.totalCredits, 0);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading ledger summary:', error);
          this.isLoading = false;
        }
      });
  
    this.User2Service.getUserLedgerEntries()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (entries) => {
          this.ledgerEntries = entries;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading ledger entries:', error);
          this.ledgerEntries = [];
          this.isLoading = false;
        }
      });
  }

  exportToCSV(): void {
    this.isLoading = true;
    
    this.User2Service.exportUserLedgerToCSV()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          const fileName = `my_ledger_${new Date().toISOString().split('T')[0]}.csv`;
          link.download = fileName;
          link.click();
          window.URL.revokeObjectURL(url);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error exporting ledger to CSV:', error);
          this.isLoading = false;
        }
      });
  }
}