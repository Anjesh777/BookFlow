import { Component, inject, OnInit } from '@angular/core';
import { UserserviceService } from '../../../core/auth/service/User-Service/userservice.service';
import { NotificationDataResponse } from '../../../core/auth/model/bookflow';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { TimeAgoPipe } from '../../../core/pipe/shared/pipes/time-ago.pipe';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [
MatButtonModule,MatDividerModule,MatIconModule,MatListModule,MatSidenavModule,MatToolbarModule
,TimeAgoPipe,CommonModule,RouterModule
  ],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.css'
})
export class UserDashboardComponent implements OnInit {
  ngOnInit(): void {
    this.getNotificationUser();
    this.getNotificatiByCompany();
    this.getAllUserNotification();
  }
  
  adminNotificationService = inject(UserserviceService);
  messageList: NotificationDataResponse[] = [];
  messageList2: NotificationDataResponse[] = []
  messageList3: NotificationDataResponse[] = []


  getNotificationUser() {
    this.adminNotificationService.getNotificationUser().subscribe({
      next: (data) => {
        this.messageList2 = data;
        console.log('Notification Data:', data);
      },
      error: (error) => {
        console.error('Error:', error);
      }
    });
  }

  constructor(
    private userService:UserserviceService
  ){

  }


  getNotificatiByCompany() {
    this.adminNotificationService.getoneNotificationFromCompany().subscribe({
      next: (data) => {
        this.messageList = data;
        console.log('Notification Data:', data);
      },
      error: (error) => {
        console.error('Error:', error);
      }
    });
  }
  
  getIconForType(type: string): string {
    switch (type) {
      case 'SUCCESS':
        return 'check_circle';
      case 'ERROR':
        return 'error';
      case 'WARNING':
        return 'warning';
      case 'INFORMATION':
      default:
        return 'info';
    }
  }
  
  getColorClassForType(type: string): { bg: string, text: string } {
    switch (type) {
      case 'SUCCESS':
        return { bg: 'bg-green-100', text: 'text-green-600' };
      case 'ERROR':
        return { bg: 'bg-red-100', text: 'text-red-600' };
      case 'WARNING':
        return { bg: 'bg-yellow-100', text: 'text-yellow-600' };
      case 'INFORMATION':
      default:
        return { bg: 'bg-blue-100', text: 'text-blue-600' };
    }
  }
  
  getBadgeColorForType(type: string): { bg: string, text: string } {
    switch (type) {
      case 'SUCCESS':
        return { bg: 'bg-green-100', text: 'text-green-800' };
      case 'ERROR':
        return { bg: 'bg-red-100', text: 'text-red-800' };
      case 'WARNING':
        return { bg: 'bg-yellow-100', text: 'text-yellow-800' };
      case 'INFORMATION':
      default:
        return { bg: 'bg-blue-100', text: 'text-blue-800' };
    }
  }


  getAllUserNotification() {
    this.userService.getAllUserNotification().subscribe({
      next: (data) => {
        this.messageList3 = data;
        console.log('Notification Data:', data);
      },
      error: (error) => {
        console.error('Error:', error);
      }
    });
  }


}
