// company-login.component.ts
import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { PopupComponent } from '../../core/popup/popup.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/auth/service/auth.service';

@Component({
  selector: 'app-company-login',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule, MatProgressSpinnerModule],
  templateUrl: './company-login.component.html',
  styleUrl: './company-login.component.css'
})
export class CompanyLoginComponent {
  isLoading: boolean = false;
  validationError: boolean = false;
  private authService = inject(AuthService);

  constructor(public dialog: MatDialog) {}

  // Update form control names to match the API
  companyLoginform: FormGroup = new FormGroup({
    user_name: new FormControl("", [Validators.required, Validators.minLength(3)]),
    user_password: new FormControl("", [Validators.required, Validators.minLength(8)]),
  });

  onLogin(): void {
    this.validationError = true; // Set to true to show validation messages
    
    if (this.companyLoginform.valid) {
      const formData = this.companyLoginform.value;
      this.isLoading = true;

      this.authService.login(formData).subscribe({
        next: (response) => {
          this.isLoading = false;
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
      data: {
        title: 'Successfully accepted!',
        message: message,
        type: 'success'
      }
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
  }
}