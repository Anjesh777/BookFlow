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
  allUsers: User[] = []; 
  filteredUsers: User[] = []; 
  searchControl = new FormControl('');
  isLoading = false;
  selectedUser: User | null = null; 
  ledgerSummary: LedgerSummary | null = null;
  ledgerEntries: LedgerEntry[] = [];
  
  
  private destroy$ = new Subject<void>();

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

  }


  
  constructor(private accountService: AccountServiceService,  private dialog: MatDialog
  ) {}
  
  ngOnInit() {
    
    
    this.loadAllUsers();

    
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

  selectUser(user: User): void {
    this.selectedUser = user;
    this.loadUserLedger(user.user_id);
  }

  clearSelectedUser(): void {
    this.selectedUser = null;
  }

  loadUserLedger(userId: string): void {
    this.isLoading = true;
    
    this.accountService.getUserLedgerSummary(userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (summary) => {
          this.ledgerSummary = summary;
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
}