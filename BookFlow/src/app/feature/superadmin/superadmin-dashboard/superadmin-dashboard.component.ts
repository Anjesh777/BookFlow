import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../../../core/auth/service/auth.service';
import { SlidebarComponent } from "../../../core/slidebar/slidebar.component";

@Component({
  selector: 'app-superadmin-dashboard',
  standalone: true,
  imports: [SlidebarComponent],
  templateUrl: './superadmin-dashboard.component.html',
  styleUrl: './superadmin-dashboard.component.css'
})
export class SuperadminDashboardComponent {


  isLoading: boolean = false;
    http = inject(HttpClient);
    constructor(public dialog: MatDialog) {}
    openDialog():void{}
    response: string | null = null;
    private authService = inject(AuthService);



      onClick() {
    const token = this.authService.getToken();
    
    const headers = new HttpHeaders({
      'Accept': 'application/json',
      'Authorization': `Bearer ${token}` // Add the token
    });

    this.http.get<string>('http://localhost:8811/api/v1/superadmin', { headers })
      .pipe(
        catchError(error => {
          console.error('API Error:', error);
          if (error.status === 403) {
            // Handle forbidden error - maybe redirect to login
            console.log('Access forbidden - token might be expired');
          }
          return throwError(() => new Error('Something went wrong'));
        })
      )
      .subscribe({
        next: (res) => {
          console.log('Success:', res);
          this.response = res;
        },
        error: (error) => {
          console.error('Error:', error);
          this.response = 'Error occurred while fetching data';
        }
      });
  }
  
}
