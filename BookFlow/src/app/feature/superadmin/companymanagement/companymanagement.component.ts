import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTable, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { TimeAgoPipe } from '../../../core/pipe/shared/pipes/time-ago.pipe';
import { User } from '../../../core/auth/model/auth';
import { companyDetails, CompanyFilter } from '../../../core/auth/model/bookflow';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/auth/service/auth.service';
import { BookflowService } from '../../../core/auth/service/BookFlow-Service/bookflow.service';
import { UiServiceService } from '../../../core/ui/ui-service.service';
import { MatChipsModule } from '@angular/material/chips';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { CompanyEditDialogComponentComponent } from '../../../core/ui/popup/company-edit-dialog-component/company-edit-dialog-component.component';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { DaterangeComponent } from "../../../core/ui/daterange/daterange.component";
import { FormBuilder } from '@angular/forms';
import { filter } from 'rxjs';

@Component({
  selector: 'app-companymanagement',
  standalone: true,
  imports: [
    CommonModule,
    TimeAgoPipe,
    FormsModule,
    ReactiveFormsModule,
    DaterangeComponent,
],



  templateUrl: './companymanagement.component.html',
  styleUrl: './companymanagement.component.css'
})
export class CompanymanagementComponent implements OnInit {


  numberOfCompanies: number = 0;
  isLoading: boolean = false;

  companyList: companyDetails[] = [];
  http = inject(HttpClient);
  filterValue: string = '';

  searchControl;
  verifiedControl;
  statusControl;
  dateRange: { fromDate: string; toDate: string } | null = null;
  paginator: any;
  sort: any;



  constructor(
           private uiService: UiServiceService,
            public dialog: MatDialog,
            private bookflowService: BookflowService,
            private authService: AuthService,
            private fb: FormBuilder

          
      ) {

        this.searchControl = this.fb.control('');
        this.verifiedControl = this.fb.control(null);
        this.statusControl = this.fb.control(null);
      }



  ngOnInit(): void {
    this.fetchAllCompanies();

  }

  // In CompanymanagementComponent

openEditDialog(company: companyDetails) {
  const dialogRef = this.dialog.open(CompanyEditDialogComponentComponent, {
    width: '600px',
    data: company
  });

  dialogRef.afterClosed().subscribe(result => {
    if (result) {
      this.fetchAllCompanies();
    }
  });
}
  
onDateRangeChange(dateRange: {fromDate: string, toDate: string}) {
  this.dateRange=dateRange
  console.log('Date range changed:', dateRange);
}
applyFilter() {
  const filters: CompanyFilter = {
    search: this.searchControl.value || undefined,
    verified: this.verifiedControl.value === null ? undefined : this.verifiedControl.value,
    status: this.statusControl.value === null ? undefined : this.statusControl.value,
    dateRange: this.dateRange ? {
      fromDate: new Date(this.dateRange.fromDate).toISOString().split('T')[0],
      toDate: new Date(this.dateRange.toDate).toISOString().split('T')[0]
    } : undefined
  };

  this.bookflowService.searchCompanies(filters).subscribe({
    next: (response) => this.companyList = response,
    error: (error) => {
      console.error('Error:', error);
      this.uiService.showErrorDialog('Failed to fetch companies');
    }
  });
}


  displayedColumns: string[] = ['id', 'compant name', 'registraion_nmber', 'email', 'phone', 'address', 'created_at','updated_at','enable'];


  fetchAllCompanies() {
    this.isLoading = true;
    this.bookflowService.getRecentAllCompanyDetails()
      .subscribe({
        next: (response) => {
          this.companyList = response;

          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error:', error);
          this.isLoading = false;
        }
      });
  }



}
