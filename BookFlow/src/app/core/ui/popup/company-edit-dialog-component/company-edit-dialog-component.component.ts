import { CommonModule } from '@angular/common';
import { Component, inject, Inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { companyDetails } from '../../../auth/model/bookflow'; 
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../auth/service/auth.service';
import { BookflowService } from '../../../auth/service/BookFlow-Service/bookflow.service';
import { UiServiceService } from '../../../ui/ui-service.service';

@Component({
  selector: 'app-company-edit-dialog-component',
  standalone: true,
  imports: [

    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDividerModule,
    MatIconModule,
    MatChipsModule,
    
  ],
  template:`
<div class="fixed inset-0 p-4 flex flex-wrap justify-center items-center w-full h-full z-[1000] before:fixed before:inset-0 before:w-full before:h-full before:bg-[rgba(0,0,0,0.5)] overflow-auto font-[sans-serif]">
    <div class="w-full max-w-lg bg-white shadow-lg rounded-lg p-6 relative">
        <!-- Close Button -->
        <svg xmlns="http://www.w3.org/2000/svg" (click)="closeDialog()" class="w-3.5 cursor-pointer shrink-0 fill-gray-400 hover:fill-red-500 float-right" viewBox="0 0 320.591 320.591">
            <path d="M30.391 318.583a30.37 30.37 0 0 1-21.56-7.288c-11.774-11.844-11.774-30.973 0-42.817L266.643 10.665c12.246-11.459 31.462-10.822 42.921 1.424 10.362 11.074 10.966 28.095 1.414 39.875L51.647 311.295a30.366 30.366 0 0 1-21.256 7.288z"></path>
            <path d="M287.9 318.583a30.37 30.37 0 0 1-21.257-8.806L8.83 51.963C-2.078 39.225-.595 20.055 12.143 9.146c11.369-9.736 28.136-9.736 39.504 0l259.331 257.813c12.243 11.462 12.876 30.679 1.414 42.922-.456.487-.927.958-1.414 1.414a30.368 30.368 0 0 1-23.078 7.288z"></path>
        </svg>

        <h4 class="text-xl text-gray-800 font-semibold mb-6">Edit Company Details</h4>

        <form [formGroup]="editForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Company Name -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Company Name</label>
                <input type="text" formControlName="company_name" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter company name">
                <span *ngIf="editForm.get('company_name')?.hasError('required') && editForm.get('company_name')?.touched"
                    class="text-sm text-red-500">Company name is required</span>
            </div>

            <!-- Email and Registration -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Email</label>
                    <input type="email" formControlName="company_email"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="company@example.com">
                    <span *ngIf="editForm.get('company_email')?.hasError('required') && editForm.get('company_email')?.touched"
                        class="text-sm text-red-500">Email is required</span>
                    <span *ngIf="editForm.get('company_email')?.hasError('email') && editForm.get('company_email')?.touched"
                        class="text-sm text-red-500">Please enter a valid email</span>
                </div>

                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Registration Number</label>
                    <input type="text" formControlName="registration_number"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="REG123456">
                    <span *ngIf="editForm.get('registration_number')?.hasError('required') && editForm.get('registration_number')?.touched"
                        class="text-sm text-red-500">Registration number is required</span>
                </div>
            </div>

            <!-- Phone and Status -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Phone</label>
                    <input type="text" formControlName="company_phone"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="+1 (555) 123-4567">
                    <span *ngIf="editForm.get('company_phone')?.hasError('required') && editForm.get('company_phone')?.touched"
                        class="text-sm text-red-500">Phone number is required</span>
                </div>

                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Enable</label>
                    <select formControlName="enabled"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                        <option [ngValue]="true" class="text-green-600">Active</option>
                        <option [ngValue]="false" class="text-red-600">Inactive</option>
                    </select>
                </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-2">
                <label class="text-gray-800 text-sm mb-2 block">Address</label>
                <div class="relative">
                    <button type="button" id="dropdownToggle" (click)="toggleDropdown()" (click)="getListOFAllPlaces()"
                        class="w-full px-4 py-2.5 rounded text-white text-sm font-semibold tracking-wide border-none outline-none bg-blue-600 hover:bg-blue-700 active:bg-blue-600 flex justify-between items-center">
                        <span>{{selectedOption}}</span>
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-3 fill-white" viewBox="0 0 24 24">
                            <path fill-rule="evenodd"
                                d="M11.99997 18.1669a2.38 2.38 0 0 1-1.68266-.69733l-9.52-9.52a2.38 2.38 0 1 1 3.36532-3.36532l7.83734 7.83734 7.83734-7.83734a2.38 2.38 0 1 1 3.36532 3.36532l-9.52 9.52a2.38 2.38 0 0 1-1.68266.69734z"
                                clip-rule="evenodd" />
                        </svg>
                    </button>
                    
                    <ul id="dropdownMenu" 
                        class="absolute left-0 right-0 mt-1 shadow-lg bg-white py-2 px-2 z-[1000] rounded max-h-40 overflow-auto"
                        [ngClass]="{'block':isDropdownVisible,'hidden':!isDropdownVisible}">
                        <li class="mb-2">
                            <input placeholder="Search here" 
                                class="px-4 py-2.5 w-full rounded text-gray-800 text-sm border-none outline-blue-600 bg-blue-100 focus:bg-transparent"
                                formControlName="company_address"
                                (input)="filterOptions()" />
                        </li>
                        @for (item of filteredOptions; track $index) {
                            <li class="py-2.5 px-4 hover:bg-blue-50 text-black text-sm cursor-pointer rounded" 
                                (click)="selectOption(item)">{{item}}</li>
                        }
                    </ul>
                </div>
                @if (validationError && editForm.controls['company_address'].errors?.['required']) {
                    <div class="flex items-center gap-1 mt-1">
                        <i class="fa-solid fa-triangle-exclamation fa-lg" style="color: #ff0000;"></i>
                        <span class="text-red-900 text-sm">Address Is Required</span>
                    </div>
                }
            </div>

            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Verified</label>
                <select formControlName="verified"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                    <option [ngValue]="true" class="text-green-600">Verified</option>
                    <option [ngValue]="false" class="text-red-600">Not Verified</option>
                </select>
            </div>
        </div>





            <!-- Address -->
           

            <!-- Footer Actions -->
            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="closeDialog()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" (click)="updateCompany()" [disabled]="!editForm.valid"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    Save Changes
                </button>
            </div>
        </form>
    </div>
</div>

  `
})
export class CompanyEditDialogComponentComponent {

  private authService = inject(AuthService);
  private bookflowService = inject(BookflowService);

  

  editForm: FormGroup;
  isSubmitting = false;
  isDropdownVisible = false;
  http = inject(HttpClient);
  selectedOption:string ="Select Option"
  validationError:Boolean =false

  
  filteredOptions: string[] = [];
  options: string[] = [];
  

  constructor(
    private dialogRef: MatDialogRef<CompanyEditDialogComponentComponent>,
    @Inject(MAT_DIALOG_DATA) public data: companyDetails,
    private uiService: UiServiceService,

  ) {
    this.editForm = this.initForm()

  }

  private initForm(): FormGroup {
    return new FormGroup({
      company_id: new FormControl(this.data.company_id),
      company_name: new FormControl(this.data.company_name, [Validators.required]),
      registration_number: new FormControl(this.data.registration_number, [Validators.required]),
      company_email: new FormControl(this.data.company_email, [Validators.required, Validators.email]),
      company_phone: new FormControl(this.data.company_phone, [Validators.required]),
      company_address: new FormControl(this.data.company_address, [Validators.required]),
      created_at: new FormControl(this.data.company_createdAt),
      updated_at: new FormControl(this.data.company_updatedAt),
      enabled: new FormControl(this.data.enabled),
      verified: new FormControl(this.data.verified),
    });
  }

  toggleDropdown(): void {
    this.isDropdownVisible = !this.isDropdownVisible;
  }

  selectOption(option: string): void {
    console.log("Select option ", option);
    this.selectedOption = option;
    this.editForm.get('company_address')?.setValue(option); 
    this.isDropdownVisible = false;
  }

  filterOptions(): void {
    const searchValue = this.editForm.get('company_address')?.value || '';
    this.filteredOptions = this.options.filter(option =>
        option.toLowerCase().includes(searchValue.toLowerCase())
    );
  }

 
  updateCompany(): void {
    console.log('Update company:', this.editForm.value);
    if (this.editForm.valid) {
        const companyId = this.editForm.get('company_id')?.value;
        const companyData = this.editForm.value;
        
        this.bookflowService.updateCompanyDetails(companyId, companyData)
            .subscribe({
                next: () => {
                    console.log('Company updated successfully');
                    // Add success handling (e.g., show success message, close dialog)
                },
                error: (error) => {
                    console.error('Error updating company:', error);
                    // Add error handling (e.g., show error message)
                }
            });
    } else {
        // Handle invalid form
        this.editForm.markAllAsTouched();
    }
}

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

  closeDialog(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.editForm.valid) {
      this.isSubmitting = true;
      this.dialogRef.close(this.editForm.value);
    }
  }
}