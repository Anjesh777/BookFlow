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
  tokenTriger:Boolean = false;

  openDialog():void{}
  

  companyLoginform: FormGroup = new FormGroup({
    user_name: new FormControl("", [Validators.required, Validators.minLength(3)]),
    user_password: new FormControl("", [Validators.required, Validators.minLength(8)]),
  });

  
  sendVerificationLink(): void {
    console.log('sendVerificationLink called');
    const username = this.companyLoginform.get('user_name')?.value;
    
    if (!username || username.trim().length === 0) {
      this.openErrorDialog('Please enter a username');
      return;
    }

    if (this.isLoading) {
      return;
    }

    this.isLoading = true;
    console.log('Sending verification request for username:', username.trim());

    const resendTokenRequest = {
      username: username.trim()
    };

    this.authService.resendVerificationToken(resendTokenRequest)
      .subscribe({
        next: (response) => {
          console.log('Success response:', response);
          this.isLoading = false;
          if (response.status === 'success') {
            this.openSuccessDialog(response.message);
          } else {
            this.openErrorDialog(response.message);
          }
        },
        error: (error) => {
          console.error('Error details:', error);
          this.isLoading = false;
          const errorMessage = error.error?.message || 
                             error.message || 
                             'Failed to send verification token. Please try again.';
          this.openErrorDialog(errorMessage);
          this.tokenTriger = true; 
        },
        complete: () => {
          this.isLoading = false;
        }
      });
  }


  
  

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
          this.openSuccessDialog('Login successful!');
          debugger
          const userRole = this.authService.getUserRole();
          this.navigateBasedOnRole(userRole);
        },
        error: (error) => {

          this.isLoading = false;
          let errorMessage: string;

          if (error instanceof Error) {
            this.tokenTriger=true ;
            errorMessage = error.message;
          } else if (error.error?.message) {
            console.log("err1")
            errorMessage = error.error.message;
          } else if (error.message) {
            console.log("err2")

            errorMessage = error.message;
          } else {
            console.log("err3")

            errorMessage = 'An unexpected error occurred';
          }
          this.openErrorDialog(errorMessage);
          console.error('Login error:', error);

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
