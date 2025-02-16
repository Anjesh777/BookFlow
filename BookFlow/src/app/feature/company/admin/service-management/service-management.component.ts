import { Component, OnInit } from '@angular/core';
import { Service } from '../../../../core/auth/model/admin';
import { AddServiceDialogComponent } from '../../../../core/ui/popup/add-service-dialog/add-service-dialog.component';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { UiServiceService } from '../../../../core/ui/ui-service.service';
import { AdminService } from '../../../../core/auth/service/Admin-Service/admin.service';

@Component({
  selector: 'app-service-management',
  standalone: true,
  imports: [],
  templateUrl: './service-management.component.html',
  styleUrl: './service-management.component.css'
})
export class ServiceManagementComponent implements OnInit {

  loading: boolean = false;

  service: Service[] = [];
  error: string | null = null;



  constructor(
        private uiService: UiServiceService,
        public dialog: MatDialog,
        private fb: FormBuilder,
        private admiservice:AdminService

  ){}

  ngOnInit(): void {

  }

  getAllCompanyUser() {
    this.loading = true;
    this.error = null;

    this.admiservice.getService().subscribe({
      next:(response) =>{
        this.service = response
        this.loading = false
      },
      error:(error) =>{
        console.error('Error ', error);
        this.loading = false;
      },
    })

  }


  openAddDialog(company?: Service) {
       const dialogRef = this.dialog.open(AddServiceDialogComponent, {
         width: '600px',
         data: company || {}
       });
     
       dialogRef.afterClosed().subscribe(result => {
        if(result){

          this.loading =true
          this.admiservice.addService(result).subscribe({
            next:(response) =>{

              console.log('Service added successfully:', response);
              this.loading =false
            },
            error:(error)=>{
              console.error('Error adding user:', error);

            },
          })

        }
      
         
       });
  }


}
