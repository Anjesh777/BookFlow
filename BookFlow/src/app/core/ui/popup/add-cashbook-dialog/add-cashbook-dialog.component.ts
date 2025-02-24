import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { BookflowService } from '../../../auth/service/BookFlow-Service/bookflow.service';
import { AccountServiceService } from '../../../auth/service/Account-Service/account-service.service';
import { DashboardSummary } from '../../../auth/model/account';

@Component({
  selector: 'app-add-cashbook-dialog',
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

        <h4 class="text-xl text-gray-800 font-semibold mb-6">Add New Transaction</h4>

        <form [formGroup]="transactionForm" (ngSubmit)="onSubmit()" class="space-y-4">
            <!-- Date -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Date</label>
                <input type="date" formControlName="date" 
                [max]="today"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                <span *ngIf="transactionForm.get('date')?.hasError('required') && transactionForm.get('date')?.touched"
                    class="text-sm text-red-500">Date is required</span>
            </div>

            <!-- Voucher Number -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Voucher Number</label>
                <input type="text" formControlName="voucherNumber" 
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter voucher number">
                <span *ngIf="transactionForm.get('voucherNumber')?.hasError('required') && transactionForm.get('voucherNumber')?.touched"
                    class="text-sm text-red-500">Voucher number is required</span>
            </div>

            <!-- Category -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Category</label>
                <select formControlName="category"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                    <option value="">Select Category</option>
                    <option value="INCOME">Income</option>
                    <option value="EXPENSE">Expense</option>
                    <option value="TRANSFER">Transfer</option>
                    <option value="SALARY">Salary</option>
                    <option value="UTILITIES">Utilities</option>
                    <option value="OTHER">Other</option>
                </select>
                <span *ngIf="transactionForm.get('category')?.hasError('required') && transactionForm.get('category')?.touched"
                    class="text-sm text-red-500">Category is required</span>
            </div>

            <!-- Receipt and Payment Amounts -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Receipt Amount (रु)</label>
                    <input type="number" formControlName="receiptAmount"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Enter receipt amount">
                    <span *ngIf="transactionForm.get('receiptAmount')?.hasError('min') && transactionForm.get('receiptAmount')?.touched"
                        class="text-sm text-red-500">Amount cannot be negative</span>
                </div>

                <div class="space-y-2">
                    <label class="text-sm text-gray-700 font-medium">Payment Amount (रु)</label>
                    <input type="number" formControlName="paymentAmount"
                        class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Enter payment amount">
                    <span *ngIf="transactionForm.get('paymentAmount')?.hasError('min') && transactionForm.get('paymentAmount')?.touched"
                        class="text-sm text-red-500">Amount cannot be negative</span>
                </div>
            </div>

            <!-- Description -->
            <div class="space-y-2">
                <label class="text-sm text-gray-700 font-medium">Description</label>
                <textarea formControlName="description" rows="3"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter transaction description"></textarea>
                <span *ngIf="transactionForm.get('description')?.hasError('required') && transactionForm.get('description')?.touched"
                    class="text-sm text-red-500">Description is required</span>
            </div>

            <!-- Reimbursement Pending -->
            <div class="space-y-2">
                <label class="flex items-center space-x-2">
                    <input type="checkbox" formControlName="reimbursementPending"
                        class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500">
                    <span class="text-sm text-gray-700 font-medium">Reimbursement Pending</span>
                </label>
            </div>

            <!-- Footer Actions -->
            <div class="flex justify-end items-center gap-4 mt-6 pt-4 border-t border-gray-200">
                <button type="button" (click)="onCancel()"
                    class="px-5 py-2.5 rounded-lg text-gray-700 text-sm border-none outline-none bg-gray-100 hover:bg-gray-200">
                    Cancel
                </button>
                <button type="submit" [disabled]="!transactionForm.valid || isLoading"
                    class="px-5 py-2.5 rounded-lg text-white text-sm border-none outline-none bg-blue-600 hover:bg-blue-500 disabled:opacity-50 disabled:cursor-not-allowed">
                    {{isLoading ? 'Adding...' : 'Add Transaction'}}
                </button>
            </div>
        </form>
    </div>
  </div>
  
  `,
})
export class AddCashbookDialogComponent implements OnInit{

  transactionForm: FormGroup;
  isLoading = false;


  today: string = new Date().toISOString().split('T')[0];


  summary: DashboardSummary = {
    currentBalance: 0,
    totalReceiptsToday: 0,
    totalPaymentsToday: 0,
    pendingReimbursements: 0
  };

  cashbook = inject(AccountServiceService)

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddCashbookDialogComponent>,
    private accountService: AccountServiceService,

  ) {
    this.transactionForm = this.fb.group({
      date: [new Date(), Validators.required],
      voucherNumber: ['', Validators.required],
      category: ['', Validators.required],
      receiptAmount: [0, [Validators.required, Validators.min(0)]],
      paymentAmount: [0, [Validators.required, Validators.min(0)]],
      description: ['', Validators.required],
      reimbursementPending: [false],
    });
  }
    ngOnInit(): void {


    }

    

    
    onSubmit() {
        if (this.transactionForm.valid) {
          this.isLoading = true;
          const formData = this.transactionForm.value;
          const transactionData = {
            ...formData,
            receiptAmount: Number(formData.receiptAmount),
            paymentAmount: Number(formData.paymentAmount),
            date: new Date(formData.date).toISOString().split('T')[0]
          };
      
          this.cashbook.addTransaction(transactionData).subscribe({
            next: (response) => {
              console.log('Transaction added successfully:', response);
              
              this.accountService.getTransactionSummary().subscribe({
                next: (data: DashboardSummary) => {
                  this.dialogRef.close({
                    // transaction: transactionData,
                    // summary: data
                  })
                  ;
                },
                error: (error) => {
                  console.error('Error fetching summary:', error);
                  this.isLoading = false;
                }
              });

            



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
