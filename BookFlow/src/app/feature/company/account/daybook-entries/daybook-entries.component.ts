import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormsModule } from '@angular/forms';
import { CashBook, DashboardSummary, Page, search } from '../../../../core/auth/model/account';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { UiServiceService } from '../../../../core/ui/ui-service.service';
import { AccountServiceService } from '../../../../core/auth/service/Account-Service/account-service.service';
import { AddCashbookDialogComponent } from '../../../../core/ui/popup/add-cashbook-dialog/add-cashbook-dialog.component';
import { filter, firstValueFrom, map, tap } from 'rxjs';
import { EditCashbookDialogComponent } from '../../../../core/ui/popup/edit-cashbook-dialog/edit-cashbook-dialog.component';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { TimeAgoPipe } from '../../../../core/pipe/shared/pipes/time-ago.pipe';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DaterangeComponent } from "../../../../core/ui/daterange/daterange.component";
import { HttpEventType } from '@angular/common/http';




@Component({
  selector: 'app-daybook-entries',
  standalone: true,
  imports: [
    
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatToolbarModule,
    TimeAgoPipe,
    MatProgressSpinnerModule,
    DaterangeComponent

  ],
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
  dateRange: { fromDate: string; toDate: string } | null = null;


  summary: DashboardSummary = {
    currentBalance: 0,
    totalReceiptsToday: 0,
    totalPaymentsToday: 0,
    pendingReimbursements: 0
  };

  searchUserTransaction;

  applyFilter() {
    this.loading = true;
    const filter: search = {
      searchText: this.searchUserTransaction.value || '',
      fromDate: this.dateRange?.fromDate ? new Date(this.dateRange.fromDate) : null,
      toDate: this.dateRange?.toDate ? new Date(this.dateRange.toDate) : null
  };

    this.accountService.searchTransactions(filter.searchText,filter.fromDate,filter.toDate, this.currentPage, 10)
      .subscribe({
        next: (data: Page<CashBook>) => {
          this.transactions = data.content;
          this.totalPages = data.page.totalPages;
          this.currentPage = data.page.number;
          this.totalElements = data.page.totalElements;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error searching transactions:', error);
          this.uiService.showErrorDialog('Failed to search transactions');
          this.loading = false;
        }
      });
  }

  clearFilters() {
    this.searchUserTransaction.reset();
    this.dateRange = null;
    this.currentPage = 0;
    this.refreshData();
  }





 

  constructor(
    private uiService: UiServiceService,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private accountService: AccountServiceService,
  )
  {
    this.searchUserTransaction= this.fb.control('');
  }

  onDateRangeChange(dateRange: {fromDate: string, toDate: string}) {
    this.dateRange=dateRange
    console.log('Date range changed:', dateRange);
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

  exportToCSV() {
    this.loading = true;
    const filter = {
      searchText: this.searchUserTransaction.value || '',
      fromDate: this.dateRange?.fromDate ? new Date(this.dateRange.fromDate) : undefined,
      toDate: this.dateRange?.toDate ? new Date(this.dateRange.toDate) : undefined
    };

    this.accountService.exportTransactionsToCSV(
      filter.searchText,
      filter.fromDate,
      filter.toDate
    ).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'cashbook.csv';
        link.click();
        window.URL.revokeObjectURL(url);
        this.loading = false;
        this.uiService.showSuccessDialog('Export completed successfully');
      },
      error: (error) => {
        console.error('Error exporting transactions:', error);
        this.uiService.showErrorDialog('Failed to export transactions');
        this.loading = false;
      }
    });
  }

  async handleFileImport(event: any) {
    const file = event.target.files[0];
    if (file) {
      if (file.type !== 'text/csv' && !file.name.endsWith('.csv')) {
        this.uiService.showErrorDialog('Please upload a valid CSV file');
        return;
      }
    
      this.loading = true;
      const formData = new FormData();
      formData.append('file', file);
    
      try {
        this.accountService.importTransactionsFromCsv(file)
          .pipe(
            tap(event => {
              if (event.type === HttpEventType.UploadProgress && event.total) {
                const percentDone = Math.round(100 * event.loaded / event.total);
                console.log(`Upload progress: ${percentDone}%`);
              }
            }),
            filter(event => event.type === HttpEventType.Response),
            map(event => event.body)
          )
          .subscribe({
            next: (response) => {
              this.loading = false;
              this.uiService.showSuccessDialog("File upload successful");
              this.refreshData();
              event.target.value = '';
            },
            error: (error) => {
              this.loading = false;
              console.error('Error importing CSV:', error);
              this.uiService.showErrorDialog('Failed to import transactions');
              event.target.value = '';
            }
          });
      } catch (error) {
        this.loading = false;
        console.error('Error importing CSV:', error);
        this.uiService.showErrorDialog('Failed to import transactions');
        event.target.value = '';
      }
    }
  }



}
