import { CommonModule } from '@angular/common';
import { Component, Inject, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { AdminService } from '../../../auth/service/Admin-Service/admin.service';
import { userDetailsResponse } from '../../../auth/model/admin';

@Component({
  selector: 'app-edit-user-dalog',
  standalone: true,
  imports: [

    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    


  ],
  template: `

  
<div class="fixed inset-0 p-4 flex flex-wrap justify-center items-center w-full h-full z-[1000] before:fixed before:inset-0 before:w-full before:h-full before:bg-[rgba(0,0,0,0.5)] overflow-auto font-[sans-serif]">
    <div class="w-full max-w-lg bg-white shadow-lg rounded-lg p-6 relative">
        <!-- Close Button -->
        <svg xmlns="http://www.w3.org/2000/svg" (click)="closeDialog()" class="w-3.5 cursor-pointer shrink-0 fill-gray-400 hover:fill-red-500 float-right" viewBox="0 0 320.591 320.591">
            <path d="M30.391 318.583a30.37 30.37 0 0 1-21.56-7.288c-11.774-11.844-11.774-30.973 0-42.817L266.643 10.665c12.246-11.459 31.462-10.822 42.921 1.424 10.362 11.074 10.966 28.095 1.414 39.875L51.647 311.295a30.366 30.366 0 0 1-21.256 7.288z"></path>
            <path d="M287.9 318.583a30.37 30.37 0 0 1-21.257-8.806L8.83 51.963C-2.078 39.225-.595 20.055 12.143 9.146c11.369-9.736 28.136-9.736 39.504 0l259.331 257.813c12.243 11.462 12.876 30.679 1.414 42.922-.456.487-.927.958-1.414 1.414a30.368 30.368 0 0 1-23.078 7.288z"></path>
        </svg>

        <h4 class="text-xl text-gray-800 font-semibold mb-6">Edit User Details</h4>

        <form [formGroup]="editForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Account Name -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Account Name</label>
                <input type="text" formControlName="account" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter account name">
                <span *ngIf="editForm.get('account')?.hasError('required') && editForm.get('account')?.touched"
                    class="text-sm text-red-500">Account name is required</span>
            </div>

            <!-- Email and Full Name -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Email</label>
                    <input type="email" formControlName="email"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="user@example.com">
                    <span *ngIf="editForm.get('email')?.hasError('required') && editForm.get('email')?.touched"
                        class="text-sm text-red-500">Email is required</span>
                    <span *ngIf="editForm.get('email')?.hasError('email') && editForm.get('email')?.touched"
                        class="text-sm text-red-500">Please enter a valid email</span>
                </div>

                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Full Name</label>
                    <input type="text" formControlName="fullname"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="John Doe">
                    <span *ngIf="editForm.get('fullname')?.hasError('required') && editForm.get('fullname')?.touched"
                        class="text-sm text-red-500">Full name is required</span>
                </div>
            </div>

            <!-- Phone and Role -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Phone</label>
                    <input type="text" formControlName="phone"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="xxxxx">
                    
                </div>
                <div class="space-y-2">
  <label class="text-sm text-gray-700 font-medium">Role</label>
  <select formControlName="role"
      [disabled]="data._main_user===true"
      class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white" 
      [ngClass]="{'hover:cursor-not-allowed': data._main_user===true}">
    <option value="COMPANY_ADMIN">Admin</option>
    <option value="COMPANY_USER">User</option>
  </select>
  
  @if (data._main_user === true) {
    <span class="text-sm text-gray-500 italic">Cannot modify super admin role</span>
  }
</div>
                
            </div>

            <!-- Status -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Status</label>
                <select formControlName="status"
                [disabled]="data._main_user===true"
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white hover:cursor-not-allowed">
                  <option [ngValue]="true">Active</option>
                  <option [ngValue]="false">Inactive</option>
                </select>

                @if (data._main_user === true) {
                        <span class="text-sm text-gray-500 italic">Cannot modify super admin status</span>
                    }
            </div>

            <!-- Footer Actions -->
            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="closeDialog()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" [disabled]="!editForm.valid"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    Save Changes
                </button>
            </div>
        </form>
    </div>
</div>
  
  `,
  styleUrl: './edit-user-dalog.component.css'
})
export class EditUserDalogComponent implements OnInit{



  private adminservice = inject(AdminService);
  editForm: FormGroup;
  isSubmitting = false;

  constructor(
    private dialogRef: MatDialogRef<EditUserDalogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: userDetailsResponse
  ) {
    this.editForm = this.initForm();
  }
  ngOnInit(): void {
    if(this.data._main_user===true){
      this.editForm.get('status')?.disable();
      this.editForm.get('role')?.disable(); 
    }
  }

  private initForm(): FormGroup {
    return new FormGroup({
      user_id: new FormControl(this.data.user_id),
      account: new FormControl(this.data.account, [Validators.required]),
      email: new FormControl(this.data.email, [Validators.required, Validators.email]),
      phone: new FormControl(this.data.phone),
      fullname: new FormControl(this.data.fullname),
      role: new FormControl(this.data.role, [Validators.required]),
      status: new FormControl(this.data.status, [Validators.required]),
      created_at: new FormControl(this.data.created_at)
      
    });
  }


  updateUser(): void {
    if (this.editForm.valid) {
      const userId = this.editForm.get('user_id')?.value;
      // Use getRawValue() instead of value to include disabled controls
      const userData = this.editForm.getRawValue();
      
      this.adminservice.updateUserdetails(userId, userData).subscribe({
        next: () => {
          console.log('User updated successfully');
          this.dialogRef.close(userData);
        },
        error: (error) => {
          console.error('Error updating user:', error);
        }
      });
    } else {
      this.editForm.markAllAsTouched();
    }
  }

  closeDialog(): void {
    this.dialogRef.close();
  }
  

  onSubmit(): void {
    if (this.editForm.valid) {
      this.isSubmitting = true;
      // Get form value with disabled controls included
      const userData = this.editForm.getRawValue();
      console.log(userData);
      this.updateUser();
    }
  }


}
