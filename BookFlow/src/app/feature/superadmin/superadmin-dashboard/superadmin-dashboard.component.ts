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
import { companyDetails } from '../../../core/auth/model/bookflow';
import { TimeAgoPipe } from '../../../core/pipe/shared/pipes/time-ago.pipe';

@Component({
  selector: 'app-superadmin-dashboard',
  standalone: true,
  imports: [
      CommonModule,
      MatIconModule,
      MatButtonModule,
      MatDividerModule,
      RouterModule,
      MatSidenavModule,
      MatListModule,
      MatIconModule,
      MatToolbarModule,
      MatButtonModule,
      TimeAgoPipe
  
  ],
  templateUrl: './superadmin-dashboard.component.html',
  styleUrl: './superadmin-dashboard.component.css'
})
export class SuperadminDashboardComponent implements OnInit {


    isLoading: boolean = false;

    userCount: number = 0;
    companyCount: number = 0;
    userGrowth: String = '';
    companyGrowth: String = '';
    companyList: companyDetails[] = [];

    http = inject(HttpClient);
    response: string | null = null;
    public router = inject(Router);

    constructor(
         private uiService: UiServiceService,
          public dialog: MatDialog,
          private bookflowService: BookflowService,
          private authService: AuthService
        
    ) {}

  ngOnInit(): void {
    this.fetchCompanyData();
    this.fetchRecentCompanies();
  }

  fetchRecentCompanies() {
    this.isLoading = true;
    this.bookflowService.getRecentThreeCompanyDetails()
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


  fetchCompanyData() {
  
    this.isLoading = true;
    this.bookflowService.getAllDashboardDetails()
      .subscribe({
        next: (response) => {
          this.companyCount = response.company_count;
          this.userGrowth = response.user_growth_percentage;
          this.userCount = response.user_count;
          this.companyGrowth = response.company_growth_percentage;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error:', error);
          this.isLoading = false;
        }
      });
  }

  
}

