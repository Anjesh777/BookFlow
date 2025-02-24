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
  allUsers: User[] = []; // Store all users
  filteredUsers: User[] = []; // Store filtered users
  searchControl = new FormControl('');
  isLoading = false;
  private destroy$ = new Subject<void>();

  constructor(private accountService: AccountServiceService) {}

  ngOnInit() {
    // Load all users
    this.loadAllUsers();

    // Setup search with debounce
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
          this.filteredUsers = this.allUsers; // Initially show all users
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
      // Adjust these conditions based on your User model properties
      return (
        user.fullname?.toLowerCase().includes(query) ||
        user.email?.toLowerCase().includes(query) ||
        user.phone?.toLowerCase().includes(query) ||
        user.user_id?.toString().includes(query)
      );
    });
  }
}