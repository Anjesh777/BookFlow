import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../../core/auth/service/auth.service';
import { BookflowService } from '../../../core/auth/service/BookFlow-Service/bookflow.service';
import { UiServiceService } from '../../../core/ui/ui-service.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-superadmin-dashboard',
  standalone: true,
  imports: [CommonModule,
      MatIconModule,
      MatButtonModule,
      MatDividerModule,
      RouterModule,
      MatSidenavModule,
      MatListModule,
      MatIconModule,
      MatToolbarModule,
      MatButtonModule
  
  ],
  templateUrl: './superadmin-dashboard.component.html',
  styleUrl: './superadmin-dashboard.component.css'
})
export class SuperadminDashboardComponent implements OnInit {


    isLoading: boolean = false;

    userCount: number = 0;
    companyCount: number = 0;

    http = inject(HttpClient);
    response: string | null = null;

    public router = inject(Router);


    constructor(
         private uiService: UiServiceService,
          public dialog: MatDialog,
          private bookflowService: BookflowService,
          private authService: AuthService
        
    ) {
      
    }

  ngOnInit(): void {
    this.fetchUserData();
    this.fetchCompanyData();
  }

  
  fetchUserData() {
  
    this.isLoading = true;
    this.bookflowService.getAllUsers()
      .subscribe({
        next: (response) => {
          console.log('User count response:', response);
          this.userCount = response.count;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error:', error);
          this.isLoading = false;
        }
      });
  }

  fetchCompanyData() {
  
    this.isLoading = true;
    this.bookflowService.getAllCompany()
      .subscribe({
        next: (response) => {
          console.log('company count response:', response);
          this.companyCount = response.count;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error:', error);
          this.isLoading = false;
        }
      });
  }
  
    
    


  //   onClick() {
  //   const token = this.authService.getToken();
    
  //   const headers = new HttpHeaders({
  //     'Accept': 'application/json',
  //     'Authorization': `Bearer ${token}` 
  //   });

  //   this.http.get<string>('http://localhost:8811/api/v1/superadmin', { headers })
  //     .pipe(
  //       catchError(error => {
  //         console.error('API Error:', error);
  //         if (error.status === 403) {
  //           console.log('Access forbidden - token might be expired');
  //         }
  //         return throwError(() => new Error('Something went wrong'));
  //       })
  //     )
  //     .subscribe({
  //       next: (res) => {
  //         console.log('Success:', res);
  //         this.response = res;
  //       },
  //       error: (error) => {
  //         console.error('Error:', error);
  //         this.response = 'Error occurred while fetching data';
  //       }
  //     });
  // }
  
}
