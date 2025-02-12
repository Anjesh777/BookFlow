import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { TimeAgoPipe } from '../../../core/pipe/shared/pipes/time-ago.pipe';
import { userDetails, userDetailsResponse } from '../../../core/auth/model/admin';
import { AddUserDialogComponent } from '../../../core/ui/popup/add-user-dialog/add-user-dialog.component';
import { AdminService } from '../../../core/auth/service/Admin-Service/admin.service';
import { User } from '../../../core/auth/model/auth';
import { CommentEditDialogComponent } from '../../../core/ui/popup/comment-edit-dialog/comment-edit-dialog.component';
import { EditUserDalogComponent } from '../../../core/ui/popup/edit-user-dalog/edit-user-dalog.component';

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
    TimeAgoPipe
  ],
  templateUrl: './usermanagement.component.html',
  styleUrl: './usermanagement.component.css'
})
export class UsermanagementComponent implements OnInit {

  users: userDetailsResponse[] = [];
  loading: boolean = false;
  error: string | null = null;


  constructor(
    public dialog: MatDialog,
    private fb: FormBuilder,
    private adminservice: AdminService
  ) {}
  ngOnInit(): void {
    this.getAllCompanyUser()
  }


  
  openAddDialog(company?: userDetails) {
    const dialogRef = this.dialog.open(AddUserDialogComponent, {
      width: '600px',
      data: company || {}
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.adminservice.addUsers(result).subscribe({
          next: (response) => {
            console.log('User added successfully:', response);
            this.getAllCompanyUser();
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

  openEditDialog(company: userDetailsResponse) {
    const dialogRef = this.dialog.open(EditUserDalogComponent, {
      width: '600px',
      data: company
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.getAllCompanyUser()
      }
    });
  }

  
  deleteUser(userId:string){

    



  }
}


