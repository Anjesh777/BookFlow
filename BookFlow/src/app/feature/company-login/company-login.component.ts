import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
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

      // Transform the form data to match API expectations
      const loginRequest = {
        user_name: formData.user_name,
        user_password: formData.user_password
      };

      this.authService.login(loginRequest).subscribe({
        next: (response) => {
          this.isLoading = false;
          console.log(response.authenticationToken)

          this.openSuccessDialog('Login successful!');
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


}
