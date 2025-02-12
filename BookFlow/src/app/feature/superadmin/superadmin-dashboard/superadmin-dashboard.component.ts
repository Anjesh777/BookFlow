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
import { companyDetails, NotificationData, NotificationDataResponse } from '../../../core/auth/model/bookflow';
import { TimeAgoPipe } from '../../../core/pipe/shared/pipes/time-ago.pipe';
import { ResourceUsage } from '../../../core/auth/model/resourceusage';
import { ResourceUsageService } from '../../../core/auth/service/Resource-Service/resource-usage.service';
import { MatMenuModule } from '@angular/material/menu';
import { interval } from 'rxjs';


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
      TimeAgoPipe,
      MatMenuModule
  
  ],
  templateUrl: './superadmin-dashboard.component.html',
  styleUrl: './superadmin-dashboard.component.css'
})
export class SuperadminDashboardComponent implements OnInit {


  notificationList: NotificationDataResponse[] = []; 


    isLoading: boolean = false;

    userCount: number = 0;
    companyCount: number = 0;
    userGrowth: String = '';
    companyGrowth: String = '';
    companyList: companyDetails[] = [];
    companyOnline: number =0;

    resourceUsage: ResourceUsage = {
      storage: { used: 0, total: 0, percentage: 0 },
      memory: { used: 0, total: 0, percentage: 0 },
      cpu: { percentage: 0 }
    };
    

    http = inject(HttpClient);
    response: string | null = null;
    public router = inject(Router);

    constructor(
         private uiService: UiServiceService,
          public dialog: MatDialog,
          private bookflowService: BookflowService,
          private resourceService: ResourceUsageService


        
    ) {}


 



  ngOnInit(): void {
    this.fetchCompanyData();
    this.fetchRecentCompanies();
    this.getAllThreeComment();
    this.fetchResourceUsage();
    

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

  // getAllComment(){

  //   setTimeout(() =>{

  //     this.bookflowService.getAllNotification()
  //       .subscribe({
  //         next:(response) =>{
  //           this.notificationList=response;
  //           console.log(this.notificationList)
  //         },
  //         error:(error) =>{
  //           console.log('Error', error)
  //         }


  //       })
  //   })
  // }


  fetchResourceUsage() {
    this.resourceService.getResourceUsage()
      .subscribe({
        next: (data) => {
          this.resourceUsage = data;
        },
        error: (error) => {
          console.error('Error fetching resource usage:', error);
        }
      });
  }


    getAllThreeComment(){
  
      setTimeout(() =>{
  
        this.bookflowService.getThreeNotification()
          .subscribe({
            next:(response) =>{
              this.notificationList=response;
              console.log("message is "+this.notificationList)
            },
            error:(error) =>{
              console.log('Error', error)
            }
  
  
          })
      })
      
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

