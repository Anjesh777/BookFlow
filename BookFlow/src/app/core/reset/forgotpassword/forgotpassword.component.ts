import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../auth/service/auth.service';
import { UiServiceService } from '../../ui/ui-service.service';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-forgotpassword',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './forgotpassword.component.html',
  styleUrl: './forgotpassword.component.css'
})
export class ForgotpasswordComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private platformId = inject(PLATFORM_ID);

  isLoading: boolean = false;
  token: string | null = null;

  resetForm = new FormGroup({
    newPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(8)
    ]),
    conformPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(8)
    ])
  }, {
    validators: this.passwordMatchValidator
  });

  constructor(
    private uiService: UiServiceService,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    // Get token from query params using ActivatedRoute
    this.token = this.route.snapshot.queryParamMap.get('token');

    // If in browser environment, also check URL search params as fallback
    if (isPlatformBrowser(this.platformId)) {
      if (!this.token) {
        const searchParams = new URLSearchParams(window.location.search);
        this.token = searchParams.get('token');
      }
    }

    if (!this.token) {
      this.uiService.showErrorDialog('Invalid or missing reset token');
      this.router.navigate(['/login']);
    }
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('newPassword');
    const confirmPassword = control.get('conformPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMatch: true };
    }
    return null;
  }

  resetPassword() {
    if (!this.resetForm.valid || !this.token) {
      this.isLoading = false;
      this.uiService.showErrorDialog('Please fill all required fields correctly');
      return;
    }

    this.isLoading = true;
    const request = {
      token: this.token,
      newPassword: this.resetForm.get('newPassword')!.value!
    };

    this.authService.resetPassword(request).subscribe({
      next: (response) => {
        console.log(response);
        this.uiService.showSuccessDialog('Password reset successfully');
        this.resetForm.reset();

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        this.isLoading = false;
        this.uiService.showErrorDialog(error.message || 'Failed to reset password');
      }
    });
  }

  get passwordsMatch() {
    return this.resetForm.controls.newPassword.value ===
           this.resetForm.controls.conformPassword.value;
  }
}