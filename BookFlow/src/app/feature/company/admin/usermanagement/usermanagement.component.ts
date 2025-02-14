import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { TimeAgoPipe } from '../../../../core/pipe/shared/pipes/time-ago.pipe';
import { userDetails, userDetailsResponse, UserFilter } from '../../../../core/auth/model/admin';
import { AddUserDialogComponent } from '../../../../core/ui/popup/add-user-dialog/add-user-dialog.component';
import { AdminService } from '../../../../core/auth/service/Admin-Service/admin.service';
import { EditUserDalogComponent } from '../../../../core/ui/popup/edit-user-dalog/edit-user-dalog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DaterangeComponent } from "../../../../core/ui/daterange/daterange.component";
import { UiServiceService } from '../../../../core/ui/ui-service.service';
@Component({
  selector: 'app-usermanagement',
  standalone: true,
  imports: [
    CommonModule,
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
  templateUrl: './usermanagement.component.html',
  styleUrl: './usermanagement.component.css'
})
export class UsermanagementComponent implements OnInit {


  searchUserControl;
  verifiedUserControl;
  userRole;
  userStatusControl;
  dateRange: { fromDate: string; toDate: string } | null = null;
  

  //userList: userDetailsResponse[] =[];


  users: userDetailsResponse[] = [];
  loading: boolean = false;
  error: string | null = null;

  



  constructor(
    private uiService: UiServiceService,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private adminservice: AdminService
  ) {

    this.searchUserControl = this.fb.control('');
    this.userRole = this.fb.control('');
    this.verifiedUserControl = this.fb.control(null)
    this.userStatusControl = this.fb.control(null);

  }
  ngOnInit(): void {
    this.getAllCompanyUser()
  }


  onDateRangeChange(dateRange: {fromDate: string, toDate: string}) {
    this.dateRange=dateRange
    console.log('Date range changed:', dateRange);
  }
  
  applyFilter() {
    this.loading = true;
  
    const filters: UserFilter = {
      search: this.searchUserControl.value || undefined,
      verified: this.verifiedUserControl.value === null ? undefined : this.verifiedUserControl.value,
      status: this.userStatusControl.value === null ? undefined : this.userStatusControl.value,
      dateRange: this.dateRange ? {
        fromDate: new Date(this.dateRange.fromDate).toISOString().split('T')[0],
        toDate: new Date(this.dateRange.toDate).toISOString().split('T')[0]
      } : undefined,
      role: this.userRole.value ===null? undefined : this.userRole.value

    };
  
    console.log("Value is: " + filters.search);

    setTimeout(() => {
    this.adminservice.searchUsers(filters).subscribe({
      next: (response) => {

        this.loading = false;
        this.users = response;
      },
      error: (error) => {
        this.loading = false;
        console.error('Error:', error);
        this.uiService.showErrorDialog('Failed to fetch companies');
      }
    });
  }, 2000);

  }



  
  openAddDialog(company?: userDetails) {
    const dialogRef = this.dialog.open(AddUserDialogComponent, {
      width: '600px',
      data: company || {}
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loading =true
        this.adminservice.addUsers(result).subscribe({
          next: (response) => {
            console.log('User added successfully:', response);
            this.getAllCompanyUser();
            this.loading =false
          },
          

          error: (error) => {
            console.error('Error adding user:', error);
            // Handle error (e.g., show error message)
          }
        });
      }
    });
  }

  getAllCompanyUser() {
    this.loading = true;
    this.error = null;

    this.adminservice.getUsers().subscribe({
      next: (response) => {
        this.users = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error ', error);
        this.loading = false;
      }
    });
  }

  openEditDialog(user: userDetailsResponse) {
    const dialogRef = this.dialog.open(EditUserDalogComponent, {
      width: '600px',
      data: user
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.getAllCompanyUser()
      }
    });
  }

  
  deleteUser(userId:string){
    this.loading = true;
    this.adminservice.deleteUser(userId).subscribe({
      next:(respond) =>{
        this.loading = false;
        this.getAllCompanyUser();
      },
      error:(error) =>{
        console.error('Error ',error)
        this.loading=false
      }

    })


  }
}


