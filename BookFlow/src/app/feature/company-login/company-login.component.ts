import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { PopupComponent } from '../../core/popup/popup.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';


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

  constructor(public dialog: MatDialog) {}

  openDialog():void{}
  

  companyLoginform: FormGroup = new FormGroup({
    company_name: new FormControl("",[Validators.required,Validators.minLength(3)]),
    company_password: new FormControl("",[Validators.required,Validators.minLength(8)]),
  })

  

  onLogin(){
    this.validationError = false;
    
    if (this.companyLoginform.valid) {
      const formData = this.companyLoginform.value
      this.isLoading = true;
      this.http.post<ApiResponse>('http://localhost:8811/Company/logincmp', formData)
      .subscribe({
        
        next: (response) => {
          console.log('Success:', response);
          this.isLoading =false
          if (response.status === 'success') {
            this.openSuccessDialog('Company registered successfully!');
          } else {
            this.openErrorDialog(response.message || 'Registration failed');
          }
        },
        error: (error) => {
          this.isLoading = false
          console.error('Error:', error);
          const errorMessage = error.error?.message || 'An error occurred while registering the company';
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
