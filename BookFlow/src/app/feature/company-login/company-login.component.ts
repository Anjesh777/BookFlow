import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { PopupComponent } from '../../core/popup/popup.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/auth/service/auth.service';



interface ApiResponse {
  message?: string;
  status:string
  error?: string;
}


@Component({
  selector: 'app-company-login',
  standalone: true,
  imports: [CommonModule,RouterModule,FormsModule,ReactiveFormsModule,MatProgressSpinnerModule],
  templateUrl: './company-login.component.html',
  styleUrl: './company-login.component.css'
})
export class CompanyLoginComponent {


  isLoading: boolean = false;
  http = inject(HttpClient);
  validationError:Boolean =false
  private authService = inject(AuthService);
  private router = inject(Router); // Inject Router

  constructor(public dialog: MatDialog) {}

  openDialog():void{}
  

  companyLoginform: FormGroup = new FormGroup({
    user_name: new FormControl("", [Validators.required, Validators.minLength(3)]),
    user_password: new FormControl("", [Validators.required, Validators.minLength(8)]),
  });

  onLogin(): void {
    this.validationError = true;
    
    if (this.companyLoginform.valid) {
      const formData = this.companyLoginform.value;
      this.isLoading = true;

      const loginRequest = {
        user_name: formData.user_name,
        user_password: formData.user_password
      };

      this.authService.login(loginRequest).subscribe({
        next: (response) => {
          this.isLoading = false;
          console.log(response.accessToken)
          this.openSuccessDialog('Login successful!');
          debugger
          const userRole = this.authService.getUserRole();
          this.navigateBasedOnRole(userRole);



        },
        error: (error) => {
          this.isLoading = false;
          const errorMessage = error.error?.message || 'An error occurred during login';
          this.openErrorDialog(errorMessage);
        }
      });
    }
  }


  private openSuccessDialog(message: string): void {
    const dialogRef = this.dialog.open(PopupComponent, {
      width: '300px',
      disableClose: false,
      hasBackdrop: true,
      data: { 
        title: 'Successfully accepted!',
        message: message,
        type: 'success'
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
  
  private openErrorDialog(message: string): void {
    const dialogRef = this.dialog.open(PopupComponent, {
      width: '300px',
      data: { 
        title: 'Error',
        message: message,
        type: 'error'
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

  private navigateBasedOnRole(userRole: string | null): void {
    console.log('Navigating based on role:', userRole);
    
    switch (userRole) {
      case 'COMPANY_SUPERADMIN':
        this.router.navigate(['/superadmin-dashboard'])
          .then(() => console.log('Navigation to superadmin dashboard completed'))
          .catch(err => console.error('Navigation error:', err));
        break;
      case 'COMPANY_ADMIN':
        this.router.navigate(['/admin-dashboard'])
          .then(() => console.log('Navigation to admin dashboard completed'))
          .catch(err => console.error('Navigation error:', err));
        break;
      case 'COMPANY_USER':
        this.router.navigate(['/user-dashboard'])
          .then(() => console.log('Navigation to user dashboard completed'))
          .catch(err => console.error('Navigation error:', err));
        break;
      default:
        console.warn('Unknown role:', userRole);
        this.router.navigate(['/home'])
          .then(() => console.log('Navigation to home completed'))
          .catch(err => console.error('Navigation error:', err));
    }
  }

  


}
