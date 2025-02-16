import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Service } from '../../../auth/model/admin';

@Component({
  selector: 'app-add-service-dialog',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  template: `
  <div class="fixed inset-0 p-4 flex flex-wrap justify-center items-center w-full h-full z-[1000] before:fixed before:inset-0 before:w-full before:h-full before:bg-[rgba(0,0,0,0.5)] overflow-auto font-[sans-serif]">
    <div class="w-full max-w-lg bg-white shadow-lg rounded-lg p-6 relative">
        <!-- Close Button -->
        <svg xmlns="http://www.w3.org/2000/svg" (click)="onCancel()" class="w-3.5 cursor-pointer shrink-0 fill-gray-400 hover:fill-red-500 float-right" viewBox="0 0 320.591 320.591">
            <path d="M30.391 318.583a30.37 30.37 0 0 1-21.56-7.288c-11.774-11.844-11.774-30.973 0-42.817L266.643 10.665c12.246-11.459 31.462-10.822 42.921 1.424 10.362 11.074 10.966 28.095 1.414 39.875L51.647 311.295a30.366 30.366 0 0 1-21.256 7.288z"></path>
            <path d="M287.9 318.583a30.37 30.37 0 0 1-21.257-8.806L8.83 51.963C-2.078 39.225-.595 20.055 12.143 9.146c11.369-9.736 28.136-9.736 39.504 0l259.331 257.813c12.243 11.462 12.876 30.679 1.414 42.922-.456.487-.927.958-1.414 1.414a30.368 30.368 0 0 1-23.078 7.288z"></path>
        </svg>

        <h4 class="text-xl text-gray-800 font-semibold mb-6">Add New Service</h4>

        <form [formGroup]="serviceForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Service Name -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Service Name</label>
                <input type="text" formControlName="serviceName" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter service name">
                <span *ngIf="serviceForm.get('serviceName')?.hasError('required') && serviceForm.get('serviceName')?.touched"
                    class="text-sm text-red-500">Service name is required</span>
            </div>

            <!-- Category -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Category</label>
                <select formControlName="category"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                    <option value="">Select Category</option>
                    <option value="Maintenance">Maintenance</option>
                    <option value="Renting">Renting</option>
                    <option value="Repair">Repair</option>
                    <option value="Installation">Installation</option>
                    <option value="Consultation">Consultation</option>
                    <option value="Consultation">Customization</option>


                </select>
                <span *ngIf="serviceForm.get('category')?.hasError('required') && serviceForm.get('catagory')?.touched"
                    class="text-sm text-red-500">Category is required</span>
            </div>

            <!-- Price and Duration -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Price</label>
                    <input type="number" formControlName="price"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Enter price">
                    <span *ngIf="serviceForm.get('price')?.hasError('required') && serviceForm.get('price')?.touched"
                        class="text-sm text-red-500">Price is required</span>
                    <span *ngIf="serviceForm.get('price')?.hasError('min') && serviceForm.get('price')?.touched"
                        class="text-sm text-red-500">Price must be greater than 0</span>
                </div>

                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Duration</label>
                    <input type="text" formControlName="duration"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Enter duration (e.g., 2 hours)">
                    <span *ngIf="serviceForm.get('duration')?.hasError('required') && serviceForm.get('duration')?.touched"
                        class="text-sm text-red-500">Duration is required</span>
                </div>
            </div>

            <!-- Status -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Status</label>
                <select formControlName="status"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                    <option [ngValue]="true">Active</option>
                    <option [ngValue]="false">Inactive</option>
                </select>
            </div>

            <!-- Footer Actions -->
            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="onCancel()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" [disabled]="!serviceForm.valid || isLoading"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    {{isLoading ? 'Creating...' : 'Create Service'}}
                </button>
            </div>
        </form>
    </div>
  </div>
  `,
})
export class AddServiceDialogComponent {
  
  serviceForm: FormGroup;
  isLoading = false;


  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddServiceDialogComponent>
  ) {

    this.serviceForm = this.fb.group({
      serviceName: ['', Validators.required],
      category: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0)]],
      duration: ['', Validators.required],
      status: [true],
      serviceId: ['SERVICE_' + Math.random().toString(36).substr(2, 9)]
    });
    
  }

  onSubmit() {
    if (this.serviceForm.valid) {
      const formData = this.serviceForm.value;
      const serviceData: Service = {
        ...formData,
        price: Number(formData.price)
      };

    console.log(this.serviceForm.value)

      this.dialogRef.close(serviceData);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}