import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { userDetails } from '../../../auth/model/admin';

@Component({
  selector: 'app-add-user-dialog',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule],
  template: `
  

  <div class="fixed inset-0 p-4 flex flex-wrap justify-center items-center w-full h-full z-[1000] before:fixed before:inset-0 before:w-full before:h-full before:bg-[rgba(0,0,0,0.5)] overflow-auto font-[sans-serif]">
    <div class="w-full max-w-lg bg-white shadow-lg rounded-lg p-6 relative">
        <!-- Close Button -->
        <svg xmlns="http://www.w3.org/2000/svg" (click)="onCancel()" class="w-3.5 cursor-pointer shrink-0 fill-gray-400 hover:fill-red-500 float-right" viewBox="0 0 320.591 320.591">
            <path d="M30.391 318.583a30.37 30.37 0 0 1-21.56-7.288c-11.774-11.844-11.774-30.973 0-42.817L266.643 10.665c12.246-11.459 31.462-10.822 42.921 1.424 10.362 11.074 10.966 28.095 1.414 39.875L51.647 311.295a30.366 30.366 0 0 1-21.256 7.288z"></path>
            <path d="M287.9 318.583a30.37 30.37 0 0 1-21.257-8.806L8.83 51.963C-2.078 39.225-.595 20.055 12.143 9.146c11.369-9.736 28.136-9.736 39.504 0l259.331 257.813c12.243 11.462 12.876 30.679 1.414 42.922-.456.487-.927.958-1.414 1.414a30.368 30.368 0 0 1-23.078 7.288z"></path>
        </svg>

        <h4 class="text-xl text-gray-800 font-semibold mb-6">Add New User</h4>

        <form [formGroup]="userForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Full Name -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Full Name</label>
                <input type="text" formControlName="fullname" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter full name">
                <span *ngIf="userForm.get('fullName')?.hasError('required') && userForm.get('fullName')?.touched"
                    class="text-sm text-red-500">Full name is required</span>
            </div>

            <!-- Email -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Email</label>
                <input type="email" formControlName="email"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter email address">
                <span *ngIf="userForm.get('email')?.hasError('required') && userForm.get('email')?.touched"
                    class="text-sm text-red-500">Email is required</span>
                <span *ngIf="userForm.get('email')?.hasError('email') && userForm.get('email')?.touched"
                    class="text-sm text-red-500">Please enter a valid email</span>
            </div>

            <!-- Phone and Department -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Phone Number</label>
                    <input type="tel" formControlName="phone"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Enter phone number">
                    <span *ngIf="userForm.get('phone')?.hasError('required') && userForm.get('phone')?.touched"
                        class="text-sm text-red-500">Phone number is required</span>
                </div>

                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Account Name</label>
                    <input type="tel" formControlName="account"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Enter Account name">
                    <span *ngIf="userForm.get('account')?.hasError('required') && userForm.get('account')?.touched"
                        class="text-sm text-red-500">Account name is required</span>
                    <span *ngIf="userForm.get('account')?.hasError('maxlength') && userForm.get('account')?.touched"
                        class="text-sm text-red-500">Account name must not below 3 characters</span>
                </div>






                <!-- <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Department</label>
                    <select formControlName="department"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                        <option value="">Select Department</option>
                        <option value="IT">IT Department</option>
                        <option value="HR">HR Department</option>
                        <option value="Finance">Finance Department</option>
                        <option value="Marketing">Marketing Department</option>
                    </select>
                    <span *ngIf="userForm.get('department')?.hasError('required') && userForm.get('department')?.touched"
                        class="text-sm text-red-500">Department is required</span>
                </div> -->


            </div>

            <!-- Role and Status -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Role</label>
                    <select formControlName="role"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                        <option value="">Select Role</option>
                        <option value="COMPANY_ADMIN">Admin</option>
                        <option value="COMPANY_USER">User</option>
                        <option value="COMPANY_ACCOUTNAT">Accoutant</option>

                    </select>
                    <span *ngIf="userForm.get('role')?.hasError('required') && userForm.get('role')?.touched"
                        class="text-sm text-red-500">Role is required</span>
                </div>

                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Status</label>
                    <select formControlName="status"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                        <option [ngValue]="true">Active</option>
                        <option [ngValue]="false">Inactive</option>
                    </select>
                </div>
            </div>

            <!-- Password -->
            <!-- <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Password</label>
                <input type="password" formControlName="password"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter password">
                <span *ngIf="userForm.get('password')?.hasError('required') && userForm.get('password')?.touched"
                    class="text-sm text-red-500">Password is required</span>
                <span *ngIf="userForm.get('password')?.hasError('minlength') && userForm.get('password')?.touched"
                    class="text-sm text-red-500">Password must be at least 6 characters</span>
            </div> -->

            <!-- Footer Actions -->
            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="onCancel()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" [disabled]="!userForm.valid || isLoading"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    {{isLoading ? 'Creating...' : 'Create User'}}
                </button>
            </div>
        </form>
    </div>
</div>



  `,
})
export class AddUserDialogComponent {

  userForm: FormGroup;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddUserDialogComponent>
  ) {
    this.userForm = this.fb.group({
      fullname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      account:['',[Validators.required, Validators.minLength(3)]],
    //   department: ['', Validators.required],
      role: ['', Validators.required],
      status: ['active'],
    });
  }


  onSubmit() {
    if (this.userForm.valid) {
      const formData = this.userForm.value;
      const createdby = localStorage.getItem('username') || 'unknown';
      const userData: userDetails = {
        ...formData,
        createdby: createdby
      };

      this.dialogRef.close(userData);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

}
