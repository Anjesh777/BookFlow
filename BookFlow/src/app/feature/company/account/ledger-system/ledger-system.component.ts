import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { AccountServiceService } from '../../../../core/auth/service/Account-Service/account-service.service';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { TimeAgoPipe } from '../../../../core/pipe/shared/pipes/time-ago.pipe';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DaterangeComponent } from "../../../../core/ui/daterange/daterange.component";
import { MatExpansionModule } from '@angular/material/expansion';
import { User } from '../../../../core/auth/model/user';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { AddLedgerDialogComponent } from '../../../../core/ui/popup/add-ledger-dialog/add-ledger-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { LedgerEntry, LedgerSummary } from '../../../../core/auth/model/account';
import { EditLedgerDialogComponent } from '../../../../core/ui/popup/edit-ledger-dialog/edit-ledger-dialog.component';

@Component({
  selector: 'app-ledger-system',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatToolbarModule,
    TimeAgoPipe,
    MatProgressSpinnerModule,
    DaterangeComponent,
    MatExpansionModule
  ],
  templateUrl: './ledger-system.component.html',
  styleUrl: './ledger-system.component.css'
})
export class LedgerSystemComponent implements OnInit, OnDestroy {

  today: string = new Date().toISOString().split('T')[0];
  allUsers: User[] = []; 
  filteredUsers: User[] = []; 
  searchControl = new FormControl('');
  isLoading = false;
  selectedUser: User | null = null; 
  ledgerSummary: LedgerSummary | null = null;
  ledgerEntries: LedgerEntry[] = [];


  private destroy$ = new Subject<void>();

  startDateControl = new FormControl('');
  endDateControl = new FormControl('');


  openAddLedgerDialog(): void {
    if (!this.selectedUser) {
      return;
    }
    const dialogRef = this.dialog.open(AddLedgerDialogComponent, {
      width: '600px',
      disableClose: true
    });
    
    const dialogInstance = dialogRef.componentInstance;
    console.log('Setting user ID for ledger entry:', this.selectedUser.user_id);
    dialogInstance.setUserId(this.selectedUser.user_id);
  
    dialogRef.afterClosed().subscribe(result => {
      if (result && result.success) {
        if (this.selectedUser) {
          this.loadUserLedger(this.selectedUser?.user_id);
        } else {
          this.loadCompanyLedgerSummary();
        }
      }
    });
  }

  editLedgerEntry(entry: LedgerEntry): void {
    if (!this.selectedUser) {
      return;
    }
    
    const userId = this.selectedUser.user_id;
    
    const dialogRef = this.dialog.open(EditLedgerDialogComponent, {
      width: '600px',
      disableClose: true,
      data: {
        entry: entry,
        userId: userId
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result && result.success) {
        this.loadUserLedger(userId);
      }
    });
  }


  
  constructor(private accountService: AccountServiceService,  private dialog: MatDialog
  ) {}
  
  ngOnInit() {
    
    this.loadAllUsers();
    this.loadCompanyLedgerSummary();

    
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(query => {
      this.filterUsers(query || '');
    });
  }
  
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  filterByDateRange(): void {
    if (!this.selectedUser) {
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
      this.selectedUser.user_id, 
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
    
    if (this.selectedUser) {
      this.loadUserLedger(this.selectedUser.user_id);
    }
  }

  private loadAllUsers(): void {
    this.isLoading = true;
    this.accountService.getAllUsers()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (users) => {
          this.allUsers = users || [];
          this.filteredUsers = this.allUsers; 
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading users:', error);
          this.allUsers = [];
          this.filteredUsers = [];
          this.isLoading = false;
        }
      });
  }
  
  private filterUsers(query: string): void {
    query = query.toLowerCase().trim();
    
    if (!query) {
      this.filteredUsers = this.allUsers;
      return;
    }
    
    this.filteredUsers = this.allUsers.filter(user => {
      return (
        user.fullname?.toLowerCase().includes(query) ||
        user.email?.toLowerCase().includes(query) ||
        user.phone?.toLowerCase().includes(query) ||
        user.user_id?.toString().includes(query)
      );
    });
  }


  deleteRecord(queryID: string): void {
    this.accountService.deleteLedgerEntry(queryID)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          console.log(`Ledger entry with ID ${queryID} deleted successfully.`);
          if (this.selectedUser) {
            this.loadUserLedger(this.selectedUser.user_id);
          }
        },
        error: (error) => {
          console.error(`Error deleting ledger entry with ID ${queryID}:`, error);
        }
      });
  }

  

  selectUser(user: User): void {
    this.selectedUser = user;
    this.loadUserLedger(user.user_id);
  }

  clearSelectedUser(): void {
    this.selectedUser = null;
    this.loadCompanyLedgerSummary();

  }
  
  loadUserLedger(userId: string): void {
    this.isLoading = true;
  
    this.accountService.getUserLedgerSummary(userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (summary) => {
          this.ledgerSummary = summary;
      //    summary.outstandingBalance = Math.max(summary.totalDebits - summary.totalCredits,0);


          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading ledger summary:', error);
          this.isLoading = false;
        }
      });
  
    this.accountService.getUserLedgerEntries(userId)
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
  

  loadCompanyLedgerSummary(): void {
    this.isLoading = true;
    console.log('Attempting to load company ledger summary');
    
    this.accountService.getCompanyLedgerSummary()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (summary) => {
          console.log('Company ledger summary loaded:', summary);
          this.ledgerSummary = summary;
          summary.outstandingBalance = Math.max(summary.totalDebits - summary.totalCredits,0);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading company ledger summary:', error);
          this.isLoading = false;
        }
      });
  }


  exportToCSV(): void {
  this.isLoading = true;
  
  if (this.selectedUser) {
    this.accountService.exportLedgerToCSV(this.selectedUser.user_id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          const fileName = `ledger_${this.selectedUser?.fullname || this.selectedUser?.user_id}_${new Date().toISOString().split('T')[0]}.csv`;
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
  } else {
   
    const startDate = this.startDateControl.value;
    const endDate = this.endDateControl.value;
    
    this.accountService.exportTransactionsToCSV(
      '',  
      startDate ? new Date(startDate) : null,
      endDate ? new Date(endDate) : null
    ).pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `company_ledger_${new Date().toISOString().split('T')[0]}.csv`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error exporting company ledger to CSV:', error);
        this.isLoading = false;
      }
    });
  }
}

  
  

}