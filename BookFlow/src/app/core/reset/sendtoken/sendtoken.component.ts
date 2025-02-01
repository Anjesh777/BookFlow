import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router, RouterModule } from '@angular/router';
import { UiServiceService } from '../../ui/ui-service.service';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-sendtoken',
  standalone: true,
  imports: [CommonModule,RouterModule,FormsModule,ReactiveFormsModule,MatProgressSpinnerModule],
  templateUrl: './sendtoken.component.html',
  styleUrl: './sendtoken.component.css'
})
export class SendtokenComponent {

  private router = inject(Router);
  private authService = inject(AuthService);
  

  constructor(
      private uiService: UiServiceService,
      public dialog: MatDialog
    ) {}

  isLoading: boolean = false;
  http = inject(HttpClient);

  userForm: FormGroup = new FormGroup({
    username: new FormControl("",[
      Validators.required,
      Validators.minLength(3)
    ])}
  )

  
  sendForgotLink():void{
    console.log('sendForgotLink called');
    const username = this.userForm.get('username')?.value;
    console.log('username:', username);

    if (!username || username.trim().length === 0) {
      this.uiService.showErrorDialog('Please enter a username');
      return;
    }
    if (this.isLoading) {
      return;
    }

    this.isLoading = true;
    console.log('Sending verification request for username:', username.trim());

    const resendForgetRequest = {
      username: username.trim()
    };

    this.authService.resendForgetTokenPassword(resendForgetRequest)
      .subscribe({
        next: (response) => {
          console.log('Success response:', response);
          this.isLoading = false;
          if (response.status === 'success') {
            this.uiService.showSuccessDialog(response.message)
            this.userForm.reset();
          } else {
            this.uiService.showErrorDialog(response.message)
          }
        },
        error: (error) => {
          console.error('Error details:', error);
          this.isLoading = false;
          const errorMessage = error.error?.message || 
                             error.message || 
                             'Failed to send Forget Request. Please try again.';

          this.uiService.showErrorDialog(errorMessage);
        },
        complete: () => {
          this.isLoading = false;
        }
      });
  }
  

}
