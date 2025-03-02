import {  CommonModule } from '@angular/common';
import { ReactiveFormsModule,FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LedgerEntry } from '../../../auth/model/account';
import { MatDialogRef } from '@angular/material/dialog';
import { Component } from '@angular/core';
import { AccountServiceService } from '../../../auth/service/Account-Service/account-service.service';

@Component({
  selector: 'app-add-ledger-dialog',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule],
  template: `
  
  <div class="fixed inset-0 p-4 flex flex-wrap justify-center items-center w-full h-full z-[1000] before:fixed before:inset-0 before:w-full before:h-full before:bg-[rgba(0,0,0,0.5)] overflow-auto font-[sans-serif]">
    <div class="w-full max-w-lg bg-white shadow-lg rounded-lg p-6 relative">
        <!-- Close Button -->
        <svg xmlns="http://www.w3.org/2000/svg" (click)="onCancel()" class="w-3.5 cursor-pointer shrink-0 fill-gray-400 hover:fill-red-500 float-right" viewBox="0 0 320.591 320.591">
            <path d="M30.391 318.583a30.37 30.37 0 0 1-21.56-7.288c-11.774-11.844-11.774-30.973 0-42.817L266.643 10.665c12.246-11.459 31.462-10.822 42.921 1.424 10.362 11.074 10.966 28.095 1.414 39.875L51.647 311.295a30.366 30.366 0 0 1-21.256 7.288z"></path>
            <path d="M287.9 318.583a30.37 30.37 0 0 1-21.257-8.806L8.83 51.963C-2.078 39.225-.595 20.055 12.143 9.146c11.369-9.736 28.136-9.736 39.504 0l259.331 257.813c12.243 11.462 12.876 30.679 1.414 42.922-.456.487-.927.958-1.414 1.414a30.368 30.368 0 0 1-23.078 7.288z"></path>
        </svg>

        <h4 class="text-xl text-gray-800 font-semibold mb-6">Add Ledger Entry</h4>

        <form [formGroup]="ledgerForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Date -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Date</label>
                <input type="date" formControlName="date" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                <span *ngIf="ledgerForm.get('date')?.hasError('required') && ledgerForm.get('date')?.touched"
                    class="text-sm text-red-500">Date is required</span>
            </div>

            <!-- Particulars -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Particulars</label>
                <input type="text" formControlName="particulars" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter transaction details">
                <span *ngIf="ledgerForm.get('particulars')?.hasError('required') && ledgerForm.get('particulars')?.touched"
                    class="text-sm text-red-500">Particulars are required</span>
            </div>

            <!-- Transaction Type -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Transaction Type</label>
                <select formControlName="type"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                    <option value="debit">Debit</option>
                    <option value="credit">Credit</option>
                </select>
                <span *ngIf="ledgerForm.get('type')?.hasError('required') && ledgerForm.get('type')?.touched"
                    class="text-sm text-red-500">Transaction type is required</span>
            </div>

            <!-- Amount -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Amount (₹)</label>
                <input type="number" formControlName="amount"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter amount">
                <span *ngIf="ledgerForm.get('amount')?.hasError('required') && ledgerForm.get('amount')?.touched"
                    class="text-sm text-red-500">Amount is required</span>
                <span *ngIf="ledgerForm.get('amount')?.hasError('min') && ledgerForm.get('amount')?.touched"
                    class="text-sm text-red-500">Amount must be greater than 0</span>
            </div>

            <!-- Notes -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Notes</label>
                <textarea formControlName="notes" rows="3"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter additional notes (optional)"></textarea>
            </div>

            <!-- Invoice Number -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Invoice/Reference Number</label>
                <input type="text" formControlName="referenceNumber" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter invoice or reference number (optional)">
            </div>

            <!-- Footer Actions -->
            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="onCancel()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" [disabled]="!ledgerForm.valid || isLoading"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    {{isLoading ? 'Saving...' : 'Save Entry'}}
                </button>
            </div>
        </form>
    </div>
  </div>
  `,
})
export class AddLedgerDialogComponent {

  ledgerForm: FormGroup;
  isLoading = false;
  userId: string | number = '';

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddLedgerDialogComponent>,
    private accountService: AccountServiceService
    
  ) {
    this.ledgerForm = this.fb.group({
      date: [new Date().toISOString().split('T')[0], Validators.required],
      particulars: ['', Validators.required],
      type: ['debit', Validators.required],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      notes: [''],
      referenceNumber: [''],
      entryId: ['LEDGER_' + Math.random().toString(36).substr(2, 9)]
    });
  }

  setUserId(userId: string | number) {
    this.userId = userId;
  }

  onSubmit() {
    if (this.ledgerForm.valid) {
      this.isLoading = true;
      
      const formData = this.ledgerForm.value;
      const ledgerData: LedgerEntry = {
        ...formData,
        amount: Number(formData.amount),
        user_id: this.userId,
      };
  
      this.accountService.addLedgerData(ledgerData).subscribe({
        next: (response) => {
          console.log('Transaction added successfully:', response);
          this.dialogRef.close({ success: true });
        },
        error: (error) => {
          console.error('Failed to add transaction:', error);
          this.isLoading = false;
        }
      });
    }
  }

  onCancel() {
    this.dialogRef.close();
  }



}


