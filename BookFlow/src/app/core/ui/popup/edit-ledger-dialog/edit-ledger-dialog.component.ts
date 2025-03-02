import { CommonModule } from '@angular/common';
import { Component, Inject, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { AccountServiceService } from '../../../auth/service/Account-Service/account-service.service';
import { LedgerEntry } from '../../../auth/model/account';

@Component({
  selector: 'app-edit-ledger-dialog',
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

        <h4 class="text-xl text-gray-800 font-semibold mb-6">{{ isEdit ? 'Edit Ledger Entry' : 'Add Ledger Entry' }}</h4>

        <form [formGroup]="ledgerForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Date Field -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Date</label>
                <input type="date" formControlName="date" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    [max]="maxDate">
                <span *ngIf="ledgerForm.get('date')?.hasError('required') && ledgerForm.get('date')?.touched"
                    class="text-sm text-red-500">Date is required</span>
            </div>

            <!-- Particulars Field -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Particulars</label>
                <input type="text" formControlName="particulars" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter details">
                <span *ngIf="ledgerForm.get('particulars')?.hasError('required') && ledgerForm.get('particulars')?.touched"
                    class="text-sm text-red-500">Particulars are required</span>
            </div>

            <!-- Type and Amount -->
            <div class="grid grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Type</label>
                    <select formControlName="type"
                      class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                      <option value="debit">Debit</option>
                      <option value="credit">Credit</option>
                    </select>
                    <span *ngIf="ledgerForm.get('type')?.hasError('required') && ledgerForm.get('type')?.touched"
                        class="text-sm text-red-500">Type is required</span>
                </div>

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
            </div>

            <div class="space-y-2">
              <label class="text-sm text-gray-700 font-medium">Note</label>
                <input type="text" formControlName="notes" 
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter notes (optional)">
              </div>

            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Invoice/Reference Number</label>
                <input type="text" formControlName="referenceNumber" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter invoice or reference number (optional)">                    
            </div>


            <!-- Footer Actions -->
            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="closeDialog()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" [disabled]="!ledgerForm.valid || isSubmitting"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    {{ isSubmitting ? 'Saving...' : (isEdit ? 'Update Entry' : 'Add Entry') }}
                </button>
            </div>
        </form>
    </div>
</div>
  `
})
export class EditLedgerDialogComponent implements OnInit {
  private accountService = inject(AccountServiceService);
  ledgerForm: FormGroup;
  isSubmitting = false;
  isEdit = false;
  userId: string = '';
  maxDate: string = new Date().toISOString().split('T')[0]; // Today's date

  constructor(
    private dialogRef: MatDialogRef<EditLedgerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { entry?: LedgerEntry, userId: string },
    private fb: FormBuilder
  ) {
    this.userId = data.userId;
    this.isEdit = !!data.entry;
    this.ledgerForm = this.initForm();
  }

  ngOnInit(): void {
    // Additional initialization if needed
  }

  private initForm(): FormGroup {
    return this.fb.group({
      entryId: [this.data.entry?.entryId || null],
      date: [this.data.entry ? new Date(this.data.entry.date).toISOString().split('T')[0] : new Date().toISOString().split('T')[0], [Validators.required]],
      particulars: [this.data.entry?.particulars || '', [Validators.required]],
      type: [this.data.entry?.type || 'debit', [Validators.required]],
      amount: [this.data.entry?.amount || '', [Validators.required, Validators.min(0.01)]],
      notes: [this.data.entry?.note ],
      referenceNumber: [this.data.entry?.referenceNumber]
    });
  }

  saveLedgerEntry(): void {
    console.log('Saving ledger entry:', this.ledgerForm.value);
    if (this.ledgerForm.valid) {
      this.isSubmitting = true;
      const formValue = this.ledgerForm.value;
  
      const ledgerData = {
        entryId: formValue.entryId,
        date: new Date(formValue.date),
        particulars: formValue.particulars,
        type: formValue.type,
        amount: parseFloat(formValue.amount),
        user_id: this.userId,   
        note: formValue.notes,
        referenceNumber: formValue.referenceNumber
      };
  
      if (this.isEdit) {
        this.accountService.updateLedgerEntry(ledgerData.entryId, ledgerData).subscribe({
          next: (response) => {
            this.isSubmitting = false;
            this.dialogRef.close({ success: true, data: response });
          },
          error: (error) => {
            console.error('Error updating ledger entry:', error);
            this.isSubmitting = false;
          }
        });
      } 
    }
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.ledgerForm.valid) {
      this.saveLedgerEntry();
    }
  }

  // Utility method if needed for external setting of userId
  setUserId(userId: string): void {
    this.userId = userId;
  }
}