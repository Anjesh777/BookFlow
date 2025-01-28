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
  selector: 'app-company-register',
  standalone: true,
  imports: [CommonModule,RouterModule,FormsModule,ReactiveFormsModule,MatProgressSpinnerModule],
  templateUrl: './company-register.component.html',
  styleUrl: './company-register.component.css'
})
export class CompanyRegisterComponent {

  isLoading: boolean = false;
  http = inject(HttpClient);
  constructor(public dialog: MatDialog) {}
  openDialog():void{}

  companyForm: FormGroup = new FormGroup({
    company_name: new FormControl("",[Validators.required,Validators.minLength(3)]),
    registration_number: new FormControl("",[
      Validators.required,
      Validators.minLength(5)
    ]),

    company_email: new FormControl("",[Validators.required,Validators.email]),
    company_phone: new FormControl("",[ 
      Validators.required,
      Validators.maxLength(10),
      Validators.pattern("^[0-9]*$") ]),
    company_address: new FormControl("",[Validators.required]),
    company_password: new FormControl("",[Validators.required,Validators.minLength(8)]),
    conform_password: new FormControl("",[Validators.required, Validators.minLength(8)]),
    super_admin: new FormControl("",[Validators.required,Validators.minLength(8)])
  },
  {
    validators:this.passwordMatchValidator
  }
)


passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('company_password');
  const confirmPassword = control.get('conform_password');

  if (password && confirmPassword && password.value !== confirmPassword.value) {
    return {
      passwordMatch: true
    };
  }
  return null;
}


  filteredOptions: string[] = [];
  options: string[] = [];

  getListOFAllPlaces(){
    this.http.get<string[]>("http://localhost:8811/all/getListOFDistrict").subscribe(
      (res:string[]) =>{
        console.log("API Work For now")
        this.options=res;
        this.filteredOptions=[...this.options]
      },
      (err) => {
        console.log("Error is ",err)
      }
    )
  }

  isDropdownVisible: boolean = false;
  searchQuery: string = '';
  validationError:Boolean =false
  selectedOption:string ="Select Option"

  toggleDropdown(): void {
    this.isDropdownVisible = !this.isDropdownVisible;
  }
 
  selectOption(option: string): void {
    console.log("Select option ", option);
    this.selectedOption = option;
    this.companyForm.get('company_address')?.setValue(option); 
    this.isDropdownVisible = false;
  }

  filterOptions(): void {
    const searchValue = this.companyForm.get('company_address')?.value || '';
    this.filteredOptions = this.options.filter(option =>
        option.toLowerCase().includes(searchValue.toLowerCase())
    );
}

formValue:any;

onSave() {
  if (this.companyForm.valid) {
    const formData = this.companyForm.value;
    this.isLoading = true;
    
    this.http.post<ApiResponse>('http://localhost:8811/all/registercmp', formData)
      .subscribe({
        next: (response) => {
          console.log('Success:', response);
          this.isLoading =false
          if (response.status === 'success') {
            this.openSuccessDialog('Company registered successfully!');
            this.companyForm.reset();
            this.selectedOption = "Select Option";
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
  } else {
    this.validationError = true;
    Object.keys(this.companyForm.controls).forEach(key => {
      this.companyForm.get(key)?.markAsTouched();
    });
    this.openErrorDialog('Please fill all required fields correctly');
  }
}

private openSuccessDialog(message: string): void {
  const dialogRef = this.dialog.open(PopupComponent, {
    width: '400px',
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
ngOnChanges():void{
    this.filterOptions()
  }

}
