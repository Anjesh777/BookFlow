import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/auth/service/auth.service';
import { UiServiceService } from '../../core/ui/ui-service.service';



interface ApiResponse {
  message?: string;
  status:string
  error?: string;
}


@Component({
  selector: 'app-account-login',
  standalone: true,
  imports: [CommonModule,RouterModule,FormsModule,ReactiveFormsModule,MatProgressSpinnerModule],
  templateUrl: './account-login.component.html',
  styleUrl: './account-login.component.css'
})
export class AccountLoginComponent implements OnInit {

  isLoading: boolean = false;
  http = inject(HttpClient);
  validationError:Boolean =false
  private authService = inject(AuthService);
  private router = inject(Router); // Inject Router
  tokenTriger:Boolean = false;

  constructor(
    private uiService: UiServiceService,
    public dialog: MatDialog
  ) {}


  ngOnInit(): void {
    const userRole = this.authService.getUserRole();
    
    switch (userRole) {
      case 'COMPANY_SUPERADMIN':
        this.router.navigate(['/superadmin']);
        break;
      case 'COMPANY_ADMIN':
        this.router.navigate(['/admin']);
        break;
      case 'COMPANY_USER':
        this.router.navigate(['/user']);
        break;
      
    }
  }

  get showPassword(): boolean {
    return this.uiService.showPassword;
  }
  
  toggleShowPassword(): void {
    this.uiService.toggleShowPassword();
  }


  companyLoginform: FormGroup = new FormGroup({
    user_name: new FormControl("", [Validators.required, Validators.minLength(3)]),
    user_password: new FormControl("", [Validators.required, Validators.minLength(8)]),
  });

  
  sendVerificationLink(): void {
    console.log('sendVerificationLink called');
    const username = this.companyLoginform.get('user_name')?.value;
    
    if (!username || username.trim().length === 0) {
      this.uiService.showErrorDialog('Please enter a username');
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
            this.uiService.showSuccessDialog(response.message)
          } else {
            this.uiService.showErrorDialog(response.message)
          }
        },
        error: (error) => {
          console.error('Error details:', error);
          this.isLoading = false;
          const errorMessage = error.error?.message || 
                             error.message || 
                             'Failed to send verification token. Please try again.';

          this.uiService.showErrorDialog(errorMessage);

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
          localStorage.setItem('username', formData.user_name);
          this.uiService.showSuccessDialog('Login successful!');
          const userRole = this.authService.getUserRole();
          this.navigateBasedOnRole(userRole);
        },
        error: (error) => {

          this.isLoading = false;
          let errorMessage: string;

          if (error instanceof Error) {
            errorMessage = error.message;
            if(errorMessage=="Please verify your account before logging in"){
              this.tokenTriger=true ;
            }
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
          this.uiService.showErrorDialog(errorMessage);
          console.error('Login error:', error);
        }
      });
    }
  }


  
  private navigateBasedOnRole(userRole: string | null): void {
    console.log('Navigating based on role:', userRole);
    
    switch (userRole) {
      case 'COMPANY_SUPERADMIN':
        this.router.navigate(['/superadmin'])
          .then(() => console.log('Navigation to superadmin dashboard completed'))
          .catch(err => console.error('Navigation error:', err));
        break;
      case 'COMPANY_ADMIN':
        this.router.navigate(['/admin'])
          .then(() => console.log('Navigation to admin dashboard completed'))
          .catch(err => console.error('Navigation error:', err));
        break;
      case 'COMPANY_USER':
        this.router.navigate(['/user'])
          .then(() => console.log('Navigation to user dashboard completed'))
          .catch(err => console.error('Navigation error:', err));
        break;
      default:
        console.warn('Unknown role:', userRole);
        this.router.navigate(['/login'])
          .then(() => console.log('Navigation to home completed'))
          .catch(err => console.error('Navigation error:', err));
    }
  }

  


}
