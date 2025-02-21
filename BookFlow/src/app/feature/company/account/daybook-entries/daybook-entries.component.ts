import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormsModule } from '@angular/forms';
import { CashBook, DashboardSummary, Page } from '../../../../core/auth/model/account';
import { MatDialog } from '@angular/material/dialog';
import { UiServiceService } from '../../../../core/ui/ui-service.service';
import { AccountServiceService } from '../../../../core/auth/service/Account-Service/account-service.service';
import { AddCashbookDialogComponent } from '../../../../core/ui/popup/add-cashbook-dialog/add-cashbook-dialog.component';
import { firstValueFrom } from 'rxjs';
import { EditCashbookDialogComponent } from '../../../../core/ui/popup/edit-cashbook-dialog/edit-cashbook-dialog.component';

@Component({
  selector: 'app-daybook-entries',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './daybook-entries.component.html',
  styleUrl: './daybook-entries.component.css'
})
export class DaybookEntriesComponent implements OnInit{

  Math = Math;

  loading: boolean = false;
  transactions: CashBook[] = [];
  currentPage: number = 0;
  totalPages: number = 0;
  totalElements: number = 0;

  summary: DashboardSummary = {
    currentBalance: 0,
    totalReceiptsToday: 0,
    totalPaymentsToday: 0,
    pendingReimbursements: 0
  };
 

  constructor(
    private uiService: UiServiceService,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private accountService: AccountServiceService,
  )
  {


  }
  
  ngOnInit(): void {
   this.refreshData()

  }


  openEditDialog(cashbook: CashBook) {
    const dialogRef = this.dialog.open(EditCashbookDialogComponent, {
      width: '600px',
      data: cashbook
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.refreshData();
      }
    });
  }

  refreshData() {
    this.loading = true;
    Promise.all([
      firstValueFrom(this.accountService.getTransactionSummary()),
      firstValueFrom(this.accountService.getAllTransactions(this.currentPage, 10))
    ])
      .then(([summaryData, transactionsData]) => {
        if (summaryData) {
          this.summary = summaryData;
        }
        if (transactionsData) {
          this.transactions = transactionsData.content;
          this.totalPages = transactionsData.page.totalPages;
          this.currentPage = transactionsData.page.number;
        }
      })
      .catch(error => {
        console.error('Error fetching data:', error);
      })
      .finally(() => {
        this.loading = false;
      });
  }

  
  

  deleteTransaction(id:number){

    if (confirm('Are you sure you want to delete this service?')) {
      this.loading = true;
      this.accountService.deleteTransaction(id).subscribe({
        next: (): void => {
          this.uiService.showSuccessDialog('Record deleted successfully');
          this.refreshData()
          this.loading = false;
        },
        error: (error: any): void => {
          console.error('Error deleting service:', error);
          this.uiService.showErrorDialog('Failed to delete rRecord');
          this.loading = false;
        }
      });
     }

  }
  



  fetchTransactions(page: number) {
    this.loading = true;
    this.accountService.getAllTransactions(page, 10).subscribe({
      next: (data: Page<CashBook>) => {
        this.transactions = data.content; 
        this.totalPages = data.page.totalPages;
        this.currentPage = data.page.number;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching transactions:', error);
        this.loading = false;
      }
    });
  }

  
  getPagesArray(): number[] {
    const pagesArray: number[] = [];
    for (let i = 0; i < this.totalPages; i++) {
      pagesArray.push(i);
    }
    return pagesArray;
  }
  
  onPageChange(page: number) {
    this.fetchTransactions(page);
  }
  
  getCategoryDisplay(category: string): string {
    return category.charAt(0) + category.slice(1).toLowerCase();
  }


  openAddDialog(cashbook?: CashBook) {
      const dialogRef = this.dialog.open(AddCashbookDialogComponent, {
        width: '600px',
        data: cashbook || {}
      });
      
      dialogRef.afterClosed().subscribe(result => {
        this.refreshData();

      });
    }



}
