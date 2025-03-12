import { Component, Inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationDataResponse } from '../../../auth/model/bookflow';
import { BookflowService } from '../../../auth/service/BookFlow-Service/bookflow.service';

import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-bookflow-notification-edit-dialog',
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

        <h4 class="text-xl text-gray-800 font-semibold mb-6">Edit Notification</h4>

        <form [formGroup]="editForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Title -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Title</label>
                <input type="text" formControlName="title" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter notification title">
                <span *ngIf="editForm.get('title')?.hasError('required') && editForm.get('title')?.touched"
                    class="text-sm text-red-500">Title is required</span>
            </div>

            <!-- Message -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Message</label>
                <textarea formControlName="message"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    rows="3"
                    placeholder="Enter notification message"></textarea>
                <span *ngIf="editForm.get('message')?.hasError('required') && editForm.get('message')?.touched"
                    class="text-sm text-red-500">Message is required</span>
            </div>

            <!-- Target Audience and Notification Type -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Target Audience</label>

                    <select formControlName="targetAudience"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                        <option value="All User">All Users</option>
                        <option value="Admin">Admin</option>
                        <option value="User">User</option>
                    </select>

<!-- 
                    <input type="text" formControlName="targetAudience"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Enter target audience"> -->
                        
                    <span *ngIf="editForm.get('targetAudience')?.hasError('required') && editForm.get('targetAudience')?.touched"
                        class="text-sm text-red-500">Target audience is required</span>
                </div>

                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Notification Type</label>
                    <select formControlName="notificationType"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                        <option value="INFORMATION">Information</option>
                        <option value="SUCCESS">Success</option>
                        <option value="WARNING">Warning</option>
                        <option value="ERROR">Error</option>
                    </select>
                    <span *ngIf="editForm.get('notificationType')?.hasError('required') && editForm.get('notificationType')?.touched"
                        class="text-sm text-red-500">Notification type is required</span>
                </div>
            </div>

            <!-- Footer Actions -->
            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="onCancel()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" [disabled]="!editForm.valid || isLoading"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    {{isLoading ? 'Saving...' : 'Save Changes'}}
                </button>
            </div>
        </form>
    </div>
</div>
  
  `})
export class BookflowNotificationEditDialogComponent {

  editForm: FormGroup;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<BookflowNotificationEditDialogComponent>,
    private bookflowService: BookflowService,
    @Inject(MAT_DIALOG_DATA) public data: NotificationDataResponse
  ) {
    this.editForm = this.fb.group({
      title: [data.title, Validators.required],
      message: [data.message, Validators.required],
      targetAudience: [data.targetAudience, Validators.required],
      notificationType: [data.notificationType, Validators.required]
    });
  }

  onSubmit() {
    if (this.editForm.valid) {
      this.isLoading = true;
      const updatedData = {
        ...this.data,
        ...this.editForm.value
      };

      this.bookflowService.updateNotification(this.data.id, updatedData)
        .subscribe({
          next: (response) => {
            console.log(updatedData)
            this.isLoading = false;
            this.dialogRef.close(true);
          },
          error: (error) => {
            this.isLoading = false;
            console.error('Error updating notification:', error);
          }
        });
    }
  }

  onCancel() {
    this.dialogRef.close(false);
  }




}
