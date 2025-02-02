import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTable, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { TimeAgoPipe } from '../../../core/pipe/shared/pipes/time-ago.pipe';
import { User } from '../../../core/auth/model/auth';
import { companyDetails } from '../../../core/auth/model/bookflow';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/auth/service/auth.service';
import { BookflowService } from '../../../core/auth/service/BookFlow-Service/bookflow.service';
import { UiServiceService } from '../../../core/ui/ui-service.service';
import { MatChipsModule } from '@angular/material/chips';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';



@Component({
  selector: 'app-companymanagement',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatInputModule,
    MatFormFieldModule,
    MatDialogModule,
    TimeAgoPipe,
    MatChipsModule,
    MatOptionModule,
    MatSelectModule


  ],
  templateUrl: './companymanagement.component.html',
  styleUrl: './companymanagement.component.css'
})
export class CompanymanagementComponent implements OnInit {


  isLoading: boolean = false;

  companyList: companyDetails[] = [];
  http = inject(HttpClient);
  filterValue: string = '';


  constructor(
           private uiService: UiServiceService,
            public dialog: MatDialog,
            private bookflowService: BookflowService,
            private authService: AuthService
          
      ) {

      }
  ngOnInit(): void {
    this.fetchAllCompanies();

  }

  


  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;

  }


  addUser() {
    // Implement add user logic
  }

  editUser(user: User) {
    // Implement edit user logic
  }

  deleteUser(user: User) {
    // Implement delete user logic
  }

  toggleStatus(user: User) {
    // Implement status toggle logic
  
  }

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<companyDetails>;

  displayedColumns: string[] = ['id', 'compant name', 'registraion_nmber', 'email', 'phone', 'address', 'created_at','updated_at','enable'];


  fetchAllCompanies() {
    this.isLoading = true;
    this.bookflowService.getRecentAllCompanyDetails()
      .subscribe({
        next: (response) => {
          this.companyList = response;

          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error:', error);
          this.isLoading = false;
        }
      });
  }



}
